package app.improving.storecontext.store

import TestData._
import app.improving.storecontext.infrastructure.util._
import app.improving.{ApiMemberId, ApiStoreId, MemberId, ProductId}
import app.improving.storecontext.{
  ProductsAddedToStore,
  ProductsRemovedFromStore,
  StoreClosed,
  StoreCreated,
  StoreDeleted,
  StoreMadeReady,
  StoreOpened,
  StoreStatus,
  StoreUpdated
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class StoreAPISpec extends AnyWordSpec with Matchers {
  "The StoreAPI" should {

    "correctly process commands of type CreateStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))

      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.info.isDefined shouldBe true
      storeCreated.meta.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )
    }

    "correctly process commands of type UpdateStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true

      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
      val apiUpdateStore = ApiUpdateStore(
        storeId.map(id => ApiStoreId(id.id)),
        Some(apiStoreInfoUpdate),
        Some(apiStoreMetaInfo)
      )

      val updateStoreResult = testKit.updateStore(apiUpdateStore)

      updateStoreResult.events should have size 1

      val storeUpdated = updateStoreResult.nextEvent[StoreUpdated]

      storeUpdated.info shouldBe Some(
        convertApiStoreInfoToStoreInfo(
          apiStoreInfo.copy(
            name = testNameUpdate,
            description = testDescriptionUpdate,
            products = testProductsUpdate,
            event = Some(testEventUpdate),
            venue = Some(testVenueUpdate),
            location = Some(testLocaltionUpdate),
            sponsoringOrg = Some(testOrgUpdate)
          )
        )
      )
      storeUpdated.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember1)
      )
    }

    "correctly process commands of type DeleteStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )

      val nullApiDeleteStore = ApiDeleteStore(
        Some(ApiStoreId("other-id")),
        Some(ApiMemberId(testMember2))
      )

      val nullApiDeleteStoreResult = testKit.deleteStore(nullApiDeleteStore)

      nullApiDeleteStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
      val apiDeleteStore = ApiDeleteStore(
        storeId.map(id => ApiStoreId(id.id)),
        Some(ApiMemberId(testMember2))
      )

      val deleteStoreResult = testKit.deleteStore(apiDeleteStore)

      deleteStoreResult.events should have size 1

      val storeDeleted = deleteStoreResult.nextEvent[StoreDeleted]

      storeDeleted.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DELETED
      )
      storeDeleted.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
      )
    }

    "correctly process commands of type MakeStoreReady" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )

      val nullApiOpenStore = ApiOpenStore(
        Some(ApiStoreId("other-id")),
        Some(ApiMemberId(testMember2))
      )

      val nullApiOpenStoreResult = testKit.openStore(nullApiOpenStore)

      nullApiOpenStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiMakeStoreReady = ApiReadyStore(
        Some(ApiStoreId(storeId)),
        Some(ApiMemberId(testMember2))
      )

      val apiMakeStoreReadyResult = testKit.makeStoreReady(apiMakeStoreReady)

      apiMakeStoreReadyResult.events should have size 1

      val storeMadeReady = apiMakeStoreReadyResult.nextEvent[StoreMadeReady]

      storeMadeReady.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_READY
      )
    }

    "correctly process commands of type OpenStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )

      val nullApiOpenStore = ApiOpenStore(
        Some(ApiStoreId("other-id")),
        Some(ApiMemberId(testMember2))
      )

      val nullApiOpenStoreResult = testKit.openStore(nullApiOpenStore)

      nullApiOpenStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiMakeStoreReady = ApiReadyStore(
        Some(ApiStoreId(storeId)),
        Some(ApiMemberId(testMember2))
      )

      val apiMakeStoreReadyResult = testKit.makeStoreReady(apiMakeStoreReady)

      apiMakeStoreReadyResult.events should have size 1

      val storeMadeReady = apiMakeStoreReadyResult.nextEvent[StoreMadeReady]

      storeMadeReady.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_READY
      )

      val apiOpenStore = ApiOpenStore(
        Some(ApiStoreId(storeId)),
        Some(ApiMemberId(testMember2))
      )

      val apiOpenStoreResult = testKit.openStore(apiOpenStore)

      apiOpenStoreResult.events should have size 1

      val storeOpened = apiOpenStoreResult.nextEvent[StoreOpened]

      storeOpened.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_OPEN
      )
      storeOpened.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
      )
    }

    "correctly process commands of type CloseStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )

      val nullApiOpenStore = ApiOpenStore(
        Some(ApiStoreId("other-id")),
        Some(ApiMemberId(testMember2))
      )

      val nullApiOpenStoreResult = testKit.openStore(nullApiOpenStore)

      nullApiOpenStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiMakeStoreReady = ApiReadyStore(
        Some(ApiStoreId(storeId)),
        Some(ApiMemberId(testMember2))
      )

      val apiMakeStoreReadyResult = testKit.makeStoreReady(apiMakeStoreReady)

      apiMakeStoreReadyResult.events should have size 1

      val storeMadeReady = apiMakeStoreReadyResult.nextEvent[StoreMadeReady]

      storeMadeReady.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_READY
      )

      val apiOpenStore = ApiOpenStore(
        Some(ApiStoreId(storeId)),
        Some(ApiMemberId(testMember2))
      )

      val apiOpenStoreResult = testKit.openStore(apiOpenStore)

      apiOpenStoreResult.events should have size 1

      val storeOpened = apiOpenStoreResult.nextEvent[StoreOpened]

      storeOpened.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_OPEN
      )
      storeOpened.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
      )

      val nullApiCloseStore = ApiCloseStore(
        Some(ApiStoreId("other-id")),
        Some(ApiMemberId(testMember2))
      )

      val nullApiCloseStoreResult = testKit.closeStore(nullApiCloseStore)

      nullApiCloseStoreResult.events should have size 0

      val apiCloseStore = ApiCloseStore(
        Some(ApiStoreId(storeId)),
        Some(ApiMemberId(testMember2))
      )

      val apiCloseStoreResult = testKit.closeStore(apiCloseStore)

      apiCloseStoreResult.events should have size 1

      val storeClosed = apiCloseStoreResult.nextEvent[StoreClosed]

      storeClosed.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_CLOSED
      )
      storeClosed.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
      )
    }

    "correctly process commands of type AddProductToStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )

      val nullApiAddProductToStore = ApiAddProductsToStore(
        Some(ApiStoreId("other-id")),
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val nullApiAddProductToStoreResult =
        testKit.addProductsToStore(nullApiAddProductToStore)

      nullApiAddProductToStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
      val apiAddProductToStore = ApiAddProductsToStore(
        storeId.map(id => ApiStoreId(id.id)),
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val apiAddProductToStoreResult =
        testKit.addProductsToStore(apiAddProductToStore)

      apiAddProductToStoreResult.events should have size 1

      val productsAddedToStore =
        apiAddProductToStoreResult.nextEvent[ProductsAddedToStore]

      productsAddedToStore.info.map(_.products) shouldBe Some(
        testProducts ++ testProductsUpdate
      ).map(_.map(product => ProductId(product.productId)))
      productsAddedToStore.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
      )
    }

    "correctly process commands of type RemoveProductFromStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )

      val nullApiAddProductToStore = ApiAddProductsToStore(
        Some(ApiStoreId("other-id")),
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val nullApiAddProductToStoreResult =
        testKit.addProductsToStore(nullApiAddProductToStore)

      nullApiAddProductToStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
      val apiAddProductToStore = ApiAddProductsToStore(
        storeId.map(id => ApiStoreId(id.id)),
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val apiAddProductToStoreResult =
        testKit.addProductsToStore(apiAddProductToStore)

      apiAddProductToStoreResult.events should have size 1

      val productsAddedToStore =
        apiAddProductToStoreResult.nextEvent[ProductsAddedToStore]

      productsAddedToStore.info.map(_.products) shouldBe Some(
        testProducts ++ testProductsUpdate
      ).map(_.map(product => ProductId(product.productId)))
      productsAddedToStore.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
      )

      val nullApiRemoveProductsFromStore = ApiRemoveProductsFromStore(
        Some(ApiStoreId("other-id")),
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val nullApiRemoveProductsFromStoreResult =
        testKit.removeProductsFromStore(nullApiRemoveProductsFromStore)

      nullApiRemoveProductsFromStoreResult.events should have size 0

      val apiRemoveProductsFromStore = ApiRemoveProductsFromStore(
        storeId.map(id => ApiStoreId(id.id)),
        testProductsUpdate,
        Some(ApiMemberId(testMember3))
      )

      val apiRemoveProductsFromStoreResult =
        testKit.removeProductsFromStore(apiRemoveProductsFromStore)

      apiRemoveProductsFromStoreResult.events should have size 1

      val removeProductsFromStore =
        apiRemoveProductsFromStoreResult.nextEvent[ProductsRemovedFromStore]

      removeProductsFromStore.info.map(_.products) shouldBe Some(
        testProducts
      ).map(_.map(product => ProductId(product.productId)))
      removeProductsFromStore.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember3)
      )
    }

    "correctly process commands of type GetProductsInStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      val createStoreResult =
        testKit.createStore(apiCreateStore)

      createStoreResult.events should have size 1

      val storeCreated =
        createStoreResult.nextEvent[StoreCreated]

      storeCreated.storeId.isDefined shouldBe true
      storeCreated.meta.map(_.status) shouldBe Some(
        StoreStatus.STORE_STATUS_DRAFT
      )

      val nullApiGetProductInStore = ApiGetProductsInStore(
        Some(ApiStoreId("other-id"))
      )

      val nullApiGetProductInStoreResult =
        testKit.getProductsInStore(nullApiGetProductInStore)

      nullApiGetProductInStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
      val apiGetProductInStore = ApiGetProductsInStore(
        storeId.map(id => ApiStoreId(id.id))
      )

      val apiGetProductInStoreResult =
        testKit.getProductsInStore(apiGetProductInStore)

      apiGetProductInStoreResult.reply shouldBe ApiProductsInStore(
        storeId.map(id => ApiStoreId(id.id)),
        testProducts
      )
    }
  }
}
