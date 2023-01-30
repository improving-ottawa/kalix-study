package app.improving.gateway

import TestData._
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.organizationcontext.organization.{
  OrganizationService,
  OrganizationServiceClient
}
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

    "handle command EstablishOrganization" in {

      val command: CreateOrganization = CreateOrganization(
        Some(establishOrganization)
      )

      val organizationsCreated = gateWayAction
        .handleEstablishOrganization(command)
        .futureValue

      log.info(organizationsCreated + " organizationCreated")
      organizationsCreated.organizationCreated shouldBe defined

    }
  }
}
