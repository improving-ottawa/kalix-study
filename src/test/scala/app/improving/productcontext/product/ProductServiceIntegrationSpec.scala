package app.improving.productcontext.product

import app.improving.Main
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
        testSku
      )
      val createdProductInfo =
        client.getProductInfo(apiGetProductInfo).futureValue

      createdProductInfo.info shouldBe Some(apiProductInfo)
    }

  }

  override def beforeAll(): Unit = {
    super.beforeAll()

    val apiCreateProduct = ApiCreateProduct(
      testSku,
      Some(apiProductInfo),
      Some(apiProductMetaInfo)
    )

    client.createProduct(apiCreateProduct).futureValue
  }
  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
