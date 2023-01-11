package app.improving.eventcontext.event

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.eventcontext.infrastructure.util._
import app.improving.{EventId, MemberId}
import app.improving.eventcontext.{
  EventCancelled,
  EventDelayed,
  EventEnded,
  EventInfo,
  EventInfoChanged,
  EventMetaInfo,
  EventRescheduled,
  EventScheduled,
  EventStarted,
  EventStatus
}
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

import java.util.UUID

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventAPI(context: EventSourcedEntityContext) extends AbstractEventAPI {
  override def emptyState: EventState = EventState.defaultInstance

  override def changeEventInfo(
      currentState: EventState,
      apiChangeEventInfo: ApiChangeEventInfo
  ): EventSourcedEntity.Effect[Empty] = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val memberIdOpt =
      apiChangeEventInfo.changingMember.map(member => MemberId(member.memberId))
    currentState.event match {
      case Some(state)
          if state.eventId == Some(EventId(apiChangeEventInfo.eventId)) => {
        val event = EventInfoChanged(
          Some(EventId(apiChangeEventInfo.eventId)),
          apiChangeEventInfo.info.map(convertApiEventInfoToEventInfo),
          Some(
            EventMetaInfo(
              memberIdOpt,
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              apiChangeEventInfo.info.flatMap(_.expectedStart),
              apiChangeEventInfo.info.flatMap(_.expectedEnd),
              currentState.event
                .map(_.status)
                .getOrElse(
                  EventStatus.UNKNOWN
                )
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def scheduleEvent(
      currentState: EventState,
      apiScheduleEvent: ApiScheduleEvent
  ): EventSourcedEntity.Effect[Empty] = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val memberIdOpt =
      apiScheduleEvent.schedulingMember.map(member => MemberId(member.memberId))
    currentState.event match {
      case Some(_) => {
        effects.reply(Empty.defaultInstance)
      }
      case _ => {
        val event = EventScheduled(
          Some(EventId(apiScheduleEvent.eventId)),
          apiScheduleEvent.info.map(convertApiEventInfoToEventInfo),
          Some(
            EventMetaInfo(
              memberIdOpt,
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              apiScheduleEvent.info.flatMap(_.expectedStart),
              apiScheduleEvent.info.flatMap(_.expectedEnd),
              EventStatus.SCHEDULED
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
    }
  }

  override def cancelEvent(
      currentState: EventState,
      apiCancelEvent: ApiCancelEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiCancelEvent.eventId)) => {
        val cancelled = EventCancelled(
          event.eventId,
          apiCancelEvent.cancellingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(cancelled).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def rescheduleEvent(
      currentState: EventState,
      apiRescheduleEvent: ApiRescheduleEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiRescheduleEvent.eventId)) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

        val rescheduled = EventRescheduled(
          event.eventId,
          event.info.map(
            _.copy(
              expectedStart = apiRescheduleEvent.start,
              expectedEnd = apiRescheduleEvent.end
            )
          ),
          event.meta.map(
            _.copy(
              lastModifiedBy =
                apiRescheduleEvent.reschedulingMember.map(member =>
                  MemberId(member.memberId)
                ),
              lastModifiedOn = Some(timestamp)
            )
          )
        )
        effects.emitEvent(rescheduled).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def delayEvent(
      currentState: EventState,
      apiDelayEvent: ApiDelayEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiDelayEvent.eventId)) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            lastModifiedBy = apiDelayEvent.delayingMember
              .map(member => MemberId(member.memberId)),
            lastModifiedOn = Some(timestamp),
            status = EventStatus.DELAYED
          )
        )
        val delayed = EventDelayed(
          event.eventId,
          apiDelayEvent.reason,
          metaOpt,
          apiDelayEvent.expectedDuration
        )
        effects.emitEvent(delayed).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def startEvent(
      currentState: EventState,
      apiStartEvent: ApiStartEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiStartEvent.eventId)) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val infoOpt = event.info
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            lastModifiedOn = Some(timestamp),
            lastModifiedBy = apiStartEvent.startingMember.map(member =>
              MemberId(member.memberId)
            ),
            status = EventStatus.INPROGRESS,
            actualStart = Some(timestamp)
          )
        )
        val started = EventStarted(
          event.eventId,
          infoOpt,
          metaOpt
        )
        effects.emitEvent(started).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }
  override def endEvent(
      currentState: EventState,
      apiEndEvent: ApiEndEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiEndEvent.eventId)) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            lastModifiedOn = Some(timestamp),
            lastModifiedBy =
              apiEndEvent.endingMember.map(member => MemberId(member.memberId)),
            status = EventStatus.PAST,
            actualEnd = Some(timestamp)
          )
        )
        val ended = EventEnded(
          event.eventId,
          metaOpt
        )
        effects.emitEvent(ended).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }
  override def addLiveUpdate(
      currentState: EventState,
      apiAddLiveUpdate: ApiAddLiveUpdate
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `AddLiveUpdate` is not implemented, yet"
    )

  override def getEventById(
      currentState: EventState,
      apiGetEventById: ApiGetEventById
  ): EventSourcedEntity.Effect[ApiEvent] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiGetEventById.eventId)) => {
        effects.reply(convertEventToApiEvent(event))
      }
      case _ => effects.reply(ApiEvent.defaultInstance)
    }
  }

  override def eventInfoChanged(
      currentState: EventState,
      eventInfoChanged: EventInfoChanged
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventInfoChanged.eventId => {
        currentState.withEvent(
          event.copy(info = eventInfoChanged.info, meta = eventInfoChanged.meta)
        )
      }
      case _ => currentState
    }
  }

  override def eventScheduled(
      currentState: EventState,
      eventScheduled: EventScheduled
  ): EventState = {
    currentState.event match {
      case Some(_) => currentState // event was already scheduled.
      case _ =>
        currentState.withEvent(
          Event(
            eventScheduled.eventId,
            eventScheduled.info,
            eventScheduled.meta,
            EventStatus.SCHEDULED
          )
        )
    }
  }
  override def eventCancelled(
      currentState: EventState,
      eventCancelled: EventCancelled
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventCancelled.eventId => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            status = EventStatus.CANCELLED,
            lastModifiedOn = Some(timestamp),
            lastModifiedBy = eventCancelled.cancellingMember
          )
        )
        currentState.withEvent(
          event.copy(meta = metaOpt, status = EventStatus.CANCELLED)
        )
      }
      case _ => currentState
    }
  }

  override def eventRescheduled(
      currentState: EventState,
      eventRescheduled: EventRescheduled
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventRescheduled.eventId => {
        currentState.withEvent(
          event.copy(
            info = eventRescheduled.info,
            meta = eventRescheduled.meta,
            status = EventStatus.SCHEDULED
          )
        )
      }
      case _ => currentState
    }
  }
  override def eventDelayed(
      currentState: EventState,
      eventDelayed: EventDelayed
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventDelayed.eventId => {
        val infoOpt = event.info.map(info =>
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

        currentState.withEvent(
          event.copy(
            info = infoOpt,
            meta = eventDelayed.meta,
            status = EventStatus.DELAYED
          )
        )
      }
      case _ => currentState
    }
  }

  override def eventStarted(
      currentState: EventState,
      eventStarted: EventStarted
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventStarted.eventId => {
        currentState.withEvent(
          event.copy(
            info = eventStarted.info,
            meta = eventStarted.meta,
            status = EventStatus.INPROGRESS
          )
        )
      }
      case _ => currentState
    }
  }

  override def eventEnded(
      currentState: EventState,
      eventEnded: EventEnded
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventEnded.eventId => {
        currentState.withEvent(
          event.copy(
            meta = eventEnded.meta,
            status = EventStatus.PAST
          )
        )
      }
      case _ => currentState
    }
  }

}
