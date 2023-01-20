package app.improving.ordercontext.order

import app.improving.{ApiMemberId, ApiProductId}

object TestData {

  val testOrderId = "test-order-id"
  val testProductId = "test-product-id"
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
  val testSpecialInstruction = "test-special-instruction"
  val testOrderTotal = 100.0
  val testLineItems = Seq[ApiLineItem](testLineItem1, testLineItem2)
  val testOrderInfo = ApiOrderInfo(
    testOrderId,
    testLineItems,
    testSpecialInstruction,
    testOrderTotal
  )
  val testCreatingMemberId = "test-creating-member-id"
  val apiCreateOrder = ApiCreateOrder(
    testOrderId,
    Some(testOrderInfo),
    Some(ApiMemberId(testCreatingMemberId))
  )
  val testNewOrderStatus = ApiOrderStatus.READY
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
}
