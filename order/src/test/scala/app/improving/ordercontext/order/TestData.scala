package app.improving.ordercontext.order

import app.improving.{ApiMemberId, ApiProductId}

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
  val testNewOrderStatus = ApiOrderStatus.PENDING
  val testNewOrderStatusToInProcess = ApiOrderStatus.INPROCESS
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
  val testOrderTotalUpdate = 100.0
  val testLineItemsUpdate =
    Seq[ApiLineItem](testLineItem1Update, testLineItem2Update)
  val apiOrderInfoUpdate = ApiOrderInfoUpdate(
    testLineItemsUpdate,
    None
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
}
