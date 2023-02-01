package app.improving.storecontext

import app.improving.storecontext.store.ApiStore
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllStoresViewImpl(context: ViewContext) extends AbstractAllStoresView {

  override def emptyState: ApiStore =
    throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

  override def processStoreCreated(
      state: ApiStore,
      storeCreated: StoreCreated): UpdateEffect[ApiStore] =
    throw new UnsupportedOperationException("Update handler for 'ProcessStoreCreated' not implemented yet")

  override def processStoreDeleted(
      state: ApiStore,
      storeDeleted: StoreDeleted): UpdateEffect[ApiStore] =
    throw new UnsupportedOperationException("Update handler for 'ProcessStoreDeleted' not implemented yet")

  override def processStoreOpened(
      state: ApiStore,
      storeOpened: StoreOpened): UpdateEffect[ApiStore] =
    throw new UnsupportedOperationException("Update handler for 'ProcessStoreOpened' not implemented yet")

  override def processStoreUpdated(
      state: ApiStore,
      storeUpdated: StoreUpdated): UpdateEffect[ApiStore] =
    throw new UnsupportedOperationException("Update handler for 'ProcessStoreUpdated' not implemented yet")

  override def processStoreClosed(
      state: ApiStore,
      storeClosed: StoreClosed): UpdateEffect[ApiStore] =
    throw new UnsupportedOperationException("Update handler for 'ProcessStoreClosed' not implemented yet")

  override def processProductsAddedToStore(
      state: ApiStore,
      productsAddedToStore: ProductsAddedToStore): UpdateEffect[ApiStore] =
    throw new UnsupportedOperationException("Update handler for 'ProcessProductsAddedToStore' not implemented yet")

  override def processProductsRemovedFromStore(
      state: ApiStore,
      productsRemovedFromStore: ProductsRemovedFromStore): UpdateEffect[ApiStore] =
    throw new UnsupportedOperationException("Update handler for 'ProcessProductsRemovedFromStore' not implemented yet")

}
