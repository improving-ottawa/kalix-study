package com.improving.eventcontext.event

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import com.improving.event.ApiEventInfo
import com.improving.{EventId, MemberId, OrganizationId, event}
import com.improving.eventcontext.{
  EventCancelled,
  EventDelayed,
  EventEnded,
  EventInfo,
  EventInfoChanged,
  EventMetaInfo,
  EventRescheduled,
  EventScheduled,
  EventStarted,
  EventStatus,
  ReservationId
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
      apiChangeEventInfo: event.ApiChangeEventInfo
  ): EventSourcedEntity.Effect[Empty] = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val memberIdOpt =
      apiChangeEventInfo.changingMember.map(member => MemberId(member.memberId))
    currentState.event match {
      case Some(state)
          if state.id == Some(EventId(apiChangeEventInfo.eventId)) => {
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

  private def convertApiEventInfoToEventInfo(
      apiEventInfo: ApiEventInfo
  ): EventInfo = {
    EventInfo(
      apiEventInfo.eventName,
      apiEventInfo.description,
      apiEventInfo.eventURL,
      apiEventInfo.sponsoringOrg.map(org => OrganizationId(org.orgId)),
      apiEventInfo.geoLocation,
      apiEventInfo.reservation.map(reservation =>
        ReservationId(reservation.reservationId)
      ),
      apiEventInfo.expectedStart,
      apiEventInfo.expectedEnd,
      apiEventInfo.isPrivate
    )
  }
  override def scheduleEvent(
      currentState: EventState,
      apiScheduleEvent: event.ApiScheduleEvent
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
          apiScheduleEvent.info.map(convertApiEventInfoToEventInfo),
          Some(
            EventMetaInfo(
              memberIdOpt,
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              apiScheduleEvent.info.flatMap(_.expectedStart),
              apiScheduleEvent.info.flatMap(_.expectedEnd),
              currentState.event
                .map(_.status)
                .getOrElse(
                  EventStatus.CANCELLED
                ) //??? Need status UNKNOWN here!!!
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
    }
  }

  override def cancelEvent(
      currentState: EventState,
      apiCancelEvent: event.ApiCancelEvent
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `CancelEvent` is not implemented, yet"
    )

  override def rescheduleEvent(
      currentState: EventState,
      apiRescheduleEvent: event.ApiRescheduleEvent
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `RescheduleEvent` is not implemented, yet"
    )

  override def delayEvent(
      currentState: EventState,
      apiDelayEvent: event.ApiDelayEvent
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `DelayEvent` is not implemented, yet"
    )

  override def startEvent(
      currentState: EventState,
      apiStartEvent: event.ApiStartEvent
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `StartEvent` is not implemented, yet"
    )

  override def endEvent(
      currentState: EventState,
      apiEndEvent: event.ApiEndEvent
  ): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `EndEvent` is not implemented, yet")

  override def addLiveUpdate(
      currentState: EventState,
      apiAddLiveUpdate: event.ApiAddLiveUpdate
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `AddLiveUpdate` is not implemented, yet"
    )

  override def eventInfoChanged(
      currentState: EventState,
      eventInfoChanged: EventInfoChanged
  ): EventState = {
    currentState.event match {
      case Some(event) if event.id == eventInfoChanged.eventId => {
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
            Some(EventId(UUID.randomUUID().toString)),
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
  ): EventState =
    throw new RuntimeException(
      "The event handler for `EventCancelled` is not implemented, yet"
    )

  override def eventRescheduled(
      currentState: EventState,
      eventRescheduled: EventRescheduled
  ): EventState =
    throw new RuntimeException(
      "The event handler for `EventRescheduled` is not implemented, yet"
    )

  override def eventDelayed(
      currentState: EventState,
      eventDelayed: EventDelayed
  ): EventState =
    throw new RuntimeException(
      "The event handler for `EventDelayed` is not implemented, yet"
    )

  override def eventStarted(
      currentState: EventState,
      eventStarted: EventStarted
  ): EventState =
    throw new RuntimeException(
      "The event handler for `EventStarted` is not implemented, yet"
    )

  override def eventEnded(
      currentState: EventState,
      eventEnded: EventEnded
  ): EventState =
    throw new RuntimeException(
      "The event handler for `EventEnded` is not implemented, yet"
    )

}
