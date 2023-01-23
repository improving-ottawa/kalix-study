package app.improving.storecontext.store

import TestData._
import app.improving.storecontext.infrastructure.util._
import app.improving.{ApiMemberId, MemberId, ProductId}
import app.improving.storecontext.{
  ProductsAddedToStore,
  ProductsRemovedFromStore,
  StoreClosed,
  StoreCreated,
  StoreDeleted,
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
      storeCreated.meta.map(_.status) shouldBe Some(StoreStatus.DRAFT)
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
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiUpdateStore = ApiUpdateStore(
        storeId,
        Some(apiStoreInfoUpdate),
        Some(apiStoreMetaInfo)
      )

      val updateStoreResult = testKit.updateStore(apiUpdateStore)

      updateStoreResult.events should have size 1

      val storeUpdated = updateStoreResult.nextEvent[StoreUpdated]

      storeUpdated.info shouldBe Some(
        convertApiStoreInfoToStoreInfo(
          apiStoreInfoUpdate
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
      storeCreated.meta.map(_.status) shouldBe Some(StoreStatus.DRAFT)

      val nullApiDeleteStore = ApiDeleteStore(
        "other-id",
        Some(ApiMemberId(testMember2))
      )

      val nullApiDeleteStoreResult = testKit.deleteStore(nullApiDeleteStore)

      nullApiDeleteStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiDeleteStore = ApiDeleteStore(
        storeId,
        Some(ApiMemberId(testMember2))
      )

      val deleteStoreResult = testKit.deleteStore(apiDeleteStore)

      deleteStoreResult.events should have size 1

      val storeDeleted = deleteStoreResult.nextEvent[StoreDeleted]

      storeDeleted.meta.map(_.status) shouldBe Some(StoreStatus.DELETED)
      storeDeleted.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
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
      storeCreated.meta.map(_.status) shouldBe Some(StoreStatus.DRAFT)

      val nullApiOpenStore = ApiOpenStore(
        "other-id",
        Some(ApiMemberId(testMember2))
      )

      val nullApiOpenStoreResult = testKit.openStore(nullApiOpenStore)

      nullApiOpenStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiOpenStore = ApiOpenStore(
        storeId,
        Some(ApiMemberId(testMember2))
      )

      val apiOpenStoreResult = testKit.openStore(apiOpenStore)

      apiOpenStoreResult.events should have size 1

      val storeOpened = apiOpenStoreResult.nextEvent[StoreOpened]

      storeOpened.meta.map(_.status) shouldBe Some(StoreStatus.OPEN)
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
      storeCreated.meta.map(_.status) shouldBe Some(StoreStatus.DRAFT)

      val nullApiOpenStore = ApiOpenStore(
        "other-id",
        Some(ApiMemberId(testMember2))
      )

      val nullApiOpenStoreResult = testKit.openStore(nullApiOpenStore)

      nullApiOpenStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiOpenStore = ApiOpenStore(
        storeId,
        Some(ApiMemberId(testMember2))
      )

      val apiOpenStoreResult = testKit.openStore(apiOpenStore)

      apiOpenStoreResult.events should have size 1

      val storeOpened = apiOpenStoreResult.nextEvent[StoreOpened]

      storeOpened.meta.map(_.status) shouldBe Some(StoreStatus.OPEN)
      storeOpened.meta.flatMap(_.lastModifiedBy) shouldBe Some(
        MemberId(testMember2)
      )

      val nullApiCloseStore = ApiCloseStore(
        "other-id",
        Some(ApiMemberId(testMember2))
      )

      val nullApiCloseStoreResult = testKit.closeStore(nullApiCloseStore)

      nullApiCloseStoreResult.events should have size 0

      val apiCloseStore = ApiCloseStore(
        storeId,
        Some(ApiMemberId(testMember2))
      )

      val apiCloseStoreResult = testKit.closeStore(apiCloseStore)

      apiCloseStoreResult.events should have size 1

      val storeClosed = apiCloseStoreResult.nextEvent[StoreClosed]

      storeClosed.meta.map(_.status) shouldBe Some(StoreStatus.CLOSED)
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
      storeCreated.meta.map(_.status) shouldBe Some(StoreStatus.DRAFT)

      val nullApiAddProductToStore = ApiAddProductsToStore(
        "other-id",
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val nullApiAddProductToStoreResult =
        testKit.addProductsToStore(nullApiAddProductToStore)

      nullApiAddProductToStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiAddProductToStore = ApiAddProductsToStore(
        storeId,
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
      storeCreated.meta.map(_.status) shouldBe Some(StoreStatus.DRAFT)

      val nullApiAddProductToStore = ApiAddProductsToStore(
        "other-id",
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val nullApiAddProductToStoreResult =
        testKit.addProductsToStore(nullApiAddProductToStore)

      nullApiAddProductToStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiAddProductToStore = ApiAddProductsToStore(
        storeId,
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
        "other-id",
        testProductsUpdate,
        Some(ApiMemberId(testMember2))
      )

      val nullApiRemoveProductsFromStoreResult =
        testKit.removeProductsFromStore(nullApiRemoveProductsFromStore)

      nullApiRemoveProductsFromStoreResult.events should have size 0

      val apiRemoveProductsFromStore = ApiRemoveProductsFromStore(
        storeId,
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
      storeCreated.meta.map(_.status) shouldBe Some(StoreStatus.DRAFT)

      val nullApiGetProductInStore = ApiGetProductsInStore(
        "other-id"
      )

      val nullApiGetProductInStoreResult =
        testKit.getProductsInStore(nullApiGetProductInStore)

      nullApiGetProductInStoreResult.events should have size 0
      val storeId = testKit.currentState.store
        .flatMap(_.storeId)
        .map(_.id)
        .getOrElse("StoreId is not found.")
      val apiGetProductInStore = ApiGetProductsInStore(
        storeId
      )

      val apiGetProductInStoreResult =
        testKit.getProductsInStore(apiGetProductInStore)

      apiGetProductInStoreResult.reply shouldBe ApiProductsInStore(
        storeId,
        testProducts
      )
    }
  }
}
