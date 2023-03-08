package app.improving.loadtest

import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto._
import io.circe.parser
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax.EncoderOps
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory
import com.google.protobuf.Timestamp
import io.gatling.core.body.{BodySupport, BodyWithStringExpression, StringBody}
import io.gatling.core.controller.inject.InjectionProfile
import io.gatling.core.controller.inject.closed.ClosedInjectionBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.util.Random
import scala.concurrent.duration.DurationInt
import java.time._

class TestMemberByDateTimeQuerySimulation extends Simulation with BodySupport {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  lazy val cache = Map.empty[String, String]

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
            .take(r.nextInt(result.productsForStores(storeId).skus.size - 1))
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
            s"{ ordersForStoresForMembers: ${ordersForStoresForMembers.asJson} }" + " ordersForStoresForMembers 000000000000000000000"
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

  val QueryMemberByEventTime: ChainBuilder =
    group("Query member by event time") {
      exec(
        http("query-member-by-event-time")
          .get(_ => {
            val random = scala.util.Random
            val now =
              ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(random.nextInt(10))
            s"""/member/members-by-event-time?given_time=${now.toString}"""
          })
          .asJson
          .check(status.is(200))
          .check(bodyString.exists)
          .check(bodyString.saveAs("BODY"))
      )
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
      "Query MemberByDateTime Query Scenario Init"
    ).exec(GenerateProducts, PurchaseTickets)
      .repeat(1) {
        pace(10)
      }
      .exec(exec(QueryMemberByEventTime).repeat(1) {
        pace(10)
        pause(10)
      })
      .exec(exec(EndScenario).repeat(1) {
        pace(10)
      })

  private val injectionProfile = rampUsers(1).during(10 seconds)

  setUp(scn.inject(injectionProfile)).protocols(httpProtocol)
}