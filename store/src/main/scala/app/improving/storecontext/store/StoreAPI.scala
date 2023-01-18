package app.improving.storecontext.store

import app.improving.{ApiProductId, MemberId, ProductId, StoreId}
import app.improving.storecontext.{
  ProductsAddedToStore,
  ProductsRemovedFromStore,
  StoreClosed,
  StoreCreated,
  StoreDeleted,
  StoreMetaInfo,
  StoreOpened,
  StoreStatus,
  StoreUpdated
}
import com.google.protobuf.empty.Empty
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext
import com.google.protobuf.timestamp.Timestamp
// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class StoreAPI(context: EventSourcedEntityContext) extends AbstractStoreAPI {
  override def emptyState: StoreState = StoreState.defaultInstance

  override def createStore(
      currentState: StoreState,
      apiCreateStore: ApiCreateStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(_) => effects.reply(Empty.defaultInstance)
      case _ => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val memberIdOpt =
          apiCreateStore.creatingMember.map(member => MemberId(member.memberId))
        val event = StoreCreated(
          Some(StoreId(java.util.UUID.randomUUID().toString)),
          apiCreateStore.info.map(convertApiStoreInfoToStoreInfo),
          Some(
            StoreMetaInfo(
              memberIdOpt,
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              StoreStatus.DRAFT
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
    }
  }

  override def updateStore(
      currentState: StoreState,
      apiUpdateStore: ApiUpdateStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(store)
          if store.storeId == Some(
            StoreId(apiUpdateStore.storeId)
          ) && store.meta.map(_.status) != Some(StoreStatus.DELETED) => {
        val event = StoreUpdated(
          Some(StoreId(apiUpdateStore.storeId)),
          apiUpdateStore.info.map(convertApiStoreInfoToStoreInfo),
          apiUpdateStore.meta.map(convertApiStoreMetaInfoToStoreMetaInfo)
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def deleteStore(
      currentState: StoreState,
      apiDeleteStore: ApiDeleteStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(store)
          if store.storeId == Some(
            StoreId(apiDeleteStore.storeId)
          ) && store.meta.map(_.status) != Some(StoreStatus.DELETED) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = StoreDeleted(
          Some(StoreId(apiDeleteStore.storeId)),
          store.meta.map(
            _.copy(
              lastModifiedBy = apiDeleteStore.deletingMember.map(member =>
                MemberId(member.memberId)
              ),
              lastModifiedOn = Some(timestamp),
              status = StoreStatus.DELETED
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def openStore(
      currentState: StoreState,
      apiOpenStore: ApiOpenStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(store)
          if store.storeId == Some(StoreId(apiOpenStore.storeId)) && store.meta
            .map(_.status) != Some(StoreStatus.DELETED) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = StoreOpened(
          Some(StoreId(apiOpenStore.storeId)),
          store.info,
          store.meta.map(
            _.copy(
              lastModifiedBy = apiOpenStore.openingMember.map(member =>
                MemberId(member.memberId)
              ),
              lastModifiedOn = Some(timestamp),
              status = StoreStatus.OPEN
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def closeStore(
      currentState: StoreState,
      apiCloseStore: ApiCloseStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(store)
          if store.storeId == Some(StoreId(apiCloseStore.storeId)) && store.meta
            .map(_.status) != Some(StoreStatus.DELETED) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = StoreClosed(
          Some(StoreId(apiCloseStore.storeId)),
          store.info,
          store.meta.map(
            _.copy(
              lastModifiedBy = apiCloseStore.closingMember.map(member =>
                MemberId(member.memberId)
              ),
              lastModifiedOn = Some(timestamp),
              status = StoreStatus.CLOSED
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def addProductsToStore(
      currentState: StoreState,
      apiAddProductToStore: ApiAddProductsToStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(store)
          if store.storeId == Some(
            StoreId(apiAddProductToStore.storeId)
          ) && store.meta
            .map(_.status) != Some(StoreStatus.DELETED) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val currentProducts = store.info.map(_.products).getOrElse(Seq.empty)
        val event = ProductsAddedToStore(
          Some(StoreId(apiAddProductToStore.storeId)),
          store.info.map(
            _.copy(
              products =
                currentProducts ++ apiAddProductToStore.products.map(product =>
                  ProductId(product.productId)
                )
            )
          ),
          store.meta.map(
            _.copy(
              lastModifiedBy = apiAddProductToStore.addingMember.map(member =>
                MemberId(member.memberId)
              ),
              lastModifiedOn = Some(timestamp)
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def removeProductsFromStore(
      currentState: StoreState,
      apiRemoveProductFromStore: ApiRemoveProductsFromStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(store)
          if store.storeId == Some(
            StoreId(apiRemoveProductFromStore.storeId)
          ) && store.meta
            .map(_.status) != Some(StoreStatus.DELETED) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val currentProducts = store.info.map(_.products).getOrElse(Seq.empty)
        val productsToRemove = apiRemoveProductFromStore.products.map(product =>
          ProductId(product.productId)
        )
        val event = ProductsRemovedFromStore(
          Some(StoreId(apiRemoveProductFromStore.storeId)),
          store.info.map(
            _.copy(
              products = currentProducts.filterNot(productId =>
                productsToRemove.contains(productId)
              )
            )
          ),
          store.meta.map(
            _.copy(
              lastModifiedBy =
                apiRemoveProductFromStore.removingMember.map(member =>
                  MemberId(member.memberId)
                ),
              lastModifiedOn = Some(timestamp)
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def getProductsInStore(
      currentState: StoreState,
      apiGetProductsInStore: ApiGetProductsInStore
  ): EventSourcedEntity.Effect[ApiProductsInStore] = {
    currentState.store match {
      case Some(store)
          if store.storeId == Some(StoreId(apiGetProductsInStore.storeId))
            && store.meta
              .map(_.status) != Some(StoreStatus.DELETED) => {
        val currentProducts = store.info.map(_.products).getOrElse(Seq.empty)
        val result = ApiProductsInStore(
          apiGetProductsInStore.storeId,
          currentProducts.map(productId => ApiProductId(productId.id))
        )
        effects.reply(result)
      }
      case _ => effects.reply(ApiProductsInStore.defaultInstance)
    }
  }

  override def storeCreated(
      currentState: StoreState,
      storeCreated: StoreCreated
  ): StoreState = {
    currentState.store match {
      case Some(_) => currentState
      case _ => {
        currentState.withStore(
          Store(
            storeCreated.storeId,
            storeCreated.info,
            storeCreated.meta,
            StoreStatus.DRAFT
          )
        )
      }
    }
  }

  override def storeDeleted(
      currentState: StoreState,
      storeDeleted: StoreDeleted
  ): StoreState = {
    currentState.store match {
      case Some(store)
          if store.storeId == storeDeleted.storeId
            && store.meta
              .map(_.status) != Some(StoreStatus.DELETED) => {
        currentState.withStore(
          store.copy(
            meta = storeDeleted.meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def storeOpened(
      currentState: StoreState,
      storeOpened: StoreOpened
  ): StoreState = {
    currentState.store match {
      case Some(store)
          if store.storeId == storeOpened.storeId
            && store.meta
              .map(_.status) != Some(StoreStatus.DELETED) => {
        currentState.withStore(
          store.copy(
            info = storeOpened.info,
            meta = storeOpened.meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def storeClosed(
      currentState: StoreState,
      storeClosed: StoreClosed
  ): StoreState = {
    currentState.store match {
      case Some(store)
          if store.storeId == storeClosed.storeId
            && store.meta
              .map(_.status) != Some(StoreStatus.DELETED) => {
        currentState.withStore(
          store.copy(
            info = storeClosed.info,
            meta = storeClosed.meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def productsAddedToStore(
      currentState: StoreState,
      productAddedToStore: ProductsAddedToStore
  ): StoreState = {
    currentState.store match {
      case Some(store)
          if store.storeId == productAddedToStore.storeId
            && store.meta
              .map(_.status) != Some(StoreStatus.DELETED) => {
        currentState.withStore(
          store.copy(
            info = productAddedToStore.info,
            meta = productAddedToStore.meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def productsRemovedFromStore(
      currentState: StoreState,
      productRemovedFromStore: ProductsRemovedFromStore
  ): StoreState = {
    currentState.store match {
      case Some(store)
          if store.storeId == productRemovedFromStore.storeId
            && store.meta
              .map(_.status) != Some(StoreStatus.DELETED) => {
        currentState.withStore(
          store.copy(
            info = productRemovedFromStore.info,
            meta = productRemovedFromStore.meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def storeUpdated(
      currentState: StoreState,
      storeUpdated: StoreUpdated
  ): StoreState = {
    currentState.store match {
      case Some(store)
          if store.storeId == storeUpdated.storeId
            && store.meta
              .map(_.status) != Some(StoreStatus.DELETED) => {
        currentState.withStore(
          store.copy(
            info = storeUpdated.info,
            meta = storeUpdated.meta
          )
        )
      }
      case _ => currentState
    }
  }
}
