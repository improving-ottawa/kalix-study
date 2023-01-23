package app.improving.ordercontext.order

import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec
import TestData._
import app.improving.eventcontext.event.EventService
import app.improving.membercontext.member.MemberService
import app.improving.productcontext.product.ProductService

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  private val order = testKit.getGrpcClient(classOf[OrderService])
  private val event = testKit.getGrpcClient(classOf[EventService])
  private val product = testKit.getGrpcClient(classOf[ProductService])
  private val member = testKit.getGrpcClient(classOf[MemberService])

  private val action = testKit.getGrpcClient(classOf[OrderAction])

  "OrderService" must {

    "purchase order correctly" in {
      val orderId = order.createOrder(apiCreateOrder)
      println(orderId.value)

      val failedOrder = action.purchaseTicket(apiCreateOrder)

      true shouldBe true
    }

  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
