package app.improving.productcontext.product

import app.improving.productcontext.infrastructure.util._
import app.improving.{ApiProductId, MemberId, ProductId}
import app.improving.productcontext.{
  ProductActivated,
  ProductCreated,
  ProductDeleted,
  ProductInactivated,
  ProductInfoUpdated,
  ProductStatus
}
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import io.grpc.Status
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductAPI(context: EventSourcedEntityContext)
    extends AbstractProductAPI {
  override def emptyState: ProductState = ProductState.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

  override def createProduct(
      currentState: ProductState,
      apiCreateProduct: ApiCreateProduct
  ): EventSourcedEntity.Effect[ApiProductId] = {
    val productId = apiCreateProduct.sku
    currentState.product match {
      case Some(product) if product != Product.defaultInstance => {

        log.info(
          s"ProductAPI in createProduct - product already existed - ${product}"
        )

        effects.reply(ApiProductId.defaultInstance)
      }
      case _ => {

        log.info(
          s"ProductAPI in createProduct - apiCreateProduct - ${apiCreateProduct}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = ProductCreated(
          Some(ProductId(productId)),
          apiCreateProduct.info.map(convertApiProductInfoToProductInfo),
          apiCreateProduct.meta
            .map(
              _.copy(
                createdOn = Some(timestamp),
                lastModifiedOn = Some(timestamp)
              )
            )
            .map(convertApiProductMetaInfoToProductMetaInfo)
        )
        effects.emitEvent(event).thenReply(_ => ApiProductId(productId))
      }
    }
  }

  override def updateProductInfo(
      currentState: ProductState,
      apiUpdateProductInfo: ApiUpdateProductInfo
  ): EventSourcedEntity.Effect[Empty] =
    currentState.product match {
      case Some(product)
          if product.sku == Some(
            ProductId(apiUpdateProductInfo.sku)
          ) && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in updateProductInfo - apiUpdateProductInfo - ${apiUpdateProductInfo}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = ProductInfoUpdated(
          Some(ProductId(apiUpdateProductInfo.sku)),
          apiUpdateProductInfo.info.map(convertApiProductInfoToProductInfo),
          product.meta.map(
            _.copy(
              lastModifiedBy = apiUpdateProductInfo.updatingMember.map(member =>
                MemberId(member.memberId)
              ),
              lastModifiedOn = Some(timestamp)
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case other => {

        log.info(
          s"ProductAPI in updateProductInfo - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }

  override def deleteProduct(
      currentState: ProductState,
      apiDeleteProduct: ApiDeleteProduct
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.product match {
      case Some(product)
          if product.sku == Some(
            ProductId(apiDeleteProduct.sku)
          ) && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in deleteProduct - apiDeleteProduct - ${apiDeleteProduct}"
        )

        val event = ProductDeleted(
          product.sku,
          apiDeleteProduct.deletingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case other => {

        log.info(
          s"ProductAPI in deleteProduct - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }

  override def activateProduct(
      currentState: ProductState,
      apiActivateProduct: ApiActivateProduct
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.product match {
      case Some(product)
          if product.sku == Some(
            ProductId(apiActivateProduct.sku)
          ) && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in activateProduct - apiActivateProduct - ${apiActivateProduct}"
        )

        val event = ProductActivated(
          product.sku,
          apiActivateProduct.activatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case other => {

        log.info(
          s"ProductAPI in activateProduct - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }

  override def inactivateProduct(
      currentState: ProductState,
      apiInactivateProduct: ApiInactivateProduct
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.product match {
      case Some(product)
          if product.sku == Some(
            ProductId(apiInactivateProduct.sku)
          ) && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in inactivateProduct - apiInactivateProduct - ${apiInactivateProduct}"
        )

        val event = ProductInactivated(
          product.sku,
          apiInactivateProduct.inactivatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case other => {

        log.info(
          s"ProductAPI in inactivateProduct - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
      }
    }
  }

  override def getProductInfo(
      currentState: ProductState,
      apiGetProductInfo: ApiGetProductInfo
  ): EventSourcedEntity.Effect[ApiProductInfoResult] = {
    currentState.product match {
      case Some(product)
          if product.sku == Some(
            ProductId(apiGetProductInfo.sku)
          ) && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in getProductInfo - apiGetProductInfo - ${apiGetProductInfo}"
        )

        val apiProductInfoResult = ApiProductInfoResult(
          apiGetProductInfo.sku,
          product.info.map(convertProductInfoToApiProductInfo)
        )
        effects.reply(apiProductInfoResult)
      }
      case other => {

        log.info(
          s"ProductAPI in getProductInfo - other - ${other}"
        )

        effects.error(
          s"Product By ID ${apiGetProductInfo.sku} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
      }
    }
  }

  override def productCreated(
      currentState: ProductState,
      productCreated: ProductCreated
  ): ProductState = {
    currentState.product match {
      case Some(product)
          if product != Product.defaultInstance && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in productCreated - existing product - ${product}"
        )
        currentState
      }
      case _ => {

        log.info(
          s"ProductAPI in productCreated - productCreated - ${productCreated}"
        )

        val product = Product(
          productCreated.sku,
          productCreated.info,
          productCreated.meta,
          ProductStatus.PRODUCT_STATUS_ACTIVE
        )
        currentState.withProduct(product)
      }
    }
  }
  override def productInfoUpdated(
      currentState: ProductState,
      productInfoUpdated: ProductInfoUpdated
  ): ProductState = {
    currentState.product match {
      case Some(product)
          if product.sku == productInfoUpdated.sku && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in productInfoUpdated - productInfoUpdated - ${productInfoUpdated}"
        )

        currentState.withProduct(
          product.copy(
            info = productInfoUpdated.info,
            meta = productInfoUpdated.meta
          )
        )
      }
      case other => {

        log.info(
          s"ProductAPI in productInfoUpdated - other - ${other}"
        )

        currentState
      }
    }
  }
  override def productDeleted(
      currentState: ProductState,
      productDeleted: ProductDeleted
  ): ProductState = {
    currentState.product match {
      case Some(product)
          if product.sku == productDeleted.sku && product.status != ProductStatus.PRODUCT_STATUS_DELETED => {

        log.info(
          s"ProductAPI in productDeleted - productDeleted - ${productDeleted}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        currentState.withProduct(
          product.copy(
            meta = product.meta.map(
              _.copy(
                lastModifiedBy = productDeleted.deletingMember,
                lastModifiedOn = Some(timestamp)
              )
            ),
            status = ProductStatus.PRODUCT_STATUS_DELETED
          )
        )
      }
      case other => {

        log.info(
          s"ProductAPI in productDeleted - other - ${other}"
        )

        currentState
      }
    }
  }
  override def productActivated(
      currentState: ProductState,
      productActivated: ProductActivated
  ): ProductState = {
    currentState.product match {
      case Some(product) if product.sku == productActivated.sku => {

        log.info(
          s"ProductAPI in productActivated - productActivated - ${productActivated}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        currentState.withProduct(
          product.copy(
            meta = product.meta.map(
              _.copy(
                lastModifiedBy = productActivated.activatingMember,
                lastModifiedOn = Some(timestamp)
              )
            ),
            status = ProductStatus.PRODUCT_STATUS_ACTIVE
          )
        )
      }
      case other => {

        log.info(
          s"ProductAPI in productActivated - other - ${other}"
        )

        currentState
      }
    }
  }

  override def productInactivated(
      currentState: ProductState,
      productInactivated: ProductInactivated
  ): ProductState = {
    currentState.product match {
      case Some(product) if product.sku == productInactivated.sku => {

        log.info(
          s"ProductAPI in productInactivated - productInactivated - ${productInactivated}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        currentState.withProduct(
          product.copy(
            meta = product.meta.map(
              _.copy(
                lastModifiedBy = productInactivated.inactivatingMember,
                lastModifiedOn = Some(timestamp)
              )
            ),
            status = ProductStatus.PRODUCT_STATUS_INACTIVE
          )
        )
      }
      case other => {

        log.info(
          s"ProductAPI in productInactivated - other - ${other}"
        )

        currentState
      }
    }
  }
}
