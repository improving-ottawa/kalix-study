package app.improving.productcontext.product

import app.improving.ApiMemberId
import app.improving.productcontext.Main
import app.improving.productcontext.product.TestData._
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
class ProductServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  trait Fixture {
    private val testKit = KalixTestKit(Main.createKalix()).start()

    protected val client = testKit.getGrpcClient(classOf[ProductService])
    client.createProduct(apiCreateProduct).futureValue
  }

  "ProductService" must {

    "create product correctly" in new Fixture {

      val apiGetProductInfo = ApiGetProductInfo(
        testSku
      )
      val createdProductInfo =
        client.getProductInfo(apiGetProductInfo).futureValue

      createdProductInfo.info shouldBe Some(apiProductInfo)
    }

    "activate product correctly" in new Fixture {
      val apiActivateProduct = ApiActivateProduct(
        testSku,
        Some(ApiMemberId(testMemberId1))
      )

      client.activateProduct(apiActivateProduct).futureValue

      val apiGetProductInfo = ApiGetProductInfo(
        testSku
      )
      val createdProductInfo =
        client.getProductInfo(apiGetProductInfo).futureValue

      createdProductInfo.info shouldBe Some(apiProductInfo)
    }

    "delete product correctly" in new Fixture {
      val apiDeleteProduct = ApiDeleteProduct(
        testSku,
        Some(ApiMemberId(testMemberId1))
      )

      client.deleteProduct(apiDeleteProduct).futureValue

      val apiGetProductInfo = ApiGetProductInfo(
        testSku
      )

      intercept[Exception] {
        client.getProductInfo(apiGetProductInfo).futureValue
      }
    }

  }

  override def beforeAll(): Unit = {
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    super.afterAll()
  }
}
