package app.improving.organizationcontext.organization

import app.improving._
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {

  val testOrgId = "test-organization-id"
  val parentIdTest = "parent-id-test"
  val newParentId = "new-parent-id"
  val testTenantId = "test-tenant-id"
  val establishingMemberId = "establishing-member-id"
  val now = Instant.now()
  val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

  val apiEstablishOrganization = ApiEstablishOrganization(
    testOrgId,
    Some(
      ApiInfo(
        "name-test",
        Some("shortname-test"),
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
        Some(true),
        Some("www.test.com"),
        Some("N/A"),
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
    Seq[ApiContacts](
      ApiContacts(primaryContacts = Seq(ApiMemberId("member81")))
    ),
    Some(ApiMemberId(establishingMemberId))
  )
}
