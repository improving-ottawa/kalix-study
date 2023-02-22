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
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventEventsServiceAction(creationContext: ActionCreationContext)
    extends AbstractEventEventsServiceAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def transformEventInfoChanged(
      eventInfoChanged: EventInfoChanged
  ): Action.Effect[ApiEventInfoChanged] = {

    log.info(
      s"EventEventsServiceAction in transformEventInfoChanged - eventInfoChanged - ${eventInfoChanged}"
    )

    effects.reply(
      ApiEventInfoChanged(
        eventInfoChanged.eventId.map(_.id).getOrElse("eventId is NOT FOUND."),
        eventInfoChanged.info.map(convertEventInfoToApiEventInfo),
        eventInfoChanged.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }

  override def transformEventScheduled(
      eventScheduled: EventScheduled
  ): Action.Effect[ApiEventScheduled] = {

    log.info(
      s"EventEventsServiceAction in transformEventScheduled - eventScheduled - ${eventScheduled}"
    )

    effects.reply(
      ApiEventScheduled(
        eventScheduled.eventId.map(_.id).getOrElse("eventId is NOT FOUND."),
        eventScheduled.info.map(convertEventInfoToApiEventInfo),
        eventScheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }

  override def transformEventCancelled(
      eventCancelled: EventCancelled
  ): Action.Effect[ApiEventCancelled] = {

    log.info(
      s"EventEventsServiceAction in transformEventCancelled - eventCancelled - ${eventCancelled}"
    )

    effects.reply(
      ApiEventCancelled(
        eventCancelled.eventId.map(event => ApiEventId(event.id)),
        eventCancelled.meta.flatMap(
          _.lastModifiedBy.map(member => ApiMemberId(member.id))
        )
      )
    )
  }

  override def transformEventRescheduled(
      eventRescheduled: EventRescheduled
  ): Action.Effect[ApiEventRescheduled] = {

    log.info(
      s"EventEventsServiceAction in transformEventRescheduled - eventRescheduled - ${eventRescheduled}"
    )

    effects.reply(
      ApiEventRescheduled(
        eventRescheduled.eventId.map(_.id).getOrElse("eventId is NOT FOUND."),
        eventRescheduled.info.map(convertEventInfoToApiEventInfo),
        eventRescheduled.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }

  override def transformEventDelayed(
      eventDelayed: EventDelayed
  ): Action.Effect[ApiEventDelayed] = {

    log.info(
      s"EventEventsServiceAction in transformEventDelayed - eventDelayed - ${eventDelayed}"
    )

    effects.reply(
      ApiEventDelayed(
        eventDelayed.eventId.map(_.id).getOrElse("eventId is NOT FOUND."),
        eventDelayed.reason,
        eventDelayed.meta.map(convertEventMetaInfoToApiEventMetaInfo),
        eventDelayed.expectedDuration
      )
    )
  }

  override def transformEventStarted(
      eventStarted: EventStarted
  ): Action.Effect[ApiEventStarted] = {

    log.info(
      s"EventEventsServiceAction in transformEventStarted - eventStarted - ${eventStarted}"
    )

    effects.reply(
      ApiEventStarted(
        eventStarted.eventId.map(_.id).getOrElse("eventId is NOT FOUND."),
        eventStarted.info.map(convertEventInfoToApiEventInfo),
        eventStarted.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }

  override def transformEventEnded(
      eventEnded: EventEnded
  ): Action.Effect[ApiEventEnded] = {

    log.info(
      s"EventEventsServiceAction in transformEventEnded - eventEnded - ${eventEnded}"
    )

    effects.reply(
      ApiEventEnded(
        eventEnded.eventId.map(_.id).getOrElse("eventId is NOT FOUND."),
        eventEnded.meta.map(convertEventMetaInfoToApiEventMetaInfo)
      )
    )
  }
}
