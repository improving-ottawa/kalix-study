package app.improving.tenantcontext.tenant

import app.improving._
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec
import TestData._

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TenantServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(50, Seconds), Span(1000, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  private val client = testKit.getGrpcClient(classOf[TenantService])

  "TenantService" must {

    "create tenant correctly" in {
      val command = ApiEstablishTenant(
        testTenantId,
        Some(apiInfo)
      )
      val id = client.establishTenant(command).futureValue

      println(s"$id ----------------------- id")
      val tenant = client
        .getTenantById(ApiGetTenantById(id.tenantId))
        .futureValue

      tenant.name shouldBe apiInfo.name

      tenant.status shouldBe ApiTenantStatus.DRAFT

      val apiActivateTenant = ApiActivateTenant(
        testTenantId,
        testTenantId2
      )

      client.activateTenant(apiActivateTenant).futureValue

      val activatedTenant = client
        .getTenantById(ApiGetTenantById(id.tenantId))
        .futureValue

      activatedTenant.status shouldBe ApiTenantStatus.ACTIVE
    }

  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
