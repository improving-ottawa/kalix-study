package app.improving.ordercontext.order

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import TestData._
import app.improving.ApiMemberId

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderEventsServiceActionSpec extends AnyWordSpec with Matchers {

  "OrderEventsServiceAction" must {

    "handle command TransformOrderCreated" in {
      val service =
        OrderEventsServiceActionTestKit(new OrderEventsServiceAction(_))

      val result = service.transformOrderCreated(orderCreated)

      result.reply.orderId.isEmpty shouldBe false
      result.reply.info shouldBe Some(testOrderInfo)
      result.reply.meta.map(_.status) shouldBe Some(
        ApiOrderStatus.API_ORDER_STATUS_DRAFT
      )
    }

    "handle command TransformOrderStatusUpdated" in {
      val service =
        OrderEventsServiceActionTestKit(new OrderEventsServiceAction(_))

      val result = service.transformOrderStatusUpdated(orderStatusUpdated)

      result.reply.orderId.isEmpty shouldBe false
      result.reply.newStatus shouldBe ApiOrderStatus.API_ORDER_STATUS_READY
      result.reply.updatingMember shouldBe Some(
        ApiMemberId(testUpdatingMemberId)
      )
    }

    "handle command TransformOrderInfoUpdated" in {
      val service =
        OrderEventsServiceActionTestKit(new OrderEventsServiceAction(_))
      pending
      val result = service.transformOrderInfoUpdated(orderInfoUpdated)

      result.reply.orderId.isEmpty shouldBe false
      result.reply.info shouldBe Some(testOrderInfo)
      result.reply.meta.map(_.status) shouldBe Some(
        ApiOrderStatus.API_ORDER_STATUS_DRAFT
      )
    }

    "handle command TransformOrderCanceled" in {
      val service =
        OrderEventsServiceActionTestKit(new OrderEventsServiceAction(_))

      val result = service.transformOrderCanceled(orderCancelled)

      result.reply.orderId.isEmpty shouldBe false
      result.reply.meta.map(
        _.status
      ) shouldBe Some(ApiOrderStatus.API_ORDER_STATUS_CANCELLED)
      result.reply.cancellingMember shouldBe Some(
        ApiMemberId(testCancellingMemberId)
      )
    }
  }
}
