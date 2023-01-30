package app.improving.gateway

import app.improving._
import app.improving.organizationcontext.organization.{
  ApiOrganizationStatus,
  ApiParent,
  ApiInfo => OrgInfo,
  ApiMetaInfo => OrgMetaInfo,
  _
}
import app.improving.productcontext.product.{ApiProductInfo, ApiProductMetaInfo}
import app.improving.productcontext.product.TestData.testMemberId
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

    val testSku = "test-product-id"
    val testSku2 = "test-product-id2"
    val testSku3 = "test-product-id3"
    val testName = "test-name"
    val testDescription = "test-description"
    val testRow = "test-row"
    val testSeat = "test-seat"
    val testSection = "test-section"
    val testEventId = "test-event-id"
    val testEventId2 = "test-event-id2"
    val testEventId3 = "test-event-id3"
    val testImages = Seq[String]("test-image")
    val testPrice = 10.0
    val testCost = 3.0
    val testStoreId = "test-store-id"
    val testMemberId = "test-member-id"
    val testMemberId1 = "test-member-id1"
    val testTimestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val apiProductInfo = ApiProductInfo(
      testSku,
      testName,
      testDescription,
      testSection,
      testRow,
      testSeat,
      Some(ApiEventId(testEventId)),
      testImages,
      testPrice,
      testCost,
      Some(ApiStoreId(testStoreId))
    )
    val apiProductMetaInfo = ApiProductMetaInfo(
      Some(ApiMemberId(testMemberId)),
      Some(testTimestamp),
      Some(ApiMemberId(testMemberId1)),
      Some(testTimestamp)
    )
    val createProduct = CreateProduct(
      Some(apiProductInfo),
      Some(apiProductMetaInfo)
    )
  }
}
