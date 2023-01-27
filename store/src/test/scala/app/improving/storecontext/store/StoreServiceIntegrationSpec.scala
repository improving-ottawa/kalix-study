package app.improving.storecontext.store

import app.improving.ApiMemberId
import app.improving.storecontext.store.TestData._
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.{BeforeAndAfterAll, Ignore}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

@Ignore
class StoreServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  trait Fixture {

    private val testKit = KalixTestKit(Main.createKalix()).start()

    protected val client = testKit.getGrpcClient(classOf[StoreService])

    val command = ApiCreateStore(
      testStoreId,
      Some(apiStoreInfo),
      Some(ApiMemberId(testMember2))
    )

    client.createStore(command).futureValue
  }

  "StoreService" must {

    "create store correctly" in new Fixture {
      val apiGetProductsInStore = ApiGetProductsInStore(
        testStoreId
      )

      val productsInStore =
        client.getProductsInStore(apiGetProductsInStore).futureValue

      productsInStore.products shouldBe testProducts
    }

    "update store correctly" in new Fixture {
      val apiUpdateStore = ApiUpdateStore(
        testStoreId,
        Some(apiStoreInfoUpdate),
        Some(apiStoreMetaInfo)
      )

      client.updateStore(apiUpdateStore).futureValue

      val apiGetProductsInStore = ApiGetProductsInStore(
        testStoreId
      )

      val updatedProductsInStore =
        client.getProductsInStore(apiGetProductsInStore).futureValue

      updatedProductsInStore.products shouldBe testProductsUpdate
    }

    "delete correctly" in new Fixture {
      val apiDeleteStore = ApiDeleteStore(
        testStoreId,
        Some(ApiMemberId(testMember2))
      )

      client.deleteStore(apiDeleteStore).futureValue

      val apiGetProductsInStore = ApiGetProductsInStore(
        testStoreId
      )

      val deletedStore =
        client.getProductsInStore(apiGetProductsInStore).futureValue

      deletedStore.products shouldBe Seq.empty
    }
  }

  override def beforeAll(): Unit = {
    super.beforeAll()

  }

  override def afterAll(): Unit = {
    super.afterAll()
  }
}
