package app.improving.gateway

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.gateway.TestData.Fixture
import com.typesafe.config.{Config, ConfigFactory}
import io.circe
import org.scalatest.{Assertion, BeforeAndAfterAll}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor
import scala.io.Source
import io.circe._
import io.circe.syntax._

import scala.collection.immutable

class UIGatewayTestDriverSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures
    with Fixture {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  implicit val sys: ActorSystem = ActorSystem("UIGatewayTestDriverSpec")
  implicit val ec: ExecutionContextExecutor = sys.dispatcher

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val gatewayClientSettings: GrpcClientSettings =
    GrpcClientSettings.connectToServiceAt(
      config.getString(
        "app.improving.akka.grpc.gateway-client-url"
      ),
      config.getInt("app.improving.akka.grpc.client-url-port")
    )

  val client: TestGatewayApiActionClient = TestGatewayApiActionClient(
    gatewayClientSettings
  )

  val requestParamsOrParsingFailure: Either[ParsingFailure, Json] =
    circe.parser.parse(
      Source.fromResource("json/minimumRequestParams.json").mkString
    )

  def checkResults(
      results: ScenarioResults,
      info: ScenarioInfo
  ): Assertion = {
    results.tenants.size shouldEqual info.numTenants
    results.orgsForTenants.size shouldEqual info.numTenants
    // results.orgsForTenants.map(
    //  _._2.orgIds.size shouldEqual info.numOrgsPerTenant
    // )
    // results.membersForOrgs.size shouldEqual info.numOrgsPerTenant * info.numTenants
    // results.membersForOrgs.map(
    //  _._2.memberIds.size shouldEqual info.numMembersPerOrg
    // )
    // results.eventsForOrgs.size shouldEqual info.numOrgsPerTenant * info.numTenants
    // results.eventsForOrgs.map(
    //  _._2.eventIds.size shouldEqual info.numEventsPerOrg
    // )
    // results.storeIds.size shouldEqual info.numOrgsPerTenant * info.numTenants
    // results.storeIds.map(
    //  _.storeIds.size shouldEqual info.numStores
    // )
    // results.productsForStores.size shouldEqual info.numStores * info.numOrgsPerTenant * info.numTenants
    // results.productsForStores.map(
    //  _._2.skus.size shouldEqual info.numEventsPerOrg * info.numOrgsPerTenant * info.numTenants
    // )
  }

  "GatewayApiActionImpl" should {
    "handle small scenario w/ no orders" in {
      val json =
        requestParamsOrParsingFailure.getOrElse(JsonObject.empty.asJson)

      val info = ScenarioInfo(
        json.hcursor.downField("num_tenants").as[Int].getOrElse(0),
        json.hcursor.downField("max_orgs_depth").as[Int].getOrElse(0),
        json.hcursor.downField("max_orgs_width").as[Int].getOrElse(0),
        json.hcursor.downField("num_members_per_org").as[Int].getOrElse(0),
        json.hcursor.downField("num_events_per_org").as[Int].getOrElse(0),
        json.hcursor.downField("num_tickets_per_event").as[Int].getOrElse(0)
      )

      val results =
        client.handleStartScenario(StartScenario(Some(info))).futureValue

      checkResults(results, info)

      println(results)
    }
  }
}