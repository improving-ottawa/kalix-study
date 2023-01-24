package app.improving.ordercontext.order

import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec
import TestData.{
  apiCreateOrderPrivateEvent,
  apiCreateOrderPrivateFailedEvent,
  _
}
import app.improving.eventcontext.event.EventService
import app.improving.eventcontext.event.TestData.{
  apiScheduleEvent,
  apiSchedulePrivateEvent,
  apiSchedulePrivateFailedEvent
}
import app.improving.organizationcontext.organization.OrganizationService
import app.improving.organizationcontext.organization.TestData.apiEstablishOrganization
import app.improving.productcontext.product.ProductService
import app.improving.productcontext.product.TestData.{
  apiCreateProduct,
  apiCreateProductPrivateEvent,
  apiCreateProductPrivateFailedEvent
}
import org.scalatest.exceptions.TestFailedException

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

  private val event = testKit.getGrpcClient(classOf[EventService])
  private val product = testKit.getGrpcClient(classOf[ProductService])
  private val organization = testKit.getGrpcClient(classOf[OrganizationService])

  private val action = testKit.getGrpcClient(classOf[OrderAction])

  "OrderService" must {

    "purchase public order correctly" in {
      val eventId = event.scheduleEvent(apiScheduleEvent).futureValue

      val productId = product.createProduct(apiCreateProduct).futureValue

      println(productId + " productId")

      val purchased = action.purchaseTicket(apiCreateOrder).futureValue

      println(purchased + " purchased")
      purchased.orderId shouldBe "test-order-id"
    }

    "purchase public order fails as expected" in {

      action.purchaseTicket(apiCreateOrder).futureValue.orderId shouldBe ""

    }

    "purchase private order correctly" in {
      val eventId = event.scheduleEvent(apiSchedulePrivateEvent).futureValue

      val productId =
        product.createProduct(apiCreateProductPrivateEvent).futureValue

      val organizationId =
        organization.establishOrganization(apiEstablishOrganization).futureValue

      val purchased =
        action.purchaseTicket(apiCreateOrderPrivateEvent).futureValue

      println(purchased + " purchased")
      purchased.orderId shouldBe "test-order-id2"
    }

    "purchase private order fails as expected" in {
      val eventId =
        event.scheduleEvent(apiSchedulePrivateFailedEvent).futureValue

      val productId =
        product.createProduct(apiCreateProductPrivateFailedEvent).futureValue

      intercept[TestFailedException](
        action.purchaseTicket(apiCreateOrderPrivateFailedEvent).futureValue
      )
    }
  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
