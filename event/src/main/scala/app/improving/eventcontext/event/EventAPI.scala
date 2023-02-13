package app.improving.eventcontext.event

import app.improving.eventcontext.event.EventAPI.{
  JustMemberValidated,
  ValidatedFields,
  ValidatedFieldsWithEventInfo,
  ValidatedFieldsWithReasonAndDuration,
  ValidatedFieldsWithStartAndEnd,
  ValidationNeeded,
  ValidationNeededWithEventInfo,
  ValidationNeededWithJustMember,
  ValidationNeededWithReasonAndDuration,
  ValidationNeededWithStartAndEnd,
  validateApiEventInfo
}
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.eventcontext.infrastructure.util._
import app.improving.{ApiEventId, ApiMemberId, EventId, MemberId}
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
import com.google.protobuf.duration.Duration
import io.grpc.Status
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventAPI(context: EventSourcedEntityContext) extends AbstractEventAPI {
  override def emptyState: EventState = EventState.defaultInstance

  val changeableInfoStatuses: Set[EventStatus] = Set(
    EventStatus.SCHEDULED,
    EventStatus.DELAYED
  ) // TODO: can you change an event's info after it's started?
  val cancellableStatuses: Set[EventStatus] =
    Set(EventStatus.CANCELLED, EventStatus.SCHEDULED)
  val endableStatuses: Set[EventStatus] = Set(EventStatus.INPROGRESS)

  override def changeEventInfo(
      currentState: EventState,
      apiChangeEventInfo: ApiChangeEventInfo
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId.contains(EventId(apiChangeEventInfo.eventId)) => {
        errorOrReply(
          event.status,
          changeableInfoStatuses,
          "change event info",
          ValidationNeededWithEventInfo(
            apiChangeEventInfo.changingMember,
            apiChangeEventInfo.info
          )
        ) { validatedFields =>

          val validatedFieldsWithEventInfo: ValidatedFieldsWithEventInfo =
            validatedFields.asInstanceOf[ValidatedFieldsWithEventInfo]
          val updatingMember: ApiMemberId =
            validatedFieldsWithEventInfo.apiMemberId
          val updatingInfo: ApiEventInfo =
            validatedFieldsWithEventInfo.apiEventInfo
          val newInfo: EventInfo = buildEventInfoFromUpdateInfo(
            currentState.event.flatMap(_.info),
            updatingInfo
          )

          val event: EventInfoChanged = EventInfoChanged(
            eventId = Some(EventId(apiChangeEventInfo.eventId)),
            info = Some(newInfo),
            meta = currentState.event.flatMap(
              _.meta.map(
                _.copy(
                  actualStart = updatingInfo.expectedStart,
                  actualEnd = updatingInfo.expectedEnd,
                  lastModifiedBy =
                    Some(convertApiMemberIdToMemberId(updatingMember)),
                  lastModifiedOn = Some(nowTs)
                )
              )
            )
          )
          effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
        }

      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def scheduleEvent(
      currentState: EventState,
      apiScheduleEvent: ApiScheduleEvent
  ): EventSourcedEntity.Effect[ApiEventId] = {
    currentState.event match {
      case Some(_) => effects.reply(ApiEventId.defaultInstance)
      case _ =>
        errorOrReply(
          EventStatus.SCHEDULED,
          Set(EventStatus.SCHEDULED),
          "schedule event",
          ValidationNeededWithEventInfo(
            apiScheduleEvent.schedulingMember,
            apiScheduleEvent.info
          )
        ) { validatedFields =>
          {
            val validatedFieldsWithEventInfo: ValidatedFieldsWithEventInfo =
              validatedFields.asInstanceOf[ValidatedFieldsWithEventInfo]
            val schedulingMember: MemberId = convertApiMemberIdToMemberId(
              validatedFieldsWithEventInfo.apiMemberId
            )
            val apiEventInfo: ApiEventInfo =
              validatedFieldsWithEventInfo.apiEventInfo
            val timestamp: Timestamp = nowTs

            val eventId: String = apiScheduleEvent.eventId
            val event = EventScheduled(
              eventId = Some(EventId(eventId)),
              info = apiScheduleEvent.info.map(convertApiEventInfoToEventInfo),
              meta = Some(
                EventMetaInfo(
                  Some(schedulingMember),
                  Some(timestamp),
                  Some(schedulingMember),
                  Some(timestamp),
                  apiEventInfo.expectedStart, // TODO: should we keep actualStart and actualEnd as None until the event is actually started/ended?
                  apiEventInfo.expectedEnd,
                  EventStatus.SCHEDULED
                )
              )
            )
            effects.emitEvent(event).thenReply(_ => ApiEventId(eventId))
          }
        }

    }

  }

  override def cancelEvent(
      currentState: EventState,
      apiCancelEvent: ApiCancelEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId.contains(EventId(apiCancelEvent.eventId)) =>
        errorOrReply(
          event.status,
          cancellableStatuses,
          "cancel event",
          ValidationNeededWithJustMember(apiCancelEvent.cancellingMember)
        ) { validatedFields =>
          val validatedFieldsWithJustMember: JustMemberValidated =
            validatedFields.asInstanceOf[JustMemberValidated]
          val metaOpt: Option[EventMetaInfo] = event.meta.map(
            _.copy(
              lastModifiedBy = Some(
                convertApiMemberIdToMemberId(
                  validatedFieldsWithJustMember.apiMemberId
                )
              ),
              lastModifiedOn = Some(nowTs),
              status = EventStatus.CANCELLED
            )
          )

          val cancelled = EventCancelled(
            eventId = event.eventId,
            meta = metaOpt
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
          if event.eventId.contains(EventId(apiRescheduleEvent.eventId)) => {
        errorOrReply(
          event.status,
          changeableInfoStatuses,
          "reschedule event",
          ValidationNeededWithStartAndEnd(
            apiRescheduleEvent.reschedulingMember,
            apiRescheduleEvent.start,
            apiRescheduleEvent.end
          )
        ) { validatedFields =>
          {

            val validatedFieldsWithStartAndEnd: ValidatedFieldsWithStartAndEnd =
              validatedFields.asInstanceOf[ValidatedFieldsWithStartAndEnd]
            val reschedulingMember: ApiMemberId =
              validatedFieldsWithStartAndEnd.apiMemberId
            val start: Timestamp = validatedFieldsWithStartAndEnd.start
            val end: Timestamp = validatedFieldsWithStartAndEnd.end

            val rescheduled: EventRescheduled = EventRescheduled(
              eventId = event.eventId,
              info = event.info.map(
                _.copy(
                  expectedStart = Some(start),
                  expectedEnd = Some(end)
                )
              ),
              meta = event.meta.map(
                _.copy(
                  lastModifiedBy =
                    Some(convertApiMemberIdToMemberId(reschedulingMember)),
                  lastModifiedOn = Some(nowTs)
                )
              )
            )
            effects.emitEvent(rescheduled).thenReply(_ => Empty.defaultInstance)
          }
        }

      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  // TODO if we're setting meta's actualStart and actualEnd to the expected start and end on scheduleEvent and rescheduleEvent, shouldn't we do the same for delayEvent?
  override def delayEvent(
      currentState: EventState,
      apiDelayEvent: ApiDelayEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId.contains(EventId(apiDelayEvent.eventId)) =>
        errorOrReply(
          event.status,
          changeableInfoStatuses,
          "delay event",
          ValidationNeededWithReasonAndDuration(
            maybeApiMemberId = apiDelayEvent.delayingMember,
            maybeReason = Some(apiDelayEvent.reason),
            maybeDuration = apiDelayEvent.expectedDuration
          )
        ) { validatedFields =>
          val validatedFieldsWithReasonAndDuration
              : ValidatedFieldsWithReasonAndDuration =
            validatedFields.asInstanceOf[ValidatedFieldsWithReasonAndDuration]
          val metaOpt = event.meta.map(
            _.copy(
              lastModifiedBy = Some(
                convertApiMemberIdToMemberId(
                  validatedFieldsWithReasonAndDuration.apiMemberId
                )
              ),
              lastModifiedOn = Some(nowTs),
              status = EventStatus.DELAYED
            )
          )

          val delayed = EventDelayed(
            eventId = event.eventId,
            reason = validatedFieldsWithReasonAndDuration.reason,
            meta = metaOpt,
            expectedDuration =
              Some(validatedFieldsWithReasonAndDuration.duration)
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
          if event.eventId.contains(EventId(apiStartEvent.eventId)) =>
        errorOrReply(
          event.status,
          changeableInfoStatuses, // TODO should you allow starting an event if it's already started? you could combine with "actual start" not being defined until the event actually starts, and only set it on start event if it's not already set
          "start event",
          ValidationNeededWithJustMember(apiStartEvent.startingMember)
        ) { validatedFields =>

          val validatedFieldsWithJustMember: JustMemberValidated =
            validatedFields.asInstanceOf[JustMemberValidated]
          val timestamp: Timestamp = nowTs

          val metaOpt: Option[EventMetaInfo] = event.meta.map(
            _.copy(
              lastModifiedOn = Some(timestamp),
              lastModifiedBy = Some(
                convertApiMemberIdToMemberId(
                  validatedFieldsWithJustMember.apiMemberId
                )
              ),
              status = EventStatus.INPROGRESS,
              actualStart = Some(timestamp)
            )
          )

          val started = EventStarted(
            eventId = event.eventId,
            meta = metaOpt
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
          if event.eventId.contains(EventId(apiEndEvent.eventId)) =>
        errorOrReply(
          event.status,
          endableStatuses,
          "end event",
          ValidationNeededWithJustMember(apiEndEvent.endingMember)
        ) { validatedFields =>
          val validatedFieldsWithJustMember: JustMemberValidated =
            validatedFields.asInstanceOf[JustMemberValidated]
          val endingMember: MemberId = convertApiMemberIdToMemberId(
            validatedFieldsWithJustMember.apiMemberId
          )
          val timestamp = nowTs
          val metaOpt = event.meta.map(meta =>
            meta.copy(
              lastModifiedOn = Some(timestamp),
              lastModifiedBy = Some(endingMember),
              status = EventStatus.PAST,
              actualEnd = Some(timestamp)
            )
          )
          val ended = EventEnded(
            eventId = event.eventId,
            meta = metaOpt
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
      case _ =>
        effects.error(
          s"Event By ID ${apiGetEventById.eventId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
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
        currentState.withEvent(
          event.copy(meta = eventCancelled.meta, status = EventStatus.CANCELLED)
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

  private def nowTs = {
    val now = java.time.Instant.now()
    Timestamp.of(now.getEpochSecond, now.getNano)
  }

  private def errorOrReply[A](
      eventStatus: EventStatus,
      allowableStatuses: Set[EventStatus],
      attemptedAction: String,
      fieldsNeeded: ValidationNeeded
  )(
      block: ValidatedFields => EventSourcedEntity.Effect[A]
  ): EventSourcedEntity.Effect[A] = {

    if (allowableStatuses.contains(eventStatus)) {
      fieldsNeeded.toValidatedFields match {
        case Left(missingFields) =>
          effects.error(
            s"Message is missing the following fields: ${missingFields.mkString(", ")}"
          )
        case Right(validatedFields) => block(validatedFields)
      }
    } else effects.error(s"You cannot $attemptedAction from state $eventStatus")
  }

}

object EventAPI {

  private def getMissingApiEventInfoFields(
      maybeApiEventInfo: Option[ApiEventInfo]
  ): Set[String] = {
    maybeApiEventInfo
      .map(apiEventInfo => {
        Map(
          "ApiEventInfo.Name" -> Some(apiEventInfo.eventName).filterNot(
            _.isEmpty
          ),
          "ApiEventInfo.Description" -> Some(apiEventInfo.description)
            .filterNot(_.isEmpty),
          "ApiEventInfo.EventUrl" -> Some(apiEventInfo.eventURL).filterNot(
            _.isEmpty
          ),
          "ApiEventInfo.SponsoringOrg" -> apiEventInfo.sponsoringOrg,
          "ApiEventInfo.GeoLocation" -> apiEventInfo.geoLocation,
          "ApiEventInfo.Reservation" -> apiEventInfo.reservation, //TODO does EventInfo require a reservation ID? It's in here but not in the riddl; on the other hand, the riddl seems to have a sometimes-optional top-level ReservationId field (see eventContext.riddl)
          "ApiEventInfo.ExpectedStart" -> apiEventInfo.expectedStart,
          "ApiEventInfo.ExpectedEnd" -> apiEventInfo.expectedEnd,
          "ApiEventInfo.IsPrivate" -> apiEventInfo.isPrivate
        )
          .filter(_._2.isEmpty)
          .keySet
      })
      .getOrElse(Set.empty[String])
  }

  private def validateApiEventInfo(
      maybeApiEventInfo: Option[ApiEventInfo]
  ): Boolean = {
    getMissingApiEventInfoFields(maybeApiEventInfo).isEmpty
  }

  abstract class ValidationNeeded(maybeApiMemberId: Option[ApiMemberId])
  case class ValidationNeededWithJustMember(
      maybeApiMemberId: Option[ApiMemberId]
  ) extends ValidationNeeded(maybeApiMemberId)
  case class ValidationNeededWithEventInfo(
      maybeApiMemberId: Option[ApiMemberId],
      maybeApiEventInfo: Option[ApiEventInfo]
  ) extends ValidationNeeded(maybeApiMemberId)
  case class ValidationNeededWithStartAndEnd(
      maybeApiMemberId: Option[ApiMemberId],
      maybeStart: Option[Timestamp],
      maybeEnd: Option[Timestamp]
  ) extends ValidationNeeded(maybeApiMemberId)
  case class ValidationNeededWithReasonAndDuration(
      maybeApiMemberId: Option[ApiMemberId],
      maybeReason: Option[String],
      maybeDuration: Option[Duration]
  ) extends ValidationNeeded(maybeApiMemberId)

  object ValidationNeeded {
    implicit class ValidationNeededOps(validationNeeded: ValidationNeeded) {
      def toValidatedFields: Either[Set[String], ValidatedFields] = {
        validationNeeded match {
          case ValidationNeededWithJustMember(maybeApiMemberId) =>
            maybeApiMemberId match {
              case Some(apiMemberId: ApiMemberId) =>
                Right(JustMemberValidated(apiMemberId))
              case _ => Left(Set("Member"))
            }
          case ValidationNeededWithEventInfo(
                maybeApiMemberId,
                maybeApiEventInfo
              ) =>
            (maybeApiMemberId, maybeApiEventInfo) match {
              case (Some(apiMemberId), Some(apiEventInfo))
                  if validateApiEventInfo(maybeApiEventInfo) =>
                Right(ValidatedFieldsWithEventInfo(apiMemberId, apiEventInfo))
              case _ =>
                Left(
                  Map(
                    "Member" -> maybeApiMemberId,
                    "ApiEventInfo" -> maybeApiEventInfo
                  ).filter(_._2.isEmpty).keySet ++ getMissingApiEventInfoFields(
                    maybeApiEventInfo
                  )
                )
            }
          case ValidationNeededWithStartAndEnd(
                maybeApiMemberId,
                maybeStart,
                maybeEnd
              ) =>
            (maybeApiMemberId, maybeStart, maybeEnd) match {
              case (Some(apiMemberId), Some(start), Some(end)) =>
                Right(ValidatedFieldsWithStartAndEnd(apiMemberId, start, end))
              case _ =>
                Left(
                  Map(
                    "Member" -> maybeApiMemberId,
                    "Start" -> maybeStart,
                    "End" -> maybeEnd
                  ).filter(_._2.isEmpty).keySet
                )
            }
          case ValidationNeededWithReasonAndDuration(
                maybeApiMemberId,
                maybeReason,
                maybeDuration
              ) =>
            (maybeApiMemberId, maybeReason, maybeDuration) match {
              case (Some(apiMemberId), Some(reason), Some(duration))
                  if reason.nonEmpty =>
                Right(
                  ValidatedFieldsWithReasonAndDuration(
                    apiMemberId,
                    reason,
                    duration
                  )
                )
              case _ =>
                Left(
                  Map(
                    "Member" -> maybeApiMemberId,
                    "Reason" -> maybeReason.filter(_.nonEmpty),
                    "Duration" -> maybeDuration
                  ).filter(_._2.isEmpty).keySet
                )
            }
        }
      }
    }
  }

  abstract class ValidatedFields(apiMemberId: ApiMemberId)

  case class JustMemberValidated(apiMemberId: ApiMemberId)
      extends ValidatedFields(apiMemberId)

  case class ValidatedFieldsWithEventInfo(
      apiMemberId: ApiMemberId,
      apiEventInfo: ApiEventInfo
  ) extends ValidatedFields(apiMemberId)

  case class ValidatedFieldsWithStartAndEnd(
      apiMemberId: ApiMemberId,
      start: Timestamp,
      end: Timestamp
  ) extends ValidatedFields(apiMemberId)

  case class ValidatedFieldsWithReasonAndDuration(
      apiMemberId: ApiMemberId,
      reason: String,
      duration: Duration
  ) extends ValidatedFields(apiMemberId)

  // private def getMissingApiInfoFields(apiEventInfo: Option[ApiEventInfo]) =

}
