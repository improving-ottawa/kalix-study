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
  ): EventSourcedEntity.Effect[ApiOrderId] = {
    currentState.order match {
      case Some(_) => effects.reply(ApiOrderId.defaultInstance)
      case _ => {
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
              OrderStatus.DRAFT
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => ApiOrderId(orderId))
      }
    }
  }

  override def updateOrderStatus(
      currentState: OrderState,
      apiUpdateOrderStatus: ApiUpdateOrderStatus
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.order match {
      case Some(order)
          if order.orderId == Some(OrderId(apiUpdateOrderStatus.orderId)) => {
        val event = OrderStatusUpdated(
          order.orderId,
          convertApiOrderStatusToOrderStatus(apiUpdateOrderStatus.newStatus),
          apiUpdateOrderStatus.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def updateOrderInfo(
      currentState: OrderState,
      apiUpdateOrderInfo: ApiUpdateOrderInfo
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.order match {
      case Some(order)
          if order.orderId == Some(OrderId(apiUpdateOrderInfo.orderId)) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val orderInfo = apiUpdateOrderInfo.update
          .map(convertApiUpdateOrderInfoToOrderInfo)
          .map(calculateOrderTotal)
        val updatingMemberIdOpt =
          apiUpdateOrderInfo.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        val event = OrderInfoUpdated(
          order.orderId,
          orderInfo,
          order.meta.map(
            _.copy(
              lastModifiedBy = updatingMemberIdOpt,
              lastModifiedOn = Some(timestamp),
              status =
                currentState.order.map(_.status).getOrElse(OrderStatus.UNKNOWN)
            )
          ),
          updatingMemberIdOpt
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def cancelOrder(
      currentState: OrderState,
      apiCancelOrder: ApiCancelOrder
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.order match {
      case Some(order)
          if order.orderId == Some(OrderId(apiCancelOrder.orderId)) => {
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
              status = OrderStatus.CANCELLED
            )
          ),
          cancellingMemberIdOpt
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def getOrderInfo(
      currentState: OrderState,
      apiGetOrderInfo: ApiGetOrderInfo
  ): EventSourcedEntity.Effect[ApiOrderInfoResult] = {
    currentState.order match {
      case Some(order)
          if order.orderId == Some(OrderId(apiGetOrderInfo.orderId)) => {
        val result = ApiOrderInfoResult(
          Some(ApiOrderId(apiGetOrderInfo.orderId)),
          order.info.map(convertOrderInfoToApiOrderInfo)
        )
        effects.reply(result)
      }
      case _ =>
        effects.error(
          s"OrderInfo ID ${apiGetOrderInfo.orderId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
    }
  }
  override def orderCreated(
      currentState: OrderState,
      orderCreated: OrderCreated
  ): OrderState = {
    currentState.order match {
      case Some(_) => currentState
      case _ => {
        currentState.withOrder(
          Order(
            orderCreated.orderId,
            orderCreated.info,
            orderCreated.meta,
            OrderStatus.DRAFT
          )
        )
      }
    }
  }

  override def orderStatusUpdated(
      currentState: OrderState,
      orderStatusUpdated: OrderStatusUpdated
  ): OrderState = {
    currentState.order match {
      case Some(order) if order.orderId == orderStatusUpdated.orderId => {
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
        currentState.withOrder(
          order.copy(
            status = orderStatusUpdated.newStatus,
            meta = updatedMetaOpt
          )
        )
      }
      case _ => currentState
    }
  }

  override def orderInfoUpdated(
      currentState: OrderState,
      orderInfoUpdated: OrderInfoUpdated
  ): OrderState = {
    currentState.order match {
      case Some(order) if order.orderId == orderInfoUpdated.orderId => {
        currentState.withOrder(
          order.copy(
            info = orderInfoUpdated.info,
            meta = orderInfoUpdated.meta
          )
        )
      }
      case _ => currentState
    }
  }
  override def orderCanceled(
      currentState: OrderState,
      orderCanceled: OrderCanceled
  ): OrderState = {
    currentState.order match {
      case Some(order) if order.orderId == orderCanceled.orderId => {
        currentState.withOrder(
          order.copy(
            info = orderCanceled.info,
            meta = orderCanceled.meta,
            status = OrderStatus.CANCELLED
          )
        )
      }
      case _ => currentState
    }
  }

}
