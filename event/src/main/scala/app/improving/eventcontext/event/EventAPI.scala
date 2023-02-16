package app.improving.eventcontext.event

import app.improving.eventcontext.event.EventAPI._
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
  EventReleased,
  EventRescheduled,
  EventScheduled,
  EventStarted,
  EventStatus,
  ReservationAddedToEvent
}
import com.google.protobuf.duration.Duration
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

  // It's possible to add a reservation to an update if you're the status is in the set of statuses in reservablestatuses, update the info if it's in updateableStatuses,
  // reschedule if it's in reschedulableStatuses, etc. Note that status isn't the only constraint: the command fields must be validated, and for delay and start we verify that the reservation has been set.
  val reservableStatuses: Set[EventStatus] = Set(
    EventStatus.EVENT_STATUS_SCHEDULED
  )
  val updateableStatuses: Set[EventStatus] =
    Set(EventStatus.EVENT_STATUS_SCHEDULED, EventStatus.EVENT_STATUS_DELAYED)
  val reschedulableStatuses: Set[EventStatus] =
    Set(
      EventStatus.EVENT_STATUS_SCHEDULED,
      EventStatus.EVENT_STATUS_DELAYED,
      EventStatus.EVENT_STATUS_CANCELLED
    )
  val delayableStatuses: Set[EventStatus] =
    Set(EventStatus.EVENT_STATUS_SCHEDULED, EventStatus.EVENT_STATUS_INPROGRESS)
  val startableStatuses: Set[EventStatus] =
    Set(
      EventStatus.EVENT_STATUS_SCHEDULED,
      EventStatus.EVENT_STATUS_DELAYED
    )
  val cancellableStatuses: Set[EventStatus] =
    Set(EventStatus.EVENT_STATUS_SCHEDULED, EventStatus.EVENT_STATUS_DELAYED)
  val endableStatuses: Set[EventStatus] = Set(
    EventStatus.EVENT_STATUS_INPROGRESS
  )

  override def changeEventInfo(
      currentState: EventState,
      apiChangeEventInfo: ApiChangeEventInfo
  ): EventSourcedEntity.Effect[Empty] = currentState.event match {
    case Some(event) =>
      errorOrReply(
        event.status,
        updateableStatuses,
        "change event info",
        ValidationNeededWithEventUpdateInfo(
          apiChangeEventInfo.changingMember,
          apiChangeEventInfo.info
        )
      ) { validatedFields =>
        log.info(
          s"EventAPI in changeEventInfo - apiChangeEventInfo - $apiChangeEventInfo"
        )
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
    case other =>
      log.info(
        s"EventAPI in changeEventInfo - other - $other"
      )
      effects.reply(Empty.defaultInstance)
  }

  override def scheduleEvent(
      currentState: EventState,
      apiScheduleEvent: ApiScheduleEvent
  ): EventSourcedEntity.Effect[ApiEventId] = currentState.event match {
    case Some(_) =>
      log.info(
        s"EventAPI in scheduleEvent - event already existed - ${currentState.event}"
      )
      effects.error(
        s"Event already exists with id ${apiScheduleEvent.eventId}"
      )
    case _ =>
      errorOrReply(
        EventStatus.EVENT_STATUS_SCHEDULED,
        Set(EventStatus.EVENT_STATUS_SCHEDULED),
        "schedule event",
        ValidationNeededWithEventInfo(
          apiScheduleEvent.schedulingMember,
          apiScheduleEvent.info
        )
      ) { validatedFields =>
        log.info(
          s"EventAPI in scheduleEvent - apiScheduleEvent - $apiScheduleEvent"
        )
        val validatedFieldsWithEventInfo: ValidatedFieldsWithEventInfo =
          validatedFields.asInstanceOf[ValidatedFieldsWithEventInfo]
        val schedulingMember: MemberId = convertApiMemberIdToMemberId(
          validatedFieldsWithEventInfo.apiMemberId
        )
        val apiEventInfo: ApiEventInfo =
          validatedFieldsWithEventInfo.apiEventInfo
        val timestamp: Timestamp = nowTs
        val eventId = Some(EventId(apiScheduleEvent.eventId))
        val event = EventScheduled(
          eventId = eventId,
          info = apiScheduleEvent.info.map(convertApiEventInfoToEventInfo),
          meta = Some(
            EventMetaInfo(
              Some(schedulingMember),
              Some(timestamp),
              Some(schedulingMember),
              Some(timestamp),
              apiScheduleEvent.info.flatMap(_.expectedStart),
              apiScheduleEvent.info.flatMap(_.expectedEnd),
              EventStatus.EVENT_STATUS_SCHEDULED
            )
          )
        )
        effects
          .emitEvent(event)
          .thenReply(_ =>
            eventId
              .map(id => ApiEventId(id.id))
              .getOrElse(ApiEventId.defaultInstance)
          )
      }
  }

  override def cancelEvent(
      currentState: EventState,
      apiCancelEvent: ApiCancelEvent
  ): EventSourcedEntity.Effect[Empty] =
    currentState.event match {
      case Some(event) if currentState.event.exists(_.reservation.nonEmpty) =>
        errorOrReply(
          event.status,
          cancellableStatuses,
          "cancel event",
          ValidationNeededWithJustMember(apiCancelEvent.cancellingMember)
        ) { validatedFields =>

          log.info(
            s"EventAPI in cancelEvent - apiCancelEvent - $apiCancelEvent"
          )
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
              status = EventStatus.EVENT_STATUS_CANCELLED
            )
          )

          val cancelled = EventCancelled(
            eventId = event.eventId,
            meta = metaOpt
          )
          effects.emitEvent(cancelled).thenReply(_ => Empty.defaultInstance)
        }
      case other =>
        log.info(
          s"EventAPI in cancelEvent - other - $other"
        )
        effects.error("State is missing the following fields: Reservation")
    }

  override def rescheduleEvent(
      currentState: EventState,
      apiRescheduleEvent: ApiRescheduleEvent
  ): EventSourcedEntity.Effect[Empty] =
    currentState.event match {
      case Some(event)
          if currentState.event
            .map(_.reservation)
            .exists(_.nonEmpty) =>
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
          log.info(
            s"EventAPI in rescheduleEvent - apiRescheduleEvent - $apiRescheduleEvent"
          )
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
      case Some(_) =>
        effects.error("State is missing the following fields: Reservation")
      case other =>
        log.info(
          s"EventAPI in rescheduleEvent - other - $other"
        )
        effects.reply(Empty.defaultInstance)
    }

  override def delayEvent(
      currentState: EventState,
      apiDelayEvent: ApiDelayEvent
  ): EventSourcedEntity.Effect[Empty] = currentState.event match {
    case Some(event) =>
      errorOrReply(
        event.status,
        delayableStatuses,
        "delay event",
        ValidationNeededWithReasonDurationAndReservation(
          maybeApiMemberId = apiDelayEvent.delayingMember,
          maybeApiReason = Some(apiDelayEvent.reason),
          maybeApiDuration = apiDelayEvent.expectedDuration,
          maybeReservation =
            if (event.reservation.nonEmpty) Some(event.reservation) else None
        )
      ) { validatedFields =>
        log.info(
          s"EventAPI in delayEvent - apiDelayEvent - $apiDelayEvent"
        )
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
            status = EventStatus.EVENT_STATUS_DELAYED
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
    case other =>
      log.info(
        s"EventAPI in delayEvent - other - $other"
      )
      effects.reply(Empty.defaultInstance)
  }

  override def startEvent(
      currentState: EventState,
      apiStartEvent: ApiStartEvent
  ): EventSourcedEntity.Effect[Empty] = currentState.event match {
    case Some(event) =>
      errorOrReply(
        event.status,
        startableStatuses,
        "start event",
        ValidationNeededWithStateReservation(
          apiStartEvent.startingMember,
          if (event.reservation.nonEmpty) Some(event.reservation) else None
        )
      ) { validatedFields =>
        log.info(
          s"EventAPI in startEvent - apiStartEvent - $apiStartEvent"
        )
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
            status = EventStatus.EVENT_STATUS_INPROGRESS,
            actualStart = Some(timestamp)
          )
        )

        val started = EventStarted(
          eventId = event.eventId,
          meta = metaOpt
        )
        effects.emitEvent(started).thenReply(_ => Empty.defaultInstance)

      }
    case other =>
      log.info(
        s"EventAPI in startEvent - other - $other"
      )
      effects.reply(Empty.defaultInstance)
  }

  override def endEvent(
      currentState: EventState,
      apiEndEvent: ApiEndEvent
  ): EventSourcedEntity.Effect[Empty] = currentState.event match {
    case Some(event) =>
      errorOrReply(
        event.status,
        endableStatuses,
        "end event",
        ValidationNeededWithJustMember(apiEndEvent.endingMember)
      ) { validatedFields =>
        log.info(
          s"EventAPI in endEvent - apiEndEvent - $apiEndEvent"
        )
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
            status = EventStatus.EVENT_STATUS_PAST,
            actualEnd = Some(timestamp)
          )
        )
        val ended = EventEnded(
          eventId = event.eventId,
          meta = metaOpt
        )
        effects.emitEvent(ended).thenReply(_ => Empty.defaultInstance)

      }
    case other =>
      log.info(
        s"EventAPI in endEvent - other - $other"
      )
      effects.reply(Empty.defaultInstance)
  }

  override def addReservationToEvent(
      currentState: EventState,
      apiAddReservationToEvent: ApiAddReservationToEvent
  ): EventSourcedEntity.Effect[Empty] = currentState.event match {
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
          Some(apiAddReservationToEvent.reservation)
        )
      ) { validatedFields =>
        val validatedFieldsWithReservation: ValidatedFieldsWithReservation =
          validatedFields.asInstanceOf[ValidatedFieldsWithReservation]
        val reservationAdded = ReservationAddedToEvent(
          eventId = event.eventId,
          reservation = validatedFieldsWithReservation.reservation,
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

  override def addLiveUpdate(
      currentState: EventState,
      apiAddLiveUpdate: ApiAddLiveUpdate
  ): EventSourcedEntity.Effect[Empty] = effects.error(
    "The command handler for `AddLiveUpdate` is not implemented, yet"
  )

  override def getEventById(
      currentState: EventState,
      apiGetEventById: ApiGetEventById
  ): EventSourcedEntity.Effect[ApiEvent] = currentState.event match {
    case Some(event) =>
      log.info(
        s"EventAPI in getEventById - apiGetEventById - $apiGetEventById"
      )
      effects.reply(convertEventToApiEvent(event))
    case other =>
      log.info(
        s"EventAPI in getEventById - other - $other"
      )
      effects.error(
        s"Event By ID ${apiGetEventById.eventId} IS NOT FOUND.",
        Status.Code.NOT_FOUND
      )
  }

  override def eventInfoChanged(
      currentState: EventState,
      eventInfoChanged: EventInfoChanged
  ): EventState = currentState.event match {
    case Some(event) =>
      log.info(
        s"EventAPI in eventInfoChanged - eventInfoChanged - $eventInfoChanged"
      )
      currentState.withEvent(
        event.copy(info = eventInfoChanged.info, meta = eventInfoChanged.meta)
      )
    case other =>
      log.info(
        s"EventAPI in eventInfoChanged - other - $other"
      )
      currentState
  }

  override def eventScheduled(
      currentState: EventState,
      eventScheduled: EventScheduled
  ): EventState = currentState.event match {
    case Some(_) =>
      log.info(
        s"EventAPI in eventScheduled - eventScheduled already existed"
      )
      currentState // event was already scheduled.
    case _ =>
      log.info(
        s"EventAPI in eventScheduled - eventScheduled $eventScheduled"
      )
      currentState.withEvent(
        Event(
          eventScheduled.eventId,
          eventScheduled.info,
          "",
          eventScheduled.meta,
          EventStatus.EVENT_STATUS_SCHEDULED
        )
      )
  }

  override def eventCancelled(
      currentState: EventState,
      eventCancelled: EventCancelled
  ): EventState = currentState.event match {
    case Some(event) if event.eventId == eventCancelled.eventId =>
      log.info(
        s"EventAPI in eventCancelled - eventCancelled $eventCancelled"
      )
      currentState.withEvent(
        event.copy(
          meta = eventCancelled.meta,
          status = EventStatus.EVENT_STATUS_CANCELLED
        )
      )
    case other =>
      log.info(
        s"EventAPI in eventCancelled - other $other"
      )
      currentState
  }

  override def eventRescheduled(
      currentState: EventState,
      eventRescheduled: EventRescheduled
  ): EventState =
    currentState.event match {
      case Some(event) =>
        log.info(
          s"EventAPI in eventRescheduled - eventRescheduled $eventRescheduled"
        )
        currentState.withEvent(
          event.copy(
            info = eventRescheduled.info,
            meta = eventRescheduled.meta,
            status = EventStatus.EVENT_STATUS_SCHEDULED
          )
        )
      case other =>
        log.info(
          s"EventAPI in eventRescheduled - other $other"
        )
        currentState
    }

  override def eventDelayed(
      currentState: EventState,
      eventDelayed: EventDelayed
  ): EventState =
    currentState.event match {
      case Some(event) =>
        log.info(
          s"EventAPI in eventDelayed - eventDelayed $eventDelayed"
        )
        val infoOpt = event.info.map(info =>
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

        currentState.withEvent(
          event.copy(
            info = infoOpt,
            meta = eventDelayed.meta,
            status = EventStatus.EVENT_STATUS_DELAYED
          )
        )
      case other =>
        log.info(
          s"EventAPI in eventDelayed - other $other"
        )
        currentState
    }

  override def eventStarted(
      currentState: EventState,
      eventStarted: EventStarted
  ): EventState =
    currentState.event match {
      case Some(event) if event.eventId == eventStarted.eventId =>
        log.info(
          s"EventAPI in eventStarted - eventStarted $eventStarted"
        )
        currentState.withEvent(
          event.copy(
            meta = eventStarted.meta,
            status = EventStatus.EVENT_STATUS_INPROGRESS
          )
        )
      case other =>
        log.info(
          s"EventAPI in eventStarted - other $other"
        )
        currentState
    }

  override def eventEnded(
      currentState: EventState,
      eventEnded: EventEnded
  ): EventState = currentState.event match {
    case Some(event) =>
      log.info(
        s"EventAPI in eventEnded - eventEnded $eventEnded"
      )
      currentState.withEvent(
        event.copy(
          meta = eventEnded.meta,
          status = EventStatus.EVENT_STATUS_PAST
        )
      )
    case other =>
      log.info(
        s"EventAPI in eventEnded - other $other"
      )

      currentState
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

  override def releaseEvent(
      currentState: EventState,
      apiReleaseEvent: ApiReleaseEvent
  ): EventSourcedEntity.Effect[Empty] = effects
    .emitEvent(
      EventReleased(
        Some(EventId(apiReleaseEvent.eventId)),
        apiReleaseEvent.releasingMember.map(apiId => MemberId(apiId.memberId))
      )
    )
    .deleteEntity()
    .thenReply(_ => Empty.defaultInstance)

  override def eventReleased(
      currentState: EventState,
      eventReleased: EventReleased
  ): EventState = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

    currentState.copy(event =
      currentState.event.map(
        _.copy(
          status = EventStatus.EVENT_STATUS_RELEASED,
          meta = currentState.event.flatMap(
            _.meta.map(
              _.copy(
                lastModifiedBy = eventReleased.releasingMember,
                lastModifiedOn = Some(timestamp)
              )
            )
          )
        )
      )
    )
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
  ): Set[String] = maybeApiEventInfo
    .map(apiEventInfo =>
      Map(
        "ApiEventInfo.Name" -> Some(apiEventInfo.eventName).filterNot(
          _.isEmpty
        ),
        "ApiEventInfo.Description" -> Some(apiEventInfo.description)
          .filterNot(_.isEmpty),
        "ApiEventInfo.EventUrl" -> Some(apiEventInfo.eventUrl).filterNot(
          _.isEmpty
        ),
        "ApiEventInfo.SponsoringOrg" -> apiEventInfo.sponsoringOrg,
        "ApiEventInfo.ExpectedStart" -> apiEventInfo.expectedStart,
        "ApiEventInfo.ExpectedEnd" -> apiEventInfo.expectedEnd
      )
        .filter(_._2.isEmpty)
        .keySet
    )
    .getOrElse(Set.empty[String])

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
      maybeReservation: Option[String]
  ) extends ValidationNeeded

  case class ValidationNeededWithApiReservation(
      maybeApiMemberId: Option[ApiMemberId],
      maybeApiReservation: Option[String]
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
      maybeReservation: Option[String]
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
                  ValidatedFieldsWithEventUpdateInfo(
                    apiMemberId,
                    apiEventInfo
                  )
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
                Right(
                  ValidatedFieldsWithReservation(apiMemberId, reservation)
                )
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
                    apiReservation
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
      reservation: String
  ) extends ValidatedFields(apiMemberId)

  case class ValidatedFieldsWithReservation(
      apiMemberId: ApiMemberId,
      reservation: String
  ) extends ValidatedFields(apiMemberId)
}
