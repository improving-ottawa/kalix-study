package app.improving.eventcontext.event

import app.improving._
import com.google.protobuf.duration.Duration
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
  ReservationId
}
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {
  val now: Instant = java.time.Instant.now()
  val start: Timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val end: Timestamp = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
  val durationDelayed: Long = 1000000L
  val testEventId: ApiEventId = ApiEventId("test-event-id")
  val testEventId2: ApiEventId = ApiEventId("test-event-id2")
  val testEventId3: ApiEventId = ApiEventId("test-event-id3")
  val apiScheduleEvent: ApiScheduleEvent = ApiScheduleEvent(
    testEventId.eventId,
    Some(
      ApiEventInfo(
        "try-out-event",
        "School footbal try out",
        "www.nowhere.com",
        Some(ApiOrganizationId("test-organization-id")),
        Some(ApiGeoLocation(0.12, 0.438, 4.322)),
        Some(start),
        Some(end)
      )
    ),
    Some(ApiMemberId("1"))
  )

  val apiAddReservationToEvent: ApiAddReservationToEvent =
    ApiAddReservationToEvent(
      testEventId.eventId,
      "reservation33",
      Some(ApiMemberId("5"))
    )

  val apiStartEvent: ApiStartEvent = ApiStartEvent(
    testEventId.eventId,
    Some(ApiMemberId("1"))
  )

  val apiEndEvent: ApiEndEvent = ApiEndEvent(
    testEventId.eventId,
    Some(ApiMemberId("2"))
  )

  val apiDelayEvent: ApiDelayEvent = ApiDelayEvent(
    testEventId.eventId,
    "The venue has been scheduled to shutdown",
    Some(Duration.of(durationDelayed, 0)),
    Some(ApiMemberId("2"))
  )

  val apiCancelEvent: ApiCancelEvent = ApiCancelEvent(
    testEventId.eventId,
    Some(ApiMemberId("2"))
  )

  val apiRescheduleEvent: ApiRescheduleEvent = ApiRescheduleEvent(
    testEventId.eventId,
    Some(Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)),
    Some(Timestamp.of(now.getEpochSecond + 2000000L, now.getNano)),
    Some(ApiMemberId("2"))
  )

  val apiSchedulePrivateEvent: ApiScheduleEvent = ApiScheduleEvent(
    testEventId2.eventId,
    Some(
      ApiEventInfo(
        "try-out-event",
        "School footbal try out",
        "www.nowhere.com",
        Some(ApiOrganizationId("test-organization-id")),
        Some(ApiGeoLocation(0.12, 0.438, 4.322)),
        Some(start),
        Some(end),
        isPrivate = true
      )
    ),
    Some(ApiMemberId("1"))
  )

  val apiSchedulePrivateFailedEvent: ApiScheduleEvent = ApiScheduleEvent(
    testEventId3.eventId,
    Some(
      ApiEventInfo(
        "try-out-event",
        "School footbal try out",
        "www.nowhere.com",
        Some(ApiOrganizationId("test-organization-id")),
        Some(ApiGeoLocation(0.12, 0.438, 4.322)),
        Some(start),
        Some(end),
        isPrivate = true
      )
    ),
    Some(ApiMemberId("1"))
  )

  val eventInfo = EventInfo(
    "try-out-event",
    "School footbal try out",
    "www.nowhere.com",
    Some(OrganizationId("test-organization-id")),
    Some(GeoLocation(0.12, 0.438, 4.322)),
    Some(ReservationId("reserve-1")),
    Some(start),
    Some(end),
    false
  )
  val eventMetaInfo = EventMetaInfo(
    Some(MemberId(testMemberId)),
    Some(start),
    Some(MemberId(testMemberId)),
    Some(end),
    Some(start),
    Some(end)
  )
  val eventInfoChanged = EventInfoChanged(
    Some(EventId(testEventId)),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventScheduled = EventScheduled(
    Some(EventId(testEventId)),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventCancelled = EventCancelled(
    Some(EventId(testEventId)),
    Some(MemberId(testMemberId))
  )
  val eventRescheduled = EventRescheduled(
    Some(EventId(testEventId)),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventDelayed = EventDelayed(
    Some(EventId(testEventId)),
    testReason,
    Some(eventMetaInfo),
    expectedDuration
  )
  val eventStarted = EventStarted(
    Some(EventId(testEventId)),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventEnded = EventEnded(
    Some(EventId(testEventId)),
    Some(eventMetaInfo)
  )
}
