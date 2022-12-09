package com.improving.eventcontext.event

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import com.improving.{ApiMemberId, event}
import com.improving.event.{
  ApiChangeEventInfo,
  ApiEventInfo,
  ApiReservationId,
  ApiScheduleEvent
}
import com.improving.eventcontext.{EventInfoChanged, EventScheduled}
import com.improving.organization.ApiOrganizationId
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventAPISpec extends AnyWordSpec with Matchers {
  "The EventAPI" should {
    "have example test that can be removed" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // use the testkit to execute a command:
      // val result: EventSourcedResult[R] = testKit.someOperation(SomeRequest("id"));
      // verify the emitted events
      // val actualEvent: ExpectedEvent = result.nextEventOfType[ExpectedEvent]
      // actualEvent shouldBe expectedEvent
      // verify the final state after applying the events
      // testKit.state() shouldBe expectedState
      // verify the reply
      // result.reply shouldBe expectedReply
      // verify the final state after the command
    }

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
            "lat1.234-lon2.346",
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

      val eventIdOpt = testKit.currentState.event.flatMap(_.id)

      val changeEventInfo = ApiChangeEventInfo(
        eventIdOpt.map(_.id).getOrElse("id not found"),
        Some(
          ApiEventInfo(
            "postponed-try-out",
            "postponed school footbal try out",
            "www.nowhere.com",
            Some(ApiOrganizationId("org-1")),
            "lat1.234-lon2.346",
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
            "lat1.234-lon2.346",
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

      val eventIdOpt = testKit.currentState.event.flatMap(_.id)

      eventIdOpt.isDefined shouldBe true
    }

    "correctly process commands of type CancelEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.cancelEvent(event.ApiCancelEvent(...))
    }

    "correctly process commands of type RescheduleEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.rescheduleEvent(event.ApiRescheduleEvent(...))
    }

    "correctly process commands of type DelayEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.delayEvent(event.ApiDelayEvent(...))
    }

    "correctly process commands of type StartEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.startEvent(event.ApiStartEvent(...))
    }

    "correctly process commands of type EndEvent" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.endEvent(event.ApiEndEvent(...))
    }

    "correctly process commands of type AddLiveUpdate" in {
      val testKit = EventAPITestKit(new EventAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.addLiveUpdate(event.ApiAddLiveUpdate(...))
    }
  }
}
