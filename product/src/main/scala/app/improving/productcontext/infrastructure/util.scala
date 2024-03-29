package app.improving.productcontext.infrastructure

import app.improving.productcontext.ProductDetails.Ticket
import app.improving.productcontext.product.ApiProductDetails.ApiTicket
import app.improving.productcontext.{
  OpenTicket,
  ProductCreated,
  ProductDetails,
  ProductInfo,
  ProductInfoUpdate,
  ProductMetaInfo,
  ProductStatus,
  ReservedTicket,
  RestrictedTicket,
  TicketEventCorrTableRow
}
import app.improving.productcontext.product.{
  ApiOpenTicket,
  ApiProduct,
  ApiProductCreated,
  ApiProductDetails,
  ApiProductInfo,
  ApiProductInfoUpdate,
  ApiProductMetaInfo,
  ApiProductStatus,
  ApiReservedTicket,
  ApiRestrictedTicket
}
import app.improving.{
  ApiEventId,
  ApiMemberId,
  ApiSku,
  ApiStoreId,
  EventId,
  MemberId,
  StoreId
}

object util {

  def convertApiProductInfoToProductInfo(
      apiProductInfo: ApiProductInfo
  ): ProductInfo = {
    ProductInfo(
      apiProductInfo.name,
      apiProductInfo.description,
      apiProductInfo.productDetails.map(
        convertApiProductDetailsToProductDetails
      ),
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
  ): ApiProduct =
    ApiProduct(
      productCreated.sku.getOrElse(ApiSku.defaultInstance).toString,
      productCreated.info.map(convertProductInfoToApiProductInfo),
      productCreated.meta.map(convertProductMetaInfoToApiProductMetaInfo),
      ApiProductStatus.API_PRODUCT_STATUS_ACTIVE
    )

  def convertProductCreatedToTicketEventCorrTableRow(
      productCreated: ApiProductCreated
  ): TicketEventCorrTableRow = {
    TicketEventCorrTableRow(
      sku = Some(ApiSku(productCreated.sku)),
      info = productCreated.info,
      meta = productCreated.meta,
      status = ProductStatus.PRODUCT_STATUS_ACTIVE.toString(),
      event = productCreated.info.flatMap(extractEventIdFromProductInfo)
    )
  }

  def convertProductCreatedToTicketEventCorrTableRow(
      productCreated: ProductCreated
  ): TicketEventCorrTableRow = {
    TicketEventCorrTableRow(
      sku = productCreated.sku.map(id => ApiSku(id.id)),
      info = productCreated.info.map(convertProductInfoToApiProductInfo),
      meta =
        productCreated.meta.map(convertProductMetaInfoToApiProductMetaInfo),
      status = ProductStatus.PRODUCT_STATUS_ACTIVE.toString(),
      event = productCreated.info
        .flatMap(extractEventIdFromProductInfo)
        .map(id => ApiEventId(id.eventId))
    )
  }

  def extractEventIdFromProductInfo(
      productInfo: ApiProductInfo
  ): Option[ApiEventId] = {
    productInfo.productDetails.flatMap(
      _.apiTicket match {
        case ApiTicket.ReservedTicket(value)   => value.event
        case ApiTicket.RestrictedTicket(value) => value.event
        case ApiTicket.OpenTicket(value)       => value.event
        case ApiTicket.Empty                   => None
      }
    )
  }

  def extractEventIdFromProductInfo(
      productInfo: ProductInfo
  ): Option[ApiEventId] = {
    productInfo.productDetails.flatMap(
      _.ticket match {
        case Ticket.ReservedTicket(value) =>
          value.event.map(id => ApiEventId(id.id))
        case Ticket.RestrictedTicket(value) =>
          value.event.map(id => ApiEventId(id.id))
        case Ticket.OpenTicket(value) =>
          value.event.map(id => ApiEventId(id.id))
        case Ticket.Empty => None
      }
    )
  }

  def convertApiReservedTicketToReservedTicket(
      apiReservedTicket: ApiReservedTicket
  ): ReservedTicket = {
    ReservedTicket(
      section = apiReservedTicket.section,
      row = apiReservedTicket.row,
      set = apiReservedTicket.set,
      event = apiReservedTicket.event.map(event => EventId(event.eventId))
    )
  }

  def convertApiRestrictedTicketToRestrictedTicket(
      apiRestrictedTicket: ApiRestrictedTicket
  ): RestrictedTicket = {
    RestrictedTicket(
      section = apiRestrictedTicket.section,
      event = apiRestrictedTicket.event.map(event => EventId(event.eventId))
    )
  }

  def convertApiOpenTicketToOpenTicket(
      apiOpenTicket: ApiOpenTicket
  ): OpenTicket = {
    OpenTicket(
      event = apiOpenTicket.event.map(event => EventId(event.eventId))
    )
  }

  def convertApiProductDetailsToProductDetails(
      apiProductDetails: ApiProductDetails
  ): ProductDetails = {
    apiProductDetails.apiTicket match {
      case ApiTicket.ReservedTicket(value) =>
        ProductDetails(
          ProductDetails.Ticket.ReservedTicket(
            convertApiReservedTicketToReservedTicket(value)
          )
        )
      case ApiTicket.RestrictedTicket(value) =>
        ProductDetails(
          ProductDetails.Ticket.RestrictedTicket(
            convertApiRestrictedTicketToRestrictedTicket(value)
          )
        )
      case ApiTicket.OpenTicket(value) =>
        ProductDetails(
          ProductDetails.Ticket.OpenTicket(
            convertApiOpenTicketToOpenTicket(value)
          )
        )
      case ApiTicket.Empty =>
        ProductDetails(ProductDetails.Ticket.Empty)
    }
  }

  def convertReservedTicketToApiReservedTicket(
      reservedTicket: ReservedTicket
  ): ApiReservedTicket = {
    ApiReservedTicket(
      section = reservedTicket.section,
      row = reservedTicket.row,
      set = reservedTicket.set,
      event = reservedTicket.event.map(event => ApiEventId(event.id))
    )
  }

  def convertRestrictedTicketToApiRestrictedTicket(
      restrictedTicket: RestrictedTicket
  ): ApiRestrictedTicket = {
    ApiRestrictedTicket(
      section = restrictedTicket.section,
      event = restrictedTicket.event.map(event => ApiEventId(event.id))
    )
  }

  def convertOpenTicketToApiOpenTicket(
      openTicket: OpenTicket
  ): ApiOpenTicket = {
    ApiOpenTicket(
      event = openTicket.event.map(event => ApiEventId(event.id))
    )
  }

  def convertProductDetailsToApiProductDetails(
      productDetails: ProductDetails
  ): ApiProductDetails = {
    productDetails.ticket match {
      case Ticket.ReservedTicket(value) =>
        ApiProductDetails(
          ApiProductDetails.ApiTicket.ReservedTicket(
            convertReservedTicketToApiReservedTicket(value)
          )
        )
      case Ticket.RestrictedTicket(value) =>
        ApiProductDetails(
          ApiProductDetails.ApiTicket.RestrictedTicket(
            convertRestrictedTicketToApiRestrictedTicket(value)
          )
        )
      case Ticket.OpenTicket(value) =>
        ApiProductDetails(
          ApiProductDetails.ApiTicket.OpenTicket(
            convertOpenTicketToApiOpenTicket(value)
          )
        )
      case Ticket.Empty =>
        ApiProductDetails(ApiProductDetails.ApiTicket.Empty)
    }
  }
}
