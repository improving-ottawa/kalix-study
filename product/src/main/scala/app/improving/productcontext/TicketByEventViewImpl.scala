package app.improving.productcontext

import app.improving.ApiEventId
import app.improving.productcontext.infrastructure.util._
import app.improving.productcontext.product.ApiProductStatus
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TicketByEventViewImpl(context: ViewContext)
    extends AbstractTicketByEventView {

  override def emptyState: TicketEventCorrTableRow =
    TicketEventCorrTableRow.defaultInstance

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
        info = productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
        meta = productInfoUpdated.meta.map(
          convertProductMetaInfoToApiProductMetaInfo
        ),
        event = productInfoUpdated.info
          .flatMap(extractEventIdFromProductInfo)
          .map(id => ApiEventId(id.eventId))
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
        status = ApiProductStatus.API_PRODUCT_STATUS_ACTIVE.toString()
      )
    )
  }

  override def processProductInactivated(
      state: TicketEventCorrTableRow,
      productInactivated: ProductInactivated
  ): UpdateEffect[TicketEventCorrTableRow] = {
    effects.updateState(
      state.copy(
        status = ApiProductStatus.API_PRODUCT_STATUS_INACTIVE.toString()
      )
    )
  }

}
