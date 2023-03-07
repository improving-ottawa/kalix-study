package app.improving.productcontext

import app.improving.ApiMemberId
import app.improving.eventcontext.event._
import app.improving.productcontext.infrastructure.util._
import app.improving.productcontext.product._
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

  object TicketByEventTimeProductViewTable
      extends AbstractTicketByEventTimeProductViewTable {

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
          info =
            productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
          meta = productInfoUpdated.meta.map(
            convertProductMetaInfoToApiProductMetaInfo
          ),
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

    override def processProductReleased(
        state: TicketEventCorrTableRow,
        productReleased: ProductReleased
    ): UpdateEffect[TicketEventCorrTableRow] = {

      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

      effects.updateState(
        state.copy(
          meta = state.meta.map(
            _.copy(
              lastModifiedBy = productReleased.releasingMember.map(member =>
                ApiMemberId(member.id)
              ),
              lastModifiedOn = Some(timestamp)
            )
          ),
          status = ApiProductStatus.API_PRODUCT_STATUS_RELEASED.toString()
        )
      )
    }
  }

  object TicketByEventTimeEventViewTable
      extends AbstractTicketByEventTimeEventViewTable {

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
            eventScheduled.eventId,
            eventScheduled.info,
            "reservation-id", // ??? Where is it from ???
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
          eventRescheduled.eventId,
          eventRescheduled.info,
          "reservation-id", // ??? Where is it from ???
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

    override def processReservationAddedToEvent(
        state: ApiEvent,
        apiReservationAddedToEvent: ApiReservationAddedToEvent
    ): UpdateEffect[ApiEvent] = {

      log.info(
        s"TicketByEventTimeQueryView in processReservationAddedToEvent - apiReservationAddedToEvent ${apiReservationAddedToEvent}"
      )

      effects.updateState(
        state.copy(
          meta = apiReservationAddedToEvent.meta,
          reservation = apiReservationAddedToEvent.reservation
        )
      )
    }

    override def processEventReleased(
        state: ApiEvent,
        apiEventReleased: ApiEventReleased
    ): UpdateEffect[ApiEvent] = {
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

      effects.updateState(
        state.copy(meta =
          state.meta.map(
            _.copy(
              lastModifiedBy = apiEventReleased.releasingMember,
              lastModifiedOn = Some(timestamp),
              status = ApiEventStatus.API_EVENT_STATUS_RELEASED
            )
          )
        )
      )
    }
  }
}
