package app.improving.ordercontext

import app.improving.ApiMemberId
import app.improving.ordercontext.order.{ApiOrder, ApiOrderStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import app.improving.ordercontext.infrastructure.util._
import com.google.protobuf.timestamp.Timestamp

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllOrdersViewImpl(context: ViewContext) extends AbstractAllOrdersView {

  override def emptyState: ApiOrder = ApiOrder.defaultInstance

  override def processOrderCreated(
      state: ApiOrder,
      orderCreated: OrderCreated
  ): UpdateEffect[ApiOrder] = {
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        ApiOrder(
          orderCreated.orderId.map(_.id).getOrElse("OrderId is NOT FOUND."),
          orderCreated.info.map(convertOrderInfoToApiOrderInfo),
          orderCreated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
          ApiOrderStatus.DRAFT
        )
      )
  }

  override def processOrderStatusUpdated(
      state: ApiOrder,
      orderStatusUpdated: OrderStatusUpdated
  ): UpdateEffect[ApiOrder] = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        status =
          convertOrderStatusToApiOrderStatus(orderStatusUpdated.newStatus),
        meta = state.meta.map(
          _.copy(
            lastModifiedBy = orderStatusUpdated.updatingMember.map(member =>
              ApiMemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp),
            status =
              convertOrderStatusToApiOrderStatus(orderStatusUpdated.newStatus)
          )
        )
      )
    )
  }

  override def processOrderInfoUpdated(
      state: ApiOrder,
      orderInfoUpdated: OrderInfoUpdated
  ): UpdateEffect[ApiOrder] = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        info = orderInfoUpdated.info.map(convertOrderInfoToApiOrderInfo),
        meta = state.meta.map(
          _.copy(
            lastModifiedBy = orderInfoUpdated.updatingMember.map(member =>
              ApiMemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp)
          )
        )
      )
    )
  }

  override def processOrderCanceled(
      state: ApiOrder,
      orderCanceled: OrderCanceled
  ): UpdateEffect[ApiOrder] = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        status = ApiOrderStatus.CANCELLED,
        meta = state.meta.map(
          _.copy(
            lastModifiedBy = orderCanceled.cancellingMember.map(member =>
              ApiMemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp),
            status = ApiOrderStatus.CANCELLED
          )
        )
      )
    )
  }
}
