package app.improving.gateway

import TestData._
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.ApiMemberId
import app.improving.organizationcontext.organization.{
  OrganizationService,
  OrganizationServiceClient
}
import app.improving.ApiMemberId
import app.improving.membercontext.AllMembersRequest
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

      val command: CreateEvent = CreateEvent(
        Some(scheduleEvent)
      )

      val eventCreated = gateWayAction
        .handleScheduleEvent(command)
        .futureValue

      log.info(eventCreated + " eventCreated")
      eventCreated.eventCreated shouldBe defined

    }

    "handle command ScheduleEvents" in {

      val command: CreateEvents = CreateEvents(
        Seq(scheduleEvent)
      )

      val eventsCreated = gateWayAction
        .handleScheduleEvents(command)
        .futureValue

      log.info(eventsCreated + " eventsCreated")
      eventsCreated.eventsCreated.isEmpty shouldBe false

    }

    "handle command CreateStore" in {

      val command: CreateStore = CreateStore(
        Some(
          EstablishStore(
            Some(apiStoreInfo),
            Some(ApiMemberId(testMember1))
          )
        )
      )

      val storeCreated = gateWayAction
        .handleCreateStore(command)
        .futureValue

      println(storeCreated + " storeCreated")
      storeCreated.storeCreated shouldBe defined

    }

    "handle command CreateStores" in {

      val command: CreateStores = CreateStores(
        Seq(
          EstablishStore(
            Some(apiStoreInfo),
            Some(ApiMemberId(testMember1))
          )
        )
      )

      val storesCreated = gateWayAction
        .handleCreateStores(command)
        .futureValue

      println(storesCreated + " storesCreated")
      storesCreated.storesCreated.isEmpty shouldBe false

    }

    "handle command CreateProduct" in {
      val productCreated: ProductCreated = gateWayAction
        .handleCreateProduct(CreateProduct(Some(establishProduct)))
        .futureValue

      println(productCreated + " productCreated")
      productCreated.productCreated shouldBe defined
    }

    "handle command CreateProducts" in {
      val productsCreated: ProductsCreated = gateWayAction
        .handleCreateProducts(CreateProducts(Seq(establishProduct)))
        .futureValue

      println(productsCreated + " productsCreated")
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

      println(memberRegistered + " memberRegistered")
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

      println(membersRegistered + " membersRegistered")
      membersRegistered.membersRegistered.isEmpty shouldBe false
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
}
