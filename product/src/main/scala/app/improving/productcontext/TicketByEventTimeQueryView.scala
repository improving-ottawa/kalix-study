package app.improving.productcontext

import app.improving.ApiMemberId
import app.improving.eventcontext.EventCancelled
import app.improving.eventcontext.EventDelayed
import app.improving.eventcontext.EventEnded
import app.improving.eventcontext.EventRescheduled
import app.improving.eventcontext.EventScheduled
import app.improving.eventcontext.EventStarted
import app.improving.eventcontext.event.{ApiEvent, ApiEventStatus}
import app.improving.eventcontext.infrastructure.util.{
  convertEventInfoToApiEventInfo,
  convertEventMetaInfoToApiEventMetaInfo,
  convertEventReScheduledToApiEvent,
  convertEventScheduledToApiEvent
}
import app.improving.productcontext.infrastructure.util.{
  convertProductCreatedToApiProduct,
  convertProductInfoToApiProductInfo,
  convertProductMetaInfoToApiProductMetaInfo
}
import app.improving.productcontext.product.{ApiProduct, ApiProductStatus}
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TicketByEventTimeQueryView(context: ViewContext)
    extends AbstractTicketByEventTimeQueryView {

  object TicketByEventProductViewTable
      extends AbstractTicketByEventProductViewTable {

    override def emptyState: ApiProduct = ApiProduct.defaultInstance

    override def processProductCreated(
        state: ApiProduct,
        productCreated: ProductCreated
    ): UpdateEffect[ApiProduct] = {
      if (state != emptyState) effects.ignore()
      else
        effects.updateState(
          convertProductCreatedToApiProduct(productCreated)
        )
    }

    override def processProductInfoUpdated(
        state: ApiProduct,
        productInfoUpdated: ProductInfoUpdated
    ): UpdateEffect[ApiProduct] = effects.updateState(
      state.copy(
        info = productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
        meta = productInfoUpdated.meta.map(
          convertProductMetaInfoToApiProductMetaInfo
        )
      )
    )

    override def processProductDeleted(
        state: ApiProduct,
        productDeleted: ProductDeleted
    ): UpdateEffect[ApiProduct] = effects.deleteState()

    override def processProductActivated(
        state: ApiProduct,
        productActivated: ProductActivated
    ): UpdateEffect[ApiProduct] = {
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val metaOpt = state.meta.map(
        _.copy(
          lastModifiedBy = productActivated.activatingMember.map(member =>
            ApiMemberId(member.id)
          ),
          lastModifiedOn = Some(timestamp)
        )
      )
      effects.updateState(
        state.copy(
          meta = metaOpt,
          status = ApiProductStatus.API_PRODUCT_STATUS_ACTIVE
        )
      )
    }

    override def processProductInactivated(
        state: ApiProduct,
        productInactivated: ProductInactivated
    ): UpdateEffect[ApiProduct] = {
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val metaOpt = state.meta.map(
        _.copy(
          lastModifiedBy = productInactivated.inactivatingMember.map(member =>
            ApiMemberId(member.id)
          ),
          lastModifiedOn = Some(timestamp)
        )
      )
      effects.updateState(
        state.copy(
          meta = metaOpt,
          status = ApiProductStatus.API_PRODUCT_STATUS_INACTIVE
        )
      )
    }

  }

  object TicketByEventEventViewTable
      extends AbstractTicketByEventEventViewTable {

    override def emptyState: ApiEvent = ApiEvent.defaultInstance

    override def processEventScheduled(
        state: ApiEvent,
        eventScheduled: EventScheduled
    ): UpdateEffect[ApiEvent] = {
      if (state != emptyState) effects.ignore()
      else
        effects.updateState(
          convertEventScheduledToApiEvent(eventScheduled)
        )
    }

    override def processEventRescheduled(
        state: ApiEvent,
        eventRescheduled: EventRescheduled
    ): UpdateEffect[ApiEvent] =
      effects.updateState(
        convertEventReScheduledToApiEvent(eventRescheduled)
      )

    override def processEventStarted(
        state: ApiEvent,
        eventStarted: EventStarted
    ): UpdateEffect[ApiEvent] =
      effects.updateState(
        state.copy(
          info = eventStarted.info.map(convertEventInfoToApiEventInfo),
          meta = eventStarted.meta.map(convertEventMetaInfoToApiEventMetaInfo),
          status = ApiEventStatus.API_EVENT_STATUS_INPROGRESS
        )
      )

    override def processEventEnded(
        state: ApiEvent,
        eventEnded: EventEnded
    ): UpdateEffect[ApiEvent] =
      effects.updateState(
        state.copy(
          meta = eventEnded.meta.map(convertEventMetaInfoToApiEventMetaInfo),
          status = ApiEventStatus.API_EVENT_STATUS_PAST
        )
      )

    override def processEventDelayed(
        state: ApiEvent,
        eventDelayed: EventDelayed
    ): UpdateEffect[ApiEvent] = {
      val infoOpt = state.info.map(info =>
        info.copy(
          expectedStart =
            for {
              timestamp <- info.expectedStart
              duration <- eventDelayed.expectedDuration
            } yield (
              Timestamp.of(
                timestamp.seconds + duration.seconds,
                timestamp.nanos + duration.nanos
              )
            ),
          expectedEnd =
            for {
              timestamp <- info.expectedEnd
              duration <- eventDelayed.expectedDuration
            } yield (
              Timestamp.of(
                timestamp.seconds + duration.seconds,
                timestamp.nanos + duration.nanos
              )
            )
        )
      )
      effects.updateState(
        state.copy(
          info = infoOpt,
          meta = eventDelayed.meta.map(convertEventMetaInfoToApiEventMetaInfo),
          status = ApiEventStatus.API_EVENT_STATUS_DELAYED
        )
      )
    }

    override def processEventCancelled(
        state: ApiEvent,
        eventCancelled: EventCancelled
    ): UpdateEffect[ApiEvent] = {
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val metaOpt = state.meta.map(meta =>
        meta.copy(
          status = ApiEventStatus.API_EVENT_STATUS_CANCELLED,
          lastModifiedOn = Some(timestamp),
          lastModifiedBy = eventCancelled.cancellingMember.map(member =>
            ApiMemberId(member.id)
          )
        )
      )
      effects.updateState(
        state.copy(
          meta = metaOpt,
          status = ApiEventStatus.API_EVENT_STATUS_CANCELLED
        )
      )
    }

  }
}
