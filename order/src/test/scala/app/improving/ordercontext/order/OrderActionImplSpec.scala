package app.improving.ordercontext.order

import app.improving._
import com.google.protobuf.empty.Empty
import kalix.scalasdk.testkit.MockRegistry
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrderActionImplSpec
    extends AnyWordSpec
    with Matchers
    with AsyncMockFactory {

  "OrderActionImpl" must {

    "handle command purchase ticket" in {
      val mockOrderService = mock[OrderAction]
      (mockOrderService.purchaseTicket _)
        .when(*)
        .returns(Future.successful(ApiOrderId.defaultInstance))

      val mockRegistry = MockRegistry.withMock(mockOrderService)

      val service = OrderActionImplTestKit(new OrderActionImpl(_), mockRegistry)

      val apiCreateOrder = ApiCreateOrder(
        "test-id",
        Some(
          ApiOrderInfo(
            Seq[ApiLineItem](
              ApiLineItem(Some(ApiSku("pro-id-1")), 10.0, 10, 100.0)
            ),
            Some("special instruction")
          )
        ),
        Some(ApiMemberId("test-member1")),
        Some(ApiStoreId("store-id"))
      )
      service
        .purchaseTicket(apiCreateOrder)
        .reply shouldBe Empty.defaultInstance
    }

  }
}
