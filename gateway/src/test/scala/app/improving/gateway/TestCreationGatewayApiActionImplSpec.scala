package app.improving.gateway

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.gateway.util.util.endFromResults
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor

class TestCreationGatewayApiActionImplSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(500, Seconds), Span(5000, Millis))

  implicit val sys: ActorSystem = ActorSystem("OrderActionImpl")
  implicit val ec: ExecutionContextExecutor = sys.dispatcher

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val testGateWayClientSettings: GrpcClientSettings =
    GrpcClientSettings.connectToServiceAt(
      config.getString(
        "app.improving.akka.grpc.gateway-client-url"
      ),
      config.getInt("app.improving.akka.grpc.client-url-port")
    )

  val testCreationGatewayApiActionClient = TestCreationGatewayApiActionClient(
    testGateWayClientSettings
  )

  "TestCreationGatewayApiActionImpl" must {

    "handle command HandleStartScenario base case" in {
      val numTenants = 1
      val maxOrgsDepth = 2
      val maxOrgsWidth = 2
      val numMembersPerOrg = 1
      val numEventsPerOrg = 1
      val numTicketsPerEvent = 1

      val scenarioResult = testCreationGatewayApiActionClient
        .handleStartScenario(
          StartScenario(
            Some(
              ScenarioInfo(
                numTenants,
                maxOrgsDepth,
                maxOrgsWidth,
                numMembersPerOrg,
                numEventsPerOrg,
                numTicketsPerEvent
              )
            )
          )
        )
        .futureValue

      scenarioResult.tenants.isEmpty shouldBe false
      scenarioResult.storesForOrgs.isEmpty shouldBe false
      scenarioResult.eventsForOrgs.isEmpty shouldBe false
      scenarioResult.orgsForTenants.isEmpty shouldBe false
      scenarioResult.orgsForTenants.isEmpty shouldBe false
      scenarioResult.membersForOrgs.isEmpty shouldBe false
      scenarioResult.productsForStores.isEmpty shouldBe false

      testCreationGatewayApiActionClient
        .handleEndScenario(
          endFromResults(scenarioResult, Seq.empty)
        )
        .futureValue
    }

  }
}
