package app.improving.ordercontext.order

import app.improving.{ApiMemberId, ApiOrderId}
import app.improving.ordercontext.OrderCanceled
import app.improving.ordercontext.OrderCreated
import app.improving.ordercontext.OrderInfoUpdated
import app.improving.ordercontext.OrderStatusUpdated
import app.improving.ordercontext.infrastructure.util._
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderEventsServiceAction(creationContext: ActionCreationContext)
    extends AbstractOrderEventsServiceAction {

  override def transformOrderCreated(
      orderCreated: OrderCreated
  ): Action.Effect[ApiOrderCreated] = {
    effects.reply(
      ApiOrderCreated(
        orderCreated.orderId.map(order => ApiOrderId(order.id)),
        orderCreated.info.map(convertOrderInfoToApiOrderInfo),
        orderCreated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo)
      )
    )
  }
  override def transformOrderStatusUpdated(
      orderStatusUpdated: OrderStatusUpdated
  ): Action.Effect[ApiOrderStatusUpdated] = {
    effects.reply(
      ApiOrderStatusUpdated(
        orderStatusUpdated.orderId.map(order => ApiOrderId(order.id)),
        convertOrderStatusToApiOrderStatus(orderStatusUpdated.newStatus),
        orderStatusUpdated.updatingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
  override def transformOrderInfoUpdated(
      orderInfoUpdated: OrderInfoUpdated
  ): Action.Effect[ApiOrderInfoUpdated] = {
    effects.reply(
      ApiOrderInfoUpdated(
        orderInfoUpdated.orderId.map(order => ApiOrderId(order.id)),
        orderInfoUpdated.info.map(convertOrderInfoToApiOrderInfo),
        orderInfoUpdated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
        orderInfoUpdated.updatingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
  override def transformOrderCanceled(
      orderCanceled: OrderCanceled
  ): Action.Effect[ApiOrderCanceled] = {
    effects.reply(
      ApiOrderCanceled(
        orderCanceled.orderId.map(order => ApiOrderId(order.id)),
        orderCanceled.info.map(convertOrderInfoToApiOrderInfo),
        orderCanceled.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
        orderCanceled.cancellingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
}
