package app.improving.eventcontext.event

import com.google.protobuf.duration.Duration
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{ApiEventId, ApiGeoLocation, ApiMemberId, ApiOrganizationId, MemberId, OrganizationId}
import app.improving.eventcontext.{EventInfo, EventScheduled, EventStatus, ReservationId}
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import TestData._
import app.improving.eventcontext.infrastructure.util.convertApiEventInfoToEventInfo
// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventAPISpec extends AnyWordSpec with Matchers {


  trait WithTestKit {
    val testKit: EventAPITestKit = EventAPITestKit(new EventAPI(_))
  }

  trait Scheduled extends WithTestKit {
    testKit.scheduleEvent(apiScheduleEvent)
  }

  "The EventAPI" should {

    "correctly process commands of type ChangeEventInfo" in new Scheduled  {

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      val changeEventInfo = ApiChangeEventInfo(
        eventIdOpt.map(_.id).getOrElse("id not found"),
        Some(
          ApiEventInfo(
            "postponed-try-out",
            "postponed school footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            Some(false)
          )
        ),
        Some(ApiMemberId("2"))
      )

      val changeEventInfoResult = testKit.changeEventInfo(changeEventInfo)

      changeEventInfoResult.events should have size 1

      val infoOpt = testKit.currentState.event.flatMap(_.info)

      infoOpt.map(_.eventName) shouldBe changeEventInfo.info.map(_.eventName)

    }

    "only overwrite fields filled in on ChangeEventInfo" in new Scheduled {
      val newInfo: ApiEventInfo = ApiEventInfo(
        eventName = "",
        eventURL = "",
        description = "",
        sponsoringOrg = Some(ApiOrganizationId("organization 121")),
        geoLocation = None,
        reservation = Some(ApiReservationId("reservation 33")),
        expectedStart = None,
        expectedEnd = None,
        isPrivate = Some(true)
      )

      val result: EventSourcedResult[Empty] = testKit.changeEventInfo(ApiChangeEventInfo(eventId = apiScheduleEvent.eventId,
        info = Some(newInfo),
        changingMember = Some(ApiMemberId("member 25"))))

      result.isReply shouldBe true
      result.reply shouldBe Empty.defaultInstance

      val oldInfo: EventInfo = convertApiEventInfoToEventInfo(apiScheduleEvent.info.get)
      val updatedInfo: EventInfo = result.updatedState.asInstanceOf[EventState].event.get.info.get
      updatedInfo.eventName shouldBe oldInfo.eventName
      updatedInfo.eventURL shouldBe oldInfo.eventURL
      updatedInfo.description shouldBe oldInfo.description
      updatedInfo.sponsoringOrg shouldBe Some(OrganizationId("organization 121"))
      updatedInfo.geoLocation shouldBe oldInfo.geoLocation
      updatedInfo.reservation shouldBe Some(ReservationId("reservation 33"))
      updatedInfo.expectedStart shouldBe oldInfo.expectedStart
      updatedInfo.expectedEnd shouldBe oldInfo.expectedEnd
      updatedInfo.isPrivate shouldBe true

    }

    "correctly process commands of type ScheduleEvent" in new WithTestKit {

      val result: EventSourcedResult[ApiEventId] =
        testKit.scheduleEvent(apiScheduleEvent)

      result.events should have size 1

      val next = result.nextEvent[EventScheduled]

      next.info.map(_.eventName) shouldBe apiScheduleEvent.info.map(_.eventName)

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      eventIdOpt.isDefined shouldBe true
    }

    "correctly process commands of type CancelEvent" in new Scheduled {

      val cancelled = apiCancelEvent

      val cancelledEventInfoResult = testKit.cancelEvent(cancelled)

      cancelledEventInfoResult.events should have size 1

      val statusOpt = testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.CANCELLED)

      val metaOpt = testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))
    }

    "correctly process commands of type RescheduleEvent" in new Scheduled {
      val newStart = apiRescheduleEvent.start.get
      val newEnd = apiRescheduleEvent.end.get

      val rescheduled = apiRescheduleEvent

      val rescheduledEventInfoResult = testKit.rescheduleEvent(rescheduled)

      rescheduledEventInfoResult.events should have size 1

      val statusOpt = testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.SCHEDULED)

      val rescheduledStart =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedStart)

      rescheduledStart shouldEqual Some(newStart)

      val rescheduledEnd =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedEnd)

      rescheduledEnd shouldEqual Some(newEnd)

      val metaOpt = testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))
    }

    "correctly process commands of type DelayEvent" in new Scheduled {

      val delayed = apiDelayEvent

      val delayedEventInfoResult = testKit.delayEvent(delayed)

      delayedEventInfoResult.events should have size 1

      val statusOpt = testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.DELAYED)

      val delayedStart =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedStart)

      val newStart = Timestamp.of(start.seconds + durationDelayed, start.nanos)
      val newEnd = Timestamp.of(end.seconds + durationDelayed, end.nanos)

      delayedStart shouldEqual Some(newStart)

      val delayedEnd =
        testKit.currentState.event.flatMap(_.info).flatMap(_.expectedEnd)

      delayedEnd shouldEqual Some(newEnd)

      val metaOpt = testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))
    }

    "correctly process commands of type StartEvent" in new Scheduled {

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      val started = ApiStartEvent(
        eventIdOpt.map(_.id).getOrElse("eventId not found"),
        Some(ApiMemberId("2"))
      )

      val startedEventInfoResult = testKit.startEvent(started)

      startedEventInfoResult.events should have size 1

      val statusOpt = testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.INPROGRESS)

      val metaOpt = testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))

      val actualStart =
        metaOpt.flatMap(_.actualStart).getOrElse(Timestamp.defaultInstance)

      ((actualStart.seconds * 1000000000) + actualStart.nanos > (start.seconds * 1000000000) + start.nanos) shouldBe true
    }

    "if event status is scheduled, reject an end event command" in new Scheduled {
      val result: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)
      result.errorDescription shouldBe "You cannot end event from state SCHEDULED"
    }

    "if event status is delayed, reject an end event command" in new Scheduled {
      testKit.delayEvent(apiDelayEvent)
      val result: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)
      result.errorDescription shouldBe "You cannot end event from state DELAYED"
    }

    "if event status is cancelled, reject all commands (other than cancel)" in new Scheduled {
      testKit.cancelEvent(apiCancelEvent)

      val scheduleResult: EventSourcedResult[ApiEventId] = testKit.scheduleEvent(apiScheduleEvent)
      val rescheduleResult: EventSourcedResult[Empty] = testKit.rescheduleEvent(apiRescheduleEvent)
      val delayResult: EventSourcedResult[Empty] = testKit.delayEvent(apiDelayEvent)
      val startResult: EventSourcedResult[Empty] = testKit.startEvent(apiStartEvent)
      val endResult: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)

      scheduleResult.reply shouldBe ApiEventId.defaultInstance
      rescheduleResult.errorDescription shouldBe "You cannot reschedule event from state CANCELLED"
      delayResult.errorDescription shouldBe "You cannot delay event from state CANCELLED"
      startResult.errorDescription shouldBe "You cannot start event from state CANCELLED"
      endResult.errorDescription shouldBe "You cannot end event from state CANCELLED"

      val cancelResult: EventSourcedResult[Empty] = testKit.cancelEvent(apiCancelEvent)
      cancelResult.isReply shouldBe true
      cancelResult.reply shouldBe Empty.defaultInstance
    }

    "if event status is in progress, reject all commands other than end event" in new Scheduled {
      testKit.startEvent(apiStartEvent)

      val scheduleResult: EventSourcedResult[ApiEventId] = testKit.scheduleEvent(apiScheduleEvent)
      val rescheduleResult: EventSourcedResult[Empty] = testKit.rescheduleEvent(apiRescheduleEvent)
      val delayResult: EventSourcedResult[Empty] = testKit.delayEvent(apiDelayEvent)
      val startResult: EventSourcedResult[Empty] = testKit.startEvent(apiStartEvent)
      val cancelResult: EventSourcedResult[Empty] = testKit.cancelEvent(apiCancelEvent)

      scheduleResult.reply shouldBe ApiEventId.defaultInstance
      rescheduleResult.errorDescription shouldBe "You cannot reschedule event from state INPROGRESS"
      delayResult.errorDescription shouldBe "You cannot delay event from state INPROGRESS"
      cancelResult.errorDescription shouldBe "You cannot cancel event from state INPROGRESS"
      startResult.errorDescription shouldBe "You cannot start event from state INPROGRESS"

      val endResult: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)
      endResult.isReply shouldBe true
      endResult.reply shouldBe Empty.defaultInstance
    }

    "if event status is past, reject all commands" in new Scheduled {
      testKit.startEvent(apiStartEvent)
      testKit.endEvent(apiEndEvent)

      val scheduleResult: EventSourcedResult[ApiEventId] = testKit.scheduleEvent(apiScheduleEvent)
      val rescheduleResult: EventSourcedResult[Empty] = testKit.rescheduleEvent(apiRescheduleEvent)
      val delayResult: EventSourcedResult[Empty] = testKit.delayEvent(apiDelayEvent)
      val cancelResult: EventSourcedResult[Empty] = testKit.cancelEvent(apiCancelEvent)
      val startResult: EventSourcedResult[Empty] = testKit.startEvent(apiStartEvent)
      val endResult: EventSourcedResult[Empty] = testKit.endEvent(apiEndEvent)

      scheduleResult.reply shouldBe ApiEventId.defaultInstance
      rescheduleResult.errorDescription shouldBe "You cannot reschedule event from state PAST"
      delayResult.errorDescription shouldBe "You cannot delay event from state PAST"
      cancelResult.errorDescription shouldBe "You cannot cancel event from state PAST"
      startResult.errorDescription shouldBe "You cannot start event from state PAST"
      endResult.errorDescription shouldBe "You cannot end event from state PAST"
    }

    "correctly process commands of type EndEvent" in new Scheduled {

      testKit.startEvent(apiStartEvent)

      val endedEventInfoResult = testKit.endEvent(apiEndEvent)

      endedEventInfoResult.events should have size 1

      val statusOpt = testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.PAST)

      val metaOpt = testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))

      metaOpt.flatMap(_.actualStart) shouldBe defined

    }

    "correctly process commands of type AddLiveUpdate" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.addLiveUpdate(event.ApiAddLiveUpdate(...))
    }

    "Reject a schedule event command with missing fields" in {
      val testKit = EventAPITestKit(new EventAPI(_))

      val missingName = apiScheduleEvent.info.map(_.copy(eventName = ""))
      val missingDescription = apiScheduleEvent.info.map(_.copy(description = ""))
      val missingUrl = apiScheduleEvent.info.map(_.copy(eventURL = ""))
      val missingSponsor = apiScheduleEvent.info.map(_.copy(sponsoringOrg = None))
      val missingGeo = apiScheduleEvent.info.map(_.copy(geoLocation = None))
      val missingReservation = apiScheduleEvent.info.map(_.copy(reservation = None))
      val missingStart = apiScheduleEvent.info.map(_.copy(expectedStart = None))
      val missingEnd = apiScheduleEvent.info.map(_.copy(expectedEnd = None))
      val missingThreeFields = apiScheduleEvent.info.map(_.copy(expectedEnd = None, expectedStart = None, geoLocation = None))

      val missingNameResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingName))
      val missingDescrResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingDescription))
      val missingUrlResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingUrl))
      val missingSponsorResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingSponsor))
      val missingGeoResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingGeo))
      val missingReservationResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingReservation))
      val missingStartResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingStart))
      val missingEndResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingEnd))
      val missingThreeFieldsResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = missingThreeFields))
      val missingInfoResult = testKit.scheduleEvent(apiScheduleEvent.copy(info = None))

      missingNameResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.Name"
      missingDescrResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.Description"
      missingUrlResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.EventUrl"
      missingSponsorResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.SponsoringOrg"
      missingGeoResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.GeoLocation"
      missingReservationResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.Reservation"
      missingStartResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.ExpectedStart"
      missingEndResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.ExpectedEnd"
      missingThreeFieldsResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo.ExpectedEnd, ApiEventInfo.ExpectedStart, ApiEventInfo.GeoLocation"
      missingInfoResult.errorDescription shouldBe "Message is missing the following fields: ApiEventInfo"

    }

  }
}
