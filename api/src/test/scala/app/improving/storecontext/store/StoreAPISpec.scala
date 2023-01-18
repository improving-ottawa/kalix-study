package app.improving.storecontext.store

import app.improving.storecontext.store
import com.google.protobuf.empty.Empty
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class StoreAPISpec extends AnyWordSpec with Matchers {
  "The StoreAPI" should {
    "have example test that can be removed" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
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

    "correctly process commands of type CreateStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.createStore(ApiCreateStore(...))
    }

    "correctly process commands of type UpdateStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.updateStore(ApiUpdateStore(...))
    }

    "correctly process commands of type DeleteStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.deleteStore(ApiDeleteStore(...))
    }

    "correctly process commands of type OpenStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.openStore(ApiOpenStore(...))
    }

    "correctly process commands of type CloseStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.closeStore(ApiCloseStore(...))
    }

    "correctly process commands of type AddProductsToStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.addProductsToStore(ApiAddProductsToStore(...))
    }

    "correctly process commands of type RemoveProductsFromStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[Empty] = testKit.removeProductsFromStore(ApiRemoveProductsFromStore(...))
    }

    "correctly process commands of type GetProductsInStore" in {
      val testKit = StoreAPITestKit(new StoreAPI(_))
      pending
      // val result: EventSourcedResult[ApiProductsInStore] = testKit.getProductsInStore(ApiGetProductsInStore(...))
    }
  }
}
