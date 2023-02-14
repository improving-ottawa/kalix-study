package app.improving.eventcontext.event

import app.improving._
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
import com.google.protobuf.duration.Duration

object TestData {
  val now = java.time.Instant.now()
  val start = Timestamp.of(now.getEpochSecond, now.getNano)
  val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
  val expectedDuration = Some(Duration.of(20, 0))
  val testEventId = "test-event-id"
  val testEventId2 = "test-event-id2"
  val testEventId3 = "test-event-id3"
  val testMemberId = "test-member-id"
  val testReason = "test-reason"
  val apiScheduleEvent = ApiScheduleEvent(
    testEventId,
    Some(
      ApiEventInfo(
        "try-out-event",
        "School footbal try out",
        "www.nowhere.com",
        Some(ApiOrganizationId("test-organization-id")),
        Some(ApiGeoLocation(0.12, 0.438, 4.322)),
        Some(ApiReservationId("reserve-1")),
        Some(start),
        Some(end),
        false
      )
    ),
    Some(ApiMemberId("1"))
  )

  val apiSchedulePrivateEvent = ApiScheduleEvent(
    testEventId2,
    Some(
      ApiEventInfo(
        "try-out-event",
        "School footbal try out",
        "www.nowhere.com",
        Some(ApiOrganizationId("test-organization-id")),
        Some(ApiGeoLocation(0.12, 0.438, 4.322)),
        Some(ApiReservationId("reserve-1")),
        Some(start),
        Some(end),
        true
      )
    ),
    Some(ApiMemberId("1"))
  )

  val apiSchedulePrivateFailedEvent = ApiScheduleEvent(
    testEventId3,
    Some(
      ApiEventInfo(
        "try-out-event",
        "School footbal try out",
        "www.nowhere.com",
        Some(ApiOrganizationId("test-organization-id")),
        Some(ApiGeoLocation(0.12, 0.438, 4.322)),
        Some(ApiReservationId("reserve-1")),
        Some(start),
        Some(end),
        true
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
