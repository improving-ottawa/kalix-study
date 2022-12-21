package app.improving.tenantcontext.tenant

import app.improving.common.infrastructure.util.convertApiAddressToAddress
import app.improving.tenant.{
  ApiActivateTenant,
  ApiChangeTenantName,
  ApiEstablishTenant,
  ApiInfo,
  ApiSuspendTenant,
  ApiUpdatePrimaryContact
}
import app.improving.{
  ApiAddress,
  ApiCAPostalCode,
  ApiContact,
  ApiEmailAddress,
  ApiMobileNumber
}
import app.improving.tenantcontext.TenantStatus
import app.improving.tenantcontext.infrastructure.util.convertApiContactToContact
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TenantAPISpec extends AnyWordSpec with Matchers {
  "The TenantAPI" should {

    val testTenantId = "test-tenant-id"
    val testTenantId2 = "test-tenant-id2"
    val newName = "new-name"
    val apiContact = ApiContact(
      "firtname",
      "lastname",
      Some(ApiEmailAddress("email@email.com")),
      Some(ApiMobileNumber("999-999-999")),
      "username"
    )
    val newApiContact = ApiContact(
      "firtname1",
      "lastname1",
      Some(ApiEmailAddress("email1@email.com")),
      Some(ApiMobileNumber("888-888-888")),
      "username1"
    )
    val apiAddress = ApiAddress(
      "line1",
      "line2",
      "city",
      "state",
      "canada",
      ApiAddress.PostalCode.CaPostalCode(
        ApiCAPostalCode.defaultInstance
      )
    )
    val apiInfo = ApiInfo(
      "name",
      Some(apiContact),
      Some(apiAddress)
    )

    "correctly process commands of type EstablishTenant" in {
      val testKit = TenantAPITestKit(new TenantAPI(_))
      val command = ApiEstablishTenant(
        testTenantId,
        Some(apiInfo)
      )
      val result = testKit.establishTenant(command)

      result.events should have size 1

      testKit.currentState.tenant.map(_.status) shouldBe Some(
        TenantStatus.DRAFT
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
        .map(_.currentStatus) shouldBe Some(TenantStatus.DRAFT)
    }

    "correctly process commands of type ActivateTenant" in {
      val testKit = TenantAPITestKit(new TenantAPI(_))
      val command = ApiEstablishTenant(
        testTenantId,
        Some(apiInfo)
      )
      val result = testKit.establishTenant(command)

      result.events should have size 1

      val activateTenantEvent = ApiActivateTenant(
        testTenantId,
        testTenantId2
      )

      val activateTenantResult = testKit.activateTenant(activateTenantEvent)

      activateTenantResult.events should have size 1

      testKit.currentState.tenant.map(_.status) shouldBe Some(
        TenantStatus.ACTIVE
      )

      testKit.currentState.tenant
        .flatMap(_.meta)
        .map(_.currentStatus) shouldBe Some(TenantStatus.ACTIVE)
    }

    "correctly process commands of type SuspendTenant" in {
      val testKit = TenantAPITestKit(new TenantAPI(_))
      val command = ApiEstablishTenant(
        testTenantId,
        Some(apiInfo)
      )
      val result = testKit.establishTenant(command)

      result.events should have size 1

      val suspendTenantEvent = ApiSuspendTenant(
        testTenantId,
        testTenantId2
      )

      val activateTenantResult = testKit.suspendTenant(suspendTenantEvent)

      activateTenantResult.events should have size 1

      testKit.currentState.tenant.map(_.status) shouldBe Some(
        TenantStatus.SUSPENDED
      )

      testKit.currentState.tenant
        .flatMap(_.meta)
        .map(_.currentStatus) shouldBe Some(TenantStatus.SUSPENDED)
    }

    "correctly process commands of type UpdatePrimaryContact" in {
      val testKit = TenantAPITestKit(new TenantAPI(_))
      val command = ApiEstablishTenant(
        testTenantId,
        Some(apiInfo)
      )
      val result = testKit.establishTenant(command)

      result.events should have size 1

      val updatePrimaryContactEvent = ApiUpdatePrimaryContact(
        testTenantId,
        Some(newApiContact),
        testTenantId2
      )

      val updatePrimaryContactResult =
        testKit.updatePrimaryContact(updatePrimaryContactEvent)

      updatePrimaryContactResult.events should have size 1

      testKit.currentState.tenant.flatMap(_.primaryContact) shouldBe Some(
        convertApiContactToContact(newApiContact)
      )

    }

    "correctly process commands of type ChangeTenantName" in {
      val testKit = TenantAPITestKit(new TenantAPI(_))
      val command = ApiEstablishTenant(
        testTenantId,
        Some(apiInfo)
      )
      val result = testKit.establishTenant(command)

      result.events should have size 1

      val changeTenantNameEvent = ApiChangeTenantName(
        testTenantId,
        newName,
        testTenantId2
      )

      val changeTenantNameResult =
        testKit.changeTenantName(changeTenantNameEvent)

      changeTenantNameResult.events should have size 1

      testKit.currentState.tenant.map(_.name) shouldBe Some(
        newName
      )
    }
  }
}
