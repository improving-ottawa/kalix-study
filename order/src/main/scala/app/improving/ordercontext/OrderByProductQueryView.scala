package app.improving.ordercontext

import app.improving.ApiMemberId
import app.improving.ordercontext.infrastructure.util._
import app.improving.ordercontext.order.{ApiOrder, ApiOrderStatus}
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderByProductQueryView(context: ViewContext)
    extends AbstractOrderByProductQueryView {

  override def emptyState: ApiOrder = ApiOrder.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

  override def processOrderCreated(
      state: ApiOrder,
      orderCreated: OrderCreated
  ): UpdateEffect[ApiOrder] = {
    if (state != emptyState) {

      log.info(
        s"OrderByProductQueryView in processOrderCreated - state already existed"
      )

      effects.ignore()
    } else {

      log.info(
        s"OrderByProductQueryView in processOrderCreated - orderCreated $orderCreated"
      )

      effects.updateState(
        ApiOrder(
          orderCreated.orderId.map(_.id).getOrElse("OrderId is NOT FOUND."),
          orderCreated.info.map(convertOrderInfoToApiOrderInfo),
          orderCreated.meta.map(convertOrderMetaInfoToApiOrderMetaInfo),
          ApiOrderStatus.API_ORDER_STATUS_DRAFT
        )
      )
    }
  }

  override def processOrderStatusUpdated(
      state: ApiOrder,
      orderStatusUpdated: OrderStatusUpdated
  ): UpdateEffect[ApiOrder] = {

    log.info(
      s"OrderByProductQueryView in processOrderStatusUpdated - orderStatusUpdated $orderStatusUpdated"
    )

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

    log.info(
      s"OrderByProductQueryView in processOrderInfoUpdated - orderInfoUpdated $orderInfoUpdated"
    )

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

    log.info(
      s"OrderByProductQueryView in processOrderCanceled - orderCanceled $orderCanceled"
    )

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        status = ApiOrderStatus.API_ORDER_STATUS_CANCELLED,
        meta = state.meta.map(
          _.copy(
            lastModifiedBy = orderCanceled.cancellingMember.map(member =>
              ApiMemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp),
            status = ApiOrderStatus.API_ORDER_STATUS_CANCELLED
          )
        )
      )
    )
  }

  override def processOrderReleased(
      state: ApiOrder,
      orderReleased: OrderReleased
  ): UpdateEffect[ApiOrder] = effects.deleteState()

  // {
  //  log.info(
  //    s"OrderByProductQueryView in processOrderReleased - orderReleased $orderReleased"
  //  )
//
  //  val now = java.time.Instant.now()
  //  val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  //  effects.updateState(
  //    state.copy(
  //      status = ApiOrderStatus.API_ORDER_STATUS_RELEASED,
  //      meta = state.meta.map(
  //        _.copy(
  //          lastModifiedBy = orderReleased.releasingMember.map(member =>
  //            ApiMemberId(member.id)
  //          ),
  //          lastModifiedOn = Some(timestamp),
  //          status = ApiOrderStatus.API_ORDER_STATUS_RELEASED
  //        )
  //      )
  //    )
  //  )
  // }

  override def processOrderPending(
      state: ApiOrder,
      orderPending: OrderPending
  ): UpdateEffect[ApiOrder] = {
    log.info(
      s"OrderByProductQueryView in processOrderPending - orderPending $orderPending"
    )

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        status = ApiOrderStatus.API_ORDER_STATUS_PENDING,
        meta = state.meta.map(
          _.copy(
            lastModifiedBy =
              orderPending.pendingMember.map(member => ApiMemberId(member.id)),
            lastModifiedOn = Some(timestamp),
            status = ApiOrderStatus.API_ORDER_STATUS_PENDING
          )
        )
      )
    )
  }

  override def processOrderInProgressed(
      state: ApiOrder,
      orderInProgressed: OrderInProgressed
  ): UpdateEffect[ApiOrder] = {
    log.info(
      s"OrderByProductQueryView in processOrderInProgressed - orderInProgressed $orderInProgressed"
    )

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        status = ApiOrderStatus.API_ORDER_STATUS_INPROCESS,
        meta = state.meta.map(
          _.copy(
            lastModifiedBy = orderInProgressed.inProgressingMember.map(member =>
              ApiMemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp),
            status = ApiOrderStatus.API_ORDER_STATUS_INPROCESS
          )
        )
      )
    )
  }

  override def processOrderReadied(
      state: ApiOrder,
      orderReadied: OrderReadied
  ): UpdateEffect[ApiOrder] = {
    log.info(
      s"OrderByProductQueryView in processOrderReadied - orderReadied $orderReadied"
    )

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        status = ApiOrderStatus.API_ORDER_STATUS_READY,
        meta = state.meta.map(
          _.copy(
            lastModifiedBy =
              orderReadied.readyingMember.map(member => ApiMemberId(member.id)),
            lastModifiedOn = Some(timestamp),
            status = ApiOrderStatus.API_ORDER_STATUS_READY
          )
        )
      )
    )
  }

  override def processOrderDelivered(
      state: ApiOrder,
      orderDelivered: OrderDelivered
  ): UpdateEffect[ApiOrder] = {
    log.info(
      s"OrderByProductQueryView in processOrderDelivered - orderDelivered $orderDelivered"
    )

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    effects.updateState(
      state.copy(
        status = ApiOrderStatus.API_ORDER_STATUS_DELIVERED,
        meta = state.meta.map(
          _.copy(
            lastModifiedBy = orderDelivered.deliveringMember.map(member =>
              ApiMemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp),
            status = ApiOrderStatus.API_ORDER_STATUS_DELIVERED
          )
        )
      )
    )
  }
}
