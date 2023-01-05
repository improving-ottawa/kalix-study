package app.improving.organizationcontext.organization

import app.improving._
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {

  val testOrgId = "test-org-id"
  val parentIdTest = "parent-id-test"
  val newParentId = "new-parent-id"
  val establishingMemberId = "establishing-member-id"
  val now = Instant.now()
  val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

  val apiEstablishOrganization = ApiEstablishOrganization(
    testOrgId,
    Some(
      ApiInfo(
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
        )
      )
    ),
    Some(ApiParent(parentIdTest)),
    Seq[ApiMemberId](
      ApiMemberId("member1"),
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
      ApiMetaInfo(
        Some(timestamp),
        Some(ApiMemberId(establishingMemberId)),
        Some(timestamp),
        Some(ApiMemberId(establishingMemberId)),
        ApiOrganizationStatus.DRAFT,
        Seq.empty[ApiOrganizationId]
      )
    )
  )
}
