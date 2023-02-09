package app.improving.productcontext.infrastructure

import app.improving.{ApiEventId, ApiMemberId, ApiStoreId, EventId, MemberId, ProductId, StoreId}
import app.improving.productcontext.{ProductCreated, ProductDetails, ProductInfo, ProductInfoUpdate, ProductMetaInfo}
import app.improving.productcontext.product.{ApiProduct, ApiProductDetails, ApiProductInfo, ApiProductInfoUpdate, ApiProductMetaInfo, ApiProductStatus}

object util {

  def convertApiProductInfoToProductInfo(
      apiProductInfo: ApiProductInfo
  ): ProductInfo = {
    ProductInfo(
      Some(ProductId(apiProductInfo.sku)),
      apiProductInfo.name,
      apiProductInfo.description,
      convertApiProductDetailsToProductDetails(apiProductInfo.productDetails),
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

  def convertApiProductInfoUpdateToProductInfoUpdate(
    apiProductInfoUpdate: ApiProductInfoUpdate
  ): ProductInfoUpdate = {
    ProductInfoUpdate(
      name = apiProductInfoUpdate.name,
      description = apiProductInfoUpdate.description,
      image = apiProductInfoUpdate.image,
      price = apiProductInfoUpdate.price,
      cost = apiProductInfoUpdate.cost,
      store = apiProductInfoUpdate.store.map(store => StoreId(store.storeId))
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
      convertProductDetailsToApiProductDetails(productInfo.productDetails),
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
      productCreated.sku.map(_.id).getOrElse("ProductId is not found"),
      productCreated.info.map(convertProductInfoToApiProductInfo),
      productCreated.meta.map(convertProductMetaInfoToApiProductMetaInfo),
      ApiProductStatus.ACTIVE
    )
  }

  def convertApiProductDetailsToProductDetails(
    apiProductDetails: ApiProductDetails
  ): ProductDetails = {
    apiProductDetails match {
      case ApiProductDetails.RESERVED_TICKET => ProductDetails.RESERVED_TICKET
      case ApiProductDetails.RESTRICTED_TICKET => ProductDetails.RESTRICTED_TICKET
      case ApiProductDetails.OPEN_TICKET => ProductDetails.OPEN_TICKET
      case ApiProductDetails.Unrecognized(unrecognizedValue) => ProductDetails.Unrecognized(unrecognizedValue)
    }
  }

  def convertProductDetailsToApiProductDetails(
                                                productDetails: ProductDetails
                                              ): ApiProductDetails = {
    productDetails match {
      case ProductDetails.RESERVED_TICKET => ApiProductDetails.RESERVED_TICKET
      case ProductDetails.RESTRICTED_TICKET => ApiProductDetails.RESTRICTED_TICKET
      case ProductDetails.OPEN_TICKET => ApiProductDetails.OPEN_TICKET
      case ProductDetails.Unrecognized(unrecognizedValue) => ApiProductDetails.Unrecognized(unrecognizedValue)
    }
  }
}
