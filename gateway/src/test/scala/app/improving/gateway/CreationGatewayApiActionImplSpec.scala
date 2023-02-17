package app.improving.gateway

import TestData._
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.ApiMemberId
import app.improving.ordercontext.order.ApiLineItem
import app.improving.productcontext.product.ApiProductDetails
import app.improving.productcontext.product.ApiProductDetails.ApiTicket
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class CreationGatewayApiActionImplSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures
    with Fixture {

  "CreationGatewayApiActionImpl" must {
    implicit val sys: ActorSystem = ActorSystem("OrderActionImpl")
    implicit val ec: ExecutionContextExecutor = sys.dispatcher

    val log = LoggerFactory.getLogger(this.getClass)

    lazy val config: Config = ConfigFactory.load()

    val gateWatyClientSettings = GrpcClientSettings.connectToServiceAt(
      config.getString(
        "app.improving.akka.grpc.gateway-client-url"
      ),
      config.getInt("app.improving.akka.grpc.client-url-port")
    )

    val gateWayAction: CreationGatewayApiAction =
      CreationGatewayApiActionClient(
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
          Some(apiEstablishOrganization)
        )

        val organizationCreated = gateWayAction
          .handleEstablishOrganization(command)
          .futureValue

        log.info(organizationCreated + " organizationCreated")
        organizationCreated.organizationCreated shouldBe defined

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
              sponsoringOrg =
                organizationsCreated.organizationsCreated.headOption
            )
          )
        )

        val eventCreated = gateWayAction
          .handleScheduleEvent(createEvent)
          .futureValue

        val eventId = eventCreated.eventCreated

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
    }
  }
}
