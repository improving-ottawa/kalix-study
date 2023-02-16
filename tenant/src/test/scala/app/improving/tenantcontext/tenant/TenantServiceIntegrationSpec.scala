package app.improving.tenantcontext.tenant

import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec
import TestData._
import app.improving.tenantcontext.Main

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

      val tenant = client
        .getTenantById(ApiGetTenantById(testTenantId))
        .futureValue

      tenant.name shouldBe apiInfo.name

      tenant.status shouldBe ApiTenantStatus.DRAFT

      val apiActivateTenant = ApiActivateTenant(
        testTenantId,
        testTenantId2
      )

      client.activateTenant(apiActivateTenant).futureValue

      val activatedTenant = client
        .getTenantById(ApiGetTenantById(testTenantId))
        .futureValue

      activatedTenant.status shouldBe ApiTenantStatus.ACTIVE
    }

    "deactivate tenant correctly" in {

      val apiSuspendTenant = ApiSuspendTenant(
        testTenantId,
        testTenantId2
      )
      client.suspendTenant(apiSuspendTenant).futureValue

      val suspendedTenant = client
        .getTenantById(ApiGetTenantById(testTenantId))
        .futureValue

      suspendedTenant.status shouldBe ApiTenantStatus.SUSPENDED
    }

    "updatePrimaryContact correctly" in {

      val apiUpdatePrimaryContact = ApiUpdatePrimaryContact(
        testTenantId,
        Some(newApiContact),
        testTenantId2
      )
      client.updatePrimaryContact(apiUpdatePrimaryContact).futureValue

      val updatePrimaryContactTenant = client
        .getTenantById(ApiGetTenantById(testTenantId))
        .futureValue

      updatePrimaryContactTenant.primaryContact shouldBe Some(newApiContact)
    }

    "changeTenantName correctly" in {
      val apiChangeTenantName = ApiChangeTenantName(
        testTenantId,
        testNewName,
        testTenantId2
      )

      client.changeTenantName(apiChangeTenantName).futureValue

      val changedNewNameTenant = client
        .getTenantById(ApiGetTenantById(testTenantId))
        .futureValue

      changedNewNameTenant.name shouldBe testNewName
    }
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    val command = ApiEstablishTenant(
      testTenantId,
      Some(apiInfo)
    )
    client.establishTenant(command).futureValue
  }
  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
