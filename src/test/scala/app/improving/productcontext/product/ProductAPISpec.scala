package app.improving.productcontext.product

import app.improving.productcontext.product
import com.google.protobuf.empty.Empty
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class ProductAPISpec extends AnyWordSpec with Matchers {
  "The ProductAPI" should {
    "have example test that can be removed" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // use the testkit to execute a command:
      // val result: EventSourcedResult[R] = testKit.someOperation(SomeRequest("id"));
      // verify the emitted events
      // val actualEvent: ExpectedEvent = result.nextEventOfType[ExpectedEvent]
      // actualEvent shouldBe expectedEvent
      // verify the final state after applying the events
      // testKit.state() shouldBe expectedState
      // verify the reply
      // result.reply shouldBe expectedReply
      // verify the final state after the command
    }

    "correctly process commands of type CreateProduct" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.createProduct(ApiCreateProduct(...))
    }

    "correctly process commands of type UpdateProductInfo" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.updateProductInfo(ApiUpdateProductInfo(...))
    }

    "correctly process commands of type DeleteProduct" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.deleteProduct(ApiDeleteProduct(...))
    }

    "correctly process commands of type ActivateProduct" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.activateProduct(ApiActivateProduct(...))
    }

    "correctly process commands of type InactivateProduct" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.inactivateProduct(ApiInactivateProduct(...))
    }

    "correctly process commands of type GetProductInfo" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[ApiProductInfoResult] = testKit.getProductInfo(ApiGetProductInfo(...))
    }

    "correctly process commands of type GetProductsInStore" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[ApiProductsInStore] = testKit.getProductsInStore(ApiGetProductsInStore(...))
    }

    "correctly process commands of type GetTicketsForEvent" in {
      val testKit = ProductAPITestKit(new ProductAPI(_))
      pending
      // val result: EventSourcedResult[ApiTicketsForEvent] = testKit.getTicketsForEvent(ApiGetTicketsForEvent(...))
    }
  }
}
