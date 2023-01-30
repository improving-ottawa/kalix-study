package app.improving.gateway

import app.improving._
import app.improving.organizationcontext.organization.{
  ApiOrganizationStatus,
  ApiParent,
  ApiInfo => OrgInfo,
  ApiMetaInfo => OrgMetaInfo,
  _
}
import app.improving.storecontext.store.ApiStoreInfo
import app.improving.tenantcontext.tenant.{
  ApiInfo => TenantInfo,
  ApiMetaInfo => TenantMetaInfo,
  _
}
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {
  trait Fixture {
    val tenantInfo: TenantInfo = TenantInfo(
      "tenantName",
      Some(
        ApiContact(
          firstName = "Some",
          lastName = "One",
          emailAddress = Some(ApiEmailAddress("someone@gmail.com")),
          phone = Some(ApiMobileNumber("1234567")),
          userName = "Someone"
        )
      ),
      Some(
        ApiAddress(
          line1 = "Line 1 St",
          line2 = "Line 2 Ave",
          city = "Sometown",
          stateProvince = "Ontario",
          country = "Canada",
          postalCode =
            ApiAddress.PostalCode.CaPostalCode(ApiCAPostalCode("A1B2C3"))
        )
      )
    )

    val testOrgId = "test-organization-id"
    val parentIdTest = "parent-id-test"
    val newParentId = "new-parent-id"
    val testTenantId = "test-tenant-id"
    val establishingMemberId = "establishing-member-id"
    val now = Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

    val establishOrganization = EstablishOrganization(
      Some(
        OrgInfo(
          "name-test",
          "shortname-test",
          Some(
            ApiAddress(
              "line1",
              "line2",
              "city",
              "state",
              "canada",
              ApiAddress.PostalCode.CaPostalCode(
                ApiCAPostalCode.defaultInstance
              )
            )
          ),
          true,
          "www.test.com",
          "N/A",
          Some(ApiTenantId(testTenantId))
        )
      ),
      Some(ApiParent(parentIdTest)),
      Seq[ApiMemberId](
        ApiMemberId("test-member-id"),
        ApiMemberId("member2"),
        ApiMemberId("member3")
      ),
      Seq[ApiMemberId](
        ApiMemberId("member10"),
        ApiMemberId("member11"),
        ApiMemberId("member12")
      ),
      Seq.empty,
      Some(ApiMemberId(establishingMemberId)),
      Some(
        OrgMetaInfo(
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          ApiOrganizationStatus.DRAFT,
          Seq.empty[ApiOrganizationId]
        )
      )
    )

    val apiEstablishOrganization = ApiEstablishOrganization(
      testOrgId,
      Some(
        OrgInfo(
          "name-test",
          "shortname-test",
          Some(
            ApiAddress(
              "line1",
              "line2",
              "city",
              "state",
              "canada",
              ApiAddress.PostalCode.CaPostalCode(
                ApiCAPostalCode.defaultInstance
              )
            )
          ),
          true,
          "www.test.com",
          "N/A",
          Some(ApiTenantId(testTenantId))
        )
      ),
      Some(ApiParent(parentIdTest)),
      Seq[ApiMemberId](
        ApiMemberId("test-member-id"),
        ApiMemberId("member2"),
        ApiMemberId("member3")
      ),
      Seq[ApiMemberId](
        ApiMemberId("member10"),
        ApiMemberId("member11"),
        ApiMemberId("member12")
      ),
      Seq.empty,
      Some(ApiMemberId(establishingMemberId)),
      Some(
        OrgMetaInfo(
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          ApiOrganizationStatus.DRAFT,
          Seq.empty[ApiOrganizationId]
        )
      )
    )

    val testStoreId = "test-store-id"
    val testName = "test-name"
    val testDescription = "test-description"
    val testProductId1 = "test-product-id1"
    val testProductId2 = "test-product-id2"
    val testEventId = "test-event-id"
    val testEvent = ApiEventId(testEventId)
    val testVenueId = "test-venue-id"
    val testVenue = ApiVenueId(testVenueId)
    val testLocationId = "test-location-id"
    val testLocaltion = ApiLocationId(testLocationId)
    val testProducts = Seq[ApiProductId](
      ApiProductId(testProductId1),
      ApiProductId(testProductId2)
    )
    val testOrg = ApiOrganizationId(testOrgId)
    val testMember1 = "test-member1"
    val testMember2 = "test-member2"
    val testMember3 = "test-member3"
    val apiStoreInfo = ApiStoreInfo(
      testStoreId,
      testName,
      testDescription,
      testProducts,
      Some(testEvent),
      Some(testVenue),
      Some(testLocaltion),
      Some(testOrg)
    )
  }
}
