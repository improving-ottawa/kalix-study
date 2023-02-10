package app.improving.eventcontext.infrastructure

import app.improving.{
  ApiGeoLocation,
  ApiMemberId,
  ApiOrganizationId,
  GeoLocation,
  MemberId,
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

  def buildEventInfoFromUpdateInfo(
      eventInfo: Option[EventInfo],
      updatingInfo: ApiEventInfo
  ): EventInfo = {
    EventInfo(
      Some(updatingInfo.eventName)
        .filter(_.nonEmpty)
        .orElse(eventInfo.map(_.eventName))
        .getOrElse(""),
      Some(updatingInfo.description)
        .filter(_.nonEmpty)
        .orElse(eventInfo.map(_.description))
        .getOrElse(""),
      Some(updatingInfo.eventURL)
        .filter(_.nonEmpty)
        .orElse(eventInfo.map(_.eventURL))
        .getOrElse(""),
      updatingInfo.sponsoringOrg
        .map(org => OrganizationId(org.organizationId))
        .orElse(eventInfo.flatMap(_.sponsoringOrg)),
      updatingInfo.geoLocation
        .map(location =>
          GeoLocation(location.latitude, location.longitude, location.elevation)
        )
        .orElse(eventInfo.flatMap(_.geoLocation)),
      updatingInfo.reservation
        .map(reservation => ReservationId(reservation.reservationId))
        .orElse(eventInfo.flatMap(_.reservation)),
      updatingInfo.expectedStart.orElse(eventInfo.flatMap(_.expectedStart)),
      updatingInfo.expectedEnd.orElse(eventInfo.flatMap(_.expectedEnd)),
      updatingInfo.isPrivate.getOrElse(
        true
      ) // TODO Defaulting isPrivate to true--is that correct?
    )
  }

  def convertApiEventInfoToEventInfo(
      apiEventInfo: ApiEventInfo
  ): EventInfo = {
    EventInfo(
      apiEventInfo.eventName,
      apiEventInfo.description,
      apiEventInfo.eventURL,
      apiEventInfo.sponsoringOrg.map(org => OrganizationId(org.organizationId)),
      apiEventInfo.geoLocation.map(location =>
        GeoLocation(location.latitude, location.longitude, location.elevation)
      ),
      apiEventInfo.reservation.map(reservation =>
        ReservationId(reservation.reservationId)
      ),
      apiEventInfo.expectedStart,
      apiEventInfo.expectedEnd,
      apiEventInfo.isPrivate.getOrElse(
        true
      ) // Defaulting isPrivate to true--is that correct?
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
      Some(eventInfo.isPrivate)
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

  def convertEventScheduledToApiEvent(
      eventScheduled: EventScheduled
  ): ApiEvent = {
    ApiEvent(
      eventScheduled.eventId.map(_.id).getOrElse("Event ID Not Found!"),
      eventScheduled.info.map(info => convertEventInfoToApiEventInfo(info)),
      eventScheduled.meta.map(meta =>
        convertEventMetaInfoToApiEventMetaInfo(meta)
      ),
      ApiEventStatus.SCHEDULED
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
      ApiEventStatus.SCHEDULED
    )
  }

  def convertApiMemberIdToMemberId(apiMemberId: ApiMemberId): MemberId =
    MemberId(apiMemberId.memberId)

}
