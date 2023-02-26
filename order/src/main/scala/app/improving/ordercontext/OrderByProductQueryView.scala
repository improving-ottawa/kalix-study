package app.improving.ordercontext

import app.improving.ApiMemberId
import app.improving.ordercontext.infrastructure.util._
import app.improving.ordercontext.order.{ApiLineItem, ApiOrder, ApiOrderStatus}
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
        s"OrderByProductQueryView in processOrderCreated - orderCreated ${orderCreated}"
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
      s"OrderByProductQueryView in processOrderStatusUpdated - orderStatusUpdated ${orderStatusUpdated}"
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
      s"OrderByProductQueryView in processOrderInfoUpdated - orderInfoUpdated ${orderInfoUpdated}"
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
      s"OrderByProductQueryView in processOrderCanceled - orderCanceled ${orderCanceled}"
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

  override def processOrderClearLineItems(
      state: ApiOrder,
      orderClearLineItems: OrderClearLineItems
  ): UpdateEffect[ApiOrder] = {

    log.info(
      s"OrderByProductQueryView in processOrderClearLineItems - orderClearLineItems ${orderClearLineItems}"
    )

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

    effects.updateState(
      state.copy(
        meta = state.meta.map(
          _.copy(
            lastModifiedBy = orderClearLineItems.clearingMember.map(member =>
              ApiMemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp)
          )
        ),
        info = state.info.map(
          _.copy(
            lineItems = Seq.empty[ApiLineItem],
            specialInstructions = ""
          )
        )
      )
    )
  }
}
