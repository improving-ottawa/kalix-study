package app.improving.productcontext

import app.improving.productcontext.infrastructure.util._
import app.improving.productcontext.product.{ApiProduct, ApiProductStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllProductsViewImpl(context: ViewContext)
    extends AbstractAllProductsView {

  override def emptyState: ApiProduct = ApiProduct.defaultInstance

  override def processProductCreated(
      state: ApiProduct,
      productCreated: ProductCreated
  ): UpdateEffect[ApiProduct] = {
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        convertProductCreatedToApiProduct(productCreated)
      )
  }

  override def processProductInfoUpdated(
      state: ApiProduct,
      productInfoUpdated: ProductInfoUpdated
  ): UpdateEffect[ApiProduct] =
    effects.updateState(
      state.copy(
        info = productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
        meta = productInfoUpdated.meta.map(
          convertProductMetaInfoToApiProductMetaInfo
        )
      )
    )

  override def processProductDeleted(
      state: ApiProduct,
      productDeleted: ProductDeleted
  ): UpdateEffect[ApiProduct] =
    effects.deleteState()

  override def processProductActivated(
      state: ApiProduct,
      productActivated: ProductActivated
  ): UpdateEffect[ApiProduct] = {
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
    effects.updateState(
      state.copy(
        status = ApiProductStatus.API_PRODUCT_STATUS_INACTIVE
      )
    )
  }

}
