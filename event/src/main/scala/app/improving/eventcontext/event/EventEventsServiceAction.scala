package app.improving.eventcontext.event

import app.improving.{ApiEventId, ApiMemberId}
import app.improving.eventcontext.EventCancelled
import app.improving.eventcontext.EventDelayed
import app.improving.eventcontext.EventEnded
import app.improving.eventcontext.EventInfoChanged
import app.improving.eventcontext.EventRescheduled
import app.improving.eventcontext.EventScheduled
import app.improving.eventcontext.EventStarted
import app.improving.eventcontext.infrastructure.util._
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventEventsServiceAction(creationContext: ActionCreationContext)
    extends AbstractEventEventsServiceAction {

  override def transformEventInfoChanged(
      eventInfoChanged: EventInfoChanged
  ): Action.Effect[ApiEventInfoChanged] = {
    effects.reply(
      ApiEventInfoChanged(
        eventInfoChanged.eventId.map(event => ApiEventId(event.id)),
        eventInfoChanged.info.map(convertEventInfoToApiEventInfo),
        eventInfoChanged.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }
  override def transformEventScheduled(
      eventScheduled: EventScheduled
  ): Action.Effect[ApiEventScheduled] = {
    effects.reply(
      ApiEventScheduled(
        eventScheduled.eventId.map(event => ApiEventId(event.id)),
        eventScheduled.info.map(convertEventInfoToApiEventInfo),
        eventScheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }
  override def transformEventCancelled(
      eventCancelled: EventCancelled
  ): Action.Effect[ApiEventCancelled] = {
    effects.reply(
      ApiEventCancelled(
        eventCancelled.eventId.map(event => ApiEventId(event.id)),
        eventCancelled.cancellingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
  override def transformEventRescheduled(
      eventRescheduled: EventRescheduled
  ): Action.Effect[ApiEventRescheduled] = {
    effects.reply(
      ApiEventRescheduled(
        eventRescheduled.eventId.map(event => ApiEventId(event.id)),
        eventRescheduled.info.map(convertEventInfoToApiEventInfo),
        eventRescheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }
  override def transformEventDelayed(
      eventDelayed: EventDelayed
  ): Action.Effect[ApiEventDelayed] = {
    effects.reply(
      ApiEventDelayed(
        eventDelayed.eventId.map(event => ApiEventId(event.id)),
        eventDelayed.reason,
        eventDelayed.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        eventDelayed.expectedDuration
      )
    )
  }
  override def transformEventStarted(
      eventStarted: EventStarted
  ): Action.Effect[ApiEventStarted] = {
    effects.reply(
      ApiEventStarted(
        eventStarted.eventId.map(event => ApiEventId(event.id)),
        eventStarted.info.map(convertEventInfoToApiEventInfo),
        eventStarted.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }
  override def transformEventEnded(
      eventEnded: EventEnded
  ): Action.Effect[ApiEventEnded] = {
    effects.reply(
      ApiEventEnded(
        eventEnded.eventId.map(event => ApiEventId(event.id)),
        eventEnded.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }
}
