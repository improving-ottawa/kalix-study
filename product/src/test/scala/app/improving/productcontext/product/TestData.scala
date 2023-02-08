package app.improving.productcontext.product

import app.improving.{ApiEventId, ApiMemberId, ApiStoreId}
import com.google.protobuf.timestamp.Timestamp

object TestData {

  val testSku = "test-product-id"
  val testSku2 = "test-product-id2"
  val testSku3 = "test-product-id3"
  val testName = "test-name"
  val testDescription = "test-description"
  val testRow = "test-row"
  val testSeat = "test-seat"
  val testSection = "test-section"
  val testEventId = "test-event-id"
  val testEventId2 = "test-event-id2"
  val testEventId3 = "test-event-id3"
  val testImages = Seq[String]("test-image")
  val testPrice = 10.0
  val testCost = 3.0
  val testStoreId = "test-store-id"
  val testMemberId = "test-member-id"
  val testMemberId1 = "test-member-id1"
  val now = java.time.Instant.now()
  val testTimestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val apiProductInfo = ApiProductInfo(
    testSku,
    testName,
    testDescription,
    testSection,
    testRow,
    testSeat,
    Some(ApiEventId(testEventId)),
    testImages,
    testPrice,
    testCost,
    Some(ApiStoreId(testStoreId))
  )
  val apiProductInfoPrivateEvent = ApiProductInfo(
    testSku2,
    testName,
    testDescription,
    testSection,
    testRow,
    testSeat,
    Some(ApiEventId(testEventId2)),
    testImages,
    testPrice,
    testCost,
    Some(ApiStoreId(testStoreId))
  )
  val apiProductInfoPrivateFailedEvent = ApiProductInfo(
    testSku3,
    testName,
    testDescription,
    testSection,
    testRow,
    testSeat,
    Some(ApiEventId(testEventId3)),
    testImages,
    testPrice,
    testCost,
    Some(ApiStoreId(testStoreId))
  )
  val testNameUpdate = "test-name-update"
  val testDescriptionUpdate = "test-description-update"
  val testRowUpdate = "test-row-update"
  val testSeatUpdate = "test-seat-update"
  val testSectionUpdate = "test-section-update"
  val testEventIdUpdate = "test-event-id-update"
  val testImagesUpdate = Seq[String]("test-image-update")
  val testPriceUpdate = 100.0
  val testCostUpdate = 30.0
  val testStoreIdUpdate = "test-store-id-update"
  val testMemberIdUpdate = "test-member-id-update"
  val testMemberId1Update = "test-member-id1-update"
  val apiProductInfoUpdate = ApiProductInfoUpdate(
    Option(testNameUpdate),
    Option(testDescriptionUpdate),
    testImagesUpdate,
    None,
    Option(testCostUpdate),
    Some(ApiStoreId(testStoreIdUpdate))
  )

  val apiProductInfoAfterUpdate = apiProductInfo.copy(
    name = testNameUpdate,
    description = testDescriptionUpdate,
    image = testImagesUpdate,
    cost = testCostUpdate,
    store = Some(ApiStoreId(testStoreIdUpdate))
  )

  val apiProductMetaInfo = ApiProductMetaInfo(
    Some(ApiMemberId(testMemberId)),
    Some(testTimestamp),
    Some(ApiMemberId(testMemberId1)),
    Some(testTimestamp)
  )

  val apiProductMetaInfoUpdate = ApiProductMetaInfo(
    Some(ApiMemberId(testMemberId)),
    Some(testTimestamp),
    Some(ApiMemberId(testMemberId1Update)),
    Some(testTimestamp)
  )

  val apiUpdateProductInfo = ApiUpdateProductInfo(
    testSku,
    Some(apiProductInfoUpdate),
    Some(ApiMemberId(testMemberId1Update))
  )

  val anotherApiUpdateProductInfo = ApiUpdateProductInfo(
    "unrelated-id",
    Some(apiProductInfoUpdate),
    Some(ApiMemberId(testMemberId1Update))
  )

  val apiDeleteProduct = ApiDeleteProduct(
    testSku,
    Some(ApiMemberId(testMemberId1))
  )

  val apiCreateProduct = ApiCreateProduct(
    testSku,
    Some(apiProductInfo),
    Some(apiProductMetaInfo)
  )

  val apiCreateProductPrivateEvent = ApiCreateProduct(
    testSku2,
    Some(apiProductInfoPrivateEvent),
    Some(apiProductMetaInfo)
  )

  val apiCreateProductPrivateFailedEvent = ApiCreateProduct(
    testSku3,
    Some(apiProductInfoPrivateFailedEvent),
    Some(apiProductMetaInfo)
  )
}
