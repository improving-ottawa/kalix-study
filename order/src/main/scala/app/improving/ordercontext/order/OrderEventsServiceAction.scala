package app.improving.ordercontext.order

import app.improving.ApiMemberId
import app.improving.ordercontext.{
  OrderCanceled,
  OrderCreated,
  OrderDelivered,
  OrderInProgressed,
  OrderInfoUpdated,
  OrderPending,
  OrderReadied,
  OrderReleased,
  OrderStatusUpdated
}
import app.improving.ordercontext.infrastructure.util._
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderEventsServiceAction(creationContext: ActionCreationContext)
    extends AbstractOrderEventsServiceAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def transformOrderCreated(
      orderCreated: OrderCreated
  ): Action.Effect[ApiOrderCreated] = {

    log.info(
      s"OrderEventsServiceAction in transformOrderCreated - orderCreated - ${orderCreated}"
    )

    effects.reply(
      ApiOrderCreated(
        orderCreated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderCreated.info.map(convertOrderInfoToApiOrderInfo),
        orderCreated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo)
      )
    )
  }

  override def transformOrderStatusUpdated(
      orderStatusUpdated: OrderStatusUpdated
  ): Action.Effect[ApiOrderStatusUpdated] = {

    log.info(
      s"OrderEventsServiceAction in transformOrderStatusUpdated - orderStatusUpdated - ${orderStatusUpdated}"
    )

    effects.reply(
      ApiOrderStatusUpdated(
        orderStatusUpdated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        convertOrderStatusToApiOrderStatus(orderStatusUpdated.newStatus),
        orderStatusUpdated.updatingMember.map(member => ApiMemberId(member.id))
      )
    )
  }

  override def transformOrderInfoUpdated(
      orderInfoUpdated: OrderInfoUpdated
  ): Action.Effect[ApiOrderInfoUpdated] = {

    log.info(
      s"OrderEventsServiceAction in transformOrderInfoUpdated - orderInfoUpdated - ${orderInfoUpdated}"
    )

    effects.reply(
      ApiOrderInfoUpdated(
        orderInfoUpdated.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderInfoUpdated.info.map(convertOrderInfoToApiOrderInfo),
        orderInfoUpdated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
        orderInfoUpdated.updatingMember.map(member => ApiMemberId(member.id))
      )
    )
  }

  override def transformOrderCanceled(
      orderCanceled: OrderCanceled
  ): Action.Effect[ApiOrderCanceled] = {

    log.info(
      s"OrderEventsServiceAction in transformOrderCanceled - orderCanceled - ${orderCanceled}"
    )

    effects.reply(
      ApiOrderCanceled(
        orderCanceled.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderCanceled.info.map(convertOrderInfoToApiOrderInfo),
        orderCanceled.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
        orderCanceled.cancellingMember.map(member => ApiMemberId(member.id))
      )
    )
  }

  override def transformOrderReleased(
      orderReleased: OrderReleased
  ): Action.Effect[ApiOrderReleased] = {
    log.info(
      s"OrderEventsServiceAction in transformOrderReleased - orderReleased - ${orderReleased}"
    )

    effects.reply(
      ApiOrderReleased(
        orderReleased.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderReleased.releasingMember.map(member => ApiMemberId(member.id))
      )
    )
  }

  override def transformOrderPending(
      orderPending: OrderPending
  ): Action.Effect[ApiOrderPending] = {
    log.info(
      s"OrderEventsServiceAction in transformOrderPending - orderPending - ${orderPending}"
    )

    effects.reply(
      ApiOrderPending(
        orderPending.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderPending.pendingMember.map(member => ApiMemberId(member.id))
      )
    )
  }

  override def transformOrderInProgressed(
      orderInProgressed: OrderInProgressed
  ): Action.Effect[ApiOrderInProgressed] = {
    log.info(
      s"OrderEventsServiceAction in transformOrderInProgressed - orderInProgressed - ${orderInProgressed}"
    )

    effects.reply(
      ApiOrderInProgressed(
        orderInProgressed.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderInProgressed.inProgressingMember.map(member =>
          ApiMemberId(member.id)
        )
      )
    )
  }

  override def transformOrderReadied(
      orderReadied: OrderReadied
  ): Action.Effect[ApiOrderReadied] = {
    log.info(
      s"OrderEventsServiceAction in transformOrderReadied - orderReadied - ${orderReadied}"
    )

    effects.reply(
      ApiOrderReadied(
        orderReadied.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderReadied.readyingMember.map(member => ApiMemberId(member.id))
      )
    )
  }

  override def transformOrderDelivered(
      orderDelivered: OrderDelivered
  ): Action.Effect[ApiOrderDelivered] = {
    log.info(
      s"OrderEventsServiceAction in transformOrderDelivered - orderDelivered - ${orderDelivered}"
    )

    effects.reply(
      ApiOrderDelivered(
        orderDelivered.orderId.map(_.id).getOrElse("orderId is NOT FOUND."),
        orderDelivered.deliveringMember.map(member => ApiMemberId(member.id))
      )
    )
  }
}
