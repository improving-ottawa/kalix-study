package app.improving.eventcontext.event

import com.google.protobuf.duration.Duration
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{ApiGeoLocation, ApiMemberId, MemberId}
import app.improving.eventcontext.{EventScheduled, EventStatus}
import app.improving.organizationcontext.organization.ApiOrganizationId
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventAPISpec extends AnyWordSpec with Matchers {
  "The EventAPI" should {

    "correctly process commands of type ChangeEventInfo" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      val now = java.time.Instant.now()
      val start = Timestamp.of(now.getEpochSecond, now.getNano)
      val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)

      val event = ApiScheduleEvent(
        "event-1",
        Some(
          ApiEventInfo(
            "try-out-event",
            "School footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            false
          )
        ),
        Some(ApiMemberId("1"))
      )

      val result: EventSourcedResult[Empty] = testKit.scheduleEvent(event)

      result.events should have size 1

      val _ = result.nextEvent[EventScheduled]

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
            false
          )
        ),
        Some(ApiMemberId("2"))
      )

      val changeEventInfoResult = testKit.changeEventInfo(changeEventInfo)

      changeEventInfoResult.events should have size 1

      val infoOpt = testKit.currentState.event.flatMap(_.info)

      infoOpt.map(_.eventName) shouldBe changeEventInfo.info.map(_.eventName)

    }

    "correctly process commands of type ScheduleEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))

      val now = java.time.Instant.now()
      val start = Timestamp.of(now.getEpochSecond, now.getNano)
      val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)

      val event = ApiScheduleEvent(
        "event-1",
        Some(
          ApiEventInfo(
            "try-out-event",
            "School footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            false
          )
        ),
        Some(ApiMemberId("1"))
      )
      val result: EventSourcedResult[Empty] = testKit.scheduleEvent(event)

      result.events should have size 1

      val next = result.nextEvent[EventScheduled]

      next.info.map(_.eventName) shouldBe event.info.map(_.eventName)

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      eventIdOpt.isDefined shouldBe true
    }

    "correctly process commands of type CancelEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      val now = java.time.Instant.now()
      val start = Timestamp.of(now.getEpochSecond, now.getNano)
      val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)

      val event = ApiScheduleEvent(
        "event-1",
        Some(
          ApiEventInfo(
            "try-out-event",
            "School footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            false
          )
        ),
        Some(ApiMemberId("1"))
      )
      val result: EventSourcedResult[Empty] = testKit.scheduleEvent(event)

      result.events should have size 1

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      val cancelled = ApiCancelEvent(
        eventIdOpt.map(_.id).getOrElse("eventId not found"),
        Some(ApiMemberId("2"))
      )

      val cancelledEventInfoResult = testKit.cancelEvent(cancelled)

      cancelledEventInfoResult.events should have size 1

      val statusOpt = testKit.currentState.event.map(_.status)

      statusOpt shouldBe Some(EventStatus.CANCELLED)

      val metaOpt = testKit.currentState.event.flatMap(_.meta)

      metaOpt.flatMap(_.lastModifiedBy) shouldBe Some(MemberId("2"))
    }

    "correctly process commands of type RescheduleEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))

      val now = java.time.Instant.now()
      val start = Timestamp.of(now.getEpochSecond, now.getNano)
      val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)

      val event = ApiScheduleEvent(
        "event-1",
        Some(
          ApiEventInfo(
            "try-out-event",
            "School footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            false
          )
        ),
        Some(ApiMemberId("1"))
      )
      val result: EventSourcedResult[Empty] = testKit.scheduleEvent(event)

      result.events should have size 1

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      val newStart = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
      val newEnd = Timestamp.of(now.getEpochSecond + 2000000L, now.getNano)

      val rescheduled = ApiRescheduleEvent(
        eventIdOpt.map(_.id).getOrElse("eventId not found"),
        Some(newStart),
        Some(newEnd),
        Some(ApiMemberId("2"))
      )

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

    "correctly process commands of type DelayEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      val now = java.time.Instant.now()
      val start = Timestamp.of(now.getEpochSecond, now.getNano)
      val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)

      val event = ApiScheduleEvent(
        "event-1",
        Some(
          ApiEventInfo(
            "try-out-event",
            "School footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            false
          )
        ),
        Some(ApiMemberId("1"))
      )
      val result: EventSourcedResult[Empty] = testKit.scheduleEvent(event)

      result.events should have size 1

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      val durationDelayed = 1000000L

      val delayed = ApiDelayEvent(
        eventIdOpt.map(_.id).getOrElse("eventId not found"),
        "The venue has been scheduled to shutdown",
        Some(Duration.of(durationDelayed, 0)),
        Some(ApiMemberId("2"))
      )

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

    "correctly process commands of type StartEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      val now = java.time.Instant.now()
      val start = Timestamp.of(now.getEpochSecond, now.getNano)
      val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)

      val event = ApiScheduleEvent(
        "event-1",
        Some(
          ApiEventInfo(
            "try-out-event",
            "School footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            false
          )
        ),
        Some(ApiMemberId("1"))
      )
      val result: EventSourcedResult[Empty] = testKit.scheduleEvent(event)

      result.events should have size 1

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

    "correctly process commands of type EndEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      val now = java.time.Instant.now()
      val start = Timestamp.of(now.getEpochSecond, now.getNano)
      val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)

      val event = ApiScheduleEvent(
        "event-1",
        Some(
          ApiEventInfo(
            "try-out-event",
            "School footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            Some(ApiGeoLocation(0.12, 0.438, 4.322)),
            Some(ApiReservationId("reserve-1")),
            Some(start),
            Some(end),
            false
          )
        ),
        Some(ApiMemberId("1"))
      )
      val result: EventSourcedResult[Empty] = testKit.scheduleEvent(event)

      result.events should have size 1

      val eventIdOpt = testKit.currentState.event.flatMap(_.eventId)

      val ended = ApiEndEvent(
        eventIdOpt.map(_.id).getOrElse("eventId not found"),
        Some(ApiMemberId("2"))
      )

      val endedEventInfoResult = testKit.endEvent(ended)

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
  }
}
