package app.improving.productcontext.product

import app.improving.productcontext.infrastructure.util._
import app.improving.{ApiMemberId, ApiProductId, MemberId, ProductId}
import app.improving.productcontext.{
  ProductActivated,
  ProductCreated,
  ProductDeleted,
  ProductInactivated,
  ProductInfoUpdated,
  ProductStatus
}
import app.improving.productcontext.product.TestData._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductAPISpec extends AnyWordSpec with Matchers {
  "The ProductAPI" should {

    "correctly process commands of type CreateProduct" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      val apiCreateProduct = ApiCreateProduct(
        Some(testSku),
        Some(apiProductInfo),
        Some(apiProductMetaInfo)
      )

      val createProductResult =
        testKit.createProduct(apiCreateProduct)

      createProductResult.events should have size 1

      val productCreated =
        createProductResult.nextEvent[ProductCreated]

      productCreated.sku.isDefined shouldBe true
      productCreated.info.isDefined shouldBe true
      productCreated.meta.isDefined shouldBe true

      testKit.currentState.product.map(_.status) shouldBe Some(
        ProductStatus.PRODUCT_STATUS_DRAFT
      )
    }

    "correctly process commands of type UpdateProductInfo" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      val apiCreateProduct = ApiCreateProduct(
        Some(testSku),
        Some(apiProductInfo),
        Some(apiProductMetaInfo)
      )

      val createProductResult =
        testKit.createProduct(apiCreateProduct)

      createProductResult.events should have size 1

      val productCreated =
        createProductResult.nextEvent[ProductCreated]

      productCreated.sku.isDefined shouldBe true

      val sku = testKit.currentState.product
        .flatMap(_.sku)
        .map(_.id)
        .getOrElse("ProductId is not found.")
      val updateProductInfoResult =
        testKit.updateProductInfo(
          apiUpdateProductInfo.copy(sku = Some(ApiProductId(sku)))
        )

      updateProductInfoResult.events should have size 1

      val productInfoUpdate =
        updateProductInfoResult.nextEvent[ProductInfoUpdated]

      productInfoUpdate.info shouldBe Some(
        convertApiProductInfoToProductInfo(
          apiProductInfoAfterUpdate
        )
      )

      val nullUppdateProductInfoResult =
        testKit.updateProductInfo(anotherApiUpdateProductInfo)

      nullUppdateProductInfoResult.events should have size 0
    }

    "correctly process commands of type DeleteProduct" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      val apiCreateProduct = ApiCreateProduct(
        Some(testSku),
        Some(apiProductInfo),
        Some(apiProductMetaInfo)
      )

      val createProductResult =
        testKit.createProduct(apiCreateProduct)

      createProductResult.events should have size 1

      val productCreated =
        createProductResult.nextEvent[ProductCreated]

      productCreated.sku.isDefined shouldBe true
      val sku = testKit.currentState.product
        .flatMap(_.sku)
      val deleteProductResult =
        testKit.deleteProduct(
          apiDeleteProduct.copy(sku = sku.map(id => ApiProductId(id.id)))
        )

      deleteProductResult.events should have size 1

      val deleteProduct =
        deleteProductResult.nextEvent[ProductDeleted]

      deleteProduct.deletingMember shouldBe Some(MemberId(testMemberId1))

      testKit.currentState.product.map(_.status) shouldBe Some(
        ProductStatus.PRODUCT_STATUS_DELETED
      )
    }

    "correctly process commands of type InactivateProduct and ActivateProduct" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      val apiCreateProduct = ApiCreateProduct(
        Some(testSku),
        Some(apiProductInfo),
        Some(apiProductMetaInfo)
      )

      val createProductResult =
        testKit.createProduct(apiCreateProduct)

      createProductResult.events should have size 1

      val productCreated =
        createProductResult.nextEvent[ProductCreated]

      productCreated.sku.isDefined shouldBe true

      testKit.currentState.product.map(_.status) shouldBe Some(
        ProductStatus.PRODUCT_STATUS_DRAFT
      )
      val sku = testKit.currentState.product
        .flatMap(_.sku)
      val apiInactivateProduct = ApiInactivateProduct(
        sku.map(id => ApiProductId(id.id)),
        Some(ApiMemberId(testMemberId1))
      )

      val inactivateProductResult =
        testKit.inactivateProduct(apiInactivateProduct)

      inactivateProductResult.events should have size 1

      val productInactivated =
        inactivateProductResult.nextEvent[ProductInactivated]

      productInactivated.sku shouldBe sku

      testKit.currentState.product.map(_.status) shouldBe Some(
        ProductStatus.PRODUCT_STATUS_INACTIVE
      )

      val nullApiActivateProduct = ApiActivateProduct(
        Some(ApiProductId("otherSku")),
        Some(ApiMemberId(testMemberId1))
      )

      val nullApiActivateProductResult =
        testKit.activateProduct(nullApiActivateProduct)

      nullApiActivateProductResult.events should have size 0

      testKit.currentState.product.map(_.status) shouldBe Some(
        ProductStatus.PRODUCT_STATUS_INACTIVE
      )

      val apiActivateProduct = ApiActivateProduct(
        sku.map(id => ApiProductId(id.id)),
        Some(ApiMemberId(testMemberId1))
      )

      val apiActivateProductResult = testKit.activateProduct(apiActivateProduct)

      apiActivateProductResult.events should have size 1

      val productActivated =
        apiActivateProductResult.nextEvent[ProductActivated]

      productActivated.sku shouldBe sku

      testKit.currentState.product.map(_.status) shouldBe Some(
        ProductStatus.PRODUCT_STATUS_ACTIVE
      )

      val nullApiInactivateProduct = ApiInactivateProduct(
        Some(ApiProductId("othersku")),
        Some(ApiMemberId(testMemberId1))
      )

      val nullApiInActivateProductResult =
        testKit.inactivateProduct(nullApiInactivateProduct)

      nullApiInActivateProductResult.events should have size 0

      testKit.currentState.product.map(_.status) shouldBe Some(
        ProductStatus.PRODUCT_STATUS_ACTIVE
      )
    }
  }
}
