package app.improving.productcontext

import app.improving.ApiMemberId
import app.improving.eventcontext.EventCancelled
import app.improving.eventcontext.EventDelayed
import app.improving.eventcontext.EventEnded
import app.improving.eventcontext.EventStarted
import app.improving.eventcontext.event._
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
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TicketByEventTimeQueryView(context: ViewContext)
    extends AbstractTicketByEventTimeQueryView {

  private val log = LoggerFactory.getLogger(this.getClass)

  object TicketByEventProductViewTable
      extends AbstractTicketByEventProductViewTable {

    override def emptyState: ApiProduct = ApiProduct.defaultInstance

    override def processProductCreated(
        state: ApiProduct,
        productCreated: ProductCreated
    ): UpdateEffect[ApiProduct] = {
      if (state != emptyState) {

        log.info(
          s"TicketByEventTimeQueryView in processProductCreated - state already existed"
        )

        effects.ignore()
      } else {

        log.info(
          s"TicketByEventTimeQueryView in processProductCreated - productCreated ${productCreated}"
        )

        effects.updateState(
          convertProductCreatedToApiProduct(productCreated)
        )
      }
    }

    override def processProductInfoUpdated(
        state: ApiProduct,
        productInfoUpdated: ProductInfoUpdated
    ): UpdateEffect[ApiProduct] = {

      log.info(
        s"TicketByEventTimeQueryView in processProductInfoUpdated - productInfoUpdated ${productInfoUpdated}"
      )

      effects.updateState(
        state.copy(
          info =
            productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
          meta = productInfoUpdated.meta.map(
            convertProductMetaInfoToApiProductMetaInfo
          )
        )
      )
    }

    override def processProductDeleted(
        state: ApiProduct,
        productDeleted: ProductDeleted
    ): UpdateEffect[ApiProduct] = {

      log.info(
        s"TicketByEventTimeQueryView in processProductDeleted - productDeleted ${productDeleted}"
      )

      effects.deleteState()
    }

    override def processProductActivated(
        state: ApiProduct,
        productActivated: ProductActivated
    ): UpdateEffect[ApiProduct] = {

      log.info(
        s"TicketByEventTimeQueryView in processProductActivated - productActivated ${productActivated}"
      )

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

      log.info(
        s"TicketByEventTimeQueryView in processProductInactivated - productInactivated ${productInactivated}"
      )

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
        eventScheduled: ApiEventScheduled
    ): UpdateEffect[ApiEvent] = {
      if (state != emptyState) {

        log.info(
          s"TicketByEventTimeQueryView in processEventScheduled - state already existed"
        )

        effects.ignore()
      } else {

        log.info(
          s"TicketByEventTimeQueryView in processEventScheduled - eventScheduled ${eventScheduled}"
        )

        effects.updateState(
          ApiEvent(
            eventScheduled.eventId
              .map(_.eventId)
              .getOrElse("EventId is NOT FOUND."),
            eventScheduled.info,
            eventScheduled.meta,
            ApiEventStatus.API_EVENT_STATUS_SCHEDULED
          )
        )
      }
    }

    override def processEventRescheduled(
        state: ApiEvent,
        eventRescheduled: ApiEventRescheduled
    ): UpdateEffect[ApiEvent] = {

      log.info(
        s"TicketByEventTimeQueryView in processEventRescheduled - eventRescheduled ${eventRescheduled}"
      )

      effects.updateState(
        ApiEvent(
          eventRescheduled.eventId
            .map(_.eventId)
            .getOrElse("EventId is NOT FOUND."),
          eventRescheduled.info,
          eventRescheduled.meta,
          ApiEventStatus.API_EVENT_STATUS_SCHEDULED
        )
      )
    }

    override def processEventStarted(
        state: ApiEvent,
        eventStarted: ApiEventStarted
    ): UpdateEffect[ApiEvent] = {

      log.info(
        s"TicketByEventTimeQueryView in processEventStarted - eventStarted ${eventStarted}"
      )

      effects.updateState(
        state.copy(
          info = eventStarted.info,
          meta = eventStarted.meta,
          status = ApiEventStatus.API_EVENT_STATUS_INPROGRESS
        )
      )
    }

    override def processEventEnded(
        state: ApiEvent,
        eventEnded: ApiEventEnded
    ): UpdateEffect[ApiEvent] = {

      log.info(
        s"TicketByEventTimeQueryView in processEventEnded - eventEnded ${eventEnded}"
      )

      effects.updateState(
        state.copy(
          meta = eventEnded.meta,
          status = ApiEventStatus.API_EVENT_STATUS_PAST
        )
      )
    }

    override def processEventDelayed(
        state: ApiEvent,
        eventDelayed: ApiEventDelayed
    ): UpdateEffect[ApiEvent] = {

      log.info(
        s"TicketByEventTimeQueryView in processEventDelayed - eventDelayed ${eventDelayed}"
      )

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
          meta = eventDelayed.meta,
          status = ApiEventStatus.API_EVENT_STATUS_DELAYED
        )
      )
    }

    override def processEventCancelled(
        state: ApiEvent,
        eventCancelled: ApiEventCancelled
    ): UpdateEffect[ApiEvent] = {

      log.info(
        s"TicketByEventTimeQueryView in processEventCancelled - eventCancelled ${eventCancelled}"
      )

      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val metaOpt = state.meta.map(meta =>
        meta.copy(
          status = ApiEventStatus.API_EVENT_STATUS_CANCELLED,
          lastModifiedOn = Some(timestamp),
          lastModifiedBy = eventCancelled.cancellingMember.map(member =>
            ApiMemberId(member.memberId)
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
