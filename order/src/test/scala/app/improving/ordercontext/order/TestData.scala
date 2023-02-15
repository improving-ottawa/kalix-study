package app.improving.ordercontext.order

import app.improving.{ApiMemberId, ApiOrderId, ApiProductId}

object TestData {

  val testOrderId: Option[ApiOrderId] = Some(ApiOrderId("test-order-id"))
  val testOrderId2: Option[ApiOrderId] = Some(ApiOrderId("test-order-id2"))
  val testOrderId3: Option[ApiOrderId] = Some(ApiOrderId("test-order-id3"))
  val testProductId: Option[ApiProductId] = Some(
    ApiProductId("test-product-id")
  )
  val testProductId2: Option[ApiProductId] = Some(
    ApiProductId("test-product-id2")
  )
  val testProductId3: Option[ApiProductId] = Some(
    ApiProductId("test-product-id3")
  )
  val testQuantity = 10
  val testLineTotal = 20
  val testQuantity2 = 13
  val testLineTotal2 = 26
  val testLineItem1: ApiLineItem = ApiLineItem(
    testProductId.map(id => ApiProductId(id.productId)),
    testQuantity,
    testLineTotal
  )
  val testLineItem2: ApiLineItem = ApiLineItem(
    testProductId.map(id => ApiProductId(id.productId)),
    testQuantity2,
    testLineTotal2
  )
  val testLineItem3: ApiLineItem = ApiLineItem(
    testProductId2.map(id => ApiProductId(id.productId)),
    testQuantity2,
    testLineTotal2
  )
  val testLineItem4: ApiLineItem = ApiLineItem(
    testProductId3.map(id => ApiProductId(id.productId)),
    testQuantity2,
    testLineTotal2
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
    testSpecialInstruction
  )
  val testOrderInfoPrivateEvent: ApiOrderInfo = ApiOrderInfo(
    testLineItemsPrivateEvent,
    testSpecialInstruction
  )
  val testOrderInfoPrivateFailedEvent: ApiOrderInfo = ApiOrderInfo(
    testLineItemsPrivateFailedEvent,
    testSpecialInstruction
  )
  val testCreatingMemberId = "test-member-id"
  val testCreatingMemberId1 = "test-member-id1"
  val apiCreateOrder: ApiCreateOrder = ApiCreateOrder(
    testOrderId,
    Some(testOrderInfo),
    Some(ApiMemberId(testCreatingMemberId))
  )
  val apiCreateOrderPrivateEvent: ApiCreateOrder = ApiCreateOrder(
    testOrderId2.map(id => ApiOrderId(id.orderId)),
    Some(testOrderInfoPrivateEvent),
    Some(ApiMemberId(testCreatingMemberId))
  )
  val apiCreateOrderPrivateFailedEvent: ApiCreateOrder = ApiCreateOrder(
    testOrderId3.map(id => ApiOrderId(id.orderId)),
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
    Some(ApiOrderId("other-id")),
    testNewOrderStatus
  )
  val testProductIdUpdate = "test-product-id-update"
  val testQuantityUpdate = 19
  val testLineTotalUpdate = 26
  val testQuantity2Update = 12
  val testLineTotal2Update = 200
  val testLineItem1Update: ApiLineItem = ApiLineItem(
    Some(ApiProductId(testProductIdUpdate)),
    testQuantityUpdate,
    testLineTotalUpdate
  )
  val testLineItem2Update: ApiLineItem = ApiLineItem(
    Some(ApiProductId(testProductIdUpdate)),
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
    Some(ApiOrderId("other-id")),
    Some(apiOrderInfoUpdate),
    Some(ApiMemberId(testUpdatingMemberId))
  )
  val testCancellingMemberId = "test-cancelling-member-id"
  val apiCancelOrder: ApiCancelOrder = ApiCancelOrder(
    testOrderId,
    Some(ApiMemberId(testCancellingMemberId))
  )
  val nullApiCancelOrder: ApiCancelOrder = ApiCancelOrder(
    Some(ApiOrderId("other-id")),
    Some(ApiMemberId(testCancellingMemberId))
  )
  val requestingMemberId = "requesting-member-id"
  val apiGetOrderInfo: ApiGetOrderInfo = ApiGetOrderInfo(
    testOrderId,
    Some(ApiMemberId(requestingMemberId))
  )
}
