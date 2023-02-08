package app.improving.eventcontext.infrastructure

import app.improving.{
  ApiGeoLocation,
  ApiMemberId,
  ApiOrganizationId,
  GeoLocation,
  OrganizationId
}
import app.improving.eventcontext.event._
import app.improving.eventcontext.{
  EventInfo,
  EventMetaInfo,
  EventRescheduled,
  EventScheduled,
  EventStatus,
  ReservationId
}

object util {

  def convertApiEventInfoToEventInfo(
      apiEventInfo: ApiEventInfo
  ): EventInfo = {
    EventInfo(
      apiEventInfo.eventName,
      apiEventInfo.description,
      apiEventInfo.eventUrl,
      apiEventInfo.sponsoringOrg.map(org => OrganizationId(org.organizationId)),
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
      case EventStatus.SCHEDULED  => ApiEventStatus.API_EVENT_STATUS_SCHEDULED
      case EventStatus.INPROGRESS => ApiEventStatus.API_EVENT_STATUS_INPROGRESS
      case EventStatus.PAST       => ApiEventStatus.API_EVENT_STATUS_PAST
      case EventStatus.CANCELLED  => ApiEventStatus.API_EVENT_STATUS_CANCELLED
      case EventStatus.DELAYED    => ApiEventStatus.API_EVENT_STATUS_DELAYED
      case EventStatus.UNKNOWN    => ApiEventStatus.API_EVENT_STATUS_UNKNOWN
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

  def convertEventScheduledToApiEvent(
      eventScheduled: EventScheduled
  ): ApiEvent = {
    ApiEvent(
      eventScheduled.eventId.map(_.id).getOrElse("Event ID Not Found!"),
      eventScheduled.info.map(info => convertEventInfoToApiEventInfo(info)),
      eventScheduled.meta.map(meta =>
        convertEventMetaInfoToApiEventMetaInfo(meta)
      ),
      ApiEventStatus.API_EVENT_STATUS_SCHEDULED
    )
  }

  def convertEventReScheduledToApiEvent(
      eventRescheduled: EventRescheduled
  ): ApiEvent = {
    ApiEvent(
      eventRescheduled.eventId.map(_.id).getOrElse("Event ID Not Found!"),
      eventRescheduled.info.map(info => convertEventInfoToApiEventInfo(info)),
      eventRescheduled.meta.map(meta =>
        convertEventMetaInfoToApiEventMetaInfo(meta)
      ),
      ApiEventStatus.API_EVENT_STATUS_SCHEDULED
    )
  }

}
