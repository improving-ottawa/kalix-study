package app.improving.loadtest

import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto._
import io.circe.parser
import io.circe.syntax.EncoderOps
import io.gatling.core.Predef._
import io.gatling.core.body.BodySupport
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import org.slf4j.LoggerFactory

import java.time._
import scala.concurrent.duration.DurationInt
import scala.util.Random

class TestOrderByProductQuerySimulation extends Simulation with BodySupport {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(
      s"https://${config.getString("app.improving.akka.grpc.gateway-client-url")}"
    )
    .acceptHeader(
      "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,application/json"
    )
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
    )

  private val GenerateProducts = group("Create Products") {
    exec(
      http("create-products")
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
  }.pause(5)
    .exec(session => {
      val scenarioResults = session("ScenarioResults").as[String]
      log.info(scenarioResults + " scenario result -------------")
      parser.decode[ScenarioResults](scenarioResults) match {
        case Left(error) =>
          throw new IllegalStateException(
            s"ScenarioResults is not returned properly - $error!!!"
          )
        case Right(result) =>
          log.info(
            result.storesForOrgs + " result storeIds +++++++++++++++++++++++++"
          )
          val r = new Random()
          val storeId = result.storesForOrgs.values
            .take(1)
            .head
            .storeIds
            .head
            .storeId
          log.info(
            storeId + " storeId +++++++++++++++++++++++++"
          )
          val products = result
            .productsForStores(storeId)
            .skus
          log.info(
            result.productsForStores(
              storeId
            ) + " result.productsForStores(storeId) +++++++++++++++++++++++++"
          )
          log.info(
            products + " products +++++++++++++++++++++++++"
          )
          val memberIds =
            result.membersForOrgs.values.toSeq.flatMap(_.memberIds)
          log.info(
            memberIds + " memberIds +++++++++++++++++++++++++"
          )
          val memberId = memberIds(r.nextInt(memberIds.size))
          val ordersForStoresForMembers = Map(
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
          log.info(
            s"{ ordersForStoresForMembers: ${ordersForStoresForMembers.asJson} }" + " ordersForStoresForMembers +++++++++++++++++++++++++"
          )
          session
            .set(
              "EndScenarioNoOrders",
              s"""{
                 |   "end_scenario":{
                 |      "tenants": ${result.tenants.asJson},
                 |      "orgs": ${result.orgsForTenants.values.toSeq
                  .flatMap(_.orgIds)
                  .asJson},
                 |      "members": ${result.membersForOrgs.values.toSeq
                  .flatMap(_.memberIds)
                  .asJson},
                 |      "events": ${result.eventsForOrgs.values.toSeq
                  .flatMap(_.eventIds)
                  .asJson},
                 |      "stores": ${result.storesForOrgs.values.toSeq
                  .flatMap(_.storeIds)
                  .asJson},
                 |      "products": ${result.productsForStores.values.toSeq
                  .flatMap(_.skus)
                  .asJson},
                 |""".stripMargin
            )
            .set(
              "OrdersForStoresForMembers",
              s"{ ordersForStoresForMembers: ${ordersForStoresForMembers.asJson} }"
            )
            .set(
              "Skus",
              s"""{ "skus": ${products.asJson} }"""
            )
            .set("Skus-size", products.size)
      }
    })

  val PurchaseTickets: ChainBuilder = group("Purchase tickets") {
    exec(
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
        .check(bodyString.saveAs("OrdersPurchased"))
    )
  }
  val orderQueryRequest = http("Order By Product Request")
    .get("/orders/get-orders-by-product-id")
    .queryParam("sku", "#{sku}")

  val QueryOrderByProduct: ChainBuilder =
    group("Query order by product id") {
      repeat(
        session => {
          session("Skus-size").as[Int]
        }, {
          "productIndex"
        }
      ) {
        exec(session => {
          val products =
            parser.decode[Skus](session("Skus").as[String]) match {
              case Left(error) =>
                throw new IllegalStateException(
                  s"Products in the session can not be parsed - $error!!!"
                )
              case Right(value) => value
            }
          val productIndex = session("productIndex").as[Int]
          val sku = products.skus.toArray.apply(productIndex).sku
          log.info(
            s"sku to search for order +++++++++++++++++++++++++ " + sku
          )
          session.set("sku", sku)
        }).exec(orderQueryRequest)
      }
    }

  val EndScenario: ChainBuilder = group("EndScenario") {
    exec(
      http("end-scenario")
        .post("/gateway/end-scenario")
        .body(
          StringBody(session => {
            log.info(
              s"""${session("EndScenarioNoOrders").as[String]} ${session(
                  "OrdersPurchased"
                ).as[String]}
                ]} ---------------- session(EndScenarioNoOrders).as[String]"""
            )
            s"""|${session("EndScenarioNoOrders").as[String]}
                |
                "orders":${parser.decode[ApiOrderIds](
                 session("OrdersPurchased").as[String]
               ) match {
                 case Left(error) =>
                   throw new IllegalStateException(
                     s"ScenarioResults is not returned properly - $error!!!"
                   )
                 case Right(result) => result.orderIds.asJson
               }}
                |    }
                |}""".stripMargin
          })
        )
        .asJson
        .check(status.is(200))
        .check(bodyBytes.exists)
        .check(bodyLength.is(2))
    )
  }

  val Init: ChainBuilder = exec(GenerateProducts, PurchaseTickets)

  private val scn =
    scenario(
      "Query Order By Product Id Query Scenario Init"
    ).exec(Init)
      .repeat(1) {
        pace(10)
      }
      .exec(exec(QueryOrderByProduct).repeat(1) {
        pace(10)
        pause(10)
      })
      .exec(exec(EndScenario).repeat(1) {
        pace(10)
      })

  private val injectionProfile = rampUsers(1).during(10 seconds)

  setUp(scn.inject(injectionProfile)).protocols(httpProtocol)
}
