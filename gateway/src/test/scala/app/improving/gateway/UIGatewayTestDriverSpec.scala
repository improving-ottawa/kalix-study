package app.improving.gateway

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.{ApiMemberId, OrderId}
import app.improving.gateway.TestData.Fixture
import app.improving.ordercontext.order.{ApiLineItem, ApiOrderInfo}
import com.typesafe.config.{Config, ConfigFactory}
import io.circe
import org.scalatest.{Assertion, BeforeAndAfterAll}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import app.improving.gateway.util.util._
import app.improving.membercontext.member.MembersByEventTimeRequest
import app.improving.productcontext.AllProductsRequest
import app.improving.productcontext.product.ApiProductDetails.ApiTicket
import com.google.protobuf.timestamp.Timestamp
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.Source
import io.circe._
import io.circe.syntax._

import java.time.{LocalDateTime, ZoneOffset}
import scala.util.Random

class UIGatewayTestDriverSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures
    with Fixture {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(500, Seconds), Span(5000, Millis))

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

  val gatewayActionClient = UiGatewayApiActionClient(
    gatewayClientSettings
  )

  val creationGateWayAction: CreationGatewayApiAction =
    CreationGatewayApiActionClient(
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

      client
        .handleEndScenario(
          endFromResults(results, Seq.empty)
        )
        .futureValue
    }
    "handle command HandlePurchaseTicket base case" in {
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

      val scenarioResult =
        client.handleStartScenario(StartScenario(Some(info))).futureValue

      checkResults(scenarioResult, info)

      val r = new Random()
      val storeId = scenarioResult.productsForStores.keySet
        .take(1)
        .head
      val products = scenarioResult
        .productsForStores(storeId)
        .skus
        .take(r.nextInt(4))
      val memberIds =
        scenarioResult.membersForOrgs.values.toSeq.flatMap(_.memberIds)

      val memberId = memberIds(r.nextInt(memberIds.size))

      val orderIds = gatewayActionClient
        .handlePurchaseTickets(
          PurchaseTicketsRequest(
            Map(
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
          )
        )
        .futureValue

      orderIds.orderIds.isEmpty shouldBe false

      client
        .handleEndScenario(
          endFromResults(
            scenarioResult,
            orderIds.orderIds.map(id => OrderId(id.orderId))
          )
        )
        .futureValue
    }

    "handle command find member by event time correctly" in {
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

      val scenarioResult =
        client.handleStartScenario(StartScenario(Some(info))).futureValue

      checkResults(scenarioResult, info)

      val ScenarioResults(
        tenants,
        orgsForTenants,
        membersForOrgs,
        eventsForOrgs,
        storesForOrgs,
        productsForStores
      ) = scenarioResult

      val r = new Random()
      val storeId = productsForStores.keySet
        .take(1)
        .head
      val products = productsForStores(storeId).skus
        .take(r.nextInt(4))
      val memberIds = membersForOrgs.values.toSeq.flatMap(_.memberIds)

      val memberId = memberIds(r.nextInt(memberIds.size))

      val orderIds = gatewayActionClient
        .handlePurchaseTickets(
          PurchaseTicketsRequest(
            Map(
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
          )
        )
        .futureValue

      // Create order for querying the members by event time.

      orderIds.orderIds.isEmpty shouldBe false

      // Get the timestamp to query

      val productInfos = Future
        .sequence(
          products.map(sku =>
            creationGateWayAction
              .handleGetProductInfoById(
                GetProductInfoById(sku.sku)
              )
              .map(_.info)
          )
        )
        .futureValue
        .flatten

      val eventIds = productInfos
        .map(info => {
          info.productDetails.flatMap(detail => {
            detail.apiTicket match {
              case ApiTicket.Empty                   => None
              case ApiTicket.ReservedTicket(value)   => value.event
              case ApiTicket.RestrictedTicket(value) => value.event
              case ApiTicket.OpenTicket(value)       => value.event
            }
          })
        })
        .flatten

      val events = Future
        .sequence(
          eventIds.map(id =>
            creationGateWayAction.handleGetEventById(GetEventById(id.eventId))
          )
        )
        .futureValue

      val timestampOpt =
        events.headOption.flatMap(event =>
          event.info.map(info => {
            val expectStart = info.getExpectedStart
            val expectEnd = info.getExpectedEnd
            Timestamp.of(
              expectStart.seconds,
              (expectStart.nanos + expectEnd.nanos) / 2
            )
          })
        )

      val result = gatewayActionClient
        .handleGetMembersByEventTime(
          MembersByEventTimeRequest(timestampOpt)
        )
        .futureValue

      log.info(result + " result")
      result.members.isEmpty shouldBe false

      creationGateWayAction.handleReleaseMembers(
        ReleaseMembers(
          result.members.map(data => ApiMemberId(data.memberId))
        )
      )
    }
  }
}
