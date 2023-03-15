package app.improving.gateway

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import app.improving.{ApiMemberId, ApiOrderIds, ApiSku}
import app.improving.gateway.TestData.Fixture
import app.improving.ordercontext.order.{ApiLineItem, ApiOrderInfo}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import scala.collection.parallel.CollectionConverters._
import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

class ParallelTestSpec
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
    GrpcClientSettings
      .connectToServiceAt(
        "localhost",
        9000
      )
      .withTls(false)

  val client: TestGatewayApiActionClient = TestGatewayApiActionClient(
    gatewayClientSettings
  )

  val gatewayActionClient: UiGatewayApiActionClient = UiGatewayApiActionClient(
    gatewayClientSettings
  )

  val creationGateWayAction: CreationGatewayApiAction =
    CreationGatewayApiActionClient(
      gatewayClientSettings
    )

  "GatewayApiActionImpl" should {
    "run parallel requests repetitively" in {
      val info = ScenarioInfo(
        1, 2, 2, 1, 1, 1
      )

      val results =
        client.handleStartScenario(StartScenario(Some(info)))

      println(results.futureValue)
      assert(results.isCompleted)

      val scenarioResult = results.futureValue
      (1 to 100).map { _ =>
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

        var memberIdsToUse = memberIds
          .take(r.nextInt(memberIds.size))
          .map { member => (member, products(r.between(1, products.size))) }
          .par

        def getResponse(
            member: ApiMemberId,
            product: ApiSku
        ): ApiOrderIds = {
          gatewayActionClient
            .handlePurchaseTickets(
              PurchaseTicketsRequest(
                Map(
                  member.memberId -> OrdersForStores(
                    Map(
                      storeId ->
                        ApiOrderInfo(
                          Seq[ApiLineItem](
                            ApiLineItem(Some(product), 1)
                          )
                        )
                    )
                  )
                )
              )
            )
            .futureValue
        }

        while (memberIdsToUse.length < 10) {
          memberIdsToUse = memberIdsToUse ++ memberIdsToUse
        }

        memberIdsToUse.take(10)

        memberIdsToUse.foreach { case (member, order) =>
          (1 to 10).map { _ =>
            val result = getResponse(member, order)
            assert(result.orderIds.nonEmpty)
            println(result.orderIds)
            Thread.sleep(3000)
          }
        }

      }
    }
  }
}
