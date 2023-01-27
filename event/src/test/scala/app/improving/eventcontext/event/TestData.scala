package app.improving.eventcontext.event

import app.improving._
import com.google.protobuf.timestamp.Timestamp

object TestData {
  val now = java.time.Instant.now()
  val start = Timestamp.of(now.getEpochSecond, now.getNano)
  val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
  val testEventId = "test-event-id"
  val testEventId2 = "test-event-id2"
  val testEventId3 = "test-event-id3"
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
}
