package app.improving.eventcontext.event

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.eventcontext.infrastructure.util._
import app.improving.{ApiEventId, EventId, MemberId}
import app.improving.eventcontext.{
  EventCancelled,
  EventDelayed,
  EventEnded,
  EventInfoChanged,
  EventMetaInfo,
  EventRescheduled,
  EventScheduled,
  EventStarted,
  EventStatus
}
import io.grpc.Status
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventAPI(context: EventSourcedEntityContext) extends AbstractEventAPI {
  override def emptyState: EventState = EventState.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

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

        log.info(
          s"EventAPI in changeEventInfo - apiChangeEventInfo - ${apiChangeEventInfo}"
        )

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
                  EventStatus.EVENT_STATUS_UNKNOWN
                )
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case other => {

        log.info(
          s"EventAPI in changeEventInfo - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }

  override def scheduleEvent(
      currentState: EventState,
      apiScheduleEvent: ApiScheduleEvent
  ): EventSourcedEntity.Effect[ApiEventId] = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val memberIdOpt =
      apiScheduleEvent.schedulingMember.map(member => MemberId(member.memberId))
    currentState.event match {
      case Some(event) => {

        log.info(
          s"EventAPI in scheduleEvent - event already existed - ${event}"
        )

        effects.reply(ApiEventId.defaultInstance)
      }
      case _ => {

        log.info(
          s"EventAPI in scheduleEvent - apiScheduleEvent - ${apiScheduleEvent}"
        )

        val eventId = apiScheduleEvent.eventId
        val event = EventScheduled(
          Some(EventId(eventId)),
          apiScheduleEvent.info.map(convertApiEventInfoToEventInfo),
          Some(
            EventMetaInfo(
              memberIdOpt,
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              apiScheduleEvent.info.flatMap(_.expectedStart),
              apiScheduleEvent.info.flatMap(_.expectedEnd),
              EventStatus.EVENT_STATUS_SCHEDULED
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => ApiEventId(eventId))
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

        log.info(
          s"EventAPI in cancelEvent - apiCancelEvent - ${apiCancelEvent}"
        )

        val cancelled = EventCancelled(
          event.eventId,
          apiCancelEvent.cancellingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(cancelled).thenReply(_ => Empty.defaultInstance)
      }
      case other => {

        log.info(
          s"EventAPI in cancelEvent - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }

  override def rescheduleEvent(
      currentState: EventState,
      apiRescheduleEvent: ApiRescheduleEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiRescheduleEvent.eventId)) => {

        log.info(
          s"EventAPI in rescheduleEvent - apiRescheduleEvent - ${apiRescheduleEvent}"
        )

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
      case other => {

        log.info(
          s"EventAPI in rescheduleEvent - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }

  override def delayEvent(
      currentState: EventState,
      apiDelayEvent: ApiDelayEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiDelayEvent.eventId)) => {

        log.info(
          s"EventAPI in delayEvent - apiDelayEvent - ${apiDelayEvent}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            lastModifiedBy = apiDelayEvent.delayingMember
              .map(member => MemberId(member.memberId)),
            lastModifiedOn = Some(timestamp),
            status = EventStatus.EVENT_STATUS_DELAYED
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
      case other => {

        log.info(
          s"EventAPI in delayEvent - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }

  override def startEvent(
      currentState: EventState,
      apiStartEvent: ApiStartEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiStartEvent.eventId)) => {

        log.info(
          s"EventAPI in startEvent - apiStartEvent - ${apiStartEvent}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val infoOpt = event.info
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            lastModifiedOn = Some(timestamp),
            lastModifiedBy = apiStartEvent.startingMember.map(member =>
              MemberId(member.memberId)
            ),
            status = EventStatus.EVENT_STATUS_INPROGRESS,
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
      case other => {

        log.info(
          s"EventAPI in startEvent - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }
  override def endEvent(
      currentState: EventState,
      apiEndEvent: ApiEndEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId == Some(EventId(apiEndEvent.eventId)) => {

        log.info(
          s"EventAPI in endEvent - apiEndEvent - ${apiEndEvent}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            lastModifiedOn = Some(timestamp),
            lastModifiedBy =
              apiEndEvent.endingMember.map(member => MemberId(member.memberId)),
            status = EventStatus.EVENT_STATUS_PAST,
            actualEnd = Some(timestamp)
          )
        )
        val ended = EventEnded(
          event.eventId,
          metaOpt
        )
        effects.emitEvent(ended).thenReply(_ => Empty.defaultInstance)
      }
      case other => {

        log.info(
          s"EventAPI in endEvent - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
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

        log.info(
          s"EventAPI in getEventById - apiGetEventById - ${apiGetEventById}"
        )

        effects.reply(convertEventToApiEvent(event))
      }
      case other => {

        log.info(
          s"EventAPI in getEventById - other - ${other}"
        )

        effects.error(
          s"Event By ID ${apiGetEventById.eventId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
      }
    }
  }

  override def eventInfoChanged(
      currentState: EventState,
      eventInfoChanged: EventInfoChanged
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventInfoChanged.eventId => {

        log.info(
          s"EventAPI in eventInfoChanged - eventInfoChanged - ${eventInfoChanged}"
        )

        currentState.withEvent(
          event.copy(info = eventInfoChanged.info, meta = eventInfoChanged.meta)
        )
      }
      case other => {

        log.info(
          s"EventAPI in eventInfoChanged - other - ${other}"
        )

        currentState
      }
    }
  }

  override def eventScheduled(
      currentState: EventState,
      eventScheduled: EventScheduled
  ): EventState = {
    currentState.event match {
      case Some(event) => {

        log.info(
          s"EventAPI in eventScheduled - eventScheduled already existed"
        )

        currentState
      } // event was already scheduled.
      case _ => {

        log.info(
          s"EventAPI in eventScheduled - eventScheduled ${eventScheduled}"
        )

        currentState.withEvent(
          Event(
            eventScheduled.eventId,
            eventScheduled.info,
            eventScheduled.meta,
            EventStatus.EVENT_STATUS_SCHEDULED
          )
        )
      }
    }
  }
  override def eventCancelled(
      currentState: EventState,
      eventCancelled: EventCancelled
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventCancelled.eventId => {

        log.info(
          s"EventAPI in eventCancelled - eventCancelled ${eventCancelled}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val metaOpt = event.meta.map(meta =>
          meta.copy(
            status = EventStatus.EVENT_STATUS_CANCELLED,
            lastModifiedOn = Some(timestamp),
            lastModifiedBy = eventCancelled.cancellingMember
          )
        )
        currentState.withEvent(
          event.copy(
            meta = metaOpt,
            status = EventStatus.EVENT_STATUS_CANCELLED
          )
        )
      }
      case other => {

        log.info(
          s"EventAPI in eventCancelled - other ${other}"
        )

        currentState
      }
    }
  }

  override def eventRescheduled(
      currentState: EventState,
      eventRescheduled: EventRescheduled
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventRescheduled.eventId => {

        log.info(
          s"EventAPI in eventRescheduled - eventRescheduled ${eventRescheduled}"
        )

        currentState.withEvent(
          event.copy(
            info = eventRescheduled.info,
            meta = eventRescheduled.meta,
            status = EventStatus.EVENT_STATUS_SCHEDULED
          )
        )
      }
      case other => {

        log.info(
          s"EventAPI in eventRescheduled - other ${other}"
        )

        currentState
      }
    }
  }
  override def eventDelayed(
      currentState: EventState,
      eventDelayed: EventDelayed
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventDelayed.eventId => {

        log.info(
          s"EventAPI in eventDelayed - eventDelayed ${eventDelayed}"
        )

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
            status = EventStatus.EVENT_STATUS_DELAYED
          )
        )
      }
      case other => {

        log.info(
          s"EventAPI in eventDelayed - other ${other}"
        )

        currentState
      }
    }
  }

  override def eventStarted(
      currentState: EventState,
      eventStarted: EventStarted
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventStarted.eventId => {

        log.info(
          s"EventAPI in eventStarted - eventStarted ${eventStarted}"
        )

        currentState.withEvent(
          event.copy(
            info = eventStarted.info,
            meta = eventStarted.meta,
            status = EventStatus.EVENT_STATUS_INPROGRESS
          )
        )
      }
      case other => {

        log.info(
          s"EventAPI in eventStarted - other ${other}"
        )

        currentState
      }
    }
  }

  override def eventEnded(
      currentState: EventState,
      eventEnded: EventEnded
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == eventEnded.eventId => {

        log.info(
          s"EventAPI in eventEnded - eventEnded ${eventEnded}"
        )

        currentState.withEvent(
          event.copy(
            meta = eventEnded.meta,
            status = EventStatus.EVENT_STATUS_PAST
          )
        )
      }
      case other => {

        log.info(
          s"EventAPI in eventEnded - other ${other}"
        )

        currentState
      }
    }
  }

}
