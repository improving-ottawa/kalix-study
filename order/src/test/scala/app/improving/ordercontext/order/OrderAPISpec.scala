package app.improving.ordercontext.order

import TestData._
import app.improving.{ApiMemberId, MemberId}
import app.improving.ordercontext.infrastructure.util.{
  convertApiOrderStatusToOrderStatus,
  convertLineItemToApiLineItem
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

      orderCreated.info
        .map(_.lineItems)
        .map(lineItemSeq => lineItemSeq.map(convertLineItemToApiLineItem))
        .get shouldBe testLineItems

      val nullCreateOrderResult = testKit.createOrder(apiCreateOrder)

      nullCreateOrderResult.events should have size 0
    }

    "correctly process commands of type UpdateOrderStatus" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val updateOrderStatusResult =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus
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

      val updateOrderInfoResult =
        testKit.updateOrderInfo(
          apiUpdateOrderInfo
        )

      updateOrderInfoResult.events should have size 1

      val orderInfoUpdated = updateOrderInfoResult.nextEvent[OrderInfoUpdated]

      orderInfoUpdated.orderId shouldBe defined
      orderInfoUpdated.info shouldBe defined
      orderInfoUpdated.meta shouldBe defined

      orderInfoUpdated.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testUpdatingMemberId)
      )

      orderInfoUpdated.info
        .map(_.orderTotal) shouldBe testLineItemsUpdate.map(_.lineTotal)

      orderInfoUpdated.info
        .map(_.specialInstructions)
        .getOrElse("") shouldBe Some(testSpecialInstruction)

//      val nullUpdateOrderInfoResult =
//        testKit.updateOrderInfo(nullApiUpdateOrderInfo)
//
//      nullUpdateOrderInfoResult.events should have size 0

      val updateOrderStatusResultToPending =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus
        )

      updateOrderStatusResultToPending.events should have size 1

      val updateOrderStatusResultToInProcess =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus.copy(
            newStatus = testNewOrderStatusToInProcess
          )
        )

      updateOrderStatusResultToInProcess.events should have size 1

      val updateInProcessOrderInfoResult =
        testKit.updateOrderInfo(
          apiUpdateOrderInfo
        )

      updateInProcessOrderInfoResult.events should have size 0
    }

    "correctly process commands of type CancelOrder" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val cancelOrderResult =
        testKit.cancelOrder(
          apiCancelOrder
        )

      cancelOrderResult.events should have size 1

      val orderCancelled = cancelOrderResult.nextEvent[OrderCanceled]

      orderCancelled.orderId shouldBe defined
      orderCancelled.info shouldBe defined
      orderCancelled.meta shouldBe defined

      orderCancelled.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_CANCELLED
      )
      orderCancelled.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testCancellingMemberId)
      )

//      val nullCancelOrderResult = testKit.cancelOrder(nullApiCancelOrder)
//      nullCancelOrderResult.events should have size 0
    }

    "correctly process commands of type GetOrderInfo" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1
      val getOrderInfoResult =
        testKit.getOrderInfo(
          apiGetOrderInfo
        )

      val result = getOrderInfoResult.reply

      result.orderId.isEmpty shouldBe false
      result.info shouldBe defined

      result.info shouldBe Some(testOrderInfo)
    }

    "correctly cancel in process order" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val updateOrderInfoResult =
        testKit.updateOrderInfo(
          apiUpdateOrderInfo
        )

      updateOrderInfoResult.events should have size 1

      val orderInfoUpdated = updateOrderInfoResult.nextEvent[OrderInfoUpdated]

      orderInfoUpdated.orderId shouldBe defined
      orderInfoUpdated.info shouldBe defined
      orderInfoUpdated.meta shouldBe defined

      orderInfoUpdated.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testUpdatingMemberId)
      )

      orderInfoUpdated.info
        .map(_.orderTotal) shouldBe testLineItemsUpdate.map(_.lineTotal)

      orderInfoUpdated.info
        .map(_.specialInstructions)
        .getOrElse("") shouldBe Some(testSpecialInstruction)

      val updateOrderStatusResultToPending =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus
        )

      updateOrderStatusResultToPending.events should have size 1

      val updateOrderStatusResultToInProcess =
        testKit.updateOrderStatus(
          apiUpdateOrderStatus.copy(
            newStatus = testNewOrderStatusToInProcess
          )
        )

      updateOrderStatusResultToInProcess.events should have size 1

      val updateInProcessOrderInfoResult =
        testKit.updateOrderInfo(
          apiUpdateOrderInfo
        )

      updateInProcessOrderInfoResult.events should have size 0

      orderCreated.orderId shouldBe defined

      val cancelOrderResult =
        testKit.cancelOrder(
          apiCancelOrder
        )

      cancelOrderResult.events should have size 1

      val orderCancelled = cancelOrderResult.nextEvent[OrderCanceled]

      orderCancelled.orderId shouldBe defined
      orderCancelled.info shouldBe defined
      orderCancelled.meta shouldBe defined

      orderCancelled.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_CANCELLED
      )
      orderCancelled.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testCancellingMemberId)
      )
    }

    "correctly update statuses" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val apiOrderPending = ApiPendingOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val pendingResult = testKit.pendingOrder(apiOrderPending)

      pendingResult.events should have size 1

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_PENDING
      )

      val apiInProgressOrder = ApiInProgressOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val inProcessOrderResult = testKit.inProgressOrder(apiInProgressOrder)

      inProcessOrderResult.events should have size 1

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_INPROCESS
      )

      val apiReadyOrder = ApiReadyOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val apiReadyOrderResult = testKit.readyOrder(apiReadyOrder)

      apiReadyOrderResult.events should have size 1

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_READY
      )

      val apiDeliverOrder = ApiDeliverOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val apiDeliverOrderResult = testKit.deliverOrder(apiDeliverOrder)

      apiDeliverOrderResult.events should have size 1

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_DELIVERED
      )
    }

    "correctly not update statuses" in {
      val testKit = OrderAPITestKit(new OrderAPI(_))

      val createOrderResult = testKit.createOrder(apiCreateOrder)

      createOrderResult.events should have size 1

      val orderCreated = createOrderResult.nextEvent[OrderCreated]

      orderCreated.orderId shouldBe defined

      val apiDeliverOrder = ApiDeliverOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val apiDeliverOrderResult = testKit.deliverOrder(apiDeliverOrder)

      apiDeliverOrderResult.events should have size 0

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_DRAFT
      )

      val apiInProgressOrder = ApiInProgressOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val inProcessOrderResult = testKit.inProgressOrder(apiInProgressOrder)

      inProcessOrderResult.events should have size 0

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_DRAFT
      )

      val apiReadyOrder = ApiReadyOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val apiReadyOrderResult = testKit.readyOrder(apiReadyOrder)

      apiReadyOrderResult.events should have size 0

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_DRAFT
      )

      val apiOrderPending = ApiPendingOrder(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        Some(ApiMemberId(testUpdatingMemberId))
      )

      val pendingResult = testKit.pendingOrder(apiOrderPending)

      pendingResult.events should have size 1

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_PENDING
      )

      val apiReadyOrderResult1 = testKit.readyOrder(apiReadyOrder)

      apiReadyOrderResult1.events should have size 0

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_PENDING
      )

      val inProcessOrderResult1 = testKit.inProgressOrder(apiInProgressOrder)

      inProcessOrderResult1.events should have size 1

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_INPROCESS
      )

      val apiDeliverOrderResult1 = testKit.deliverOrder(apiDeliverOrder)

      apiDeliverOrderResult1.events should have size 0

      testKit.currentState.getOrder.meta.map(_.status) shouldBe Some(
        OrderStatus.ORDER_STATUS_INPROCESS
      )

    }
  }
}
