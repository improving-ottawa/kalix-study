package app.improving.storecontext

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
  ): UpdateEffect[ApiStore] = {
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        ApiStore(
          storeCreated.storeId
            .map(_.id)
            .getOrElse("StoreId IS NOT FOUND."),
          storeCreated.info.map(convertStoreInfoToApiStoreInfo),
          storeCreated.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
          ApiStoreStatus.DRAFT
        )
      )
  }

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
        storeOpened.storeId
          .map(_.id)
          .getOrElse("StoreId IS NOT FOUND."),
        storeOpened.info.map(convertStoreInfoToApiStoreInfo),
        storeOpened.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        ApiStoreStatus.OPEN
      )
    )
  }

  override def processStoreUpdated(
      state: ApiStore,
      storeUpdated: StoreUpdated
  ): UpdateEffect[ApiStore] = {
    effects.updateState(
      ApiStore(
        storeUpdated.storeId
          .map(_.id)
          .getOrElse("StoreId IS NOT FOUND."),
        storeUpdated.info.map(convertStoreInfoToApiStoreInfo),
        storeUpdated.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        storeUpdated.meta
          .map(meta => convertStoreStatusToApiStoreStatus(meta.status))
          .getOrElse(ApiStoreStatus.UNKNOWN)
      )
    )
  }
  override def processStoreClosed(
      state: ApiStore,
      storeClosed: StoreClosed
  ): UpdateEffect[ApiStore] = {
    effects.updateState(
      ApiStore(
        storeClosed.storeId
          .map(_.id)
          .getOrElse("StoreId IS NOT FOUND."),
        storeClosed.info.map(convertStoreInfoToApiStoreInfo),
        storeClosed.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        ApiStoreStatus.CLOSED
      )
    )
  }

  override def processProductsAddedToStore(
      state: ApiStore,
      productsAddedToStore: ProductsAddedToStore
  ): UpdateEffect[ApiStore] = {
    effects.updateState(
      ApiStore(
        productsAddedToStore.storeId
          .map(_.id)
          .getOrElse("StoreId IS NOT FOUND."),
        productsAddedToStore.info.map(convertStoreInfoToApiStoreInfo),
        productsAddedToStore.meta.map(convertStoreMetaInfoToApiStoreMetaInfo),
        ApiStoreStatus.CLOSED
      )
    )
  }
  override def processProductsRemovedFromStore(
      state: ApiStore,
      productsRemovedFromStore: ProductsRemovedFromStore
  ): UpdateEffect[ApiStore] = {
    effects.updateState(
      ApiStore(
        productsRemovedFromStore.storeId
          .map(_.id)
          .getOrElse("StoreId IS NOT FOUND."),
        productsRemovedFromStore.info.map(convertStoreInfoToApiStoreInfo),
        productsRemovedFromStore.meta.map(
          convertStoreMetaInfoToApiStoreMetaInfo
        ),
        ApiStoreStatus.CLOSED
      )
    )
  }
}
