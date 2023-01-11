package app.improving.productcontext.product

import app.improving.productcontext.ProductActivated
import app.improving.productcontext.ProductCreated
import app.improving.productcontext.ProductDeleted
import app.improving.productcontext.ProductInactivated
import app.improving.productcontext.ProductInfoUpdated
import app.improving.productcontext.product
import com.google.protobuf.empty.Empty
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductAPI(context: EventSourcedEntityContext) extends AbstractProductAPI {
  override def emptyState: ProductState =
    throw new UnsupportedOperationException("Not implemented yet, replace with your empty entity state")

  override def createProduct(currentState: ProductState, apiCreateProduct: ApiCreateProduct): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `CreateProduct` is not implemented, yet")

  override def updateProductInfo(currentState: ProductState, apiUpdateProductInfo: ApiUpdateProductInfo): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `UpdateProductInfo` is not implemented, yet")

  override def deleteProduct(currentState: ProductState, apiDeleteProduct: ApiDeleteProduct): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `DeleteProduct` is not implemented, yet")

  override def activateProduct(currentState: ProductState, apiActivateProduct: ApiActivateProduct): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `ActivateProduct` is not implemented, yet")

  override def inactivateProduct(currentState: ProductState, apiInactivateProduct: ApiInactivateProduct): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `InactivateProduct` is not implemented, yet")

  override def getProductInfo(currentState: ProductState, apiGetProductInfo: ApiGetProductInfo): EventSourcedEntity.Effect[ApiProductInfoResult] =
    effects.error("The command handler for `GetProductInfo` is not implemented, yet")

  override def getProductsInStore(currentState: ProductState, apiGetProductsInStore: ApiGetProductsInStore): EventSourcedEntity.Effect[ApiProductsInStore] =
    effects.error("The command handler for `GetProductsInStore` is not implemented, yet")

  override def getTicketsForEvent(currentState: ProductState, apiGetTicketsForEvent: ApiGetTicketsForEvent): EventSourcedEntity.Effect[ApiTicketsForEvent] =
    effects.error("The command handler for `GetTicketsForEvent` is not implemented, yet")

  override def productCreated(currentState: ProductState, productCreated: ProductCreated): ProductState =
    throw new RuntimeException("The event handler for `ProductCreated` is not implemented, yet")

  override def productInfoUpdated(currentState: ProductState, productInfoUpdated: ProductInfoUpdated): ProductState =
    throw new RuntimeException("The event handler for `ProductInfoUpdated` is not implemented, yet")

  override def productDeleted(currentState: ProductState, productDeleted: ProductDeleted): ProductState =
    throw new RuntimeException("The event handler for `ProductDeleted` is not implemented, yet")

  override def productActivated(currentState: ProductState, productActivated: ProductActivated): ProductState =
    throw new RuntimeException("The event handler for `ProductActivated` is not implemented, yet")

  override def productInactivated(currentState: ProductState, productInactivated: ProductInactivated): ProductState =
    throw new RuntimeException("The event handler for `ProductInactivated` is not implemented, yet")

}
