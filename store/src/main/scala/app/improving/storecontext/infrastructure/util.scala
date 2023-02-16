package app.improving.storecontext.infrastructure

import app.improving.{
  ApiEventId,
  ApiLocationId,
  ApiMemberId,
  ApiOrganizationId,
  ApiSku,
  ApiVenueId,
  EventId,
  LocationId,
  MemberId,
  OrganizationId,
  Sku,
  VenueId
}
import app.improving.storecontext.{
  StoreInfo,
  StoreMetaInfo,
  StoreStatus,
  StoreUpdateInfo
}
import app.improving.storecontext.store.{
  ApiStoreInfo,
  ApiStoreMetaInfo,
  ApiStoreStatus,
  ApiStoreUpdateInfo
}

object util {

  def convertApiStoreInfoToStoreInfo(apiStoreInfo: ApiStoreInfo): StoreInfo = {
    StoreInfo(
      apiStoreInfo.name,
      apiStoreInfo.description,
      apiStoreInfo.products.map(id => Sku(id.sku)),
      apiStoreInfo.event.map(event => EventId(event.eventId)),
      apiStoreInfo.venue.map(venue => VenueId(venue.venueId)),
      apiStoreInfo.location.map(location => LocationId(location.locationId)),
      apiStoreInfo.sponsoringOrg.map(org => OrganizationId(org.organizationId))
    )
  }

  def convertApiStoreUpdateInfoToStoreUpdateInfo(
      apiStoreUpdateInfo: ApiStoreUpdateInfo
  ): StoreUpdateInfo = {
    StoreUpdateInfo(
      name = apiStoreUpdateInfo.name,
      description = apiStoreUpdateInfo.description,
      products = apiStoreUpdateInfo.products.map(product => Sku(product.sku)),
      event = apiStoreUpdateInfo.event.map(event => EventId(event.eventId)),
      venue = apiStoreUpdateInfo.venue.map(venue => VenueId(venue.venueId)),
      location = apiStoreUpdateInfo.location.map(location =>
        LocationId(location.locationId)
      ),
      sponsoringOrg = apiStoreUpdateInfo.sponsoringOrg.map(org =>
        OrganizationId(org.organizationId)
      )
    )
  }

  def convertStoreInfoToApiStoreInfo(storeInfo: StoreInfo): ApiStoreInfo = {
    ApiStoreInfo(
      storeInfo.name,
      storeInfo.description,
      storeInfo.products.map(product => ApiSku(product.id)),
      storeInfo.event.map(event => ApiEventId(event.id)),
      storeInfo.venue.map(venue => ApiVenueId(venue.id)),
      storeInfo.location.map(location => ApiLocationId(location.id)),
      storeInfo.sponsoringOrg.map(org => ApiOrganizationId(org.id))
    )
  }

  def convertApiStoreStatusToStoreStatus(
      status: ApiStoreStatus
  ): StoreStatus = {
    status match {
      case ApiStoreStatus.API_STORE_STATUS_DRAFT =>
        StoreStatus.STORE_STATUS_DRAFT
      case ApiStoreStatus.API_STORE_STATUS_READY =>
        StoreStatus.STORE_STATUS_READY
      case ApiStoreStatus.API_STORE_STATUS_OPEN => StoreStatus.STORE_STATUS_OPEN
      case ApiStoreStatus.API_STORE_STATUS_CLOSED =>
        StoreStatus.STORE_STATUS_CLOSED
      case ApiStoreStatus.API_STORE_STATUS_DELETED =>
        StoreStatus.STORE_STATUS_DELETED
      case ApiStoreStatus.API_STORE_STATUS_RELEASED =>
        StoreStatus.STORE_STATUS_RELEASED
      case ApiStoreStatus.API_STORE_STATUS_UNKNOWN =>
        StoreStatus.STORE_STATUS_UNKNOWN
      case ApiStoreStatus.Unrecognized(unrecognizedValue) =>
        StoreStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertStoreStatusToApiStoreStatus(
      status: StoreStatus
  ): ApiStoreStatus = {
    status match {
      case StoreStatus.STORE_STATUS_DRAFT =>
        ApiStoreStatus.API_STORE_STATUS_DRAFT
      case StoreStatus.STORE_STATUS_READY =>
        ApiStoreStatus.API_STORE_STATUS_READY
      case StoreStatus.STORE_STATUS_OPEN => ApiStoreStatus.API_STORE_STATUS_OPEN
      case StoreStatus.STORE_STATUS_CLOSED =>
        ApiStoreStatus.API_STORE_STATUS_CLOSED
      case StoreStatus.STORE_STATUS_DELETED =>
        ApiStoreStatus.API_STORE_STATUS_DELETED
      case StoreStatus.STORE_STATUS_RELEASED =>
        ApiStoreStatus.API_STORE_STATUS_RELEASED
      case StoreStatus.STORE_STATUS_UNKNOWN =>
        ApiStoreStatus.API_STORE_STATUS_UNKNOWN
      case StoreStatus.Unrecognized(unrecognizedValue) =>
        ApiStoreStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertApiStoreMetaInfoToStoreMetaInfo(
      apiStoreMetaInfo: ApiStoreMetaInfo
  ): StoreMetaInfo = {
    StoreMetaInfo(
      apiStoreMetaInfo.createdBy.map(member => MemberId(member.memberId)),
      apiStoreMetaInfo.createdOn,
      apiStoreMetaInfo.lastModifiedBy.map(member => MemberId(member.memberId)),
      apiStoreMetaInfo.lastModifiedOn,
      convertApiStoreStatusToStoreStatus(apiStoreMetaInfo.status)
    )
  }

  def convertStoreMetaInfoToApiStoreMetaInfo(
      storeMetaInfo: StoreMetaInfo
  ): ApiStoreMetaInfo = {
    ApiStoreMetaInfo(
      storeMetaInfo.createdBy.map(member => ApiMemberId(member.id)),
      storeMetaInfo.createdOn,
      storeMetaInfo.lastModifiedBy.map(member => ApiMemberId(member.id)),
      storeMetaInfo.lastModifiedOn,
      convertStoreStatusToApiStoreStatus(storeMetaInfo.status)
    )
  }
}
