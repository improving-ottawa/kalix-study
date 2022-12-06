package com.improving.organizationcontext.organization

import com.google.protobuf.timestamp.Timestamp
import com.improving._
import com.improving.organization.{
  ApiAddress,
  ApiCAPostalCode,
  ApiEditOrganizationInfo,
  ApiEstablishOrganization,
  ApiInfo,
  ApiMemberId,
  ApiMetaInfo,
  ApiOrganizationId,
  ApiParent,
  ApiStatus,
  ApiUpdateInfo
}
import com.improving.organizationcontext.{
  MetaInfo,
  OrganizationEstablished,
  OrganizationInfoUpdated
}
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.testkit.EventSourcedResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationAPISpec extends AnyWordSpec with Matchers {
  "The OrganizationAPI" should {
    "have organization established and edit update info event triggered" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))
      val now = Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

      val apiEstablishOrganization = ApiEstablishOrganization(
        "test-org-id",
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
        Some(ApiParent("parent-id-test")),
        Seq.empty,
        Seq.empty,
        Seq.empty,
        Some(ApiMemberId("establishing-member-id")),
        Some(
          ApiMetaInfo(
            Some(timestamp),
            Some(ApiMemberId("establishing-member-id")),
            Some(timestamp),
            Some(ApiMemberId("establishing-member-id")),
            ApiStatus.DRAFT,
            Seq.empty[ApiOrganizationId]
          )
        )
      )

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1

      val establishedOrganization =
        establishOrganizationResult.nextEvent[OrganizationEstablished]

      establishedOrganization.meta.isDefined shouldBe true

      val apiEditOrganizationInfo = ApiEditOrganizationInfo(
        orgId = "testOrgId",
        newInfo = Some(
          ApiUpdateInfo(
            name = "sample-name",
            shortName = "sample-shortname",
            address = Some(
              ApiAddress(
                line1 = "line1",
                line2 = "line2",
                city = "city",
                stateProvince = "state",
                country = "canada",
                postalCode = ApiAddress.PostalCode.CaPostalCode(
                  ApiCAPostalCode.defaultInstance
                )
              )
            )
          )
        )
      )
      val updateInfoResult =
        testKit.editOrganizationInfo(apiEditOrganizationInfo)

      updateInfoResult.events should have size 1

      val organizationInfoUpdated =
        updateInfoResult.nextEvent[OrganizationInfoUpdated]

      organizationInfoUpdated.orgId.isDefined shouldBe true

    }

//    "correctly process commands of type OrganizationContactsUpdated" in {
//      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))
//      pending
//      // val result: EventSourcedResult[Empty] = testKit.organizationContactsUpdated(api.OrganizationOwnersAdded(...))
//    }
  }
}
