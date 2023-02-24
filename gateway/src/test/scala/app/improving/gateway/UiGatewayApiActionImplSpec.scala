package app.improving.gateway

import TestData._
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.{
  ApiEventId,
  ApiMemberId,
  ApiOrganizationId,
  ApiSku,
  ApiStoreId
}
import app.improving.eventcontext.AllEventsRequest
import app.improving.gateway.util.util.endFromResults
import app.improving.membercontext.AllMembersRequest
import app.improving.membercontext.member.MembersByEventTimeRequest
import app.improving.ordercontext.AllOrdersRequest
import app.improving.ordercontext.order.ApiLineItem
import app.improving.organizationcontext.AllOrganizationsRequest
import app.improving.productcontext.AllProductsRequest
import app.improving.productcontext.product.ApiProductDetails
import app.improving.productcontext.product.ApiProductDetails.ApiTicket
import app.improving.storecontext.AllStoresRequest
import app.improving.storecontext.store.ApiStore
import app.improving.tenantcontext.GetAllTenantRequest
import app.improving.tenantcontext.tenant.ApiReleaseTenant
import com.google.protobuf.timestamp.Timestamp
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.{Logger, LoggerFactory}

import java.time.{LocalDateTime, ZoneOffset}
import scala.concurrent.ExecutionContextExecutor

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class UiGatewayApiActionImplSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures
    with Fixture {
  implicit val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(5000, Millis))

  implicit val sys: ActorSystem = ActorSystem("OrderActionImpl")
  implicit val ec: ExecutionContextExecutor = sys.dispatcher

  val log: Logger = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val gateWayClientSettings: GrpcClientSettings =
    GrpcClientSettings.connectToServiceAt(
      config.getString(
        "app.improving.akka.grpc.gateway-client-url"
      ),
      config.getInt("app.improving.akka.grpc.client-url-port")
    )

  val creationGateWayAction: CreationGatewayApiAction =
    CreationGatewayApiActionClient(
      gateWayClientSettings
    )

  val gateWayAction: UiGatewayApiAction =
    UiGatewayApiActionClient(
      gateWayClientSettings
    )

  "UiGatewayApiActionImpl" must {

    "handle get all organizations correctly" in {
      val command: CreateOrganization = CreateOrganization(
        Some(apiEstablishOrganization)
      )

      val organizationCreated = creationGateWayAction
        .handleEstablishOrganization(command)
        .futureValue

      val result =
        gateWayAction
          .handleGetAllOrganizations(AllOrganizationsRequest())
          .futureValue

      result.organizations.nonEmpty shouldBe true

      creationGateWayAction.handleReleaseOrganizations(
        ReleaseOrganizations(
          organizationCreated.organizationCreated.toList.map(id =>
            ApiOrganizationId(id.id)
          )
        )
      )
    }

    "handle get all events correctly" in {
      val command: CreateEvent = CreateEvent(
        Some(apiEventInfo),
        Some(ApiMemberId(testMember1))
      )

      val eventScheduled = creationGateWayAction
        .handleScheduleEvent(command)
        .futureValue

      val result =
        gateWayAction.handleGetAllEvents(AllEventsRequest()).futureValue

      result.events.nonEmpty shouldBe true

      creationGateWayAction.handleReleaseEvents(
        ReleaseEvents(eventScheduled.eventCreated.toList)
      )
    }

    "handle get all tenants correctly" in {
      val tenant = creationGateWayAction
        .handleEstablishTenant(CreateTenant(Some(tenantInfo)))
        .futureValue

      val result =
        gateWayAction.handleGetAllTenants(GetAllTenantRequest()).futureValue

      result.tenants.nonEmpty shouldBe true

      creationGateWayAction.handleReleaseTenants(
        ReleaseTenants(
          tenant.tenantCreated.map(id => ApiReleaseTenant(id.tenantId)).toList
        )
      )
    }

    "handle get all stores correctly" in {

      val command: CreateStore = CreateStore(
        Some(apiStoreInfo),
        Some(ApiMemberId(testMember1))
      )

      val storeCreated = creationGateWayAction
        .handleCreateStore(command)
        .futureValue

      val result =
        gateWayAction.handleGetAllStores(AllStoresRequest()).futureValue

      result.stores.isEmpty shouldBe false

      creationGateWayAction.handleReleaseStores(
        ReleaseStores(
          storeCreated.storeCreated.toList.map(id => ApiStoreId(id.storeId))
        )
      )
    }

    "handle get all products correctly" in {
      val productCreated: ProductCreated = creationGateWayAction
        .handleCreateProduct(CreateProduct(Some(establishProduct)))
        .futureValue

      val result =
        gateWayAction.handleGetAllProducts(AllProductsRequest()).futureValue

      result.products.isEmpty shouldBe false

      creationGateWayAction.handleReleaseProducts(
        ReleaseProducts(
          productCreated.productCreated.toList.map(id => ApiSku(id.sku))
        )
      )
    }

    "handle get all orders correctly" in {

      val memberRegistered: MemberRegistered = creationGateWayAction
        .handleRegisterMember(
          RegisterMember(
            Some(
              EstablishMember(
                Some(memberApiInfo),
                Some(ApiMemberId(testMemberId))
              )
            )
          )
        )
        .futureValue

      val memberId = memberRegistered.memberRegistered

      val createEvent: CreateEvent = CreateEvent(
        Some(apiEventInfo),
        memberId
      )

      val eventCreated = creationGateWayAction
        .handleScheduleEvent(createEvent)
        .futureValue

      val eventId = eventCreated.eventCreated

      val command: CreateOrganizations = CreateOrganizations(
        Seq(
          establishOrganization.copy(members =
            establishOrganization.members ++ memberId.toSeq
          )
        )
      )

      val organizationsCreated = creationGateWayAction
        .handleEstablishOrganizations(command)
        .futureValue

      val apiProductInfoForEvent = apiProductInfo.copy(productDetails =
        apiProductInfo.productDetails
          .flatMap { details =>
            val ticket: ApiTicket = details.apiTicket
            if (ticket.isOpenTicket)
              ticket.openTicket.map(t =>
                ApiProductDetails.of(
                  ApiTicket.OpenTicket(
                    t.copy(event = eventId)
                  )
                )
              )
            else if (ticket.isReservedTicket)
              ticket.reservedTicket.map(t =>
                ApiProductDetails.of(
                  ApiTicket.ReservedTicket(t.copy(event = eventId))
                )
              )
            else
              ticket.restrictedTicket.map(t =>
                ApiProductDetails.of(
                  ApiTicket.RestrictedTicket(t.copy(event = eventId))
                )
              )
          }
      )
      val apiProductMetaInfoForEvent =
        apiProductMetaInfo.copy(createdBy = memberId)
      val establisProductForEvent = EstablishProduct(
        Some(apiProductInfoForEvent),
        Some(apiProductMetaInfoForEvent)
      )
      val productCreated =
        creationGateWayAction
          .handleCreateProduct(
            CreateProduct(Some(establisProductForEvent))
          )
          .futureValue

      val orderCreated = creationGateWayAction
        .handleCreateOrder(
          CreateOrder(
            Some(
              EstablishOrder(
                Some(
                  testOrderInfo.copy(lineItems =
                    Seq(
                      ApiLineItem(
                        productCreated.productCreated,
                        1,
                        10
                      )
                    )
                  )
                ),
                memberId
              )
            )
          )
        )
        .futureValue

      log.info(orderCreated + " orderCreated")

      val result =
        gateWayAction.handleGetAllOrders(AllOrdersRequest()).futureValue

      result.orders.nonEmpty shouldBe true

      creationGateWayAction.handleReleaseMembers(
        ReleaseMembers(
          memberRegistered.memberRegistered.toList
        )
      )

      creationGateWayAction.handleReleaseOrganizations(
        ReleaseOrganizations(
          organizationsCreated.organizationsCreated.toList.map(id =>
            ApiOrganizationId(id.organizationId)
          )
        )
      )

      creationGateWayAction.handleReleaseEvents(
        ReleaseEvents(
          eventId.toList
        )
      )

      creationGateWayAction.handleReleaseProducts(
        ReleaseProducts(productCreated.productCreated.toList)
      )

      creationGateWayAction.handleReleaseOrders(
        ReleaseOrders(orderCreated.orderCreated.toList)
      )
    }

    "handle command get all members correctly" in {
      val membersRegistered: MembersRegistered = creationGateWayAction
        .handleRegisterMembers(
          RegisterMembers(
            Seq(
              EstablishMember(
                Some(memberApiInfo),
                Some(ApiMemberId(testMemberId))
              )
            )
          )
        )
        .futureValue

      val result =
        gateWayAction.handleGetAllMembers(AllMembersRequest()).futureValue

      result.members.isEmpty shouldBe false

      creationGateWayAction.handleReleaseMembers(
        ReleaseMembers(
          membersRegistered.membersRegistered.toList.map(id =>
            ApiMemberId(id.memberId)
          )
        )
      )
    }
  }
}
