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
  EventStatus
}
import com.google.protobuf.duration.Duration
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {
  val now: Instant = java.time.Instant.now()
  val start: Timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val end: Timestamp = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
  val expectedDuration: Duration = Duration.of(20, 0)
  val testEventId: EventId = EventId("test-event-id")
  val testEventId2: EventId = EventId("test-event-id2")
  val testEventId3: EventId = EventId("test-event-id3")
  val testMemberId: String = "test-member-id"
  val testReason = "test-reason"
  val apiScheduleEvent: ApiScheduleEvent = ApiScheduleEvent(
    testEventId.id,
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
      testEventId.id,
      "reservation33",
      Some(ApiMemberId("5"))
    )

  val apiStartEvent: ApiStartEvent = ApiStartEvent(
    testEventId.id,
    Some(ApiMemberId("1"))
  )

  val apiEndEvent: ApiEndEvent = ApiEndEvent(
    testEventId.id,
    Some(ApiMemberId("2"))
  )

  val apiDelayEvent: ApiDelayEvent = ApiDelayEvent(
    testEventId.id,
    "The venue has been scheduled to shutdown",
    Some(expectedDuration),
    Some(ApiMemberId("2"))
  )

  val apiCancelEvent: ApiCancelEvent = ApiCancelEvent(
    testEventId.id,
    Some(ApiMemberId("2"))
  )

  val apiRescheduleEvent: ApiRescheduleEvent = ApiRescheduleEvent(
    testEventId.id,
    Some(Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)),
    Some(Timestamp.of(now.getEpochSecond + 2000000L, now.getNano)),
    Some(ApiMemberId("2"))
  )

  val apiSchedulePrivateEvent: ApiScheduleEvent = ApiScheduleEvent(
    testEventId2.id,
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
    testEventId3.id,
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

  val eventInfo: EventInfo = EventInfo(
    "try-out-event",
    "School footbal try out",
    "www.nowhere.com",
    Some(OrganizationId("test-organization-id")),
    Some(GeoLocation(0.12, 0.438, 4.322)),
    Some(start),
    Some(end)
  )
  val eventMetaInfo: EventMetaInfo = EventMetaInfo(
    Some(MemberId(testMemberId)),
    Some(start),
    Some(MemberId(testMemberId)),
    Some(end),
    Some(start),
    Some(end)
  )
  val eventInfoChanged: EventInfoChanged = EventInfoChanged(
    Some(testEventId),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventScheduled: EventScheduled = EventScheduled(
    Some(testEventId),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventCancelled: EventCancelled = EventCancelled(
    Some(testEventId),
    Some(
      EventMetaInfo(
        scheduledBy = Some(MemberId(testMemberId)),
        scheduledOn = Some(Timestamp.of(Instant.now().getEpochSecond, 0)),
        lastModifiedBy = Some(MemberId(testMemberId)),
        lastModifiedOn = Some(Timestamp.of(Instant.now().getEpochSecond, 0)),
        actualStart = Some(Timestamp.of(Instant.now().getEpochSecond, 0)),
        actualEnd =
          Some(Timestamp.of(Instant.now().getEpochSecond + 10000L, 0)),
        status = EventStatus.EVENT_STATUS_CANCELLED
      )
    )
  )
  val eventRescheduled: EventRescheduled = EventRescheduled(
    Some(testEventId),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventDelayed: EventDelayed = EventDelayed(
    Some(testEventId),
    testReason,
    Some(eventMetaInfo),
    Some(expectedDuration)
  )
  val eventStarted: EventStarted = EventStarted(
    Some(testEventId),
    Some(eventInfo),
    Some(eventMetaInfo)
  )
  val eventEnded: EventEnded = EventEnded(
    Some(testEventId),
    Some(eventMetaInfo)
  )
}
