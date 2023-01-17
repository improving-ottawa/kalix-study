package app.improving.ordercontext.order

import app.improving.ordercontext.OrderCanceled
import app.improving.ordercontext.OrderCreated
import app.improving.ordercontext.OrderInfoUpdated
import app.improving.ordercontext.OrderStatusChanged
import app.improving.ordercontext.order
import com.google.protobuf.empty.Empty
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderAPI(context: EventSourcedEntityContext) extends AbstractOrderAPI {
  override def emptyState: OrderState =
    throw new UnsupportedOperationException("Not implemented yet, replace with your empty entity state")

  override def createOrder(currentState: OrderState, apiCreateOrder: ApiCreateOrder): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `CreateOrder` is not implemented, yet")

  override def changeOrderStatus(currentState: OrderState, apiChangeOrderStatus: ApiChangeOrderStatus): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `ChangeOrderStatus` is not implemented, yet")

  override def updateOrderInfo(currentState: OrderState, apiUpdateOrderInfo: ApiUpdateOrderInfo): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `UpdateOrderInfo` is not implemented, yet")

  override def cancelOrder(currentState: OrderState, apiCancelOrder: ApiCancelOrder): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `CancelOrder` is not implemented, yet")

  override def getOrderInfo(currentState: OrderState, apiGetOrderInfo: ApiGetOrderInfo): EventSourcedEntity.Effect[ApiOrderInfo] =
    effects.error("The command handler for `GetOrderInfo` is not implemented, yet")

  override def orderCreated(currentState: OrderState, orderCreated: OrderCreated): OrderState =
    throw new RuntimeException("The event handler for `OrderCreated` is not implemented, yet")

  override def orderStatusChanged(currentState: OrderState, orderStatusChanged: OrderStatusChanged): OrderState =
    throw new RuntimeException("The event handler for `OrderStatusChanged` is not implemented, yet")

  override def orderInfoUpdated(currentState: OrderState, orderInfoUpdated: OrderInfoUpdated): OrderState =
    throw new RuntimeException("The event handler for `OrderInfoUpdated` is not implemented, yet")

  override def orderCanceled(currentState: OrderState, orderCanceled: OrderCanceled): OrderState =
    throw new RuntimeException("The event handler for `OrderCanceled` is not implemented, yet")

}
