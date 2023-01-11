package app.improving.storecontext.store

import app.improving.storecontext.ProductAddedToStore
import app.improving.storecontext.ProductInventoryDecremented
import app.improving.storecontext.ProductInventoryIncremented
import app.improving.storecontext.ProductRemovedFromStore
import app.improving.storecontext.StoreClosed
import app.improving.storecontext.StoreCreated
import app.improving.storecontext.StoreDeleted
import app.improving.storecontext.StoreOpened
import app.improving.storecontext.store
import com.google.protobuf.empty.Empty
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class StoreAPI(context: EventSourcedEntityContext) extends AbstractStoreAPI {
  override def emptyState: StoreState =
    throw new UnsupportedOperationException("Not implemented yet, replace with your empty entity state")

  override def createStore(currentState: StoreState, apiCreateStore: ApiCreateStore): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `CreateStore` is not implemented, yet")

  override def updateStore(currentState: StoreState, apiUpdateStore: ApiUpdateStore): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `UpdateStore` is not implemented, yet")

  override def deleteStore(currentState: StoreState, apiDeleteStore: ApiDeleteStore): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `DeleteStore` is not implemented, yet")

  override def openStore(currentState: StoreState, apiOpenStore: ApiOpenStore): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `OpenStore` is not implemented, yet")

  override def closeStore(currentState: StoreState, apiCloseStore: ApiCloseStore): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `CloseStore` is not implemented, yet")

  override def addProductToStore(currentState: StoreState, apiAddProductToStore: ApiAddProductToStore): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `AddProductToStore` is not implemented, yet")

  override def removeProductFromStore(currentState: StoreState, apiRemoveProductFromStore: ApiRemoveProductFromStore): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `RemoveProductFromStore` is not implemented, yet")

  override def incrementProductInventory(currentState: StoreState, apiIncrementProductInventory: ApiIncrementProductInventory): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `IncrementProductInventory` is not implemented, yet")

  override def decrementProductInventory(currentState: StoreState, apiDecrementProductInventory: ApiDecrementProductInventory): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `DecrementProductInventory` is not implemented, yet")

  override def getStoreInfo(currentState: StoreState, apiGetStoreInfo: ApiGetStoreInfo): EventSourcedEntity.Effect[Empty] =
    effects.error("The command handler for `GetStoreInfo` is not implemented, yet")

  override def storeCreated(currentState: StoreState, storeCreated: StoreCreated): StoreState =
    throw new RuntimeException("The event handler for `StoreCreated` is not implemented, yet")

  override def storeDeleted(currentState: StoreState, storeDeleted: StoreDeleted): StoreState =
    throw new RuntimeException("The event handler for `StoreDeleted` is not implemented, yet")

  override def storeOpened(currentState: StoreState, storeOpened: StoreOpened): StoreState =
    throw new RuntimeException("The event handler for `StoreOpened` is not implemented, yet")

  override def storeClosed(currentState: StoreState, storeClosed: StoreClosed): StoreState =
    throw new RuntimeException("The event handler for `StoreClosed` is not implemented, yet")

  override def productAddedToStore(currentState: StoreState, productAddedToStore: ProductAddedToStore): StoreState =
    throw new RuntimeException("The event handler for `ProductAddedToStore` is not implemented, yet")

  override def productRemovedFromStore(currentState: StoreState, productRemovedFromStore: ProductRemovedFromStore): StoreState =
    throw new RuntimeException("The event handler for `ProductRemovedFromStore` is not implemented, yet")

  override def productInventoryIncremented(currentState: StoreState, productInventoryIncremented: ProductInventoryIncremented): StoreState =
    throw new RuntimeException("The event handler for `ProductInventoryIncremented` is not implemented, yet")

  override def productInventoryDecremented(currentState: StoreState, productInventoryDecremented: ProductInventoryDecremented): StoreState =
    throw new RuntimeException("The event handler for `ProductInventoryDecremented` is not implemented, yet")

}
