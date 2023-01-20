package app.improving.storecontext.store

import app.improving.{
  ApiEventId,
  ApiLocationId,
  ApiMemberId,
  ApiOrganizationId,
  ApiProductId,
  ApiVenueId
}
import com.google.protobuf.timestamp.Timestamp

object TestData {

  val testStoreId = "test-store-id"
  val testName = "test-name"
  val testDescription = "test-description"
  val testProductId1 = "test-product-id1"
  val testProductId2 = "test-product-id2"
  val testEventId = "test-event-id"
  val testEvent = ApiEventId(testEventId)
  val testVenueId = "test-venue-id"
  val testVenue = ApiVenueId(testVenueId)
  val testLocationId = "test-location-id"
  val testLocaltion = ApiLocationId(testLocationId)
  val testProducts = Seq[ApiProductId](
    ApiProductId(testProductId1),
    ApiProductId(testProductId2)
  )
  val testOrgId = "test-org-id"
  val testOrg = ApiOrganizationId(testOrgId)
  val testMember1 = "test-member1"
  val testMember2 = "test-member2"
  val testMember3 = "test-member3"
  val apiStoreInfo = ApiStoreInfo(
    testStoreId,
    testName,
    testDescription,
    testProducts,
    Some(testEvent),
    Some(testVenue),
    Some(testLocaltion),
    Some(testOrg)
  )
  val apiCreateStore = ApiCreateStore(
    testStoreId,
    Some(apiStoreInfo),
    Some(ApiMemberId(testMember1))
  )

  val testStoreIdUpdate = "test-store-id-update"
  val testNameUpdate = "test-name-update"
  val testDescriptionUpdate = "test-description-update"
  val testProductId1Update = "test-product-id1-update"
  val testProductId2Update = "test-product-id2-update"
  val testEventIdUpdate = "test-event-id-update"
  val testEventUpdate = ApiEventId(testEventIdUpdate)
  val testVenueIdUpdate = "test-venue-id-update"
  val testVenueUpdate = ApiVenueId(testVenueIdUpdate)
  val testLocationIdUpdate = "test-location-id-update"
  val testLocaltionUpdate = ApiLocationId(testLocationIdUpdate)
  val testProductsUpdate = Seq[ApiProductId](
    ApiProductId(testProductId1Update),
    ApiProductId(testProductId2Update)
  )
  val testOrgIdUpdate = "test-org-id-update"
  val testOrgUpdate = ApiOrganizationId(testOrgIdUpdate)
  val testMember1Update = "test-member1-update"
  val testMember2Update = "test-member2-update"
  val apiStoreInfoUpdate = ApiStoreInfo(
    testStoreIdUpdate,
    testNameUpdate,
    testDescriptionUpdate,
    testProductsUpdate,
    Some(testEventUpdate),
    Some(testVenueUpdate),
    Some(testLocaltionUpdate),
    Some(testOrgUpdate)
  )
  val now = java.time.Instant.now()
  val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val apiStoreMetaInfo = ApiStoreMetaInfo(
    testStoreId,
    Some(ApiMemberId(testMember1)),
    Some(timestamp),
    Some(ApiMemberId(testMember1)),
    Some(timestamp)
  )
}
