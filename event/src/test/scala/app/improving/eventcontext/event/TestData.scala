package app.improving.eventcontext.event

import app.improving._
import com.google.protobuf.duration.Duration
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {
  val now: Instant = java.time.Instant.now()
  val start: Timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val end: Timestamp = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
  val durationDelayed: Long = 1000000L
  val testEventId: String = "test-event-id"
  val testEventId2: String = "test-event-id2"
  val testEventId3: String = "test-event-id3"
  val apiScheduleEvent: ApiScheduleEvent = ApiScheduleEvent(
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
        Some(false)
      )
    ),
    Some(ApiMemberId("1"))
  )

  val apiStartEvent: ApiStartEvent = ApiStartEvent(
    testEventId,
    Some(ApiMemberId("1"))
  )

  val apiEndEvent: ApiEndEvent = ApiEndEvent(
    testEventId,
    Some(ApiMemberId("2"))
  )

  val apiDelayEvent: ApiDelayEvent = ApiDelayEvent(
    testEventId,
    "The venue has been scheduled to shutdown",
    Some(Duration.of(durationDelayed, 0)),
    Some(ApiMemberId("2"))
  )

  val apiCancelEvent: ApiCancelEvent = ApiCancelEvent(
    testEventId,
    Some(ApiMemberId("2"))
  )

  val apiRescheduleEvent: ApiRescheduleEvent = ApiRescheduleEvent(
    testEventId,
    Some(Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)),
    Some(Timestamp.of(now.getEpochSecond + 2000000L, now.getNano)),
    Some(ApiMemberId("2"))
  )

  val apiSchedulePrivateEvent: ApiScheduleEvent = ApiScheduleEvent(
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
        Some(true)
      )
    ),
    Some(ApiMemberId("1"))
  )

  val apiSchedulePrivateFailedEvent: ApiScheduleEvent = ApiScheduleEvent(
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
        Some(true)
      )
    ),
    Some(ApiMemberId("1"))
  )
}
