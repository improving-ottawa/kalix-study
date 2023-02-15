package app.improving.eventcontext.event

import app.improving.eventcontext.event.EventAPI.{
  JustMemberValidated,
  ValidatedFields,
  ValidatedFieldsWithEventInfo,
  ValidatedFieldsWithEventUpdateInfo,
  ValidatedFieldsWithReasonDurationAndReservation,
  ValidatedFieldsWithReservation,
  ValidatedFieldsWithStartAndEnd,
  ValidationNeeded,
  ValidationNeededWithApiReservation,
  ValidationNeededWithEventInfo,
  ValidationNeededWithEventUpdateInfo,
  ValidationNeededWithJustMember,
  ValidationNeededWithReasonDurationAndReservation,
  ValidationNeededWithStartAndEnd,
  ValidationNeededWithStateReservation,
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
  EventStatus,
  ReservationAddedToEvent,
  ReservationId
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

  //It's possible to add a reservation to an update if you're the status is in the set of statuses in reservablestatuses, update the info if it's in updateableStatuses,
  //reschedule if it's in reschedulableStatuses, etc. Note that status isn't the only constraint: the command fields must be validated, and for delay and start we verify that the reservation has been set.
  val reservableStatuses: Set[EventStatus] = Set(EventStatus.SCHEDULED)
  val updateableStatuses: Set[EventStatus] =
    Set(EventStatus.SCHEDULED, EventStatus.DELAYED)
  val reschedulableStatuses: Set[EventStatus] =
    Set(EventStatus.SCHEDULED, EventStatus.DELAYED, EventStatus.CANCELLED)
  val delayableStatuses: Set[EventStatus] =
    Set(EventStatus.SCHEDULED, EventStatus.INPROGRESS)
  val startableStatuses: Set[EventStatus] =
    Set(EventStatus.SCHEDULED, EventStatus.DELAYED)
  val cancellableStatuses: Set[EventStatus] =
    Set(EventStatus.SCHEDULED, EventStatus.DELAYED)
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
          updateableStatuses,
          "change event info",
          ValidationNeededWithEventUpdateInfo(
            apiChangeEventInfo.changingMember,
            apiChangeEventInfo.info
          )
        ) { validatedFields =>

          val validatedFieldsWithEventUpdateInfo
              : ValidatedFieldsWithEventUpdateInfo =
            validatedFields.asInstanceOf[ValidatedFieldsWithEventUpdateInfo]
          val updatingMember: ApiMemberId =
            validatedFieldsWithEventUpdateInfo.apiMemberId
          val updatingInfo: ApiEventUpdateInfo =
            validatedFieldsWithEventUpdateInfo.apiEventUpdateInfo
          val newInfo: Option[EventInfo] =
            event.info.map(buildEventInfoFromUpdateInfo(_, updatingInfo))
          val infoChanged: EventInfoChanged = EventInfoChanged(
            eventId = Some(EventId(apiChangeEventInfo.eventId)),
            info = newInfo,
            meta = event.meta.map(
              _.copy(
                actualStart = updatingInfo.expectedStart,
                actualEnd = updatingInfo.expectedEnd,
                lastModifiedBy =
                  Some(convertApiMemberIdToMemberId(updatingMember)),
                lastModifiedOn = Some(nowTs)
              )
            )
          )
          effects.emitEvent(infoChanged).thenReply(_ => Empty.defaultInstance)
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
          reschedulableStatuses,
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
          delayableStatuses,
          "delay event",
          ValidationNeededWithReasonDurationAndReservation(
            maybeApiMemberId = apiDelayEvent.delayingMember,
            maybeApiReason = Some(apiDelayEvent.reason),
            maybeApiDuration = apiDelayEvent.expectedDuration,
            maybeReservation = event.reservation
          )
        ) { validatedFields =>
          val validatedFieldsWithReasonDurationAndReservation
              : ValidatedFieldsWithReasonDurationAndReservation =
            validatedFields
              .asInstanceOf[ValidatedFieldsWithReasonDurationAndReservation]
          val metaOpt = event.meta.map(
            _.copy(
              lastModifiedBy = Some(
                convertApiMemberIdToMemberId(
                  validatedFieldsWithReasonDurationAndReservation.apiMemberId
                )
              ),
              lastModifiedOn = Some(nowTs),
              status = EventStatus.DELAYED
            )
          )

          val delayed = EventDelayed(
            eventId = event.eventId,
            reason = validatedFieldsWithReasonDurationAndReservation.apiReason,
            meta = metaOpt,
            expectedDuration =
              Some(validatedFieldsWithReasonDurationAndReservation.apiDuration)
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
          startableStatuses,
          "start event",
          ValidationNeededWithStateReservation(
            apiStartEvent.startingMember,
            event.reservation
          )
        ) { validatedFields =>

          val validatedFieldsWithReservation: ValidatedFieldsWithReservation =
            validatedFields.asInstanceOf[ValidatedFieldsWithReservation]
          val timestamp: Timestamp = nowTs

          val metaOpt: Option[EventMetaInfo] = event.meta.map(
            _.copy(
              lastModifiedOn = Some(timestamp),
              lastModifiedBy = Some(
                convertApiMemberIdToMemberId(
                  validatedFieldsWithReservation.apiMemberId
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

  override def addReservationToEvent(
      currentState: EventState,
      apiAddReservationToEvent: ApiAddReservationToEvent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.event match {
      case Some(event)
          if event.eventId.contains(
            EventId(apiAddReservationToEvent.eventId)
          ) =>
        errorOrReply(
          event.status,
          reservableStatuses,
          "add reservation to event",
          ValidationNeededWithApiReservation(
            apiAddReservationToEvent.reservingMember,
            apiAddReservationToEvent.reservation
          )
        ) { validatedFields =>
          val validatedFieldsWithReservation: ValidatedFieldsWithReservation =
            validatedFields.asInstanceOf[ValidatedFieldsWithReservation]
          val reservationAdded = ReservationAddedToEvent(
            eventId = event.eventId,
            reservation = Some(validatedFieldsWithReservation.reservation),
            meta = event.meta.map(
              _.copy(
                lastModifiedBy = Some(
                  convertApiMemberIdToMemberId(
                    validatedFieldsWithReservation.apiMemberId
                  )
                ),
                lastModifiedOn = Some(nowTs)
              )
            )
          )
          effects
            .emitEvent(reservationAdded)
            .thenReply(_ => Empty.defaultInstance)
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
            None,
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

  override def reservationAddedToEvent(
      currentState: EventState,
      reservationAddedToEvent: ReservationAddedToEvent
  ): EventState = {
    currentState.event match {
      case Some(event) if event.eventId == reservationAddedToEvent.eventId =>
        currentState.withEvent(
          event.copy(
            reservation = reservationAddedToEvent.reservation,
            meta = reservationAddedToEvent.meta
          )
        )
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
          val (messageFields, stateFields) =
            missingFields.partition(_.startsWith("Api"))
          val missingMessage = messageFields.headOption.map(_ =>
            s"Message is missing the following fields: ${messageFields.toSeq.sorted.mkString(", ")}"
          )
          val missingState = stateFields.headOption.map(_ =>
            s"State is missing the following fields: ${stateFields.toSeq.sorted.mkString(", ")}"
          )
          val error: String =
            (missingMessage.toList ++ missingState.toList).mkString("\n")
          effects.error(
            error
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
          "ApiEventInfo.ExpectedStart" -> apiEventInfo.expectedStart,
          "ApiEventInfo.ExpectedEnd" -> apiEventInfo.expectedEnd
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

  sealed trait ValidationNeeded
  case class ValidationNeededWithJustMember(
      maybeApiMemberId: Option[ApiMemberId]
  ) extends ValidationNeeded
  case class ValidationNeededWithEventInfo(
      maybeApiMemberId: Option[ApiMemberId],
      maybeApiEventInfo: Option[ApiEventInfo]
  ) extends ValidationNeeded

  case class ValidationNeededWithStateReservation(
      maybeApiMemberId: Option[ApiMemberId],
      maybeReservation: Option[ReservationId]
  ) extends ValidationNeeded

  case class ValidationNeededWithApiReservation(
      maybeApiMemberId: Option[ApiMemberId],
      maybeApiReservation: Option[ApiReservationId]
  ) extends ValidationNeeded

  case class ValidationNeededWithEventUpdateInfo(
      maybeApiMemberId: Option[ApiMemberId],
      maybeApiEventUpdateInfo: Option[ApiEventUpdateInfo]
  ) extends ValidationNeeded
  case class ValidationNeededWithStartAndEnd(
      maybeApiMemberId: Option[ApiMemberId],
      maybeStart: Option[Timestamp],
      maybeEnd: Option[Timestamp]
  ) extends ValidationNeeded
  case class ValidationNeededWithReasonDurationAndReservation(
      maybeApiMemberId: Option[ApiMemberId],
      maybeApiReason: Option[String],
      maybeApiDuration: Option[Duration],
      maybeReservation: Option[ReservationId]
  ) extends ValidationNeeded

  object ValidationNeeded {
    implicit class MessageValidationNeededOps(
        validationNeeded: ValidationNeeded
    ) {
      def toValidatedFields: Either[Set[String], ValidatedFields] = {
        validationNeeded match {
          case ValidationNeededWithJustMember(maybeApiMemberId) =>
            maybeApiMemberId match {
              case Some(apiMemberId: ApiMemberId) =>
                Right(JustMemberValidated(apiMemberId))
              case _ => Left(Set("ApiMember"))
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
                    "ApiMember" -> maybeApiMemberId,
                    "ApiEventInfo" -> maybeApiEventInfo
                  ).filter(_._2.isEmpty).keySet ++ getMissingApiEventInfoFields(
                    maybeApiEventInfo
                  )
                )
            }
          case ValidationNeededWithEventUpdateInfo(
                maybeApiMemberId,
                maybeApiEventInfo
              ) =>
            (maybeApiMemberId, maybeApiEventInfo) match {
              case (Some(apiMemberId), Some(apiEventInfo)) =>
                Right(
                  ValidatedFieldsWithEventUpdateInfo(apiMemberId, apiEventInfo)
                )
              case _ =>
                Left(
                  Map(
                    "ApiMember" -> maybeApiMemberId,
                    "ApiEventInfo" -> maybeApiEventInfo
                  ).filter(_._2.isEmpty).keySet
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
                    "ApiMember" -> maybeApiMemberId,
                    "ApiStart" -> maybeStart,
                    "ApiEnd" -> maybeEnd
                  ).filter(_._2.isEmpty).keySet
                )
            }
          case ValidationNeededWithReasonDurationAndReservation(
                maybeApiMemberId,
                maybeReason,
                maybeDuration,
                maybeReservation
              ) =>
            (
              maybeApiMemberId,
              maybeReason,
              maybeDuration,
              maybeReservation
            ) match {
              case (
                    Some(apiMemberId),
                    Some(apiReason),
                    Some(apiDuration),
                    Some(reservation)
                  ) if apiReason.nonEmpty =>
                Right(
                  ValidatedFieldsWithReasonDurationAndReservation(
                    apiMemberId,
                    apiReason,
                    apiDuration,
                    reservation
                  )
                )
              case _ =>
                Left(
                  Map(
                    "ApiMember" -> maybeApiMemberId,
                    "ApiReason" -> maybeReason.filter(_.nonEmpty),
                    "ApiDuration" -> maybeDuration,
                    "Reservation" -> maybeReservation
                  ).filter(_._2.isEmpty).keySet
                )
            }
          case ValidationNeededWithStateReservation(
                maybeApiMemberId,
                maybeReservation
              ) =>
            (maybeApiMemberId, maybeReservation) match {
              case (Some(apiMemberId), Some(reservation)) =>
                Right(ValidatedFieldsWithReservation(apiMemberId, reservation))
              case _ =>
                Left(
                  Map(
                    "ApiMember" -> maybeApiMemberId,
                    "Reservation" -> maybeReservation
                  ).filter(_._2.isEmpty).keySet
                )
            }
          case ValidationNeededWithApiReservation(
                maybeApiMemberId,
                maybeApiReservation
              ) =>
            (maybeApiMemberId, maybeApiReservation) match {
              case (Some(apiMemberId), Some(apiReservation)) =>
                Right(
                  ValidatedFieldsWithReservation(
                    apiMemberId,
                    convertApiReservationIdToReservationId(apiReservation)
                  )
                )
              case _ =>
                Left(
                  Map(
                    "ApiMember" -> maybeApiMemberId,
                    "ApiReservation" -> maybeApiReservation
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

  case class ValidatedFieldsWithEventUpdateInfo(
      apiMemberId: ApiMemberId,
      apiEventUpdateInfo: ApiEventUpdateInfo
  ) extends ValidatedFields(apiMemberId)

  case class ValidatedFieldsWithStartAndEnd(
      apiMemberId: ApiMemberId,
      start: Timestamp,
      end: Timestamp
  ) extends ValidatedFields(apiMemberId)

  case class ValidatedFieldsWithReasonDurationAndReservation(
      apiMemberId: ApiMemberId,
      apiReason: String,
      apiDuration: Duration,
      reservation: ReservationId
  ) extends ValidatedFields(apiMemberId)

  case class ValidatedFieldsWithReservation(
      apiMemberId: ApiMemberId,
      reservation: ReservationId
  ) extends ValidatedFields(apiMemberId)

}
