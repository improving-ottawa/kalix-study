package com.improving.eventcontext.infrastructure

import com.improving.OrganizationId
import com.improving.event.ApiEventInfo
import com.improving.eventcontext.{EventInfo, ReservationId}

object util {

  def convertApiEventInfoToEventInfo(
      apiEventInfo: ApiEventInfo
  ): EventInfo = {
    EventInfo(
      apiEventInfo.eventName,
      apiEventInfo.description,
      apiEventInfo.eventURL,
      apiEventInfo.sponsoringOrg.map(org => OrganizationId(org.orgId)),
      apiEventInfo.geoLocation,
      apiEventInfo.reservation.map(reservation =>
        ReservationId(reservation.reservationId)
      ),
      apiEventInfo.expectedStart,
      apiEventInfo.expectedEnd,
      apiEventInfo.isPrivate
    )
  }
}
