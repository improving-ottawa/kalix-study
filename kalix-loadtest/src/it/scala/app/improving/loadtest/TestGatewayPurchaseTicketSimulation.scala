package app.improving.loadtest

import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto._
import io.circe.parser
import io.circe.syntax.EncoderOps
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import org.slf4j.LoggerFactory

import scala.util.Random
import scala.concurrent.duration.DurationInt

class TestGatewayPurchaseTicketSimulation extends Simulation {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(
      s"https://${config.getString("app.improving.akka.grpc.gateway-client-url")}"
    ) // Here is the root for all relative URLs
    .acceptHeader(
      "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,application/json"
    ) // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
    )

  private val scn =
    scenario(
      "Gateway Start Scenario"
    ) // A scenario is a chain of requests and pauses
      .exec(
        http("start-scenario")
          .post("/gateway/start-scenario")
          .body(StringBody("""
              |{
              |   "scenario_info":{
              |      "num_tenants":1,
              |      "max_orgs_depth":2,
              |      "max_orgs_width":2,
              |      "num_members_per_org":1,
              |      "num_events_per_org":1,
              |      "num_tickets_per_event":1
              |   }
              |}
              |""".stripMargin))
          .asJson
          .check(status.is(200))
          .check(bodyString.exists)
          .check(bodyString.saveAs("ScenarioResults"))
      )
      .pause(7)
      .exec(session => {
        val scenarioResults = session("ScenarioResults").as[String]
        println(scenarioResults + " scenario result -------------")
        parser.decode[ScenarioResults](scenarioResults) match {
          case Left(error) =>
            throw new IllegalStateException(
              s"ScenarioResults is not returned properly - $error!!!"
            )
          case Right(result) =>
            println(
              result.storesForOrgs + " result storeIds +++++++++++++++++++++++++"
            )
            val r = new Random()
            val storeId = result.storesForOrgs.values
              .take(1)
              .head
              .storeIds
              .head
              .storeId
            println(
              storeId + " storeId +++++++++++++++++++++++++"
            )
            val products = result
              .productsForStores(storeId)
              .skus
              .take(r.nextInt(result.productsForStores(storeId).skus.size - 1))
            println(
              result.productsForStores(
                storeId
              ) + " result.productsForStores(storeId) +++++++++++++++++++++++++"
            )
            println(
              products + " products +++++++++++++++++++++++++"
            )
            val memberIds =
              result.membersForOrgs.values.toSeq.flatMap(_.memberIds)
            println(
              memberIds + " memberIds +++++++++++++++++++++++++"
            )
            val memberId = memberIds(r.nextInt(memberIds.size))
            var ordersForStoresForMembers = Map(
              memberId.memberId -> OrdersForStores(
                products.map { productId =>
                  storeId ->
                    ApiOrderInfo(
                      Seq[ApiLineItem](
                        ApiLineItem(Some(productId), 1)
                      )
                    )
                }.toMap
              )
            )
            println(
              s"{ ordersForStoresForMembers: ${ordersForStoresForMembers.asJson} }" + " ordersForStoresForMembers 000000000000000000000"
            )
            session.set(
              "OrdersForStoresForMembers",
              s"{ ordersForStoresForMembers: ${ordersForStoresForMembers.asJson} }"
            )
        }
      })
      .exec(
        http("purchase-ticket")
          .post("/order/purchase-ticket")
          .body(
            StringBody(session =>
              s"""${session("OrdersForStoresForMembers")
                  .as[String]}""".stripMargin
            )
          )
          .asJson
          .check(status.is(200))
          .check(bodyString.exists)
      )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
