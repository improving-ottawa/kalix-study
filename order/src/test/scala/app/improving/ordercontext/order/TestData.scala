package app.improving.ordercontext.order

import app.improving.ordercontext.{LineItem, OrderCanceled, OrderCreated, OrderInfo, OrderInfoUpdated, OrderMetaInfo, OrderStatus, OrderStatusUpdated}
import app.improving.{ApiMemberId, ApiSku, MemberId, OrderId, Sku, StoreId}
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {

  val testOrderId = "test-order-id"
  val testOrderId2 = "test-order-id2"
  val testOrderId3 = "test-order-id3"
  val testProductId = "test-product-id"
  val testProductId2 = "test-product-id2"
  val testProductId3 = "test-product-id3"
  val testPricePerItem = 1.0
  val testQuantity = 10
  val testLineTotal = 20
  val testQuantity2 = 13
  val testLineTotal2 = 26
  val testOrderTotal = 100.0
  val testLineItem1: ApiLineItem = ApiLineItem(
    Some(ApiSku(testProductId)),
    testQuantity,
    testPricePerItem * testQuantity
  )
  val testLineItem2: ApiLineItem = ApiLineItem(
    Some(ApiSku(testProductId)),
    testQuantity2,
    testPricePerItem * testQuantity2
  )
  val testLineItem3: ApiLineItem = ApiLineItem(
    Some(ApiSku(testProductId2)),
    testQuantity2,
    testPricePerItem * testQuantity2
  )
  val testLineItem4: ApiLineItem = ApiLineItem(
    Some(ApiSku(testOrderId3)),
    testQuantity2,
    testPricePerItem * testQuantity2
  )
  val testSpecialInstruction = "test-special-instruction"
  val testLineItems: Seq[ApiLineItem] =
    Seq[ApiLineItem](testLineItem1, testLineItem2)
  val testLineItemsPrivateEvent: Seq[ApiLineItem] =
    Seq[ApiLineItem](testLineItem3)
  val testLineItemsPrivateFailedEvent: Seq[ApiLineItem] =
    Seq[ApiLineItem](testLineItem3, testLineItem4)
  val testOrderInfo: ApiOrderInfo = ApiOrderInfo(
    testLineItems,
    Some(testSpecialInstruction)
  )
  val testOrderInfoPrivateEvent: ApiOrderInfo = ApiOrderInfo(
    testLineItemsPrivateEvent,
    Some(testSpecialInstruction)
  )
  val testOrderInfoPrivateFailedEvent: ApiOrderInfo = ApiOrderInfo(
    testLineItemsPrivateFailedEvent,
    Some(testSpecialInstruction)
  )
  val testCreatingMemberId = "test-member-id"
  val testCreatingMemberId1 = "test-member-id1"
  val apiCreateOrder: ApiCreateOrder = ApiCreateOrder(
    testOrderId,
    Some(testOrderInfo),
    Some(ApiMemberId(testCreatingMemberId))
  )
  val apiCreateOrderPrivateEvent: ApiCreateOrder = ApiCreateOrder(
    testOrderId2,
    Some(testOrderInfoPrivateEvent),
    Some(ApiMemberId(testCreatingMemberId))
  )
  val apiCreateOrderPrivateFailedEvent: ApiCreateOrder = ApiCreateOrder(
    testOrderId3,
    Some(testOrderInfoPrivateFailedEvent),
    Some(ApiMemberId(testCreatingMemberId1))
  )
  val testNewOrderStatus: ApiOrderStatus.Recognized =
    ApiOrderStatus.API_ORDER_STATUS_PENDING
  val testNewOrderStatusToInProcess: ApiOrderStatus.Recognized =
    ApiOrderStatus.API_ORDER_STATUS_INPROCESS
  val testUpdatingMemberId = "updating-member-id"
  val apiUpdateOrderStatus: ApiUpdateOrderStatus = ApiUpdateOrderStatus(
    testOrderId,
    testNewOrderStatus,
    Some(ApiMemberId(testUpdatingMemberId))
  )
  val nullApiUpdateOrderStatus: ApiUpdateOrderStatus = ApiUpdateOrderStatus(
    "other-id",
    testNewOrderStatus
  )
  val testProductIdUpdate = "test-product-id-update"
  val testQuantityUpdate = 19
  val testLineTotalUpdate = 26
  val testQuantity2Update = 12
  val testLineTotal2Update = 200
  val testLineItem1Update: ApiLineItem = ApiLineItem(
    Some(ApiSku(testProductIdUpdate)),
    testQuantityUpdate,
    testLineTotalUpdate
  )
  val testLineItem2Update: ApiLineItem = ApiLineItem(
    Some(ApiSku(testProductIdUpdate)),
    testQuantity2Update,
    testLineTotal2Update
  )
  val testLineItemsUpdate: Seq[ApiLineItem] =
    Seq[ApiLineItem](testLineItem1Update, testLineItem2Update)
  val apiOrderInfoUpdate: ApiOrderInfoUpdate = ApiOrderInfoUpdate(
    testLineItemsUpdate,
    None
  )
  val apiUpdateOrderInfo: ApiUpdateOrderInfo = ApiUpdateOrderInfo(
    testOrderId,
    Some(apiOrderInfoUpdate),
    Some(ApiMemberId(testUpdatingMemberId))
  )
  val nullApiUpdateOrderInfo: ApiUpdateOrderInfo = ApiUpdateOrderInfo(
    "other-id",
    Some(apiOrderInfoUpdate),
    Some(ApiMemberId(testUpdatingMemberId))
  )
  val testCancellingMemberId = "test-cancelling-member-id"
  val apiCancelOrder: ApiCancelOrder = ApiCancelOrder(
    testOrderId,
    Some(ApiMemberId(testCancellingMemberId))
  )
  val nullApiCancelOrder: ApiCancelOrder = ApiCancelOrder(
    "other-id",
    Some(ApiMemberId(testCancellingMemberId))
  )
  val requestingMemberId = "requesting-member-id"
  val apiGetOrderInfo: ApiGetOrderInfo = ApiGetOrderInfo(
    testOrderId,
    Some(ApiMemberId(requestingMemberId))
  )

  val testItem1: LineItem = LineItem(
    Some(Sku(testProductId)),
    testQuantity,
    testLineTotal
  )
  val testItem2: LineItem = LineItem(
    Some(Sku(testProductId)),
    testQuantity2,
    testLineTotal2
  )
  val testItems: Seq[LineItem] = Seq[LineItem](testItem1, testItem2)
  val testInfo: OrderInfo = OrderInfo(
    testItems,
    Some(testSpecialInstruction),
    testOrderTotal
  )
  val now: Instant = java.time.Instant.now()
  val timestamp: Timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val testStoreId = "test-store-id"
  val testMetaInfo: OrderMetaInfo = OrderMetaInfo(
    Some(MemberId(testCreatingMemberId)),
    Some(StoreId(testStoreId)),
    Some(timestamp),
    Some(MemberId(testCreatingMemberId)),
    Some(timestamp),
    OrderStatus.ORDER_STATUS_DRAFT
  )
  val orderCreated: OrderCreated = OrderCreated(
    Some(OrderId(testOrderId)),
    Some(testInfo),
    Some(testMetaInfo)
  )
  val orderStatusUpdated: OrderStatusUpdated = OrderStatusUpdated(
    Some(OrderId(testOrderId)),
    OrderStatus.ORDER_STATUS_READY,
    Some(MemberId(testUpdatingMemberId))
  )
  val orderInfoUpdated: OrderInfoUpdated = OrderInfoUpdated(
    Some(OrderId(testOrderId)),
    Some(testInfo),
    Some(testMetaInfo),
    Some(MemberId(testUpdatingMemberId))
  )
  val orderCancelled: OrderCanceled = OrderCanceled(
    Some(OrderId(testOrderId)),
    Some(testInfo),
    Some(testMetaInfo.copy(status = OrderStatus.ORDER_STATUS_CANCELLED)),
    Some(MemberId(testCancellingMemberId))
  )
}
