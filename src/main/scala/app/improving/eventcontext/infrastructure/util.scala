package app.improving.eventcontext.infrastructure

import app.improving.{ApiGeoLocation, ApiMemberId, GeoLocation, OrganizationId}
import app.improving.eventcontext.event._
import app.improving.eventcontext.{
  EventInfo,
  EventMetaInfo,
  EventStatus,
  ReservationId
}
import app.improving.organizationcontext.organization.ApiOrganizationId

object util {

  def convertApiEventInfoToEventInfo(
      apiEventInfo: ApiEventInfo
  ): EventInfo = {
    EventInfo(
      apiEventInfo.eventName,
      apiEventInfo.description,
      apiEventInfo.eventURL,
      apiEventInfo.sponsoringOrg.map(org => OrganizationId(org.orgId)),
      apiEventInfo.geoLocation.map(location =>
        GeoLocation(location.latitude, location.longitude, location.elevation)
      ),
      apiEventInfo.reservation.map(reservation =>
        ReservationId(reservation.reservationId)
      ),
      apiEventInfo.expectedStart,
      apiEventInfo.expectedEnd,
      apiEventInfo.isPrivate
    )
  }

  def convertEventInfoToApiEventInfo(eventInfo: EventInfo): ApiEventInfo = {
    ApiEventInfo(
      eventInfo.eventName,
      eventInfo.description,
      eventInfo.eventURL,
      eventInfo.sponsoringOrg.map(org => ApiOrganizationId(org.id)),
      eventInfo.geoLocation.map(location =>
        ApiGeoLocation(
          location.latitude,
          location.longitude,
          location.elevation
        )
      ),
      eventInfo.reservation.map(reservation =>
        ApiReservationId(reservation.id)
      ),
      eventInfo.expectedStart,
      eventInfo.expectedEnd,
      eventInfo.isPrivate
    )
  }

  def convertEventMetaInfoToApiEventMetaInfo(
      eventMeta: EventMetaInfo
  ): ApiEventMetaInfo = ApiEventMetaInfo(
    eventMeta.scheduledBy.map(member => ApiMemberId(member.id)),
    eventMeta.scheduledOn,
    eventMeta.lastModifiedBy.map(member => ApiMemberId(member.id)),
    eventMeta.lastModifiedOn,
    eventMeta.actualStart,
    eventMeta.actualEnd,
    convertEventStatusToApiEventStatus(eventMeta.status)
  )

  def convertEventStatusToApiEventStatus(status: EventStatus): ApiEventStatus =
    status match {
      case EventStatus.SCHEDULED  => ApiEventStatus.SCHEDULED
      case EventStatus.INPROGRESS => ApiEventStatus.INPROGRESS
      case EventStatus.PAST       => ApiEventStatus.PAST
      case EventStatus.CANCELLED  => ApiEventStatus.CANCELLED
      case EventStatus.DELAYED    => ApiEventStatus.DELAYED
      case EventStatus.UNKNOWN    => ApiEventStatus.UNKNOWN
      case EventStatus.Unrecognized(unrecognizedValue) =>
        ApiEventStatus.Unrecognized(unrecognizedValue)
    }

  def convertEventToApiEvent(event: Event): ApiEvent = {
    ApiEvent(
      event.eventId.map(_.id).getOrElse("Event ID Not Found!"),
      event.info.map(info => convertEventInfoToApiEventInfo(info)),
      event.meta.map(meta => convertEventMetaInfoToApiEventMetaInfo(meta)),
      convertEventStatusToApiEventStatus(event.status)
    )
  }
}
