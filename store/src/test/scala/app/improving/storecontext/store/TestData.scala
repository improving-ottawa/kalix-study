package app.improving.storecontext.store

import app.improving.{
  ApiEventId,
  ApiLocationId,
  ApiMemberId,
  ApiOrganizationId,
  ApiSku,
  ApiStoreId,
  ApiVenueId
}
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {

  val testStoreId = "test-store-id"
  val testName = "test-name"
  val testDescription = "test-description"
  val testProductId1 = "test-product-id1"
  val testProductId2 = "test-product-id2"
  val testEventId = "test-event-id"
  val testEvent: ApiEventId = ApiEventId(testEventId)
  val testVenueId = "test-venue-id"
  val testVenue: ApiVenueId = ApiVenueId(testVenueId)
  val testLocationId = "test-location-id"
  val testLocation: ApiLocationId = ApiLocationId(testLocationId)
  val testProducts: Seq[ApiSku] = Seq[ApiSku](
    ApiSku(testProductId1),
    ApiSku(testProductId2)
  )
  val testOrgId = "test-org-id"
  val testOrg: ApiOrganizationId = ApiOrganizationId(testOrgId)
  val testMember1 = "test-member1"
  val testMember2 = "test-member2"
  val testMember3 = "test-member3"
  val apiStoreInfo: ApiStoreInfo = ApiStoreInfo(
    testName,
    testDescription,
    testProducts,
    Some(testEvent),
    Some(testVenue),
    Some(testLocation),
    Some(testOrg)
  )
  val apiCreateStore: ApiCreateStore = ApiCreateStore(
    testStoreId,
    Some(apiStoreInfo),
    Some(ApiMemberId(testMember1))
  )

  val testStoreIdUpdate: ApiStoreId = ApiStoreId("test-store-id-update")
  val testNameUpdate = "test-name-update"
  val testDescriptionUpdate = "test-description-update"
  val testProductId1Update = "test-product-id1-update"
  val testProductId2Update = "test-product-id2-update"
  val testEventIdUpdate = "test-event-id-update"
  val testEventUpdate: ApiEventId = ApiEventId(testEventIdUpdate)
  val testVenueIdUpdate = "test-venue-id-update"
  val testVenueUpdate: ApiVenueId = ApiVenueId(testVenueIdUpdate)
  val testLocationIdUpdate = "test-location-id-update"
  val testLocationUpdate: ApiLocationId = ApiLocationId(testLocationIdUpdate)
  val testProductsUpdate: Seq[ApiSku] = Seq[ApiSku](
    ApiSku(testProductId1Update),
    ApiSku(testProductId2Update)
  )
  val testOrgIdUpdate = "test-org-id-update"
  val testOrgUpdate: ApiOrganizationId = ApiOrganizationId(testOrgIdUpdate)
  val testMember1Update = "test-member1-update"
  val testMember2Update = "test-member2-update"
  val apiStoreInfoUpdate: ApiStoreUpdateInfo = ApiStoreUpdateInfo(
    Some(testNameUpdate),
    Some(testDescriptionUpdate),
    testProductsUpdate,
    Some(testEventUpdate),
    Some(testVenueUpdate),
    Some(testLocationUpdate),
    Some(testOrgUpdate)
  )
  val now: Instant = java.time.Instant.now()
  val timestamp: Timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val apiStoreMetaInfo: ApiStoreMetaInfo = ApiStoreMetaInfo(
    Some(ApiMemberId(testMember1)),
    Some(timestamp),
    Some(ApiMemberId(testMember1)),
    Some(timestamp)
  )
}
