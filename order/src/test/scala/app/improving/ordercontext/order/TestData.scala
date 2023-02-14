package app.improving.ordercontext.order

import app.improving.ordercontext.{
  LineItem,
  OrderCanceled,
  OrderCreated,
  OrderInfo,
  OrderInfoUpdated,
  OrderMetaInfo,
  OrderStatus,
  OrderStatusUpdated
}
import app.improving.{
  ApiMemberId,
  ApiProductId,
  MemberId,
  OrderId,
  ProductId,
  StoreId
}
import com.google.protobuf.timestamp.Timestamp

object TestData {

  val testOrderId = "test-order-id"
  val testOrderId2 = "test-order-id2"
  val testOrderId3 = "test-order-id3"
  val testProductId = "test-product-id"
  val testProductId2 = "test-product-id2"
  val testProductId3 = "test-product-id3"
  val testQuantity = 10
  val testLineTotal = 20
  val testQuantity2 = 13
  val testLineTotal2 = 26
  val testLineItem1 = ApiLineItem(
    Some(ApiProductId(testProductId)),
    testQuantity,
    testLineTotal
  )
  val testLineItem2 = ApiLineItem(
    Some(ApiProductId(testProductId)),
    testQuantity2,
    testLineTotal2
  )
  val testLineItem3 = ApiLineItem(
    Some(ApiProductId(testProductId2)),
    testQuantity2,
    testLineTotal2
  )
  val testLineItem4 = ApiLineItem(
    Some(ApiProductId(testProductId3)),
    testQuantity2,
    testLineTotal2
  )
  val testSpecialInstruction = "test-special-instruction"
  val testOrderTotal = 100.0
  val testLineItems = Seq[ApiLineItem](testLineItem1, testLineItem2)
  val testLineItemsPrivateEvent = Seq[ApiLineItem](testLineItem3)
  val testLineItemsPrivateFailedEvent =
    Seq[ApiLineItem](testLineItem3, testLineItem4)
  val testOrderInfo = ApiOrderInfo(
    testOrderId,
    testLineItems,
    testSpecialInstruction,
    testOrderTotal
  )
  val testOrderInfoPrivateEvent = ApiOrderInfo(
    testOrderId,
    testLineItemsPrivateEvent,
    testSpecialInstruction,
    testOrderTotal
  )
  val testOrderInfoPrivateFailedEvent = ApiOrderInfo(
    testOrderId3,
    testLineItemsPrivateFailedEvent,
    testSpecialInstruction,
    testOrderTotal
  )
  val testCreatingMemberId = "test-member-id"
  val testCreatingMemberId1 = "test-member-id1"
  val apiCreateOrder = ApiCreateOrder(
    testOrderId,
    Some(testOrderInfo),
    Some(ApiMemberId(testCreatingMemberId))
  )
  val apiCreateOrderPrivateEvent = ApiCreateOrder(
    testOrderId2,
    Some(testOrderInfoPrivateEvent),
    Some(ApiMemberId(testCreatingMemberId))
  )
  val apiCreateOrderPrivateFailedEvent = ApiCreateOrder(
    testOrderId3,
    Some(testOrderInfoPrivateFailedEvent),
    Some(ApiMemberId(testCreatingMemberId1))
  )
  val testNewOrderStatus = ApiOrderStatus.API_ORDER_STATUS_READY
  val testUpdatingMemberId = "updating-member-id"
  val apiUpdateOrderStatus = ApiUpdateOrderStatus(
    testOrderId,
    testNewOrderStatus,
    Some(ApiMemberId(testUpdatingMemberId))
  )
  val nullApiUpdateOrderStatus = ApiUpdateOrderStatus(
    "other-id",
    testNewOrderStatus
  )
  val testProductIdUpdate = "test-product-id-update"
  val testQuantityUpdate = 19
  val testLineTotalUpdate = 26
  val testQuantity2Update = 12
  val testLineTotal2Update = 200
  val testLineItem1Update = ApiLineItem(
    Some(ApiProductId(testProductIdUpdate)),
    testQuantityUpdate,
    testLineTotalUpdate
  )
  val testLineItem2Update = ApiLineItem(
    Some(ApiProductId(testProductIdUpdate)),
    testQuantity2Update,
    testLineTotal2Update
  )
  val testSpecialInstructionUpdate = "test-special-instruction-update"
  val testOrderTotalUpdate = 100.0
  val testLineItemsUpdate =
    Seq[ApiLineItem](testLineItem1Update, testLineItem2Update)
  val apiOrderInfoUpdate = ApiOrderInfoUpdate(
    testOrderId,
    testLineItemsUpdate,
    testSpecialInstructionUpdate,
    testOrderTotalUpdate
  )
  val apiUpdateOrderInfo = ApiUpdateOrderInfo(
    testOrderId,
    Some(apiOrderInfoUpdate),
    Some(ApiMemberId(testUpdatingMemberId))
  )
  val nullApiUpdateOrderInfo = ApiUpdateOrderInfo(
    "other-id",
    Some(apiOrderInfoUpdate),
    Some(ApiMemberId(testUpdatingMemberId))
  )
  val testCancellingMemberId = "test-cancelling-member-id"
  val apiCancelOrder = ApiCancelOrder(
    testOrderId,
    Some(ApiMemberId(testCancellingMemberId))
  )
  val nullApiCancelOrder = ApiCancelOrder(
    "other-id",
    Some(ApiMemberId(testCancellingMemberId))
  )
  val requestingMemberId = "requesting-member-id"
  val apiGetOrderInfo = ApiGetOrderInfo(
    testOrderId,
    Some(ApiMemberId(requestingMemberId))
  )

  val testItem1 = LineItem(
    Some(ProductId(testProductId)),
    testQuantity,
    testLineTotal
  )
  val testItem2 = LineItem(
    Some(ProductId(testProductId)),
    testQuantity2,
    testLineTotal2
  )
  val testItems = Seq[LineItem](testItem1, testItem2)
  val testInfo = OrderInfo(
    Some(OrderId(testOrderId)),
    testItems,
    testSpecialInstruction,
    testOrderTotal
  )
  val now = java.time.Instant.now()
  val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val testStoreId = "test-store-id"
  val testMetaInfo = OrderMetaInfo(
    Some(OrderId(testOrderId)),
    Some(MemberId(testCreatingMemberId)),
    Some(StoreId(testStoreId)),
    Some(timestamp),
    Some(MemberId(testCreatingMemberId)),
    Some(timestamp),
    OrderStatus.ORDER_STATUS_DRAFT
  )
  val orderCreated = OrderCreated(
    Some(OrderId(testOrderId)),
    Some(testInfo),
    Some(testMetaInfo)
  )
  val orderStatusUpdated = OrderStatusUpdated(
    Some(OrderId(testOrderId)),
    OrderStatus.ORDER_STATUS_READY,
    Some(MemberId(testUpdatingMemberId))
  )
  val orderInfoUpdated = OrderInfoUpdated(
    Some(OrderId(testOrderId)),
    Some(testInfo),
    Some(testMetaInfo),
    Some(MemberId(testUpdatingMemberId))
  )
  val orderCancelled = OrderCanceled(
    Some(OrderId(testOrderId)),
    Some(testInfo),
    Some(testMetaInfo.copy(status = OrderStatus.ORDER_STATUS_CANCELLED)),
    Some(MemberId(testCancellingMemberId))
  )
}
