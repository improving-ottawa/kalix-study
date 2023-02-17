package app.improving.eventcontext

import app.improving.ApiMemberId
import app.improving.eventcontext.event.{ApiEvent, ApiEventStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import app.improving.eventcontext.infrastructure.util._
import com.google.protobuf.timestamp.Timestamp
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllEventsViewImpl(context: ViewContext) extends AbstractAllEventsView {

  override def emptyState: ApiEvent = ApiEvent.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

  override def processEventInfoChanged(
      state: ApiEvent,
      eventInfoChanged: EventInfoChanged
  ): UpdateEffect[ApiEvent] = {
    if (state != emptyState) {

      log.info(
        s"AllEventsViewImpl in processEventInfoChanged - state already existed"
      )

      effects.ignore()
    } else {

      log.info(
        s"AllEventsViewImpl in processEventInfoChanged - eventInfoChanged ${eventInfoChanged}"
      )

      effects.updateState(
        state.copy(
          info = eventInfoChanged.info.map(convertEventInfoToApiEventInfo),
          meta =
            eventInfoChanged.meta.map(convertEventMetaInfoToApiEventMetaInfo),
          status = ApiEventStatus.API_EVENT_STATUS_SCHEDULED
        )
      )
  }
  override def processEventScheduled(
      state: ApiEvent,
      eventScheduled: EventScheduled
  ): UpdateEffect[ApiEvent] = {

    log.info(
      s"AllEventsViewImpl in processEventScheduled - eventScheduled ${eventScheduled}"
    )

    effects.updateState(
      ApiEvent(
        info = eventScheduled.info.map(convertEventInfoToApiEventInfo),
        meta = eventScheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.API_EVENT_STATUS_SCHEDULED
      )
    )
  }

  override def processEventCancelled(
      state: ApiEvent,
      eventCancelled: EventCancelled
  ): UpdateEffect[ApiEvent] = {

    log.info(
      s"AllEventsViewImpl in processEventCancelled - eventCancelled ${eventCancelled}"
    )

    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val metaOpt = state.meta.map(meta =>
      meta.copy(
        status = ApiEventStatus.API_EVENT_STATUS_CANCELLED,
        lastModifiedOn = Some(timestamp),
        lastModifiedBy = eventCancelled.meta
          .flatMap(_.lastModifiedBy)
          .map(id => ApiMemberId(id.id))
      )
    )
    effects.updateState(
      state.copy(
        meta = metaOpt,
        status = ApiEventStatus.API_EVENT_STATUS_CANCELLED
      )
    )
  }

  override def processEventRescheduled(
      state: ApiEvent,
      eventRescheduled: EventRescheduled
  ): UpdateEffect[ApiEvent] = {

    log.info(
      s"AllEventsViewImpl in processEventRescheduled - eventRescheduled ${eventRescheduled}"
    )

    effects.updateState(
      state.copy(
        info = eventRescheduled.info.map(convertEventInfoToApiEventInfo),
        meta =
          eventRescheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.API_EVENT_STATUS_SCHEDULED
      )
    )
  }

  override def processEventDelayed(
      state: ApiEvent,
      eventDelayed: EventDelayed
  ): UpdateEffect[ApiEvent] = {

    log.info(
      s"AllEventsViewImpl in processEventDelayed - eventDelayed ${eventDelayed}"
    )

    val infoOpt = state.info.map(info =>
      info.copy(
        expectedStart =
          for {
            timestamp <- info.expectedStart
            duration <- eventDelayed.expectedDuration
          } yield Timestamp.of(
            timestamp.seconds + duration.seconds,
            timestamp.nanos + duration.nanos
          ),
        expectedEnd =
          for {
            timestamp <- info.expectedEnd
            duration <- eventDelayed.expectedDuration
          } yield Timestamp.of(
            timestamp.seconds + duration.seconds,
            timestamp.nanos + duration.nanos
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

  override def processEventStarted(
      state: ApiEvent,
      eventStarted: EventStarted
  ): UpdateEffect[ApiEvent] = {

    log.info(
      s"AllEventsViewImpl in processEventStarted - eventStarted ${eventStarted}"
    )

    effects.updateState(
      state.copy(
        meta = eventStarted.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.API_EVENT_STATUS_INPROGRESS
      )
    )
  }

  override def processEventEnded(
      state: ApiEvent,
      eventEnded: EventEnded
  ): UpdateEffect[ApiEvent] = {

    log.info(
      s"AllEventsViewImpl in processEventEnded - eventEnded ${eventEnded}"
    )

    effects.updateState(
      state.copy(
        meta = eventEnded.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.API_EVENT_STATUS_PAST
      )
    )
  }

  override def processReservationAddedToEvent(
      state: ApiEvent,
      reservationAddedToEvent: ReservationAddedToEvent
  ): UpdateEffect[ApiEvent] = {
    effects.updateState(
      state.copy(
        meta = reservationAddedToEvent.meta.map(
          convertEventMetaInfoToApiEventMetaInfo
        ),
        reservation = reservationAddedToEvent.reservation
      )
    )
  }

}
