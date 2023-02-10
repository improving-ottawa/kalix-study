package app.improving.gateway

import TestData._
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.ApiMemberId
import app.improving.eventcontext.AllEventsRequest
import app.improving.ordercontext.order.ApiLineItem
import app.improving.tenantcontext.GetAllTenantRequest
import app.improving.organizationcontext.AllOrganizationsRequest
import app.improving.ordercontext.AllOrdersRequest
import app.improving.storecontext.AllStoresRequest
import app.improving.productcontext.AllProductsRequest
import app.improving.membercontext.AllMembersRequest
import app.improving.membercontext.member.MembersByEventTimeRequest
import com.google.protobuf.timestamp.Timestamp
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import scala.concurrent.Future

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class GatewayApiActionImplSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures
    with Fixture {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  implicit val sys = ActorSystem("OrderActionImpl")
  implicit val ec = sys.dispatcher

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val gateWatyClientSettings = GrpcClientSettings.connectToServiceAt(
    config.getString(
      "app.improving.akka.grpc.gateway-client-url"
    ),
    config.getInt("app.improving.akka.grpc.client-url-port")
  )

  val gateWayAction: GatewayApiAction = GatewayApiActionClient(
    gateWatyClientSettings
  )

  "GatewayApiActionImpl" should {
    "handle command EstablishTenant" in {
      val tenantCreated: TenantCreated = gateWayAction
        .handleEstablishTenant(CreateTenant(Some(tenantInfo)))
        .futureValue

      log.info(tenantCreated + " tenantCreated")
      tenantCreated.tenantCreated shouldBe defined
    }

    "handle command EstablishTenants" in {
      val tenantsCreated: TenantsCreated = gateWayAction
        .handleEstablishTenants(CreateTenants(Seq(tenantInfo)))
        .futureValue

      log.info(tenantsCreated + " tenantsCreated")
      tenantsCreated.tenantsCreated.isEmpty shouldBe false
    }

    "handle command EstablishOrganization" in {

      val command: CreateOrganization = CreateOrganization(
        Some(establishOrganization)
      )

      val organizationCreated = gateWayAction
        .handleEstablishOrganization(command)
        .futureValue

      log.info(organizationCreated + " organizationCreated")
      organizationCreated.organizationCreated shouldBe defined

    }

    "handle command EstablishOrganizations" in {

      val command: CreateOrganizations = CreateOrganizations(
        Seq(establishOrganization)
      )

      val organizationsCreated = gateWayAction
        .handleEstablishOrganizations(command)
        .futureValue

      log.info(organizationsCreated + " organizationCreated")
      organizationsCreated.organizationsCreated.isEmpty shouldBe false

    }

    "handle command ScheduleEvent" in {

      val command: CreateEvent = scheduleEvent

      val eventCreated = gateWayAction
        .handleScheduleEvent(command)
        .futureValue

      log.info(eventCreated + " eventCreated")
      eventCreated.eventCreated shouldBe defined

    }

    "handle command ScheduleEvents" in {

      val command: CreateEvents = CreateEvents(
        Seq(apiEventInfo),
        Some(ApiMemberId(testMember))
      )

      val eventsCreated = gateWayAction
        .handleScheduleEvents(command)
        .futureValue

      log.info(eventsCreated + " eventsCreated")
      eventsCreated.eventsCreated.isEmpty shouldBe false

    }

    "handle command CreateStore" in {

      val command: CreateStore = CreateStore(
        Some(apiStoreInfo),
        Some(ApiMemberId(testMember1))
      )

      val storeCreated = gateWayAction
        .handleCreateStore(command)
        .futureValue

      log.info(storeCreated + " storeCreated")
      storeCreated.storeCreated shouldBe defined

    }

    "handle command CreateStores" in {

      val command: CreateStores = CreateStores(
        Seq(apiStoreInfo),
        Some(ApiMemberId(testMember1))
      )

      val storesCreated = gateWayAction
        .handleCreateStores(command)
        .futureValue

      log.info(storesCreated + " storesCreated")
      storesCreated.storesCreated.isEmpty shouldBe false

    }

    "handle command CreateProduct" in {
      val productCreated: ProductCreated = gateWayAction
        .handleCreateProduct(CreateProduct(Some(establishProduct)))
        .futureValue

      log.info(productCreated + " productCreated")
      productCreated.productCreated shouldBe defined
    }

    "handle command CreateProducts" in {
      val productsCreated: ProductsCreated = gateWayAction
        .handleCreateProducts(CreateProducts(Seq(establishProduct)))
        .futureValue

      log.info(productsCreated + " productsCreated")
      productsCreated.productsCreated.isEmpty shouldBe false
    }

    "handle command RegisterMember" in {
      val memberRegistered: MemberRegistered = gateWayAction
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

      log.info(memberRegistered + " memberRegistered")
      memberRegistered.memberRegistered shouldBe defined
    }

    "handle command RegisterMembers" in {
      val membersRegistered: MembersRegistered = gateWayAction
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

      log.info(membersRegistered + " membersRegistered")
      membersRegistered.membersRegistered.isEmpty shouldBe false
    }

    "handle command CreateOrder" in {
      val memberRegistered: MemberRegistered = gateWayAction
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

      val eventCreated = gateWayAction
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

      val organizationsCreated = gateWayAction
        .handleEstablishOrganizations(command)
        .futureValue

      val apiProductInfoForEvent = apiProductInfo.copy(event = eventId)
      val apiProductMetaInfoForEvent =
        apiProductMetaInfo.copy(createdBy = memberId)
      val establisProductForEvent = EstablishProduct(
        Some(apiProductInfoForEvent),
        Some(apiProductMetaInfoForEvent)
      )
      val productCreated =
        gateWayAction
          .handleCreateProduct(
            CreateProduct(Some(establisProductForEvent))
          )
          .futureValue

      val orderCreated = gateWayAction
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
      orderCreated.orderCreated shouldBe defined
    }

    "handle command for CreateOrder private event" in {
      val memberRegistered: MemberRegistered = gateWayAction
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

      val command: CreateOrganizations = CreateOrganizations(
        Seq(
          establishOrganization.copy(members =
            establishOrganization.members ++ memberId.toSeq
          )
        )
      )
      val organizationsCreated = gateWayAction
        .handleEstablishOrganizations(command)
        .futureValue

      val createEvent: CreateEvent = scheduleEventPrivate.copy(info =
        scheduleEventPrivate.info.map(
          _.copy(
            sponsoringOrg = organizationsCreated.organizationsCreated.headOption
          )
        )
      )

      val eventCreated = gateWayAction
        .handleScheduleEvent(createEvent)
        .futureValue

      val eventId = eventCreated.eventCreated

      val apiProductInfoForEvent = apiProductInfo.copy(event = eventId)
      val apiProductMetaInfoForEvent =
        apiProductMetaInfo.copy(createdBy = memberId)
      val establisProductForEvent = EstablishProduct(
        Some(apiProductInfoForEvent),
        Some(apiProductMetaInfoForEvent)
      )
      val productCreated =
        gateWayAction
          .handleCreateProduct(
            CreateProduct(Some(establisProductForEvent))
          )
          .futureValue

      val orderCreated = gateWayAction
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

      log.info(orderCreated + " orderCreated private event")
      orderCreated.orderCreated shouldBe defined
    }

    "handle failed command for CreateOrder private event" in {
      val createEvent: CreateEvent = scheduleEventPrivate

      val eventCreated = gateWayAction
        .handleScheduleEvent(createEvent)
        .futureValue

      val eventId = eventCreated.eventCreated

      val apiProductInfoForEvent = apiProductInfo.copy(event = eventId)
      val establisProductForEvent = EstablishProduct(
        Some(apiProductInfoForEvent),
        Some(apiProductMetaInfo)
      )
      val productCreated =
        gateWayAction
          .handleCreateProduct(
            CreateProduct(Some(establisProductForEvent))
          )
          .futureValue

      intercept[Exception](
        gateWayAction
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
                  None
                )
              )
            )
          )
          .futureValue
      )
    }

    "handle get all organizations correctly" in {
      val command: CreateOrganization = CreateOrganization(
        Some(establishOrganization)
      )

      val organizationCreated = gateWayAction
        .handleEstablishOrganization(command)
        .futureValue

      val result =
        gateWayAction
          .handleGetAllOrganizations(AllOrganizationsRequest())
          .futureValue
      println(result + " result")
      result.organizations.size > 0 shouldBe true
    }

    "handle get all events correctly" in {
      val createEvent: CreateEvent = scheduleEventPrivate

      val result =
        gateWayAction.handleGetAllEvents(AllEventsRequest()).futureValue

      result.events.nonEmpty shouldBe true

    }

    "handle get all tenants correctly" in {
      gateWayAction
        .handleEstablishTenant(CreateTenant(Some(tenantInfo)))
        .futureValue

      val result =
        gateWayAction.handleGetAllTenants(GetAllTenantRequest()).futureValue

      result.tenants.nonEmpty shouldBe true
    }

    "handle get all stores correctly" in {

      val command: CreateStore = CreateStore(
        Some(apiStoreInfo),
        Some(ApiMemberId(testMember1))
      )

      val storeCreated = gateWayAction
        .handleCreateStore(command)
        .futureValue

      val result =
        gateWayAction.handleGetAllStores(AllStoresRequest()).futureValue

      result.stores.isEmpty shouldBe false
    }

    "handle get all products correctly" in {
      val productCreated: ProductCreated = gateWayAction
        .handleCreateProduct(CreateProduct(Some(establishProduct)))
        .futureValue

      val result =
        gateWayAction.handleGetAllProducts(AllProductsRequest()).futureValue

      result.products.isEmpty shouldBe false

    }

    "handle get all orders correctly" in {

      val memberRegistered: MemberRegistered = gateWayAction
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

      val eventCreated = gateWayAction
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

      val organizationsCreated = gateWayAction
        .handleEstablishOrganizations(command)
        .futureValue

      val apiProductInfoForEvent = apiProductInfo.copy(event = eventId)
      val apiProductMetaInfoForEvent =
        apiProductMetaInfo.copy(createdBy = memberId)
      val establisProductForEvent = EstablishProduct(
        Some(apiProductInfoForEvent),
        Some(apiProductMetaInfoForEvent)
      )
      val productCreated =
        gateWayAction
          .handleCreateProduct(
            CreateProduct(Some(establisProductForEvent))
          )
          .futureValue

      val orderCreated = gateWayAction
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

    }

    "handle command get all members correctly" in {
      val membersRegistered: MembersRegistered = gateWayAction
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
    }
  }

  "handle command find member by event time correctly" in {

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val result = gateWayAction
      .handleGetMembersByEventTime(
        MembersByEventTimeRequest(Some(timestamp))
      )
      .futureValue

    result.members.isEmpty shouldBe false
  }
}
