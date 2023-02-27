package app.improving.ordercontext.infrastructure

import app.improving._
import app.improving.ordercontext._
import app.improving.ordercontext.order._

object util {

  def convertApiOrderInfoToOrderInfo(apiOrderInfo: ApiOrderInfo): OrderInfo = {
    OrderInfo(
      apiOrderInfo.lineItems.map(convertApiLineItemToLineItem),
      apiOrderInfo.specialInstructions
    )
  }

  def convertApiUpdateOrderInfoToOrderInfoUpdate(
      apiOrderInfoUpdate: ApiOrderInfoUpdate
  ): OrderInfoUpdate = {
    OrderInfoUpdate(
      apiOrderInfoUpdate.lineItems.map(convertApiLineItemToLineItem),
      apiOrderInfoUpdate.specialInstructions
    )
  }

  def convertApiLineItemToLineItem(apiLineItem: ApiLineItem): LineItem = {
    LineItem(
      apiLineItem.product.map(product => Sku(product.sku)),
      apiLineItem.pricePerItem,
      apiLineItem.quantity,
      apiLineItem.lineTotal
    )
  }

  def convertLineItemToApiLineItem(lineItem: LineItem): ApiLineItem = {
    ApiLineItem(
      lineItem.product.map(product => ApiSku(product.id)),
      lineItem.pricePerItem,
      lineItem.quantity,
      lineItem.lineTotal
    )
  }

  def calculateLineItemsTotal(orderInfo: OrderInfo): OrderInfo = {
    val lineItems = orderInfo.lineItems.map(lineItem =>
      lineItem.copy(
        lineTotal = lineItem.pricePerItem * lineItem.quantity
      )
    )
    orderInfo.copy(
      lineItems = lineItems
    )
  }
  def calculateOrderTotal(orderInfo: OrderInfo): OrderInfo = {
    orderInfo.copy(
      orderTotal = orderInfo.lineItems.map(item => item.lineTotal).sum
    )
  }

  def convertApiOrderStatusToOrderStatus(
      apiOrderStatus: ApiOrderStatus
  ): OrderStatus = {
    apiOrderStatus match {
      case ApiOrderStatus.API_ORDER_STATUS_DRAFT =>
        OrderStatus.ORDER_STATUS_DRAFT
      case ApiOrderStatus.API_ORDER_STATUS_PENDING =>
        OrderStatus.ORDER_STATUS_PENDING
      case ApiOrderStatus.API_ORDER_STATUS_INPROCESS =>
        OrderStatus.ORDER_STATUS_INPROCESS
      case ApiOrderStatus.API_ORDER_STATUS_READY =>
        OrderStatus.ORDER_STATUS_READY
      case ApiOrderStatus.API_ORDER_STATUS_DELIVERED =>
        OrderStatus.ORDER_STATUS_DELIVERED
      case ApiOrderStatus.API_ORDER_STATUS_CANCELLED =>
        OrderStatus.ORDER_STATUS_CANCELLED
      case ApiOrderStatus.API_ORDER_STATUS_UNKNOWN =>
        OrderStatus.ORDER_STATUS_UNKNOWN
      case ApiOrderStatus.Unrecognized(unrecognizedValue) =>
        OrderStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertOrderStatusToApiOrderStatus(
      orderStatus: OrderStatus
  ): ApiOrderStatus = {
    orderStatus match {
      case OrderStatus.ORDER_STATUS_DRAFT =>
        ApiOrderStatus.API_ORDER_STATUS_DRAFT
      case OrderStatus.ORDER_STATUS_PENDING =>
        ApiOrderStatus.API_ORDER_STATUS_PENDING
      case OrderStatus.ORDER_STATUS_INPROCESS =>
        ApiOrderStatus.API_ORDER_STATUS_INPROCESS
      case OrderStatus.ORDER_STATUS_READY =>
        ApiOrderStatus.API_ORDER_STATUS_READY
      case OrderStatus.ORDER_STATUS_DELIVERED =>
        ApiOrderStatus.API_ORDER_STATUS_DELIVERED
      case OrderStatus.ORDER_STATUS_CANCELLED =>
        ApiOrderStatus.API_ORDER_STATUS_CANCELLED
      case OrderStatus.ORDER_STATUS_RELEASED =>
        ApiOrderStatus.Unrecognized(0)
      case OrderStatus.ORDER_STATUS_UNKNOWN =>
        ApiOrderStatus.API_ORDER_STATUS_UNKNOWN
      case OrderStatus.Unrecognized(unrecognizedValue) =>
        ApiOrderStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertOrderInfoToApiOrderInfo(orderInfo: OrderInfo): ApiOrderInfo = {
    ApiOrderInfo(
      orderInfo.lineItems.map(convertLineItemToApiLineItem),
      orderInfo.specialInstructions
    )
  }

  def convertOrderMetaInfoToApiOrderMetaInfo(
      orderMetaInfo: OrderMetaInfo
  ): ApiOrderMetaInfo = {
    ApiOrderMetaInfo(
      orderMetaInfo.memberId.map(member => ApiMemberId(member.id)),
      orderMetaInfo.storeId.map(store => ApiStoreId(store.id)),
      orderMetaInfo.createdOn,
      orderMetaInfo.lastModifiedBy.map(member => ApiMemberId(member.id)),
      orderMetaInfo.lastModifiedOn,
      convertOrderStatusToApiOrderStatus(orderMetaInfo.status)
    )
  }

  def convertOrderCreatedToApiOrder(orderCreated: OrderCreated): ApiOrder = {
    ApiOrder(
      orderCreated.orderId
        .getOrElse(OrderId.defaultInstance)
        .id,
      orderCreated.info.map(convertOrderInfoToApiOrderInfo),
      orderCreated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
      ApiOrderStatus.API_ORDER_STATUS_DRAFT
    )
  }

  def convertOrderInfoUpdatedToApiOrder(
      orderInfoUpdated: OrderInfoUpdated
  ): ApiOrder = {
    ApiOrder(
      orderInfoUpdated.orderId.getOrElse(OrderId.defaultInstance).id,
      orderInfoUpdated.info.map(convertOrderInfoToApiOrderInfo),
      orderInfoUpdated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
      orderInfoUpdated.meta
        .map(meta => convertOrderStatusToApiOrderStatus(meta.status))
        .getOrElse(ApiOrderStatus.API_ORDER_STATUS_UNKNOWN)
    )
  }

  def convertOrderCancelledToApiOrder(
      orderCanceled: OrderCanceled
  ): ApiOrder = {
    ApiOrder(
      orderCanceled.orderId.getOrElse(OrderId.defaultInstance).id,
      orderCanceled.info.map(convertOrderInfoToApiOrderInfo),
      orderCanceled.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
      ApiOrderStatus.API_ORDER_STATUS_CANCELLED
    )
  }
}
