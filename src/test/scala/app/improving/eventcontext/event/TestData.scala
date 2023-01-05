package app.improving.eventcontext.event

import app.improving._
import app.improving.organizationcontext.organization.ApiOrganizationId
import com.google.protobuf.timestamp.Timestamp

object TestData {
  val now = java.time.Instant.now()
  val start = Timestamp.of(now.getEpochSecond, now.getNano)
  val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
  val testEventId = "event-1"

  val event = ApiScheduleEvent(
    testEventId,
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
}
