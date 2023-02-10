package app.improving.eventcontext.event

import app.improving.eventcontext.event.EventAPI.{
  ValidatedFields,
  ValidatedFieldsWithEventInfo,
  ValidatedFieldsWithStartAndEnd,
  ValidationNeeded,
  ValidationNeededWithEventInfo,
  ValidationNeededWithStartAndEnd
}
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.eventcontext.infrastructure.util._
import app.improving.{ApiEventId, ApiMemberId, EventId, MemberId}
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
          if state.eventId.contains(EventId(apiChangeEventInfo.eventId)) => {
        errorOrReply(
          ValidationNeededWithEventInfo(
            apiChangeEventInfo.changingMember,
            apiChangeEventInfo.info
          )
        ) { validatedFields =>

          val validatedFieldsWithEventInfo =
            validatedFields.asInstanceOf[ValidatedFieldsWithEventInfo]
          val updatingMember = validatedFieldsWithEventInfo.apiMemberId
          val updatingInfo = validatedFieldsWithEventInfo.apiEventInfo

          val event = EventInfoChanged(
            Some(EventId(apiChangeEventInfo.eventId)),
            Some(
              buildEventInfoFromUpdateInfo(
                currentState.event.flatMap(_.info),
                updatingInfo
              )
            ),
            currentState.event.flatMap(
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
          ValidationNeededWithEventInfo(
            apiScheduleEvent.schedulingMember,
            apiScheduleEvent.info
          )
        ) { validatedFields =>
          {
            val validatedFieldsWithEventInfo =
              validatedFields.asInstanceOf[ValidatedFieldsWithEventInfo]
            val schedulingMember = convertApiMemberIdToMemberId(
              validatedFieldsWithEventInfo.apiMemberId
            )
            val apiEventInfo = validatedFieldsWithEventInfo.apiEventInfo
            val timestamp = nowTs

            val eventId = apiScheduleEvent.eventId
            val event = EventScheduled(
              Some(EventId(eventId)),
              apiScheduleEvent.info.map(convertApiEventInfoToEventInfo),
              Some(
                EventMetaInfo(
                  Some(schedulingMember),
                  Some(timestamp),
                  Some(schedulingMember),
                  Some(timestamp),
                  apiEventInfo.expectedStart,
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
          if event.eventId.contains(EventId(apiRescheduleEvent.eventId)) => {
        errorOrReply(
          ValidationNeededWithStartAndEnd(
            apiRescheduleEvent.reschedulingMember,
            apiRescheduleEvent.start,
            apiRescheduleEvent.end
          )
        ) { validatedFields =>
          {

            val validatedFieldsWithStartAndEnd =
              validatedFields.asInstanceOf[ValidatedFieldsWithStartAndEnd]
            val reschedulingMember = validatedFieldsWithStartAndEnd.apiMemberId
            val start = validatedFieldsWithStartAndEnd.start
            val end = validatedFieldsWithStartAndEnd.end

            val rescheduled = EventRescheduled(
              event.eventId,
              event.info.map(
                _.copy(
                  expectedStart = Some(start),
                  expectedEnd = Some(end)
                )
              ),
              event.meta.map(
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

  private def nowTs = {
    val now = java.time.Instant.now()
    Timestamp.of(now.getEpochSecond, now.getNano)
  }

  private def errorOrReply[A](fieldsNeeded: ValidationNeeded)(
      block: ValidatedFields => EventSourcedEntity.Effect[A]
  ): EventSourcedEntity.Effect[A] = {
    fieldsNeeded.toValidatedFields match {
      case Left(missingFields) =>
        effects.error(
          s"Message is missing the following fields: ${missingFields.mkString(", ")}"
        )
      case Right(validatedFields) => block(validatedFields)
    }
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
          "ApiEventInfo.Reservation" -> apiEventInfo.reservation,
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
