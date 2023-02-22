package app.improving.productcontext.product

import app.improving.{ApiMemberId, ApiSku}
import app.improving.productcontext.ProductActivated
import app.improving.productcontext.ProductCreated
import app.improving.productcontext.ProductDeleted
import app.improving.productcontext.ProductInactivated
import app.improving.productcontext.ProductInfoUpdated
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import app.improving.productcontext.infrastructure.util._
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductEventsServiceAction(creationContext: ActionCreationContext)
    extends AbstractProductEventsServiceAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def transformProductCreated(
      productCreated: ProductCreated
  ): Action.Effect[ApiProductCreated] = {

    log.info(
      s"ProductEventsServiceAction in transformProductCreated - productCreated - $productCreated"
    )

    effects.reply(
      ApiProductCreated(
        productCreated.sku.map(sku => ApiSku(sku.id)),
        productCreated.info.map(convertProductInfoToApiProductInfo),
        productCreated.meta.map(convertProductMetaInfoToApiProductMetaInfo)
      )
    )
  }
  override def transformProductInfoUpdated(
      productInfoUpdated: ProductInfoUpdated
  ): Action.Effect[ApiProductInfoUpdated] = {

    log.info(
      s"ProductEventsServiceAction in transformProductInfoUpdated - productInfoUpdated - $productInfoUpdated"
    )

    effects.reply(
      ApiProductInfoUpdated(
        productInfoUpdated.sku.map(sku => ApiSku(sku.id)),
        productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
        productInfoUpdated.meta.map(convertProductMetaInfoToApiProductMetaInfo)
      )
    )
  }
  override def transformProductDeleted(
      productDeleted: ProductDeleted
  ): Action.Effect[ApiProductDeleted] = {

    log.info(
      s"ProductEventsServiceAction in transformProductDeleted - productDeleted - $productDeleted"
    )

    effects.reply(
      ApiProductDeleted(
        productDeleted.sku.map(sku => ApiSku(sku.id)),
        productDeleted.deletingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
  override def transformProductActivated(
      productActivated: ProductActivated
  ): Action.Effect[ApiProductActivated] = {

    log.info(
      s"ProductEventsServiceAction in transformProductActivated - productActivated - $productActivated"
    )

    effects.reply(
      ApiProductActivated(
        productActivated.sku.map(sku => ApiSku(sku.id)),
        productActivated.activatingMember.map(member => ApiMemberId(member.id))
      )
    )
  }
  override def transformProductInactivated(
      productInactivated: ProductInactivated
  ): Action.Effect[ApiProductInactivated] = {

    log.info(
      s"ProductEventsServiceAction in transformProductInactivated - productInactivated - $productInactivated"
    )

    effects.reply(
      ApiProductInactivated(
        productInactivated.sku.map(sku => ApiSku(sku.id)),
        productInactivated.inactivatingMember.map(member =>
          ApiMemberId(member.id)
        )
      )
    )
  }
}
