package app.improving.storecontext.store

import app.improving.storecontext.infrastructure.util._
import app.improving.{ApiProductId, ApiStoreId, MemberId, ProductId, StoreId}
import app.improving.storecontext.{ProductsAddedToStore, ProductsRemovedFromStore, StoreClosed, StoreCreated, StoreDeleted, StoreMadeReady, StoreMetaInfo, StoreOpened, StoreStatus, StoreUpdated}
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
  ): EventSourcedEntity.Effect[ApiStoreId] = {
    currentState.store match {
      case Some(_) => effects.reply(ApiStoreId.defaultInstance)
      case _ => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val memberIdOpt = {
          apiCreateStore.creatingMember.map(member => MemberId(member.memberId))
        }
        val storeId = apiCreateStore.storeId
        val event = StoreCreated(
          Some(StoreId(storeId)),
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
        effects.emitEvent(event).thenReply(_ => ApiStoreId(storeId))
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
          ) && !store.status.isDeleted => {
        val updateInfoOpt = apiUpdateStore.info.map(convertApiStoreUpdateInfoToStoreUpdateInfo)
        val updatedInfoOpt = store.info.map {storeInfo =>
          updateInfoOpt.fold(storeInfo) { updateInfo =>
            storeInfo.copy(
              name = updateInfo.name.getOrElse(storeInfo.name),
              description = updateInfo.description.getOrElse(storeInfo.description),
              products = if (updateInfo.products.isEmpty) storeInfo.products else updateInfo.products,
              event = updateInfo.event.orElse(storeInfo.event),
              venue = updateInfo.venue.orElse(storeInfo.venue),
              location = updateInfo.location.orElse(storeInfo.location),
              sponsoringOrg = updateInfo.sponsoringOrg.orElse(storeInfo.sponsoringOrg)
            )
          }
        }
        val event = StoreUpdated(
          Some(StoreId(apiUpdateStore.storeId)),
          updatedInfoOpt,
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
          ) && (store.status.isDraft || store.status.isClosed) => {
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

  override def makeStoreReady(
    currentState: StoreState,
    apiReadyStore: ApiReadyStore
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.store match {
      case Some(store)
        if store.storeId == Some(StoreId(apiReadyStore.storeId)) &&
          store.status.isDraft => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = StoreMadeReady(
          Some(StoreId(apiReadyStore.storeId)),
          store.info,
          store.meta.map(
            _.copy(
              lastModifiedBy = apiReadyStore.readyingMember.map(member =>
                MemberId(member.memberId)
              ),
              lastModifiedOn = Some(timestamp),
              status = StoreStatus.READY
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
          if store.storeId == Some(StoreId(apiOpenStore.storeId)) &&
            (store.status.isReady || store.status.isClosed) => {
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
          if store.storeId == Some(StoreId(apiCloseStore.storeId)) &&
            (store.status.isReady || store.status.isOpen) => {
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
          ) && !store.status.isDeleted => {
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
          ) && !store.status.isDeleted => {
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
            && !store.status.isDeleted => {
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
            && (store.status.isDraft || store.status.isClosed) => {
        currentState.withStore(
          store.copy(
            meta = storeDeleted.meta,
            status = StoreStatus.DELETED
          )
        )
      }
      case _ => currentState
    }
  }

  override def storeMadeReady(
    currentState: StoreState,
    storeMadeReady: StoreMadeReady
  ): StoreState = {
    currentState.store match {
      case Some(store)
        if store.storeId == storeMadeReady.storeId
          && store.status.isDraft => {
        currentState.withStore(
          store.copy(
            info = storeMadeReady.info,
            meta = storeMadeReady.meta,
            status = StoreStatus.READY
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
            && (store.status.isReady || store.status.isClosed) => {
        currentState.withStore(
          store.copy(
            info = storeOpened.info,
            meta = storeOpened.meta,
            status = StoreStatus.OPEN
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
            && (store.status.isReady || store.status.isOpen) => {
        currentState.withStore(
          store.copy(
            info = storeClosed.info,
            meta = storeClosed.meta,
            status = StoreStatus.CLOSED
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
            && !store.status.isDeleted => {
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
            && !store.status.isDeleted => {
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
            && !store.status.isDeleted => {
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
