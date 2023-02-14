package app.improving.productcontext

import app.improving.productcontext.ProductDetails.Ticket
import app.improving.productcontext.infrastructure.util._
import app.improving.productcontext.product.{ApiProduct, ApiProductStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TicketByEventViewImpl(context: ViewContext)
    extends AbstractTicketByEventView {

  override def emptyState: TicketEventCorrTableRow = TicketEventCorrTableRow.defaultInstance

  override def processProductCreated(
      state: TicketEventCorrTableRow,
      productCreated: ProductCreated
  ): UpdateEffect[TicketEventCorrTableRow] = {
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        convertProductCreatedToTicketEventCorrTableRow(productCreated)
      )
  }

  override def processProductInfoUpdated(
      state: TicketEventCorrTableRow,
      productInfoUpdated: ProductInfoUpdated
  ): UpdateEffect[TicketEventCorrTableRow] =
    effects.updateState(
      state.copy(
        info = productInfoUpdated.info,
        meta = productInfoUpdated.meta,
        event = productInfoUpdated.info.flatMap(extractEventIdFromProductInfo)
      )
    )

  override def processProductDeleted(
      state: TicketEventCorrTableRow,
      productDeleted: ProductDeleted
  ): UpdateEffect[TicketEventCorrTableRow] =
    effects.deleteState()

  override def processProductActivated(
      state: TicketEventCorrTableRow,
      productActivated: ProductActivated
  ): UpdateEffect[TicketEventCorrTableRow] = {
    effects.updateState(
      state.copy(
        status = ProductStatus.ACTIVE.toString()
      )
    )
  }

  override def processProductInactivated(
      state: TicketEventCorrTableRow,
      productInactivated: ProductInactivated
  ): UpdateEffect[TicketEventCorrTableRow] = {
    effects.updateState(
      state.copy(
        status = ProductStatus.INACTIVE.toString()
      )
    )
  }

}
