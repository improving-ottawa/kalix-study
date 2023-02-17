package app.improving.productcontext

import app.improving.productcontext.infrastructure.util._
import app.improving.productcontext.product.{ApiProduct, ApiProductStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllProductsViewImpl(context: ViewContext)
    extends AbstractAllProductsView {

  override def emptyState: ApiProduct = ApiProduct.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

  override def processProductCreated(
      state: ApiProduct,
      productCreated: ProductCreated
  ): UpdateEffect[ApiProduct] = {
    if (state != emptyState) {

      log.info(
        s"AllProductsViewImpl in processProductCreated - state already existed"
      )

      effects.ignore()
    } else

      log.info(
        s"AllProductsViewImpl in processProductCreated - productCreated ${productCreated}"
      )

      effects.updateState(
        convertProductCreatedToApiProduct(productCreated)
      )
  }

  override def processProductInfoUpdated(
      state: ApiProduct,
      productInfoUpdated: ProductInfoUpdated
  ): UpdateEffect[ApiProduct] = {
    log.info(
      s"AllProductsViewImpl in processProductInfoUpdated - productInfoUpdated ${productInfoUpdated}"
    )

    effects.updateState(
      state.copy(
        info = productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
        meta = productInfoUpdated.meta.map(
          convertProductMetaInfoToApiProductMetaInfo
        )
      )
    )
  }

  override def processProductDeleted(
      state: ApiProduct,
      productDeleted: ProductDeleted
  ): UpdateEffect[ApiProduct] = {

    log.info(
      s"AllProductsViewImpl in processProductDeleted - productDeleted ${productDeleted}"
    )

    effects.deleteState()
  }

  override def processProductActivated(
      state: ApiProduct,
      productActivated: ProductActivated
  ): UpdateEffect[ApiProduct] = {

    log.info(
      s"AllProductsViewImpl in processProductActivated - productActivated ${productActivated}"
    )

    effects.updateState(
      state.copy(
        status = ApiProductStatus.API_PRODUCT_STATUS_ACTIVE
      )
    )
  }

  override def processProductInactivated(
      state: ApiProduct,
      productInactivated: ProductInactivated
  ): UpdateEffect[ApiProduct] = {

    log.info(
      s"AllProductsViewImpl in processProductInactivated - productInactivated ${productInactivated}"
    )

    effects.updateState(
      state.copy(
        status = ApiProductStatus.API_PRODUCT_STATUS_INACTIVE
      )
    )
  }

  override def processProductReleased(
      state: ApiProduct,
      productReleased: ProductReleased
  ): UpdateEffect[ApiProduct] = effects.deleteState()
}
