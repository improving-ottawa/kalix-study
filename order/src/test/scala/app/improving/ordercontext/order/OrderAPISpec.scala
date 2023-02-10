package app.improving.ordercontext.order

import TestData._
import app.improving.MemberId
import app.improving.ordercontext.infrastructure.util.{
  convertApiOrderInfoToOrderInfo,
  convertApiOrderStatusToOrderStatus
}
import app.improving.ordercontext.{
  OrderCanceled,
  OrderCreated,
  OrderInfoUpdated,
  OrderStatus,
  OrderStatusUpdated
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderAPISpec extends AnyWordSpec with Matchers {
  "The OrderAPI" should {

    "correctly process commands of type CreateOrder" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined
      orderCreated.info shouldBe defined
      orderCreated.meta shouldBe defined

      orderCreated.info.map(_.orderTotal).getOrElse(0.0) shouldBe testLineItems
        .map(item => item.lineTotal * item.quantity)
        .sum

      val nullCreateOrderResult = testKit.createOrder(apiCreateOrder)

      nullCreateOrderResult.events should have size 0
    }

    "correctly process commands of type UpdateOrderStatus" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val orderId = testKit.currentState.order
        .flatMap(_.orderId)
        .map(_.id)
        .getOrElse("OrderId is not found.")
      val updateOrderStatusResult =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus.copy(
            orderId = orderId
          )
        )

      updateOrderStatusResult.events should have size 1

      val orderStatusUpdated =
        updateOrderStatusResult.nextEvent[OrderStatusUpdated]

      orderStatusUpdated.updatingMember shouldBe Some(
        MemberId(testUpdatingMemberId)
      )

      orderStatusUpdated.newStatus shouldBe convertApiOrderStatusToOrderStatus(
        testNewOrderStatus
      )

      val nullupdateOrderStatusResult =
        testKit.updateOrderStatus(nullApiUpdateOrderStatus)

      nullupdateOrderStatusResult.events should have size 0
    }

    "correctly process commands of type UpdateOrderInfo" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val orderId = testKit.currentState.order
        .flatMap(_.orderId)
        .map(_.id)
        .getOrElse("OrderId is not found.")
      val updateOrderInfoResult =
        testKit.updateOrderInfo(apiUpdateOrderInfo.copy(orderId = orderId))

      updateOrderInfoResult.events should have size 1

      val orderInfoUpdated = updateOrderInfoResult.nextEvent[OrderInfoUpdated]

      orderInfoUpdated.orderId shouldBe defined
      orderInfoUpdated.info shouldBe defined
      orderInfoUpdated.meta shouldBe defined

      orderInfoUpdated.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testUpdatingMemberId)
      )

      orderInfoUpdated.info
        .map(_.orderTotal)
        .getOrElse(0.0) shouldBe testLineItemsUpdate
        .map(item => item.lineTotal * item.quantity)
        .sum

      orderInfoUpdated.info
        .map(_.specialInstructions)
        .getOrElse("") shouldBe testSpecialInstruction

      val nullUpdateOrderInfoResult =
        testKit.updateOrderInfo(nullApiUpdateOrderInfo)

      nullUpdateOrderInfoResult.events should have size 0

      val updateOrderStatusResultToPending =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus.copy(
            orderId = orderId
          )
        )

      updateOrderStatusResultToPending.events should have size 1

      val updateOrderStatusResultToInProcess =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus.copy(
            orderId = orderId,
            newStatus = testNewOrderStatusToInProcess
          )
        )

      updateOrderStatusResultToInProcess.events should have size 1

      val updateInProcessOrderInfoResult =
        testKit.updateOrderInfo(apiUpdateOrderInfo.copy(orderId = orderId))

      updateInProcessOrderInfoResult.events should have size 0
    }

    "correctly process commands of type CancelOrder" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val orderId = testKit.currentState.order
        .flatMap(_.orderId)
        .map(_.id)
        .getOrElse("OrderId is not found.")

      val cancelOrderResult =
        testKit.cancelOrder(apiCancelOrder.copy(orderId = orderId))

      cancelOrderResult.events should have size 1

      val orderCancelled = cancelOrderResult.nextEvent[OrderCanceled]

      orderCancelled.orderId shouldBe defined
      orderCancelled.info shouldBe defined
      orderCancelled.meta shouldBe defined

      orderCancelled.meta.map(_.status) shouldBe Some(OrderStatus.CANCELLED)
      orderCancelled.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testCancellingMemberId)
      )

      val nullCancelOrderResult = testKit.cancelOrder(nullApiCancelOrder)

      nullCancelOrderResult.events should have size 0
    }

    "correctly process commands of type GetOrderInfo" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderId = testKit.currentState.order
        .flatMap(_.orderId)
        .map(_.id)
        .getOrElse("OrderId is not found.")

      val getOrderInfoResult =
        testKit.getOrderInfo(apiGetOrderInfo.copy(orderId = orderId))

      val result = getOrderInfoResult.reply

      result.orderId shouldBe defined
      result.info shouldBe defined

      result.info shouldBe Some(
        testOrderInfo.copy(orderTotal =
          testLineItems
            .map(item => item.lineTotal * item.quantity)
            .sum
        )
      )
    }
  }
}
