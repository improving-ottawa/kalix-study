package app.improving.eventcontext

import app.improving.ApiMemberId
import app.improving.eventcontext.event.{ApiEvent, ApiEventStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import app.improving.eventcontext.infrastructure.util._
import com.google.protobuf.timestamp.Timestamp

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllEventsViewImpl(context: ViewContext) extends AbstractAllEventsView {

  override def emptyState: ApiEvent = ApiEvent.defaultInstance

  override def processEventInfoChanged(
      state: ApiEvent,
      eventInfoChanged: EventInfoChanged
  ): UpdateEffect[ApiEvent] = {
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        ApiEvent(
          eventInfoChanged.eventId.map(_.id).getOrElse("EventId is NOT FOUND."),
          eventInfoChanged.info.map(convertEventInfoToApiEventInfo),
          eventInfoChanged.meta.map(convertEventMetaInfoToApiEventMetaInfo),
          ApiEventStatus.SCHEDULED
        )
      )
  }
  override def processEventScheduled(
      state: ApiEvent,
      eventScheduled: EventScheduled
  ): UpdateEffect[ApiEvent] = {
    effects.updateState(
      state.copy(
        info = eventScheduled.info.map(convertEventInfoToApiEventInfo),
        meta = eventScheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.SCHEDULED
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
        status = ApiEventStatus.CANCELLED,
        lastModifiedOn = Some(timestamp),
        lastModifiedBy =
          eventCancelled.cancellingMember.map(member => ApiMemberId(member.id))
      )
    )
    effects.updateState(
      state.copy(
        meta = metaOpt,
        status = ApiEventStatus.CANCELLED
      )
    )
  }

  override def processEventRescheduled(
      state: ApiEvent,
      eventRescheduled: EventRescheduled
  ): UpdateEffect[ApiEvent] = {
    effects.updateState(
      state.copy(
        info = eventRescheduled.info.map(convertEventInfoToApiEventInfo),
        meta =
          eventRescheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.SCHEDULED
      )
    )
  }

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
        status = ApiEventStatus.DELAYED
      )
    )
  }

  override def processEventStarted(
      state: ApiEvent,
      eventStarted: EventStarted
  ): UpdateEffect[ApiEvent] = {
    effects.updateState(
      state.copy(
        info = eventStarted.info.map(convertEventInfoToApiEventInfo),
        meta = eventStarted.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.INPROGRESS
      )
    )
  }

  override def processEventEnded(
      state: ApiEvent,
      eventEnded: EventEnded
  ): UpdateEffect[ApiEvent] = {
    effects.updateState(
      state.copy(
        meta = eventEnded.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        status = ApiEventStatus.PAST
      )
    )
  }

}
