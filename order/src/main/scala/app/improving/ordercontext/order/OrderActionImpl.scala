package app.improving.ordercontext.order

import app.improving.ApiOrderId
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderActionImpl(creationContext: ActionCreationContext) extends AbstractOrderAction {

  override def purchaseTicket(apiCreateOrder: ApiCreateOrder): Action.Effect[ApiOrderId] = {
    throw new RuntimeException("The command handler for `PurchaseTicket` is not implemented, yet")
  }
}

