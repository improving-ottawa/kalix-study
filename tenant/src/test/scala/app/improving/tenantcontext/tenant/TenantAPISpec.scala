package app.improving.tenantcontext.tenant

import app.improving.common.infrastructure.util.convertApiAddressToAddress
import TestData._
import app.improving.ApiTenantId
import app.improving.tenantcontext.TenantStatus
import app.improving.tenantcontext.infrastructure.util.convertApiContactToContact
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TenantAPISpec extends AnyWordSpec with Matchers with BeforeAndAfterAll {

  var result: EventSourcedResult[ApiTenantId] = null
  val testKit: TenantAPITestKit = TenantAPITestKit(new TenantAPI(_))

  "The TenantAPI" should {

    "correctly process commands of type EstablishTenant" in {

      result.events should have size 1

      testKit.currentState.tenant.map(_.status) shouldBe Some(
        TenantStatus.TENANT_STATUS_DRAFT
      )

      testKit.currentState.tenant.flatMap(_.info).map(_.name) shouldBe Some(
        apiInfo.name
      )

      testKit.currentState.tenant
        .flatMap(_.info)
        .flatMap(_.primaryContact) shouldBe Some(
        convertApiContactToContact(apiContact)
      )

      testKit.currentState.tenant
        .flatMap(_.info)
        .flatMap(_.address) shouldBe Some(
        convertApiAddressToAddress(apiAddress)
      )

      testKit.currentState.tenant
        .flatMap(_.meta)
        .map(_.currentStatus) shouldBe Some(TenantStatus.TENANT_STATUS_DRAFT)
    }

    "correctly process commands of type ActivateTenant" in {

      result.events should have size 1

      val tenantId = testKit.currentState.tenant
        .flatMap(_.tenantId)
        .map(_.id)
        .getOrElse("TenantId is not found.")
      val activateTenantEvent = ApiActivateTenant(
        tenantId,
        Some(testMemberId)
      )

      val activateTenantResult = testKit.activateTenant(activateTenantEvent)

      activateTenantResult.events should have size 1

      testKit.currentState.tenant.map(_.status) shouldBe Some(
        TenantStatus.TENANT_STATUS_ACTIVE
      )

      testKit.currentState.tenant
        .flatMap(_.meta)
        .map(_.currentStatus) shouldBe Some(TenantStatus.TENANT_STATUS_ACTIVE)
    }

    "correctly process commands of type SuspendTenant" in {

      result.events should have size 1
      val tenantId = testKit.currentState.tenant
        .flatMap(_.tenantId)
        .map(_.id)
        .getOrElse("TenantId is not found.")
      val suspendTenantEvent = ApiSuspendTenant(
        tenantId,
        Some(testMemberId)
      )

      val activateTenantResult = testKit.suspendTenant(suspendTenantEvent)

      activateTenantResult.events should have size 1

      testKit.currentState.tenant.map(_.status) shouldBe Some(
        TenantStatus.TENANT_STATUS_SUSPENDED
      )

      testKit.currentState.tenant
        .flatMap(_.meta)
        .map(_.currentStatus) shouldBe Some(
        TenantStatus.TENANT_STATUS_SUSPENDED
      )
    }

    "correctly process commands of type UpdatePrimaryContact" in {

      result.events should have size 1
      val tenantId = testKit.currentState.tenant
        .flatMap(_.tenantId)
        .map(_.id)
        .getOrElse("TenantId is not found.")
      val updatePrimaryContactEvent = ApiUpdatePrimaryContact(
        tenantId,
        Some(newApiContact),
        Some(testMemberId)
      )

      val updatePrimaryContactResult =
        testKit.updatePrimaryContact(updatePrimaryContactEvent)

      updatePrimaryContactResult.events should have size 1

      testKit.currentState.tenant.flatMap(_.primaryContact) shouldBe Some(
        convertApiContactToContact(newApiContact)
      )

    }

    "correctly process commands of type ChangeTenantName" in {

      result.events should have size 1
      val tenantId = testKit.currentState.tenant
        .flatMap(_.tenantId)
        .map(_.id)
        .getOrElse("TenantId is not found.")
      val changeTenantNameEvent = ApiChangeTenantName(
        tenantId,
        testNewName,
        Some(testMemberId)
      )

      val changeTenantNameResult =
        testKit.changeTenantName(changeTenantNameEvent)

      changeTenantNameResult.events should have size 1

      testKit.currentState.tenant.map(_.name) shouldBe Some(
        testNewName
      )
    }
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    val command = ApiEstablishTenant(
      testTenantId,
      Some(apiInfo)
    )
    result = testKit.establishTenant(command)
  }
}
