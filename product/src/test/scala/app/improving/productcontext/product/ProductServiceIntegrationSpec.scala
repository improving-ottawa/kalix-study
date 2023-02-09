package app.improving.productcontext.product

import app.improving.ApiMemberId
import app.improving.productcontext.Main
import app.improving.productcontext.product.TestData._
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
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

class ProductServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  private val client = testKit.getGrpcClient(classOf[ProductService])

  "ProductService" must {

    "create product correctly" in {

      val apiGetProductInfo = ApiGetProductInfo(
        Some(testSku)
      )
      val createdProductInfo =
        client.getProductInfo(apiGetProductInfo).futureValue

      createdProductInfo.info shouldBe Some(apiProductInfo)
    }

    "activate product correctly" in {
      val apiActivateProduct = ApiActivateProduct(
        Some(testSku),
        Some(ApiMemberId(testMemberId1))
      )

      client.activateProduct(apiActivateProduct).futureValue

      val apiGetProductInfo = ApiGetProductInfo(
        Some(testSku)
      )
      val createdProductInfo =
        client.getProductInfo(apiGetProductInfo).futureValue

      createdProductInfo.info shouldBe Some(apiProductInfo)
    }

    "delete product correctly" in {
      val apiDeleteProduct = ApiDeleteProduct(
        Some(testSku),
        Some(ApiMemberId(testMemberId1))
      )

      client.deleteProduct(apiDeleteProduct).futureValue

      val apiGetProductInfo = ApiGetProductInfo(
        Some(testSku)
      )

      intercept[Exception] {
        client.getProductInfo(apiGetProductInfo).futureValue
      }
    }

  }

  override def beforeAll(): Unit = {
    super.beforeAll()

    client.createProduct(apiCreateProduct).futureValue
  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
