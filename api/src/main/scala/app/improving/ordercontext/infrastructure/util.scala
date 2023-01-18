package app.improving.ordercontext.infrastructure

import app.improving._
import app.improving.ordercontext._
import app.improving.ordercontext.order.{ApiLineItem, ApiOrder, ApiOrderInfo, ApiOrderInfoUpdate, ApiOrderMetaInfo, ApiOrderStatus}

object util {

  def convertApiOrderInfoToOrderInfo(apiOrderInfo: ApiOrderInfo): OrderInfo = {
    OrderInfo(
      Some(OrderId(apiOrderInfo.orderId)),
      apiOrderInfo.lineItems.map(convertApiLineItemToLineItem),
      apiOrderInfo.specialInstructions,
      apiOrderInfo.orderTotal
    )
  }

  def convertApiUpdateOrderInfoToOrderInfo(
      apiOrderInfoUpdate: ApiOrderInfoUpdate
  ): OrderInfo = {
    OrderInfo(
      Some(OrderId(apiOrderInfoUpdate.orderId)),
      apiOrderInfoUpdate.lineItems.map(convertApiLineItemToLineItem),
      apiOrderInfoUpdate.specialInstructions,
      apiOrderInfoUpdate.orderTotal
    )
  }

  def convertApiLineItemToLineItem(apiLineItem: ApiLineItem): LineItem = {
    LineItem(
      apiLineItem.product.map(product => ProductId(product.productId)),
      apiLineItem.quantity,
      apiLineItem.lineTotal
    )
  }

  def convertLineItemToApiLineItem(lineItem: LineItem): ApiLineItem = {
    ApiLineItem(
      lineItem.product.map(product => ApiProductId(product.id)),
      lineItem.quantity,
      lineItem.lineTotal
    )
  }

  def calculateOrderTotal(orderInfo: OrderInfo): OrderInfo = {
    orderInfo.copy(
      orderTotal =
        orderInfo.lineItems.map(item => item.quantity * item.lineTotal).sum
    )
  }

  def convertApiOrderStatusToOrderStatus(
      apiOrderStatus: ApiOrderStatus
  ): OrderStatus = {
    apiOrderStatus match {
      case ApiOrderStatus.DRAFT     => OrderStatus.DRAFT
      case ApiOrderStatus.PENDING   => OrderStatus.PENDING
      case ApiOrderStatus.INPROCESS => OrderStatus.INPROCESS
      case ApiOrderStatus.READY     => OrderStatus.READY
      case ApiOrderStatus.DELIVERED => OrderStatus.DELIVERED
      case ApiOrderStatus.CANCELLED => OrderStatus.CANCELLED
      case ApiOrderStatus.UNKNOWN   => OrderStatus.UNKNOWN
      case ApiOrderStatus.Unrecognized(unrecognizedValue) =>
        OrderStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertOrderStatusToApiOrderStatus(
      orderStatus: OrderStatus
  ): ApiOrderStatus = {
    orderStatus match {
      case OrderStatus.DRAFT     => ApiOrderStatus.DRAFT
      case OrderStatus.PENDING   => ApiOrderStatus.PENDING
      case OrderStatus.INPROCESS => ApiOrderStatus.INPROCESS
      case OrderStatus.READY     => ApiOrderStatus.READY
      case OrderStatus.DELIVERED => ApiOrderStatus.DELIVERED
      case OrderStatus.CANCELLED => ApiOrderStatus.CANCELLED
      case OrderStatus.UNKNOWN   => ApiOrderStatus.UNKNOWN
      case OrderStatus.Unrecognized(unrecognizedValue) =>
        ApiOrderStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertOrderInfoToApiOrderInfo(orderInfo: OrderInfo): ApiOrderInfo = {
    ApiOrderInfo(
      orderInfo.orderId.map(_.id).getOrElse("OrderId is not set."),
      orderInfo.lineItems.map(convertLineItemToApiLineItem),
      orderInfo.specialInstructions,
      orderInfo.orderTotal
    )
  }

  def convertOrderMetaInfoToApiOrderMetaInfo(
      orderMetaInfo: OrderMetaInfo
  ): ApiOrderMetaInfo = {
    ApiOrderMetaInfo(
      orderMetaInfo.orderId.map(_.id).getOrElse("OrderId is not found."),
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
      orderCreated.orderId.map(_.id).getOrElse("OrderId is not found."),
      orderCreated.info.map(convertOrderInfoToApiOrderInfo),
      orderCreated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
      ApiOrderStatus.DRAFT
    )
  }

  def convertOrderInfoUpdatedToApiOrder(
      orderInfoUpdated: OrderInfoUpdated
  ): ApiOrder = {
    ApiOrder(
      orderInfoUpdated.orderId.map(_.id).getOrElse("OrderId is not found."),
      orderInfoUpdated.info.map(convertOrderInfoToApiOrderInfo),
      orderInfoUpdated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
      orderInfoUpdated.meta
        .map(meta => convertOrderStatusToApiOrderStatus(meta.status))
        .getOrElse(ApiOrderStatus.UNKNOWN)
    )
  }

  def convertOrderCancelledToApiOrder(
      orderCanceled: OrderCanceled
  ): ApiOrder = {
    ApiOrder(
      orderCanceled.orderId.map(_.id).getOrElse("OrderId is not found."),
      orderCanceled.info.map(convertOrderInfoToApiOrderInfo),
      orderCanceled.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
      ApiOrderStatus.CANCELLED
    )
  }
}
