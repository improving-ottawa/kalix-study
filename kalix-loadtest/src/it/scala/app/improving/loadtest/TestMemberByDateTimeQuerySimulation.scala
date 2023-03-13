package app.improving.loadtest

import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto._
import io.circe.parser
import io.circe.syntax.EncoderOps
import io.gatling.core.Predef.{pace, pause, _}
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory
import io.gatling.core.body.BodySupport
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.protocol.HttpProtocolBuilder
import shapeless.syntax.inject.InjectSyntax

import scala.util.Random
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import java.time._
import scala.collection.immutable.Map
import scala.math.floor

class TestMemberByDateTimeQuerySimulation extends Simulation with BodySupport {

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

  private var ordersForStores: Map[String, OrdersForStores] = Map()
  private var endScenarioNoOrdersRequest: String = ""

  private val GenerateProducts = group("Create Products") {
    exec(
      http("create-products")
        .post("/gateway/start-scenario")
        .body(StringBody("""
          |{
          |   "scenario_info":{
          |      "num_tenants":1,
          |      "max_orgs_depth":4,
          |      "max_orgs_width":2,
          |      "num_members_per_org":10,
          |      "num_events_per_org":3,
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
          val ordersForStoresForMembers = memberIds
            .foldLeft(Map.empty[String, OrdersForStores])((accum, elem) => {
              val ordersForStores = products
                .foldLeft(Map.empty[String, ApiOrderInfo])((accumm, elemm) => {
                  val stores = result.storesForOrgs.values
                  val storeId = stores
                    .flatMap(_.storeIds)
                    .toArray
                    .apply(r.nextInt(stores.size))
                    .storeId
                  accumm ++ Map[String, ApiOrderInfo](
                    storeId ->
                      ApiOrderInfo(
                        Seq[ApiLineItem](
                          ApiLineItem(Some(elemm), 1)
                        )
                      )
                  )
                })
              accum ++ Map(elem.memberId -> OrdersForStores(ordersForStores))
            })
          log.info(
            s"{ ordersForStoresForMembers: ${ordersForStoresForMembers.asJson} }" + " ordersForStoresForMembers 000000000000000000000"
          )
          ordersForStores = ordersForStoresForMembers
          endScenarioNoOrdersRequest = s"""{
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
          session
      }
    })

  val PurchaseTickets: ChainBuilder = group("Purchase tickets") {
    exec(
      http("purchase-ticket")
        .post("/order/purchase-ticket")
        .body(
          StringBody { _ =>
            val r = scala.util.Random
            s"""{ ordersForStoresForMembers: ${ordersForStores(
                ordersForStores.keys.toSeq(
                  r.nextInt(ordersForStores.keys.size)
                )
              ).asJson} }""".stripMargin
          }
        )
        .asJson
        .check(status.is(200))
        .check(bodyString.exists)
        .check(bodyString.saveAs("OrdersPurchased"))
    )
  }

  private var orders: Seq[ApiOrderId] = Seq()

  val QueryMemberByEventTime: ChainBuilder =
    group("Query member by event time") {
      exec(
        http("query-member-by-event-time")
          .get { _ =>
            StringBody { session =>
              orders = orders ++ (parser.decode[ApiOrderIds](
                session("OrdersPurchased").as[String]
              ) match {
                case Left(error) =>
                  throw new IllegalStateException(
                    s"ScenarioResults is not returned properly - $error!!!"
                  )
                case Right(result) => result.orderIds
              })
              ""
            }
            val random = scala.util.Random
            val now =
              ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(random.nextInt(10))
            s"""/member/get-members-by-event-time?given_time=${now.toString}"""
          }
          .asJson
          .check(status.is(200))
          .check(bodyString.exists)
          .check(bodyString.saveAs("BODY"))
      )
    }

  val ReleaseOrders: ChainBuilder = group("ReleaseOrder") {
    exec(
      http("release-order")
        .post("/event/release-orders")
        .body(
          StringBody { _ =>
            s"""|{ "orders": ${orders.asJson}""".stripMargin
          }
        )
        .asJson
        .check(status.is(200))
        .check(bodyBytes.exists)
    )
  }

  val EndScenario: ChainBuilder = group("EndScenario") {
    exec(
      http("end-scenario")
        .post("/gateway/end-scenario")
        .body(
          StringBody { _ =>
            log.info(orders.asJson.toString)
            s"""|$endScenarioNoOrdersRequest
            |   "orders": ${orders.asJson}
            |   }
            |}""".stripMargin
          }
        )
        .asJson
        .check(status.is(200))
        .check(bodyBytes.exists)
        .check(bodyLength.is(2))
    )
  }

  private def getScn(
      repeat: Int,
      myPause: Expression[FiniteDuration]
  ) = scenario(
    "Query MemberByDateTime Query Scenario Init"
  ).repeat(repeat) {
    exec(PurchaseTickets)
      .exec(exec(QueryMemberByEventTime))
      .pause(myPause)
  }

  private val scn = exec(getScn(100, 5 seconds))
    .exec(getScn(50, 4 seconds))
    .exec(getScn(70, 3 seconds))
    .exec(getScn(120, 2 seconds))
    .exec(getScn(150, 1 seconds))

  private val initialInjectionProfile = rampUsers(1).during(10 seconds)

  private val injectionProfile =
    (constantUsersPerSec(1) during (5 seconds)) :: (2 to 10).toList.flatMap(i =>
      List(
        nothingFor(150 seconds),
        rampUsers(i) during (floor(i / 2).toInt seconds)
      )
    )

  setUp(
    scenario(
      "RunScenario"
    ).exec(GenerateProducts)
      .inject(initialInjectionProfile)
      .protocols(httpProtocol)
      .andThen(
        scenario(
          "Purchase&Query"
        ).exec(scn)
          .inject(injectionProfile)
          .protocols(httpProtocol)
          .andThen(
            scenario(
              "EndScenario"
            ).exec(EndScenario)
              .inject(initialInjectionProfile)
              .protocols(httpProtocol)
          )
      )
  )
}
