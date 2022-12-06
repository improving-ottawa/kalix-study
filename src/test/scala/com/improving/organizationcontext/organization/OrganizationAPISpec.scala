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
  ApiOrganizationStatus,
  ApiOrganizationStatusUpdated,
  ApiParent,
  ApiUpdateInfo,
  ApiUpdateParent
}
import com.improving.organizationcontext.{
  MetaInfo,
  OrganizationEstablished,
  OrganizationInfoUpdated,
  OrganizationStatus,
  OrganizationStatusUpdated,
  ParentUpdated
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
      Seq.empty,
      Seq.empty,
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

    "have organization established and edit update info event triggered" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

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

    "correctly update organization parent" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1

      val apiUpdateParent =
        ApiUpdateParent(testOrgId, Some(ApiOrganizationId(newParentId)))

      val updateParentResult = testKit.updateParent(apiUpdateParent)

      updateParentResult.events should have size 1

      testKit.allEvents should have size 2

      val parentUpdated = updateParentResult.nextEvent[ParentUpdated]

      parentUpdated.newParent shouldBe defined

      parentUpdated.newParent shouldBe Some(OrganizationId(newParentId))
    }

    "correctly update organization status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1

      val apiOrganizationStatusUpdated =
        ApiOrganizationStatusUpdated(
          Some(ApiOrganizationId(testOrgId)),
          ApiOrganizationStatus.SUSPENDED
        )

      val updateOrganizationStatusResult =
        testKit.updateOrganizationStatus(apiOrganizationStatusUpdated)

      updateOrganizationStatusResult.events should have size 1

      testKit.allEvents should have size 2

      val organizationStatusUpdated =
        updateOrganizationStatusResult.nextEvent[OrganizationStatusUpdated]

      organizationStatusUpdated.newStatus shouldBe OrganizationStatus.SUSPENDED
    }
  }
}
