package app.improving.productcontext.product

import app.improving.productcontext.infrastructure.util._
import app.improving.{ApiProductId, MemberId, ProductId}
import app.improving.productcontext.{
  ProductActivated,
  ProductCreated,
  ProductDeleted,
  ProductInactivated,
  ProductInfoUpdated,
  ProductReleased,
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
    if (apiCreateProduct.sku.isDefined) {
      val productId = apiCreateProduct.sku.map(id => ProductId(id.productId))
      currentState.product match {
        case Some(_) =>
          effects.error(s"Product is already created with id $productId")
        case _ =>
          val event = ProductCreated(
            productId,
            apiCreateProduct.info.map(convertApiProductInfoToProductInfo),
            apiCreateProduct.meta.map(
              convertApiProductMetaInfoToProductMetaInfo
            )
          )
          effects
            .emitEvent(event)
            .thenReply(_ =>
              apiCreateProduct.sku.getOrElse(ApiProductId.defaultInstance)
            )
      }
    } else effects.error("CreateProduct request provides None for ProductId")
  }

  override def updateProductInfo(
      currentState: ProductState,
      apiUpdateProductInfo: ApiUpdateProductInfo
  ): EventSourcedEntity.Effect[Empty] =
    currentState.product match {
      case Some(product)
          if product.info.isDefined && product.status != ProductStatus.PRODUCT_STATUS_DELETED =>
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val productInfoUpdateOpt = apiUpdateProductInfo.info.map(
          convertApiProductInfoUpdateToProductInfoUpdate
        )
        val updatedProductInfo = product.info.map(productInfo => {
          productInfoUpdateOpt.fold(productInfo) { productInfoUpdate =>
            productInfo.copy(
              name = productInfoUpdate.name.getOrElse(productInfo.name),
              description = productInfoUpdate.description.getOrElse(
                productInfo.description
              ),
              image =
                if (productInfoUpdate.image.nonEmpty) productInfoUpdate.image
                else productInfo.image,
              price = productInfoUpdate.price.getOrElse(productInfo.price),
              cost = productInfoUpdate.cost.getOrElse(productInfo.cost),
              store =
                if (productInfoUpdate.store.isDefined) productInfoUpdate.store
                else productInfo.store
            )
          }
        })
        val event = ProductInfoUpdated(
          apiUpdateProductInfo.sku.map(id => ProductId(id.productId)),
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
      case _ => effects.reply(Empty.defaultInstance)
    }

  override def deleteProduct(
      currentState: ProductState,
      apiDeleteProduct: ApiDeleteProduct
  ): EventSourcedEntity.Effect[Empty] = currentState.product match {
    case Some(product)
        if product.status != ProductStatus.PRODUCT_STATUS_DELETED =>
      val event = ProductDeleted(
        product.sku,
        apiDeleteProduct.deletingMember.map(member => MemberId(member.memberId))
      )
      effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
    case _ => effects.reply(Empty.defaultInstance)
  }

  override def activateProduct(
      currentState: ProductState,
      apiActivateProduct: ApiActivateProduct
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.product match {
      case Some(product)
          if product.status != ProductStatus.PRODUCT_STATUS_DELETED =>
        val event = ProductActivated(
          product.sku,
          apiActivateProduct.activatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def inactivateProduct(
      currentState: ProductState,
      apiInactivateProduct: ApiInactivateProduct
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.product match {
      case Some(product)
          if product.status != ProductStatus.PRODUCT_STATUS_DELETED =>
        val event = ProductInactivated(
          product.sku,
          apiInactivateProduct.inactivatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def getProductInfo(
      currentState: ProductState,
      apiGetProductInfo: ApiGetProductInfo
  ): EventSourcedEntity.Effect[ApiProductInfoResult] = {
    currentState.product match {
      case Some(product)
          if product.status != ProductStatus.PRODUCT_STATUS_ACTIVE =>
        val apiProductInfoResult = ApiProductInfoResult(
          apiGetProductInfo.sku,
          product.info.map(convertProductInfoToApiProductInfo)
        )
        effects.reply(apiProductInfoResult)
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
  ): ProductState = currentState.product match {
    case Some(product)
        if product != Product.defaultInstance && product.status != ProductStatus.PRODUCT_STATUS_DELETED =>
      currentState
    case _ =>
      val product = Product(
        productCreated.sku,
        productCreated.info,
        productCreated.meta,
        ProductStatus.PRODUCT_STATUS_DRAFT
      )
      currentState.withProduct(product)
  }

  override def productInfoUpdated(
      currentState: ProductState,
      productInfoUpdated: ProductInfoUpdated
  ): ProductState = {
    currentState.product match {
      case Some(product)
          if product.status != ProductStatus.PRODUCT_STATUS_DELETED =>
        currentState.withProduct(
          product.copy(
            info = productInfoUpdated.info,
            meta = productInfoUpdated.meta
          )
        )
      case _ => currentState
    }
  }
  override def productDeleted(
      currentState: ProductState,
      productDeleted: ProductDeleted
  ): ProductState = {
    currentState.product match {
      case Some(product)
          if product.status != ProductStatus.PRODUCT_STATUS_DELETED =>
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
      case _ => currentState
    }
  }
  override def productActivated(
      currentState: ProductState,
      productActivated: ProductActivated
  ): ProductState = {
    currentState.product match {
      case Some(product) if product.sku == productActivated.sku =>
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
      case _ => currentState
    }
  }

  override def productInactivated(
      currentState: ProductState,
      productInactivated: ProductInactivated
  ): ProductState = currentState.product match {
    case Some(product) if product.sku == productInactivated.sku =>
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
    case _ => currentState
  }

  override def releaseProduct(
      currentState: ProductState,
      apiReleaseProduct: ApiReleaseProduct
  ): EventSourcedEntity.Effect[Empty] = effects
    .emitEvent(
      ProductReleased(
        apiReleaseProduct.sku.map(apiSku => ProductId(apiSku.productId)),
        apiReleaseProduct.releasingMember.map(apiId => MemberId(apiId.memberId))
      )
    )
    .deleteEntity()
    .thenReply(_ => Empty.defaultInstance)

  override def productReleased(
      currentState: ProductState,
      productReleased: ProductReleased
  ): ProductState = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

    currentState.copy(product =
      currentState.product.map(
        _.copy(
          status = ProductStatus.PRODUCT_STATUS_RELEASED,
          meta = currentState.product.flatMap(
            _.meta.map(
              _.copy(
                lastModifiedBy = productReleased.releasingMember,
                lastModifiedOn = Some(timestamp)
              )
            )
          )
        )
      )
    )
  }
}
