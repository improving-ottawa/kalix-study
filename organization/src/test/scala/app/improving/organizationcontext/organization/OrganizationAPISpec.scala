package app.improving.organizationcontext.organization

import TestData._
import app.improving._
import app.improving.organizationcontext.{
  MetaInfo,
  OrganizationEstablished,
  OrganizationInfoUpdated,
  OrganizationStatus,
  OrganizationStatusUpdated,
  ParentUpdated
}
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
            ),
            isPrivate = true,
            url = "www.test.com",
            logo = "N/A",
            tenant = Some(ApiTenantId(testTenantId))
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

    "return empty effect from edit and update info with terminated status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

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
            ),
            isPrivate = true,
            url = "www.test.com",
            logo = "N/A",
            tenant = Some(ApiTenantId(testTenantId))
          )
        )
      )

      val updateInfoResult =
        testKit.editOrganizationInfo(apiEditOrganizationInfo)

      updateInfoResult.events should have size 0
    }

    "correctly update organization parent" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1

      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
        .map(_.id)
        .getOrElse("Organization Id is not found.")
      val apiUpdateParent =
        ApiUpdateParent(orgId, Some(ApiOrganizationId(newParentId)))

      val updateParentResult = testKit.updateParent(apiUpdateParent)

      updateParentResult.events should have size 1

      testKit.allEvents should have size 2

      val parentUpdated = updateParentResult.nextEvent[ParentUpdated]

      parentUpdated.newParent shouldBe defined

      parentUpdated.newParent shouldBe Some(OrganizationId(newParentId))
    }

    "return empty from update organization parent with invalid state's status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiUpdateParent =
        ApiUpdateParent(testOrgId, Some(ApiOrganizationId(newParentId)))

      val updateParentResult = testKit.updateParent(apiUpdateParent)

      updateParentResult.events should have size 0

    }

    "correctly update organization status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1
      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
        .map(_.id)
        .getOrElse("Organization Id is not found.")
      val apiOrganizationStatusUpdated =
        ApiOrganizationStatusUpdated(
          orgId,
          ApiOrganizationStatus.SUSPENDED
        )

      val updateOrganizationStatusResult =
        testKit.updateOrganizationStatus(apiOrganizationStatusUpdated)

      updateOrganizationStatusResult.events should have size 1

      testKit.allEvents should have size 2

      val organizationStatusUpdated =
        updateOrganizationStatusResult.nextEvent[OrganizationStatusUpdated]

      organizationStatusUpdated.newStatus shouldBe OrganizationStatus.SUSPENDED

      testKit.currentState.organization.map(_.status) shouldBe Some(
        OrganizationStatus.SUSPENDED
      )
    }

    "return empty result from update organization status with invalid state's status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiOrganizationStatusUpdated =
        ApiOrganizationStatusUpdated(
          testOrgId,
          ApiOrganizationStatus.SUSPENDED
        )

      val updateOrganizationStatusResult =
        testKit.updateOrganizationStatus(apiOrganizationStatusUpdated)

      updateOrganizationStatusResult.events should have size 0
    }

    "correctly return result from getOrganizationInfo" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1

      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
        .map(_.id)
        .getOrElse("Organization Id is not found.")
      val apiGetOrganizationInfo = ApiGetOrganizationInfo(orgId)

      val getOrganizationInfoResult =
        testKit.getOrganizationInfo(apiGetOrganizationInfo)

      getOrganizationInfoResult.events should have size 1

      Some(
        getOrganizationInfoResult.reply
      ) == apiEstablishOrganization.info shouldBe true
    }

    "return empty result from getOrganizationInfo with invalid state" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiGetOrganizationInfo = ApiGetOrganizationInfo(testOrgId)

      val getOrganizationInfoResult =
        testKit.getOrganizationInfo(apiGetOrganizationInfo)

      getOrganizationInfoResult.events should have size 0

    }

    "correctly add members to the organization" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1
      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
        .map(_.id)
        .getOrElse("Organization Id is not found.")
      val apiAddMembersToOrganization = ApiAddMembersToOrganization(
        orgId,
        Seq[ApiMemberId](
          ApiMemberId("member1"),
          ApiMemberId("member2"),
          ApiMemberId("member4")
        ),
        Some(ApiMemberId("member100"))
      )

      val apiAddMembersToOrganizationResult =
        testKit.addMembersToOrganization(apiAddMembersToOrganization)

      apiAddMembersToOrganizationResult.events should have size 1

      val members = testKit.currentState.organization
        .map(_.members)
        .getOrElse(Seq.empty[MemberId])
      members should have size 5

      members.toSet shouldEqual Seq(
        MemberId("test-member-id"),
        MemberId("member2"),
        MemberId("member3"),
        MemberId("member1"),
        MemberId("member4")
      ).toSet

      val meta = testKit.currentState.organization
        .flatMap(_.orgMeta)
        .getOrElse(MetaInfo.defaultInstance)

      meta.lastUpdatedBy shouldBe Some(MemberId("member100"))
    }

    "should return empty result from add members to the organization with invalid state" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiAddMembersToOrganization = ApiAddMembersToOrganization(
        testOrgId,
        Seq[ApiMemberId](
          ApiMemberId("test-member-id"),
          ApiMemberId("member2"),
          ApiMemberId("member4")
        ),
        Some(ApiMemberId("member100"))
      )

      val apiAddMembersToOrganizationResult =
        testKit.addMembersToOrganization(apiAddMembersToOrganization)

      apiAddMembersToOrganizationResult.events should have size 0
    }

    "correctly remove members from organization" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1
      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
        .map(_.id)
        .getOrElse("Organization Id is not found.")
      val apiRemoveMembersFromOrganization = ApiRemoveMembersFromOrganization(
        orgId,
        Seq[ApiMemberId](
          ApiMemberId("member1"),
          ApiMemberId("member2"),
          ApiMemberId("member4")
        ),
        Some(ApiMemberId("member100"))
      )

      val apiRemoveMembersFromOrganizationResult =
        testKit.removeMembersFromOrganization(
          apiRemoveMembersFromOrganization
        )

      apiRemoveMembersFromOrganizationResult.events should have size 1

      val members = testKit.currentState.organization
        .map(_.members)
        .getOrElse(Seq.empty[MemberId])
      members should have size 2

      members shouldEqual Seq(
        MemberId("test-member-id"),
        MemberId("member3")
      )

      val meta = testKit.currentState.organization
        .flatMap(_.orgMeta)
        .getOrElse(MetaInfo.defaultInstance)

      meta.lastUpdatedBy shouldBe Some(MemberId("member100"))
    }

    "should return empty result from remove members to the organization with invalid state" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiRemoveMembersFromOrganization = ApiRemoveMembersFromOrganization(
        testOrgId,
        Seq[ApiMemberId](
          ApiMemberId("member1"),
          ApiMemberId("member2"),
          ApiMemberId("member4")
        ),
        Some(ApiMemberId("member100"))
      )

      val apiRemoveMembersFromOrganizationResult =
        testKit.removeMembersFromOrganization(apiRemoveMembersFromOrganization)

      apiRemoveMembersFromOrganizationResult.events should have size 0
    }

    "correctly add owners to the organization" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1
      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
        .map(_.id)
        .getOrElse("Organization Id is not found.")
      val apiAddOwnersToOrganization = ApiAddOwnersToOrganization(
        orgId,
        Seq[ApiMemberId](
          ApiMemberId("member10"),
          ApiMemberId("member11"),
          ApiMemberId("member12"),
          ApiMemberId("member13"),
          ApiMemberId("member14")
        ),
        Some(ApiMemberId("member11"))
      )

      val apiAddOwnersToOrganizationResult =
        testKit.addOwnersToOrganization(apiAddOwnersToOrganization)

      apiAddOwnersToOrganizationResult.events should have size 1

      val owners = testKit.currentState.organization
        .map(_.owners)
        .getOrElse(Seq.empty[MemberId])
      owners should have size 5

      owners shouldEqual Seq(
        MemberId("member10"),
        MemberId("member11"),
        MemberId("member12"),
        MemberId("member13"),
        MemberId("member14")
      )

      val meta = testKit.currentState.organization
        .flatMap(_.orgMeta)
        .getOrElse(MetaInfo.defaultInstance)

      meta.lastUpdatedBy shouldBe Some(MemberId("member11"))
    }

    "should return empty result from add owners to the organization with invalid state" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiAddOwnersToOrganization = ApiAddOwnersToOrganization(
        testOrgId,
        Seq[ApiMemberId](
          ApiMemberId("member1"),
          ApiMemberId("member2"),
          ApiMemberId("member4")
        ),
        Some(ApiMemberId("member1"))
      )

      val apiAddOwnersToOrganizationResult =
        testKit.addOwnersToOrganization(apiAddOwnersToOrganization)

      apiAddOwnersToOrganizationResult.events should have size 0
    }

    "correctly remove owners from organization" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1
      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
        .map(_.id)
        .getOrElse("Organization Id is not found.")
      val apiRemoveOwnersFromOrganization = ApiRemoveOwnersFromOrganization(
        orgId,
        Seq[ApiMemberId](
          ApiMemberId("member10"),
          ApiMemberId("member11"),
          ApiMemberId("member5"),
          ApiMemberId("member6"),
          ApiMemberId("member7")
        ),
        Some(ApiMemberId("member1"))
      )

      val apiRemoveOwnersFromOrganizationResult =
        testKit.removeOwnersFromOrganization(
          apiRemoveOwnersFromOrganization
        )

      apiRemoveOwnersFromOrganizationResult.events should have size 1

      val owners = testKit.currentState.organization
        .map(_.owners)
        .getOrElse(Seq.empty[MemberId])
      owners should have size 1

      owners shouldEqual Seq(
        MemberId("member12")
      )

      val meta = testKit.currentState.organization
        .flatMap(_.orgMeta)
        .getOrElse(MetaInfo.defaultInstance)

      meta.lastUpdatedBy shouldBe Some(MemberId("member1"))
    }

    "should return empty result from remove owners to the organization with invalid state" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiRemoveOwnersFromOrganization = ApiRemoveOwnersFromOrganization(
        testOrgId,
        Seq[ApiMemberId](
          ApiMemberId("member1"),
          ApiMemberId("member2"),
          ApiMemberId("member4")
        ),
        Some(ApiMemberId("member1"))
      )

      val apiRemoveOwnersFromOrganizationResult =
        testKit.removeOwnersFromOrganization(
          apiRemoveOwnersFromOrganization
        )

      apiRemoveOwnersFromOrganizationResult.events should have size 0
    }
  }
}
