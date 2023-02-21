package app.improving.eventcontext.infrastructure

import app.improving.{
  ApiEventId,
  ApiGeoLocation,
  ApiMemberId,
  ApiOrganizationId,
  EventId,
  GeoLocation,
  MemberId,
  OrganizationId
}
import app.improving.eventcontext.event._
import app.improving.eventcontext.{
  EventInfo,
  EventMetaInfo,
  EventScheduled,
  EventStatus
}

object util {

  def buildEventInfoFromUpdateInfo(
      eventInfo: EventInfo,
      updatingInfo: ApiEventUpdateInfo
  ): EventInfo = {
    EventInfo(
      eventName = updatingInfo.eventName
        .filter(_.nonEmpty)
        .getOrElse(eventInfo.eventName),
      description = updatingInfo.description
        .filter(_.nonEmpty)
        .getOrElse(eventInfo.description),
      eventUrl = updatingInfo.eventUrl
        .filter(_.nonEmpty)
        .getOrElse(eventInfo.eventUrl),
      sponsoringOrg = updatingInfo.sponsoringOrg
        .map(org => OrganizationId(org.organizationId)),
      geoLocation =
        if (updatingInfo.geoLocation.isDefined)
          updatingInfo.geoLocation
            .map(location =>
              GeoLocation(
                location.latitude,
                location.longitude,
                location.elevation
              )
            )
        else eventInfo.geoLocation,
      expectedStart =
        if (updatingInfo.expectedStart.isDefined) updatingInfo.expectedStart
        else eventInfo.expectedStart,
      expectedEnd =
        if (updatingInfo.expectedEnd.isDefined)
          updatingInfo.expectedEnd
        else eventInfo.expectedEnd,
      updatingInfo.isPrivate.getOrElse(eventInfo.isPrivate)
    )
  }

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
      apiEventInfo.expectedStart,
      apiEventInfo.expectedEnd,
      apiEventInfo.isPrivate
    )
  }

  def convertEventInfoToApiEventInfo(eventInfo: EventInfo): ApiEventInfo = {
    ApiEventInfo(
      eventInfo.eventName,
      eventInfo.description,
      eventInfo.eventUrl,
      eventInfo.sponsoringOrg.map(org => ApiOrganizationId(org.id)),
      eventInfo.geoLocation.map(location =>
        ApiGeoLocation(
          location.latitude,
          location.longitude,
          location.elevation
        )
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
      case EventStatus.EVENT_STATUS_SCHEDULED =>
        ApiEventStatus.API_EVENT_STATUS_SCHEDULED
      case EventStatus.EVENT_STATUS_INPROGRESS =>
        ApiEventStatus.API_EVENT_STATUS_INPROGRESS
      case EventStatus.EVENT_STATUS_PAST => ApiEventStatus.API_EVENT_STATUS_PAST
      case EventStatus.EVENT_STATUS_CANCELLED =>
        ApiEventStatus.API_EVENT_STATUS_CANCELLED
      case EventStatus.EVENT_STATUS_DELAYED =>
        ApiEventStatus.API_EVENT_STATUS_DELAYED
      case EventStatus.EVENT_STATUS_RELEASED =>
        ApiEventStatus.API_EVENT_STATUS_RELEASED
      case EventStatus.EVENT_STATUS_UNKNOWN =>
        ApiEventStatus.API_EVENT_STATUS_UNKNOWN
      case EventStatus.Unrecognized(unrecognizedValue) =>
        ApiEventStatus.Unrecognized(unrecognizedValue)
    }

  def convertEventToApiEvent(event: Event): ApiEvent =
    ApiEvent(
      event.eventId.map(_.id).getOrElse(ApiEventId.defaultInstance.eventId),
      event.info.map(convertEventInfoToApiEventInfo),
      event.reservation,
      event.meta.map(convertEventMetaInfoToApiEventMetaInfo),
      convertEventStatusToApiEventStatus(event.status)
    )

  def convertApiMemberIdToMemberId(apiMemberId: ApiMemberId): MemberId =
    MemberId(apiMemberId.memberId)

  def convertEventScheduledToApiEvent(
      eventScheduled: EventScheduled
  ): ApiEvent =
    ApiEvent(
      eventScheduled.eventId.getOrElse(EventId.defaultInstance).id,
      eventScheduled.info.map(info => convertEventInfoToApiEventInfo(info)),
      "",
      eventScheduled.meta.map(meta =>
        convertEventMetaInfoToApiEventMetaInfo(meta)
      ),
      ApiEventStatus.API_EVENT_STATUS_SCHEDULED
    )

}
