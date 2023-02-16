package app.improving.eventcontext.event

import com.google.protobuf.duration.Duration
import com.google.protobuf.timestamp.Timestamp
import app.improving.{
  ApiEventId,
  ApiGeoLocation,
  ApiMemberId,
  ApiOrganizationId,
  EventId,
  MemberId,
  OrganizationId
}
import app.improving.eventcontext.{
  EventInfo,
  EventMetaInfo,
  EventScheduled,
  EventStatus,
  ReservationAddedToEvent
}
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import TestData._
import app.improving.eventcontext.infrastructure.util.{
  convertApiEventInfoToEventInfo,
  convertApiMemberIdToMemberId
}
import com.google.protobuf.empty.Empty
// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventAPISpec extends AnyWordSpec with Matchers {

  trait WithTestKit {
    val testKit: EventAPITestKit = EventAPITestKit(new EventAPI(_))
  }

  trait ScheduledAndReserved extends WithTestKit {
    testKit.scheduleEvent(apiScheduleEvent)
    testKit.addReservationToEvent(apiAddReservationToEvent)
  }

  "The EventAPI" should {

    "correctly process commands of type ChangeEventInfo" in new ScheduledAndReserved {

      val eventIdOpt: Option[EventId] =
        testKit.currentState.event.flatMap(_.eventId)

      val changeEventInfo: ApiChangeEventInfo = ApiChangeEventInfo(
        eventIdOpt.getOrElse(EventId.defaultInstance).id,
        Some(
          ApiEventUpdateInfo(
            Some("postponed-try-out"),
            Some("postponed school footbal try out"),
            Some("www.nowhere.com"),
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(start),
            Some(end),
            Some(false)
          )
        ),
        Some(ApiMemberId("2"))
      )

      val changeEventInfoResult: EventSourcedResult[Empty] =
        testKit.changeEventInfo(changeEventInfo)

      changeEventInfoResult.events should have size 1

      val infoOpt: Option[EventInfo] =
        testKit.currentState.event.flatMap(_.info)

      infoOpt.map(_.eventName) shouldBe changeEventInfo.info.flatMap(
        _.eventName
      )

    }

    "only overwrite fields filled in on ChangeEventInfo" in new ScheduledAndReserved {
      val newInfo: ApiEventUpdateInfo = ApiEventUpdateInfo(
        eventName = Some(""),
        eventUrl = Some(""),
        description = None,
        sponsoringOrg = Some(ApiOrganizationId("organization 121")),
        geoLocation = None,
        expectedStart = None,
        expectedEnd = None,
        isPrivate = Some(true)
      )

      val result: EventSourcedResult[Empty] = testKit.changeEventInfo(
        ApiChangeEventInfo(
          eventId = apiScheduleEvent.eventId,
          info = Some(newInfo),
          changingMember = Some(ApiMemberId("member 25"))
        )
      )

      result.isReply shouldBe true
      result.reply shouldBe Empty.defaultInstance

      val oldInfo: EventInfo =
        convertApiEventInfoToEventInfo(apiScheduleEvent.info.get)
      val updatedInfo: EventInfo =
        result.updatedState.asInstanceOf[EventState].event.get.info.get
      updatedInfo.eventName shouldBe oldInfo.eventName
      updatedInfo.eventUrl shouldBe oldInfo.eventUrl
      updatedInfo.description shouldBe oldInfo.description
      updatedInfo.sponsoringOrg shouldBe Some(
        OrganizationId("organization 121")
      )
      updatedInfo.geoLocation shouldBe oldInfo.geoLocation
      updatedInfo.expectedStart shouldBe oldInfo.expectedStart
      updatedInfo.expectedEnd shouldBe oldInfo.expectedEnd
      updatedInfo.isPrivate shouldBe true

    }

    "refuse to delay, reschedule, or cancel an event without a reservation" in new WithTestKit {
      testKit.scheduleEvent(apiScheduleEvent)

      val delayResult: EventSourcedResult[Empty] =
        testKit.delayEvent(apiDelayEvent)
      delayResult.isError shouldBe true
      delayResult.errorDescription shouldBe "State is missing the following fields: Reservation"

      val rescheduleResult: EventSourcedResult[Empty] =
        testKit.rescheduleEvent(apiRescheduleEvent)
      rescheduleResult.isError shouldBe true
      rescheduleResult.errorDescription shouldBe "State is missing the following fields: Reservation"

      val cancelResult: EventSourcedResult[Empty] =
        testKit.cancelEvent(apiCancelEvent)
      cancelResult.isError shouldBe true
      cancelResult.errorDescription shouldBe "State is missing the following fields: Reservation"
    }

    "correctly process commands of type ScheduleEvent" in new WithTestKit {

      val result: EventSourcedResult[ApiEventId] =
        testKit.scheduleEvent(apiScheduleEvent)

      result.events should have size 1

      val next: EventScheduled = result.nextEvent[EventScheduled]

      next.info.map(_.eventName) shouldBe apiScheduleEvent.info.map(_.eventName)

      val eventIdOpt: Option[EventId] =
        testKit.currentState.event.flatMap(_.eventId)

      eventIdOpt.isDefined shouldBe true
    }

    "correctly process commands of type CancelEvent" in new ScheduledAndReserved {

      val cancelled: ApiCancelEvent = apiCancelEvent

      val cancelledEventInfoResult: EventSourcedResult[Empty] =
        testKit.cancelEvent(cancelled)

      cancelledEventInfoResult.events should have size 1

      val statusOpt: Option[EventStatus] =
        testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.EVENT_STATUS_CANCELLED)

      val metaOpt: Option[EventMetaInfo] =
        testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))
    }

    "correctly process commands of type RescheduleEvent" in new ScheduledAndReserved {
      val newStart: Timestamp = apiRescheduleEvent.start.get
      val newEnd: Timestamp = apiRescheduleEvent.end.get

      val rescheduled: ApiRescheduleEvent = apiRescheduleEvent

      val rescheduledEventInfoResult: EventSourcedResult[Empty] =
        testKit.rescheduleEvent(rescheduled)

      rescheduledEventInfoResult.events should have size 1

      val statusOpt: Option[EventStatus] =
        testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.EVENT_STATUS_SCHEDULED)

      val rescheduledStart: Option[Timestamp] =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedStart)

      rescheduledStart shouldEqual Some(newStart)

      val rescheduledEnd: Option[Timestamp] =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedEnd)

      rescheduledEnd shouldEqual Some(newEnd)

      val metaOpt: Option[EventMetaInfo] =
        testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))
    }

    "correctly process commands of type DelayEvent" in new ScheduledAndReserved {

      val delayed: ApiDelayEvent = apiDelayEvent

      val delayedEventInfoResult: EventSourcedResult[Empty] =
        testKit.delayEvent(delayed)

      delayedEventInfoResult.events should have size 1

      val statusOpt: Option[EventStatus] =
        testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.EVENT_STATUS_DELAYED)

      val delayedStart: Option[Timestamp] =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedStart)

      val newStart: Timestamp =
        Timestamp.of(start.seconds + expectedDuration.seconds, start.nanos)
      val newEnd: Timestamp =
        Timestamp.of(end.seconds + expectedDuration.seconds, end.nanos)

      delayedStart shouldEqual Some(newStart)

      val delayedEnd: Option[Timestamp] =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedEnd)

      delayedEnd shouldEqual Some(newEnd)

      val metaOpt: Option[EventMetaInfo] =
        testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))
    }

    "correctly process commands of type AddReservationToEvent" in new WithTestKit {
      val startResult: EventSourcedResult[ApiEventId] =
        testKit.scheduleEvent(apiScheduleEvent)
      startResult.updatedState
        .asInstanceOf[EventState]
        .event
        .get
        .reservation shouldBe ""

      val result: EventSourcedResult[Empty] =
        testKit.addReservationToEvent(apiAddReservationToEvent)
      result.reply shouldBe Empty.defaultInstance

      val event: ReservationAddedToEvent =
        result.nextEvent[ReservationAddedToEvent]
      event.eventId shouldBe Some(EventId(apiScheduleEvent.eventId))
      event.meta.get.lastModifiedBy.get shouldBe convertApiMemberIdToMemberId(
        apiAddReservationToEvent.reservingMember.get
      )
      event.reservation shouldBe apiAddReservationToEvent.reservation

      result.updatedState
        .asInstanceOf[EventState]
        .event
        .get
        .reservation shouldBe apiAddReservationToEvent.reservation
    }

    "correctly process commands of type StartEvent" in new ScheduledAndReserved {

      val eventIdOpt: Option[EventId] =
        testKit.currentState.event.flatMap(_.eventId)

      val started: ApiStartEvent = ApiStartEvent(
        eventIdOpt.getOrElse(EventId.defaultInstance).id,
        Some(ApiMemberId("2"))
      )

      val startedEventInfoResult: EventSourcedResult[Empty] =
        testKit.startEvent(started)

      startedEventInfoResult.events should have size 1

      val statusOpt: Option[EventStatus] =
        testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.EVENT_STATUS_INPROGRESS)

      val metaOpt: Option[EventMetaInfo] =
        testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))

      val actualStart: Timestamp =
        metaOpt.flatMap(_.actualStart).getOrElse(Timestamp.defaultInstance)

      ((actualStart.seconds * 1000000000) + actualStart.nanos > (start.seconds * 1000000000) + start.nanos) shouldBe true
    }

    "if event status is scheduled, reject an end event command" in new ScheduledAndReserved {
      val result: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)
      result.errorDescription shouldBe "You cannot end event from state EVENT_STATUS_SCHEDULED"
    }

    "if event status is delayed, reject an end event command" in new ScheduledAndReserved {
      testKit.delayEvent(apiDelayEvent)
      val result: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)
      result.errorDescription shouldBe "You cannot end event from state EVENT_STATUS_DELAYED"
    }

    "if event status is cancelled, reject all commands other than reschedule" in new ScheduledAndReserved {
      testKit.cancelEvent(apiCancelEvent)

      val scheduleResult: EventSourcedResult[ApiEventId] =
        testKit.scheduleEvent(apiScheduleEvent)
      val delayResult: EventSourcedResult[Empty] =
        testKit.delayEvent(apiDelayEvent)
      val startResult: EventSourcedResult[Empty] =
        testKit.startEvent(apiStartEvent)
      val endResult: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)
      val cancelResult: EventSourcedResult[Empty] =
        testKit.cancelEvent(apiCancelEvent)

      scheduleResult.errorDescription shouldBe s"Event already exists with id ${apiScheduleEvent.eventId}"
      cancelResult.errorDescription shouldBe "You cannot cancel event from state EVENT_STATUS_CANCELLED"
      delayResult.errorDescription shouldBe "You cannot delay event from state EVENT_STATUS_CANCELLED"
      startResult.errorDescription shouldBe "You cannot start event from state EVENT_STATUS_CANCELLED"
      endResult.errorDescription shouldBe "You cannot end event from state EVENT_STATUS_CANCELLED"

      val rescheduleResult: EventSourcedResult[Empty] =
        testKit.rescheduleEvent(apiRescheduleEvent)
      rescheduleResult.isReply shouldBe true
      rescheduleResult.reply shouldBe Empty.defaultInstance
    }

    "if event status is in progress, reject all commands other than end event or delay event" in new ScheduledAndReserved {
      testKit.startEvent(apiStartEvent)

      val scheduleResult: EventSourcedResult[ApiEventId] =
        testKit.scheduleEvent(apiScheduleEvent)
      val rescheduleResult: EventSourcedResult[Empty] =
        testKit.rescheduleEvent(apiRescheduleEvent)
      val startResult: EventSourcedResult[Empty] =
        testKit.startEvent(apiStartEvent)
      val cancelResult: EventSourcedResult[Empty] =
        testKit.cancelEvent(apiCancelEvent)

      scheduleResult.errorDescription shouldBe s"Event already exists with id ${apiScheduleEvent.eventId}"
      rescheduleResult.errorDescription shouldBe "You cannot reschedule event from state EVENT_STATUS_INPROGRESS"
      cancelResult.errorDescription shouldBe "You cannot cancel event from state EVENT_STATUS_INPROGRESS"
      startResult.errorDescription shouldBe "You cannot start event from state EVENT_STATUS_INPROGRESS"

      val delayResult: EventSourcedResult[Empty] =
        testKit.delayEvent(apiDelayEvent)
      delayResult.isReply shouldBe true
      delayResult.reply shouldBe Empty.defaultInstance

      testKit.startEvent(apiStartEvent)

      val endResult: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)
      endResult.isReply shouldBe true
      endResult.reply shouldBe Empty.defaultInstance
    }

    "if event status is past, reject all commands" in new ScheduledAndReserved {
      testKit.startEvent(apiStartEvent)
      testKit.endEvent(apiEndEvent)

      val scheduleResult: EventSourcedResult[ApiEventId] =
        testKit.scheduleEvent(apiScheduleEvent)
      val rescheduleResult: EventSourcedResult[Empty] =
        testKit.rescheduleEvent(apiRescheduleEvent)
      val delayResult: EventSourcedResult[Empty] =
        testKit.delayEvent(apiDelayEvent)
      val cancelResult: EventSourcedResult[Empty] =
        testKit.cancelEvent(apiCancelEvent)
      val startResult: EventSourcedResult[Empty] =
        testKit.startEvent(apiStartEvent)
      val endResult: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)

      scheduleResult.errorDescription shouldBe s"Event already exists with id ${apiScheduleEvent.eventId}"
      rescheduleResult.errorDescription shouldBe "You cannot reschedule event from state EVENT_STATUS_PAST"
      delayResult.errorDescription shouldBe "You cannot delay event from state EVENT_STATUS_PAST"
      cancelResult.errorDescription shouldBe "You cannot cancel event from state EVENT_STATUS_PAST"
      startResult.errorDescription shouldBe "You cannot start event from state EVENT_STATUS_PAST"
      endResult.errorDescription shouldBe "You cannot end event from state EVENT_STATUS_PAST"
    }

    "correctly process commands of type EndEvent" in new ScheduledAndReserved {

      testKit.startEvent(apiStartEvent)

      val endedEventInfoResult: EventSourcedResult[Empty] =
        testKit.endEvent(apiEndEvent)

      endedEventInfoResult.events should have size 1

      val statusOpt: Option[EventStatus] =
        testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.EVENT_STATUS_PAST)

      val metaOpt: Option[EventMetaInfo] =
        testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))

      metaOpt.flatMap(_.actualStart) shouldBe defined

    }

    "correctly process commands of type AddLiveUpdate" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.addLiveUpdate(event.ApiAddLiveUpdate(...))
    }

    "Reject a schedule event command with missing fields but not require optional fields" in {
      val testKit = EventAPITestKit(new EventAPI(_))

      val missingName = apiScheduleEvent.info.map(_.copy(eventName = ""))
      val missingDescription =
        apiScheduleEvent.info.map(_.copy(description = ""))
      val missingUrl = apiScheduleEvent.info.map(_.copy(eventUrl = ""))
      val missingSponsor =
        apiScheduleEvent.info.map(_.copy(sponsoringOrg = None))
      val missingStart = apiScheduleEvent.info.map(_.copy(expectedStart = None))
      val missingEnd = apiScheduleEvent.info.map(_.copy(expectedEnd = None))
      val missingThreeFields = apiScheduleEvent.info.map(
        _.copy(expectedEnd = None, expectedStart = None, sponsoringOrg = None)
      )

      val missingNameResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingName))
      val missingDescrResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingDescription))
      val missingUrlResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingUrl))
      val missingSponsorResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingSponsor))
      val missingStartResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingStart))
      val missingEndResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingEnd))
      val missingThreeFieldsResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingThreeFields))
      val missingInfoResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = None))

      missingNameResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.Name"
      missingDescrResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.Description"
      missingUrlResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.EventUrl"
      missingSponsorResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.SponsoringOrg"
      missingStartResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.ExpectedStart"
      missingEndResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.ExpectedEnd"
      missingThreeFieldsResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.ExpectedEnd, ApiEventInfo.ExpectedStart, ApiEventInfo.SponsoringOrg"
      missingInfoResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo"

      val missingGeo = apiScheduleEvent.info.map(_.copy(geoLocation = None))
      val missingGeoResult =
        testKit.scheduleEvent(apiScheduleEvent.copy(info = missingGeo))

      missingGeoResult.isReply shouldBe true
      missingGeoResult.reply.eventId shouldBe apiScheduleEvent.eventId

    }

  }
}
