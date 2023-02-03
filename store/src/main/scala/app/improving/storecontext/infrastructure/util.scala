package app.improving.storecontext.infrastructure

import app.improving.{
  ApiEventId,
  ApiLocationId,
  ApiMemberId,
  ApiOrganizationId,
  ApiProductId,
  ApiVenueId,
  EventId,
  LocationId,
  MemberId,
  OrganizationId,
  ProductId,
  StoreId,
  VenueId
}
import app.improving.storecontext.{StoreInfo, StoreMetaInfo, StoreStatus}
import app.improving.storecontext.store.{
  ApiStoreInfo,
  ApiStoreMetaInfo,
  ApiStoreStatus
}

object util {

  def convertApiStoreInfoToStoreInfo(apiStoreInfo: ApiStoreInfo): StoreInfo = {
    StoreInfo(
      Some(StoreId(apiStoreInfo.storeId)),
      apiStoreInfo.name,
      apiStoreInfo.description,
      apiStoreInfo.products.map(product => ProductId(product.productId)),
      apiStoreInfo.event.map(event => EventId(event.eventId)),
      apiStoreInfo.venue.map(venue => VenueId(venue.venueId)),
      apiStoreInfo.location.map(location => LocationId(location.locationId)),
      apiStoreInfo.sponsoringOrg.map(org => OrganizationId(org.organizationId))
    )
  }

  def convertStoreInfoToApiStoreInfo(storeInfo: StoreInfo): ApiStoreInfo = {
    ApiStoreInfo(
      storeInfo.storeId.map(_.id).getOrElse("StoreId IS NOT FOUND."),
      storeInfo.name,
      storeInfo.description,
      storeInfo.products.map(product => ApiProductId(product.id)),
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
      case ApiStoreStatus.DRAFT   => StoreStatus.DRAFT
      case ApiStoreStatus.READY   => StoreStatus.READY
      case ApiStoreStatus.OPEN    => StoreStatus.OPEN
      case ApiStoreStatus.CLOSED  => StoreStatus.CLOSED
      case ApiStoreStatus.DELETED => StoreStatus.DELETED
      case ApiStoreStatus.UNKNOWN => StoreStatus.UNKNOWN
      case ApiStoreStatus.Unrecognized(unrecognizedValue) =>
        StoreStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertStoreStatusToApiStoreStatus(
      status: StoreStatus
  ): ApiStoreStatus = {
    status match {
      case StoreStatus.DRAFT   => ApiStoreStatus.DRAFT
      case StoreStatus.READY   => ApiStoreStatus.READY
      case StoreStatus.OPEN    => ApiStoreStatus.OPEN
      case StoreStatus.CLOSED  => ApiStoreStatus.CLOSED
      case StoreStatus.DELETED => ApiStoreStatus.DELETED
      case StoreStatus.UNKNOWN => ApiStoreStatus.UNKNOWN
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
