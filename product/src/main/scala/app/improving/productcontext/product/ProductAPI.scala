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

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductAPI(context: EventSourcedEntityContext)
    extends AbstractProductAPI {
  override def emptyState: ProductState = ProductState.defaultInstance

  override def createProduct(
      currentState: ProductState,
      apiCreateProduct: ApiCreateProduct
  ): EventSourcedEntity.Effect[ApiProductId] = {
    val productId = apiCreateProduct.sku
    currentState.product match {
      case Some(product) if product != Product.defaultInstance =>
        effects.reply(ApiProductId.defaultInstance)
      case _ => {
        val event = ProductCreated(
          Some(ProductId(productId)),
          apiCreateProduct.info.map(convertApiProductInfoToProductInfo),
          apiCreateProduct.meta.map(convertApiProductMetaInfoToProductMetaInfo)
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
          ) && product.status != ProductStatus.DELETED
          && product.info.isDefined => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val productInfoUpdateOpt = apiUpdateProductInfo.info.map(convertApiProductInfoUpdateToProductInfoUpdate)
        val updatedProductInfo = product.info.map(productInfo => {
          productInfoUpdateOpt.fold(productInfo) { productInfoUpdate =>
            productInfo.copy(
              name = productInfoUpdate.name.getOrElse(productInfo.name),
              description = productInfoUpdate.description.getOrElse(productInfo.description),
              image = if (productInfoUpdate.image.nonEmpty) productInfoUpdate.image else productInfo.image,
              price = productInfoUpdate.price.getOrElse(productInfo.price),
              cost = productInfoUpdate.cost.getOrElse(productInfo.cost),
              store = productInfoUpdate.store.orElse(productInfo.store)
            )
          }
        })
        val event = ProductInfoUpdated(
          Some(ProductId(apiUpdateProductInfo.sku)),
          updatedProductInfo,
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
      case _ => effects.reply(Empty.defaultInstance)
    }

  override def deleteProduct(
      currentState: ProductState,
      apiDeleteProduct: ApiDeleteProduct
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.product match {
      case Some(product)
          if product.sku == Some(
            ProductId(apiDeleteProduct.sku)
          ) && product.status != ProductStatus.DELETED => {
        val event = ProductDeleted(
          product.sku,
          apiDeleteProduct.deletingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
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
          ) && product.status != ProductStatus.DELETED => {
        val event = ProductActivated(
          product.sku,
          apiActivateProduct.activatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
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
          ) && product.status != ProductStatus.DELETED => {
        val event = ProductInactivated(
          product.sku,
          apiInactivateProduct.inactivatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
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
          ) && product.status != ProductStatus.DELETED => {
        val apiProductInfoResult = ApiProductInfoResult(
          apiGetProductInfo.sku,
          product.info.map(convertProductInfoToApiProductInfo)
        )
        effects.reply(apiProductInfoResult)
      }
      case _ =>
        effects.error(
          s"Product By ID ${apiGetProductInfo.sku} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
    }
  }

  override def productCreated(
      currentState: ProductState,
      productCreated: ProductCreated
  ): ProductState = {
    currentState.product match {
      case Some(product)
          if product != Product.defaultInstance && product.status != ProductStatus.DELETED =>
        currentState
      case _ => {
        val product = Product(
          productCreated.sku,
          productCreated.info,
          productCreated.meta,
          ProductStatus.ACTIVE
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
          if product.sku == productInfoUpdated.sku && product.status != ProductStatus.DELETED => {
        currentState.withProduct(
          product.copy(
            info = productInfoUpdated.info,
            meta = productInfoUpdated.meta
          )
        )
      }
      case _ => currentState
    }
  }
  override def productDeleted(
      currentState: ProductState,
      productDeleted: ProductDeleted
  ): ProductState = {
    currentState.product match {
      case Some(product)
          if product.sku == productDeleted.sku && product.status != ProductStatus.DELETED => {
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
            status = ProductStatus.DELETED
          )
        )
      }
      case _ => currentState
    }
  }
  override def productActivated(
      currentState: ProductState,
      productActivated: ProductActivated
  ): ProductState = {
    currentState.product match {
      case Some(product) if product.sku == productActivated.sku => {
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
            status = ProductStatus.ACTIVE
          )
        )
      }
      case _ => currentState
    }
  }

  override def productInactivated(
      currentState: ProductState,
      productInactivated: ProductInactivated
  ): ProductState = {
    currentState.product match {
      case Some(product) if product.sku == productInactivated.sku => {
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
            status = ProductStatus.INACTIVE
          )
        )
      }
      case _ => currentState
    }
  }
}
