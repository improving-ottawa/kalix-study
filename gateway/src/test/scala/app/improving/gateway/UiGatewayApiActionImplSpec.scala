package app.improving.gateway

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.{ApiSku, ApiStoreId}
import app.improving.ordercontext.order.{ApiLineItem, ApiOrderInfo, OrderAction, OrderServiceClient}
import app.improving.productcontext.product.{ApiGetProductInfo, ProductService, ProductServiceClient}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.collection.Seq
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Random

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class UiGatewayApiActionImplSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(5000, Millis))

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

  val testGateWayAction: TestGatewayApiActionClient =
    TestGatewayApiActionClient(
      testGateWayClientSettings
    )

  val uiGateWayAction: UiGatewayApiActionClient =
    UiGatewayApiActionClient(
      testGateWayClientSettings
    )

  val productService: ProductServiceClient = ProductServiceClient(
    GrpcClientSettings.connectToServiceAt(
      config.getString(
        "app.improving.akka.grpc.product-client-url"
      ),
      config.getInt("app.improving.akka.grpc.client-url-port")
    )
  )

  val orderAction: OrderServiceClient = OrderServiceClient(
    GrpcClientSettings.connectToServiceAt(
      config.getString(
        "app.improving.akka.grpc.order-client-url"
      ),
      config.getInt("app.improving.akka.grpc.client-url-port")
    )
  )

  "UiGatewayApiActionImpl" must {

    "handle command HandlePurchaseTicket base case" in {
      val numTenants = 1
      val maxOrgsDepth = 2
      val maxOrgsWidth = 2
      val numMembersPerOrg = 1
      val numEventsPerOrg = 1
      val numTicketsPerEvent = 1

      val scenarioResult = testGateWayAction
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
      scenarioResult.orgsForTenants.isEmpty shouldBe false
      scenarioResult.membersForOrgs.isEmpty shouldBe false
      scenarioResult.eventsForOrgs.isEmpty shouldBe false
      scenarioResult.storeIds.isDefined shouldBe true
      scenarioResult.productsForStores.isEmpty shouldBe false

      log.info("in handlePurchaseTicket")

      val r = new Random()
      val stores = scenarioResult.productsForStores.keys
      val storeId = stores
        .toIndexedSeq(r.nextInt(stores.size))
      val products = scenarioResult.productsForStores(storeId).skus
      val product = Await.result(
        productService
          .getProductInfo(
            ApiGetProductInfo(
              products.toIndexedSeq(r.nextInt(products.size)).productId
            )
          ),
        10 seconds
      )
      val memberIds =
        scenarioResult.membersForOrgs.values.flatMap(_.memberIds)
      val memberId =
        memberIds.toIndexedSeq(r.nextInt(memberIds.size))
      val orderId = UUID.randomUUID().toString

      val result = uiGateWayAction
        .handlePurchaseTickets(
          PurchaseTicketsRequest(
            Map(
              memberId -> OrdersForStores(
                Map(
                  storeId -> ApiOrderInfo(
                    Seq[ApiLineItem](
                      ApiLineItem(
                        Some(ApiSku()),
                        1,
                        product.info.map(_.price).getOrElse(0.0)
                      )
                    ).toSeq,
                    r.nextString(15)
                  )
                )
              )
            )
          )
        )
        .futureValue

      result.orderIds.head.orderId shouldEqual orderId
    }

  }
}
