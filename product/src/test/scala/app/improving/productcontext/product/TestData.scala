package app.improving.productcontext.product

import app.improving.productcontext.ProductDetails.Ticket
import app.improving.productcontext.product.ApiProductDetails.ApiTicket
import app.improving.productcontext.{
  ProductActivated,
  ProductCreated,
  ProductDeleted,
  ProductDetails,
  ProductInactivated,
  ProductInfo,
  ProductInfoUpdated,
  ProductMetaInfo,
  ReservedTicket
}
import app.improving.{
  ApiEventId,
  ApiMemberId,
  ApiStoreId,
  MemberId,
  Sku,
  StoreId
}
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

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
  val testProductDetails: Option[ApiProductDetails] = Some(
    ApiProductDetails(
      ApiProductDetails.ApiTicket.ReservedTicket(
        ApiReservedTicket(
          section = testSection,
          row = testRow,
          set = testSeat,
          event = Some(ApiEventId(testEventId))
        )
      )
    )
  )
  val testProductDetails2: Option[ApiProductDetails] = Some(
    ApiProductDetails(
      ApiProductDetails.ApiTicket.ReservedTicket(
        ApiReservedTicket(
          section = testSection,
          row = testRow,
          set = testSeat,
          event = Some(ApiEventId(testEventId2))
        )
      )
    )
  )
  val testProductDetails3: Option[ApiProductDetails] = Some(
    ApiProductDetails(
      ApiProductDetails.ApiTicket.ReservedTicket(
        ApiReservedTicket(
          section = testSection,
          row = testRow,
          set = testSeat,
          event = Some(ApiEventId(testEventId3))
        )
      )
    )
  )
  val testImages: Seq[String] = Seq[String]("test-image")
  val testPrice = 10.0
  val testCost = 3.0
  val testStoreId = "test-store-id"
  val testMemberId = "test-member-id"
  val testMemberId1 = "test-member-id1"
  val now: Instant = java.time.Instant.now()
  val testTimestamp: Timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
  val apiProductInfo: ApiProductInfo = ApiProductInfo(
    testName,
    testDescription,
    testProductDetails,
    testImages,
    testPrice,
    testCost,
    Some(ApiStoreId(testStoreId))
  )
  val apiProductInfoPrivateEvent: ApiProductInfo = ApiProductInfo(
    testName,
    testDescription,
    testProductDetails2,
    testImages,
    testPrice,
    testCost,
    Some(ApiStoreId(testStoreId))
  )
  val apiProductInfoPrivateFailedEvent: ApiProductInfo = ApiProductInfo(
    testName,
    testDescription,
    testProductDetails3,
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
  val testImagesUpdate: Seq[String] = Seq[String]("test-image-update")
  val testPriceUpdate = 100.0
  val testCostUpdate = 30.0
  val testStoreIdUpdate = "test-store-id-update"
  val testMemberIdUpdate = "test-member-id-update"
  val testMemberId1Update = "test-member-id1-update"
  val apiProductInfoUpdate: ApiProductInfoUpdate = ApiProductInfoUpdate(
    Option(testNameUpdate),
    Option(testDescriptionUpdate),
    testProductDetails3,
    testImagesUpdate,
    None,
    Option(testCostUpdate),
    Some(ApiStoreId(testStoreIdUpdate))
  )

  val apiProductInfoAfterUpdate: ApiProductInfo = apiProductInfo.copy(
    name = testNameUpdate,
    description = testDescriptionUpdate,
    productDetails = testProductDetails3,
    image = testImagesUpdate,
    cost = testCostUpdate,
    store = Some(ApiStoreId(testStoreIdUpdate))
  )

  val apiProductMetaInfo: ApiProductMetaInfo = ApiProductMetaInfo(
    Some(ApiMemberId(testMemberId)),
    Some(testTimestamp),
    Some(ApiMemberId(testMemberId1)),
    Some(testTimestamp)
  )

  val apiProductMetaInfoUpdate: ApiProductMetaInfo = ApiProductMetaInfo(
    Some(ApiMemberId(testMemberId)),
    Some(testTimestamp),
    Some(ApiMemberId(testMemberId1Update)),
    Some(testTimestamp)
  )

  val apiUpdateProductInfo: ApiUpdateProductInfo = ApiUpdateProductInfo(
    testSku,
    Some(apiProductInfoUpdate),
    Some(ApiMemberId(testMemberId1Update))
  )

  val anotherApiUpdateProductInfo: ApiUpdateProductInfo = ApiUpdateProductInfo(
    "unrelated-id",
    Some(apiProductInfoUpdate),
    Some(ApiMemberId(testMemberId1Update))
  )

  val apiDeleteProduct: ApiDeleteProduct = ApiDeleteProduct(
    testSku,
    Some(ApiMemberId(testMemberId1))
  )

  val apiCreateProduct: ApiCreateProduct = ApiCreateProduct(
    testSku,
    Some(apiProductInfo),
    Some(apiProductMetaInfo)
  )

  val apiCreateProductPrivateEvent: ApiCreateProduct = ApiCreateProduct(
    testSku2,
    Some(apiProductInfoPrivateEvent),
    Some(apiProductMetaInfo)
  )

  val apiCreateProductPrivateFailedEvent: ApiCreateProduct = ApiCreateProduct(
    testSku3,
    Some(apiProductInfoPrivateFailedEvent),
    Some(apiProductMetaInfo)
  )

  val productInfo = ProductInfo(
    testSku,
    testName,
    Some(
      ProductDetails(
        Ticket.ReservedTicket.apply(ReservedTicket.defaultInstance)
      )
    ),
    testImages,
    testPrice,
    testCost,
    Some(StoreId(testStoreId))
  )
  val productMetaInfo = ProductMetaInfo(
    Some(MemberId(testMemberId)),
    Some(testTimestamp),
    Some(MemberId(testMemberId1)),
    Some(testTimestamp)
  )
  val productCreated = ProductCreated(
    Some(Sku(testSku)),
    Some(productInfo),
    Some(productMetaInfo)
  )
  val productInfoUpdated = ProductInfoUpdated(
    Some(Sku(testSku)),
    Some(productInfo),
    Some(productMetaInfo)
  )
  val productDeleted = ProductDeleted(
    Some(Sku(testSku)),
    Some(MemberId(testMemberId))
  )
  val productActivated = ProductActivated(
    Some(Sku(testSku)),
    Some(MemberId(testMemberId))
  )
  val productInactivated = ProductInactivated(
    Some(Sku(testSku)),
    Some(MemberId(testMemberId))
  )
}
