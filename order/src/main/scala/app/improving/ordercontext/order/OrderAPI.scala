package app.improving.ordercontext.order

import app.improving._
import app.improving.ordercontext._
import app.improving.ordercontext.infrastructure.util._
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import io.grpc.Status
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderAPI(context: EventSourcedEntityContext) extends AbstractOrderAPI {
  override def emptyState: OrderState = OrderState.defaultInstance

  override def createOrder(
      currentState: OrderState,
      apiCreateOrder: ApiCreateOrder
  ): EventSourcedEntity.Effect[ApiOrderId] =
    currentState.order match {
      case Some(_) =>
        effects.error(
          s"OrderAPI createOrder Error: order with id ${apiCreateOrder.orderId} already existed"
        )
      case _ =>
        val orderId = apiCreateOrder.orderId
        val orderIdOpt = Some(OrderId(orderId))
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val memberIdOpt =
          apiCreateOrder.creatingMember.map(member => MemberId(member.memberId))
        val orderInfoOpt = apiCreateOrder.info
          .map(convertApiOrderInfoToOrderInfo)
          .map(calculateOrderTotal)
        val event = OrderCreated(
          orderIdOpt,
          orderInfoOpt,
          Some(
            OrderMetaInfo(
              orderIdOpt,
              memberIdOpt,
              apiCreateOrder.storeId.map(store => StoreId(store.storeId)),
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              OrderStatus.ORDER_STATUS_DRAFT
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => ApiOrderId(orderId))
    }

  override def updateOrderStatus(
      currentState: OrderState,
      apiUpdateOrderStatus: ApiUpdateOrderStatus
  ): EventSourcedEntity.Effect[Empty] =
    currentState.order match {
      case Some(order) =>
        val event = OrderStatusUpdated(
          order.orderId,
          convertApiOrderStatusToOrderStatus(apiUpdateOrderStatus.newStatus),
          apiUpdateOrderStatus.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }

  def isValidStateChange(
      currentState: OrderStatus,
      newApiStatus: ApiOrderStatus
  ): Boolean = {
    currentState match {
      case OrderStatus.ORDER_STATUS_DRAFT =>
        newApiStatus.isApiOrderStatusPending || newApiStatus.isApiOrderStatusPending
      case OrderStatus.ORDER_STATUS_PENDING =>
        newApiStatus.isApiOrderStatusCancelled || newApiStatus.isApiOrderStatusInprocess
      case OrderStatus.ORDER_STATUS_INPROCESS =>
        newApiStatus.isApiOrderStatusReady
      case OrderStatus.ORDER_STATUS_READY =>
        newApiStatus.isApiOrderStatusDelivered
      case _ => false
    }
  }

  override def updateOrderInfo(
      currentState: OrderState,
      apiUpdateOrderInfo: ApiUpdateOrderInfo
  ): EventSourcedEntity.Effect[Empty] =
    currentState.order match {
      case Some(order)
          if order.info.isDefined &&
            (order.status.isOrderStatusDraft || order.status.isOrderStatusPending) &&
            order.orderId.contains(OrderId(apiUpdateOrderInfo.orderId)) =>
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val orderInfoUpdateOpt = apiUpdateOrderInfo.update
          .map(convertApiUpdateOrderInfoToOrderInfoUpdate)
        val updatedInfo = order.info.map { orderInfo =>
          calculateOrderTotal(
            orderInfo.copy(
              lineItems = orderInfoUpdateOpt.fold(orderInfo.lineItems) {
                orderInfoUpdate =>
                  if (orderInfoUpdate.lineItems.isEmpty) orderInfo.lineItems
                  else orderInfoUpdate.lineItems
              },
              specialInstructions =
                orderInfoUpdateOpt.fold(orderInfo.specialInstructions) {
                  orderInfoUpdate =>
                    orderInfoUpdate.specialInstructions.getOrElse(
                      orderInfo.specialInstructions
                    )
                }
            )
          )
        }

        val updatingMemberIdOpt =
          apiUpdateOrderInfo.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        val event = OrderInfoUpdated(
          order.orderId,
          updatedInfo,
          order.meta.map(
            _.copy(
              lastModifiedBy = updatingMemberIdOpt,
              lastModifiedOn = Some(timestamp),
              status = currentState.order
                .map(_.status)
                .getOrElse(OrderStatus.ORDER_STATUS_UNKNOWN)
            )
          ),
          updatingMemberIdOpt
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }

  override def cancelOrder(
      currentState: OrderState,
      apiCancelOrder: ApiCancelOrder
  ): EventSourcedEntity.Effect[Empty] =
    currentState.order match {
      case Some(order) =>
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val cancellingMemberIdOpt =
          apiCancelOrder.cancellingMember.map(member =>
            MemberId(member.memberId)
          )
        val event = OrderCanceled(
          order.orderId,
          order.info,
          order.meta.map(
            _.copy(
              lastModifiedBy = cancellingMemberIdOpt,
              lastModifiedOn = Some(timestamp),
              status = OrderStatus.ORDER_STATUS_CANCELLED
            )
          ),
          cancellingMemberIdOpt
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }

  override def getOrderInfo(
      currentState: OrderState,
      apiGetOrderInfo: ApiGetOrderInfo
  ): EventSourcedEntity.Effect[ApiOrderInfoResult] =
    currentState.order match {
      case Some(order)
          if order.orderId.contains(OrderId(apiGetOrderInfo.orderId)) =>
        val result = ApiOrderInfoResult(
          Some(ApiOrderId(apiGetOrderInfo.orderId)),
          order.info.map(convertOrderInfoToApiOrderInfo)
        )
        effects.reply(result)
      case _ =>
        effects.error(
          s"OrderInfo ID ${apiGetOrderInfo.orderId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
    }
  override def orderCreated(
      currentState: OrderState,
      orderCreated: OrderCreated
  ): OrderState = {
    currentState.order match {
      case Some(_) => currentState
      case _ =>
        currentState.withOrder(
          Order(
            orderCreated.orderId,
            orderCreated.info,
            orderCreated.meta,
            OrderStatus.ORDER_STATUS_DRAFT
          )
        )
    }
  }

  override def orderStatusUpdated(
      currentState: OrderState,
      orderStatusUpdated: OrderStatusUpdated
  ): OrderState = {
    currentState.order match {
      case Some(order) =>
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val updatedMetaOpt = order.meta.map(
          _.copy(
            lastModifiedBy = orderStatusUpdated.updatingMember.map(member =>
              MemberId(member.id)
            ),
            lastModifiedOn = Some(timestamp),
            status = orderStatusUpdated.newStatus
          )
        )
        currentState.copy(order =
          currentState.order.map(
            _.copy(
              status = orderStatusUpdated.newStatus,
              meta = updatedMetaOpt
            )
          )
        )
      case _ => currentState
    }
  }

  override def orderInfoUpdated(
      currentState: OrderState,
      orderInfoUpdated: OrderInfoUpdated
  ): OrderState = {
    currentState.order match {
      case Some(_) =>
        currentState.copy(order =
          currentState.order.map(
            _.copy(
              info = orderInfoUpdated.info,
              meta = orderInfoUpdated.meta
            )
          )
        )
      case _ => currentState
    }
  }
  override def orderCanceled(
      currentState: OrderState,
      orderCanceled: OrderCanceled
  ): OrderState =
    currentState.order match {
      case Some(_) =>
        currentState.copy(order =
          currentState.order.map(
            _.copy(
              info = orderCanceled.info,
              meta = orderCanceled.meta,
              status = OrderStatus.ORDER_STATUS_CANCELLED
            )
          )
        )
      case _ => currentState
    }

  override def releaseOrder(
      currentState: OrderState,
      apiReleaseOrder: ApiReleaseOrder
  ): EventSourcedEntity.Effect[Empty] = effects
    .emitEvent(
      OrderReleased(
        apiReleaseOrder.orderId.map(apiId => OrderId(apiId.orderId)),
        apiReleaseOrder.releasingMember.map(apiId => MemberId(apiId.memberId))
      )
    )
    .deleteEntity()
    .thenReply(_ => Empty.defaultInstance)

  override def orderReleased(
      currentState: OrderState,
      orderReleased: OrderReleased
  ): OrderState = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

    currentState.copy(order =
      currentState.order.map(
        _.copy(meta =
          currentState.order.flatMap(
            _.meta.map(
              _.copy(
                lastModifiedBy = orderReleased.releasingMember,
                lastModifiedOn = Some(timestamp),
                status = OrderStatus.ORDER_STATUS_RELEASED
              )
            )
          )
        )
      )
    )
  }
}
