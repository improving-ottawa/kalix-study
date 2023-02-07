package app.improving.productcontext.infrastructure

import app.improving.productcontext.ProductDetails.Ticket
import app.improving.productcontext.product.ApiProductDetails.ApiTicket
import app.improving.{ApiEventId, ApiMemberId, ApiStoreId, EventId, MemberId, ProductId, StoreId}
import app.improving.productcontext.{OpenTicket, ProductCreated, ProductDetails, ProductInfo, ProductInfoUpdate, ProductMetaInfo, ProductStatus, ReservedTicket, RestrictedTicket, TicketEventCorrTableRow}
import app.improving.productcontext.product.{ApiOpenTicket, ApiProduct, ApiProductDetails, ApiProductInfo, ApiProductInfoUpdate, ApiProductMetaInfo, ApiProductStatus, ApiReservedTicket, ApiRestrictedTicket}

object util {

  def convertApiProductInfoToProductInfo(
      apiProductInfo: ApiProductInfo
  ): ProductInfo = {
    ProductInfo(
      Some(ProductId(apiProductInfo.sku)),
      apiProductInfo.name,
      apiProductInfo.description,
      apiProductInfo.productDetails.map(convertApiProductDetailsToProductDetails),
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
      productInfo.productDetails.map(convertProductDetailsToApiProductDetails),
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
      ApiProductStatus.API_PRODUCT_STATUS_ACTIVE
    )
  }

  def convertProductCreatedToTicketEventCorrTableRow(productCreated: ProductCreated): TicketEventCorrTableRow = {
    TicketEventCorrTableRow(
      sku = Some(ProductId(productCreated.sku.map(_.id).getOrElse("Product is not found"))),
      info = productCreated.info,
      meta = productCreated.meta,
      status = ProductStatus.ACTIVE.toString(),
      event = productCreated.info.flatMap(extractEventIdFromProductInfo)
    )
  }

  def extractEventIdFromProductInfo(productInfo: ProductInfo): Option[EventId] = {
    productInfo.productDetails.flatMap(
      _.ticket match {
        case Ticket.ReservedTicket(value) => value.event
        case Ticket.RestrictedTicket(value) => value.event
        case Ticket.OpenTicket(value) => value.event
        case Ticket.Empty => None
      }
    )
  }

  def convertApiReservedTicketToReservedTicket(apiReservedTicket: ApiReservedTicket): ReservedTicket = {
    ReservedTicket(
      section = apiReservedTicket.section,
      row = apiReservedTicket.row,
      set = apiReservedTicket.set,
      event = apiReservedTicket.event.map(event => EventId(event.eventId))
    )
  }

  def convertApiRestrictedTicketToRestrictedTicket(apiRestrictedTicket: ApiRestrictedTicket): RestrictedTicket = {
    RestrictedTicket(
      section = apiRestrictedTicket.section,
      event = apiRestrictedTicket.event.map(event => EventId(event.eventId))
    )
  }

  def convertApiOpenTicketToOpenTicket(apiOpenTicket: ApiOpenTicket): OpenTicket = {
    OpenTicket(
      event = apiOpenTicket.event.map(event => EventId(event.eventId))
    )
  }

  def convertApiProductDetailsToProductDetails(
    apiProductDetails: ApiProductDetails
  ): ProductDetails = {
    apiProductDetails.apiTicket match {
      case ApiTicket.ReservedTicket(value) =>
        ProductDetails(ProductDetails.Ticket.ReservedTicket(convertApiReservedTicketToReservedTicket(value)))
      case ApiTicket.RestrictedTicket(value) =>
        ProductDetails(ProductDetails.Ticket.RestrictedTicket(convertApiRestrictedTicketToRestrictedTicket(value)))
      case ApiTicket.OpenTicket(value) =>
        ProductDetails(ProductDetails.Ticket.OpenTicket(convertApiOpenTicketToOpenTicket(value)))
      case ApiTicket.Empty =>
        ProductDetails(ProductDetails.Ticket.Empty)
    }
  }

  def convertReservedTicketToApiReservedTicket(reservedTicket: ReservedTicket): ApiReservedTicket = {
    ApiReservedTicket(
      section = reservedTicket.section,
      row = reservedTicket.row,
      set = reservedTicket.set,
      event = reservedTicket.event.map(event => ApiEventId(event.id))
    )
  }

  def convertRestrictedTicketToApiRestrictedTicket(restrictedTicket: RestrictedTicket): ApiRestrictedTicket = {
    ApiRestrictedTicket(
      section = restrictedTicket.section,
      event = restrictedTicket.event.map(event => ApiEventId(event.id))
    )
  }

  def convertOpenTicketToApiOpenTicket(openTicket: OpenTicket): ApiOpenTicket = {
    ApiOpenTicket(
      event = openTicket.event.map(event => ApiEventId(event.id))
    )
  }

  def convertProductDetailsToApiProductDetails(
                                                productDetails: ProductDetails
                                              ): ApiProductDetails = {
    productDetails.ticket match {
      case Ticket.ReservedTicket(value) =>
        ApiProductDetails(ApiProductDetails.ApiTicket.ReservedTicket(convertReservedTicketToApiReservedTicket(value)))
      case Ticket.RestrictedTicket(value) =>
        ApiProductDetails(ApiProductDetails.ApiTicket.RestrictedTicket(convertRestrictedTicketToApiRestrictedTicket(value)))
      case Ticket.OpenTicket(value) =>
        ApiProductDetails(ApiProductDetails.ApiTicket.OpenTicket(convertOpenTicketToApiOpenTicket(value)))
      case Ticket.Empty =>
        ApiProductDetails(ApiProductDetails.ApiTicket.Empty)
    }
  }
}
