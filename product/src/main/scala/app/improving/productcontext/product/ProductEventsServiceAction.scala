package app.improving.productcontext.product

import app.improving.{ApiMemberId, ApiProductId}
import app.improving.productcontext.ProductActivated
import app.improving.productcontext.ProductCreated
import app.improving.productcontext.ProductDeleted
import app.improving.productcontext.ProductInactivated
import app.improving.productcontext.ProductInfoUpdated
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import app.improving.productcontext.infrastructure.util._

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductEventsServiceAction(creationContext: ActionCreationContext)
    extends AbstractProductEventsServiceAction {

  override def transformProductCreated(
      productCreated: ProductCreated
  ): Action.Effect[ApiProductCreated] = {
    effects.reply(
      ApiProductCreated(
        productCreated.sku.map(sku => ApiProductId(sku.id)),
        productCreated.info.map(convertProductInfoToApiProductInfo),
        productCreated.meta.map(convertProductMetaInfoToApiProductMetaInfo)
      )
    )
  }
  override def transformProductInfoUpdated(
      productInfoUpdated: ProductInfoUpdated
  ): Action.Effect[ApiProductInfoUpdated] = {
    effects.reply(
      ApiProductInfoUpdated(
        productInfoUpdated.sku.map(sku => ApiProductId(sku.id)),
        productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
        productInfoUpdated.meta.map(convertProductMetaInfoToApiProductMetaInfo)
      )
    )
  }
  override def transformProductDeleted(
      productDeleted: ProductDeleted
  ): Action.Effect[ApiProductDeleted] = {
    effects.reply(
      ApiProductDeleted(
        productDeleted.sku.map(sku => ApiProductId(sku.id)),
        productDeleted.deletingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
  override def transformProductActivated(
      productActivated: ProductActivated
  ): Action.Effect[ApiProductActivated] = {
    effects.reply(
      ApiProductActivated(
        productActivated.sku.map(sku => ApiProductId(sku.id)),
        productActivated.activatingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
  override def transformProductInactivated(
      productInactivated: ProductInactivated
  ): Action.Effect[ApiProductInactivated] = {
    effects.reply(
      ApiProductInactivated(
        productInactivated.sku.map(sku => ApiProductId(sku.id)),
        productInactivated.inactivatingMember.map(member =>
          ApiMemberId(member.id)
        )
      )
    )
  }
}
