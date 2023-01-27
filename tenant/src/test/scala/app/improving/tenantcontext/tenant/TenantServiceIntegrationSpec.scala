package app.improving.tenantcontext.tenant

import app.improving._
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.{BeforeAndAfterAll, Ignore}
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

@Ignore
class TenantServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(50, Seconds), Span(1000, Millis))

  trait Fixture {

    private val testKit = KalixTestKit(Main.createKalix()).start()

    protected val client = testKit.getGrpcClient(classOf[TenantService])

    val command = ApiEstablishTenant(
      testTenantId,
      Some(apiInfo)
    )
    client.establishTenant(command).futureValue
  }

  "TenantService" must {

    "create tenant correctly" in new Fixture {

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

    "deactivate tenant correctly" in new Fixture {

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

    "updatePrimaryContact correctly" in new Fixture {

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

    "changeTenantName correctly" in new Fixture {
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
  }
  override def afterAll(): Unit = {
    super.afterAll()
  }
}
