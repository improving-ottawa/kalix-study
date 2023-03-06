package app.improving.productcontext.product

import TestData._
import app.improving.ApiMemberId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductEventsServiceActionSpec extends AnyWordSpec with Matchers {

  "ProductEventsServiceAction" must {

    "handle command TransformProductCreated" in {
      val service =
        ProductEventsServiceActionTestKit(new ProductEventsServiceAction(_))
      val result = service.transformProductCreated(productCreated)

      result.reply.info shouldBe Some(apiProductInfo)
      result.reply.meta shouldBe Some(apiProductMetaInfo)
      result.reply.sku.isEmpty shouldBe false
    }

    "handle command TransformProductInfoUpdated" in {
      val service =
        ProductEventsServiceActionTestKit(new ProductEventsServiceAction(_))

      val result = service.transformProductInfoUpdated(productInfoUpdated)

      result.reply.sku.isEmpty shouldBe false
      result.reply.info shouldBe Some(apiProductInfo)
      result.reply.meta shouldBe Some(apiProductMetaInfo)
    }

    "handle command TransformProductDeleted" in {
      val service =
        ProductEventsServiceActionTestKit(new ProductEventsServiceAction(_))
      val result = service.transformProductDeleted(productDeleted)

      result.reply.sku.isEmpty shouldBe false
      result.reply.deletingMember shouldBe Some(ApiMemberId(testMemberId))
    }

    "handle command TransformProductActivated" in {
      val service =
        ProductEventsServiceActionTestKit(new ProductEventsServiceAction(_))
      pending
      val result = service.transformProductActivated(productActivated)

      result.reply.sku.isEmpty shouldBe false
      result.reply.activatingMember shouldBe Some(ApiMemberId(testMemberId))
    }

    "handle command TransformProductInactivated" in {
      val service =
        ProductEventsServiceActionTestKit(new ProductEventsServiceAction(_))

      val result = service.transformProductInactivated(productInactivated)

      result.reply.sku.isEmpty shouldBe false
      result.reply.inactivatingMember shouldBe Some(ApiMemberId(testMemberId))
    }

  }
}
