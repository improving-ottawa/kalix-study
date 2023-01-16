package app.improving.storecontext.infrastructure

import app.improving.{
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
}
