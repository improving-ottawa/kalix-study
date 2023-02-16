package app.improving.storecontext

import app.improving.StoreId
import app.improving.storecontext.infrastructure.util._
import app.improving.storecontext.store.{ApiStore, ApiStoreStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllStoresViewImpl(context: ViewContext) extends AbstractAllStoresView {

  override def emptyState: ApiStore = ApiStore.defaultInstance

  override def processStoreCreated(
      state: ApiStore,
      storeCreated: StoreCreated
  ): UpdateEffect[ApiStore] =
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        ApiStore(
          storeCreated.storeId.getOrElse(StoreId.defaultInstance).id,
          storeCreated.info.map(convertStoreInfoToApiStoreInfo),
          storeCreated.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
          ApiStoreStatus.API_STORE_STATUS_DRAFT
        )
      )

  override def processStoreDeleted(
      state: ApiStore,
      storeDeleted: StoreDeleted
  ): UpdateEffect[ApiStore] = effects.deleteState()

  override def processStoreOpened(
      state: ApiStore,
      storeOpened: StoreOpened
  ): UpdateEffect[ApiStore] = {
    effects.updateState(
      ApiStore(
        storeOpened.storeId.getOrElse(StoreId.defaultInstance).id,
        storeOpened.info.map(convertStoreInfoToApiStoreInfo),
        storeOpened.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        ApiStoreStatus.API_STORE_STATUS_OPEN
      )
    )
  }

  override def processStoreUpdated(
      state: ApiStore,
      storeUpdated: StoreUpdated
  ): UpdateEffect[ApiStore] = {
    effects.updateState(
      ApiStore(
        storeUpdated.storeId.getOrElse(StoreId.defaultInstance).id,
        storeUpdated.info.map(convertStoreInfoToApiStoreInfo),
        storeUpdated.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        storeUpdated.meta
          .map(meta => convertStoreStatusToApiStoreStatus(meta.status))
          .getOrElse(ApiStoreStatus.API_STORE_STATUS_UNKNOWN)
      )
    )
  }
  override def processStoreClosed(
      state: ApiStore,
      storeClosed: StoreClosed
  ): UpdateEffect[ApiStore] =
    effects.updateState(
      ApiStore(
        storeClosed.storeId.getOrElse(StoreId.defaultInstance).id,
        storeClosed.info.map(convertStoreInfoToApiStoreInfo),
        storeClosed.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        ApiStoreStatus.API_STORE_STATUS_CLOSED
      )
    )

  override def processProductsAddedToStore(
      state: ApiStore,
      productsAddedToStore: ProductsAddedToStore
  ): UpdateEffect[ApiStore] =
    effects.updateState(
      ApiStore(
        productsAddedToStore.storeId.getOrElse(StoreId.defaultInstance).id,
        productsAddedToStore.info.map(convertStoreInfoToApiStoreInfo),
        productsAddedToStore.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        ApiStoreStatus.API_STORE_STATUS_CLOSED
      )
    )
  override def processProductsRemovedFromStore(
      state: ApiStore,
      productsRemovedFromStore: ProductsRemovedFromStore
  ): UpdateEffect[ApiStore] =
    effects.updateState(
      ApiStore(
        productsRemovedFromStore.storeId.getOrElse(StoreId.defaultInstance).id,
        productsRemovedFromStore.info.map(convertStoreInfoToApiStoreInfo),
        productsRemovedFromStore.meta.map(
          convertStoreMetaInfoToApiStoreMetaInfo
        ),
        ApiStoreStatus.API_STORE_STATUS_CLOSED
      )
    )
}
