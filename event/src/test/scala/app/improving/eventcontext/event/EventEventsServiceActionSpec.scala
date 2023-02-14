package app.improving.eventcontext.event

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import TestData._
import app.improving.ApiMemberId
import app.improving.eventcontext.infrastructure.util._

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventEventsServiceActionSpec extends AnyWordSpec with Matchers {

  "EventEventsServiceAction" must {

    "handle command TransformEventInfoChanged" in {
      val service =
        EventEventsServiceActionTestKit(new EventEventsServiceAction(_))
      val result = service.transformEventInfoChanged(eventInfoChanged)

      result.reply.eventId shouldBe defined
      result.reply.info shouldBe Some(convertEventInfoToApiEventInfo(eventInfo))
      result.reply.meta shouldBe Some(
        convertEventMetaInfoToApiEventMetaInfo(
          eventMetaInfo
        )
      )
    }

    "handle command TransformEventScheduled" in {
      val service =
        EventEventsServiceActionTestKit(new EventEventsServiceAction(_))
      val result = service.transformEventScheduled(eventScheduled)

      result.reply.eventId shouldBe defined
      result.reply.info shouldBe Some(convertEventInfoToApiEventInfo(eventInfo))
      result.reply.meta shouldBe Some(
        convertEventMetaInfoToApiEventMetaInfo(
          eventMetaInfo
        )
      )
    }

    "handle command TransformEventCancelled" in {
      val service =
        EventEventsServiceActionTestKit(new EventEventsServiceAction(_))

      val result = service.transformEventCancelled(eventCancelled)

      result.reply.eventId shouldBe defined
      result.reply.cancellingMember shouldBe Some(ApiMemberId(testMemberId))
    }

    "handle command TransformEventRescheduled" in {
      val service =
        EventEventsServiceActionTestKit(new EventEventsServiceAction(_))

      val result = service.transformEventRescheduled(eventRescheduled)

      result.reply.eventId shouldBe defined
      result.reply.info shouldBe Some(convertEventInfoToApiEventInfo(eventInfo))
      result.reply.meta shouldBe Some(
        convertEventMetaInfoToApiEventMetaInfo(
          eventMetaInfo
        )
      )
    }

    "handle command TransformEventDelayed" in {
      val service =
        EventEventsServiceActionTestKit(new EventEventsServiceAction(_))

      val result = service.transformEventDelayed(eventDelayed)

      result.reply.eventId shouldBe defined
      result.reply.meta shouldBe Some(
        convertEventMetaInfoToApiEventMetaInfo(
          eventMetaInfo
        )
      )
      result.reply.expectedDuration shouldBe expectedDuration
    }

    "handle command TransformEventStarted" in {
      val service =
        EventEventsServiceActionTestKit(new EventEventsServiceAction(_))

      val result = service.transformEventStarted(eventStarted)

      result.reply.eventId shouldBe defined
      result.reply.info shouldBe Some(convertEventInfoToApiEventInfo(eventInfo))
      result.reply.meta shouldBe Some(
        convertEventMetaInfoToApiEventMetaInfo(
          eventMetaInfo
        )
      )
    }

    "handle command TransformEventEnded" in {
      val service =
        EventEventsServiceActionTestKit(new EventEventsServiceAction(_))

      val result = service.transformEventEnded(eventEnded)

      result.reply.eventId shouldBe defined
      result.reply.meta shouldBe Some(
        convertEventMetaInfoToApiEventMetaInfo(
          eventMetaInfo
        )
      )
    }

  }
}
