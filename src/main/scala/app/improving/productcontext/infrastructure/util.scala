package app.improving.productcontext.infrastructure

import app.improving.{ApiEventId, ApiMemberId, ApiProductId, ApiStoreId, EventId, MemberId, ProductId, StoreId}
import app.improving.productcontext.{ProductCreated, ProductInfo, ProductMetaInfo, ProductStatus}
import app.improving.productcontext.product.{ApiProduct, ApiProductInfo, ApiProductMetaInfo, ApiProductStatus}

object util {

  def convertApiProductInfoToProductInfo(
      apiProductInfo: ApiProductInfo
  ): ProductInfo = {
    ProductInfo(
      Some(ProductId(apiProductInfo.sku)),
      apiProductInfo.name,
      apiProductInfo.description,
      apiProductInfo.section,
      apiProductInfo.row,
      apiProductInfo.seat,
      apiProductInfo.event.map(event => EventId(event.eventId)),
      apiProductInfo.image,
      apiProductInfo.price,
      apiProductInfo.cost,
      apiProductInfo.store.map(store => StoreId(store.storeId))
    )
  }

  def convertApiProductMetaInfoToProductMetaInfo(
      apiProductMetaInfo: ApiProductMetaInfo
  ): ProductMetaInfo = {
    ProductMetaInfo(
      apiProductMetaInfo.createdBy.map(member => MemberId(member.memberId)),
      apiProductMetaInfo.createdOn,
      apiProductMetaInfo.lastModifiedBy.map(member =>
        MemberId(member.memberId)
      ),
      apiProductMetaInfo.lastModifiedOn
    )
  }

  def convertProductMetaInfoToApiProductMetaInfo(
      productMetaInfo: ProductMetaInfo
  ): ApiProductMetaInfo = {
    ApiProductMetaInfo(
      productMetaInfo.createdBy.map(member => ApiMemberId(member.id)),
      productMetaInfo.createdOn,
      productMetaInfo.lastModifiedBy.map(member => ApiMemberId(member.id)),
      productMetaInfo.lastModifiedOn
    )
  }
  def convertProductInfoToApiProductInfo(
      productInfo: ProductInfo
  ): ApiProductInfo = {
    ApiProductInfo(
      productInfo.sku.map(_.id).getOrElse("SKU is not found"),
      productInfo.name,
      productInfo.description,
      productInfo.section,
      productInfo.row,
      productInfo.seat,
      productInfo.event.map(event => ApiEventId(event.id)),
      productInfo.image,
      productInfo.price,
      productInfo.cost,
      productInfo.store.map(store => ApiStoreId(store.id))
    )
  }

  def convertProductCreatedToApiProduct(
      productCreated: ProductCreated
  ): ApiProduct = {
    ApiProduct(
      productCreated.sku.map(sku => ApiProductId(sku.id)),
      productCreated.info.map(convertProductInfoToApiProductInfo),
      productCreated.meta.map(convertProductMetaInfoToApiProductMetaInfo),
      ApiProductStatus.ACTIVE
    )
  }
}
