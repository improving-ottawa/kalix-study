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
import com.google.protobuf.empty.Empty
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

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
        orgId = Some(ApiOrganizationId("testOrgId")),
        newInfo = Some(
          ApiUpdateInfo(
            name = Some("sample-name"),
            shortName = Some("sample-shortname"),
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
            isPrivate = Some(true),
            url = Some("www.test.com"),
            logo = Some("N/A"),
            tenant = Some(ApiTenantId(testTenantId))
          )
        ),
        editingMember = Some(ApiMemberId("member2"))
      )

      val updateInfoResult =
        testKit.editOrganizationInfo(apiEditOrganizationInfo)

      updateInfoResult.events should have size 1

      val organizationInfoUpdated =
        updateInfoResult.nextEvent[OrganizationInfoUpdated]

      organizationInfoUpdated.orgId.isDefined shouldBe true

    }

    "reject EstablishOrganization command with missing info" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val missingInfoEstbalishOrganizationCommand =
        apiEstablishOrganization.copy(info = None)
      val result =
        testKit.establishOrganization(missingInfoEstbalishOrganizationCommand)
      result.isError shouldBe true
      result.errorDescription shouldBe "Message is missing the following fields: Info"
    }

    "reject EstablishOrganization command with info missing necessary fields" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val missingInfoEstbalishOrganizationCommand =
        apiEstablishOrganization.copy(info =
          apiEstablishOrganization.info.map(_.copy(tenant = None, name = ""))
        )
      val result =
        testKit.establishOrganization(missingInfoEstbalishOrganizationCommand)
      result.isError shouldBe true
      result.errorDescription shouldBe "Message is missing the following fields: Info.Tenant, Info.Name"
    }

    "return empty effect from edit and update info with terminated status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiEditOrganizationInfo = ApiEditOrganizationInfo(
        orgId = Some(ApiOrganizationId("testOrgId")),
        newInfo = Some(
          ApiUpdateInfo(
            name = Some("sample-name"),
            shortName = Some("sample-shortname"),
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
            isPrivate = Some(true),
            url = Some("www.test.com"),
            logo = Some("N/A"),
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
      val apiUpdateParent =
        ApiUpdateParent(
          orgId = orgId.map(id => ApiOrganizationId(id.id)),
          newParent = Some(newParentId),
          updatingMember = Some(ApiMemberId("member25"))
        )

      val updateParentResult = testKit.updateParent(apiUpdateParent)

      updateParentResult.events should have size 1

      testKit.allEvents should have size 2

      val parentUpdated = updateParentResult.nextEvent[ParentUpdated]

      parentUpdated.newParent shouldBe defined

      parentUpdated.newParent shouldBe Some(
        OrganizationId(newParentId.organizationId)
      )
    }

    "return empty from update organization parent with invalid state's status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiUpdateParent =
        ApiUpdateParent(Some(testOrgId), Some(newParentId))

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
      val apiOrganizationStatusUpdated =
        ApiOrganizationStatusUpdated(
          orgId.map(id => ApiOrganizationId(id.id)),
          ApiOrganizationStatus.API_ORGANIZATION_STATUS_SUSPENDED,
          Some(ApiMemberId("member1"))
        )

      val updateOrganizationStatusResult =
        testKit.updateOrganizationStatus(apiOrganizationStatusUpdated)

      updateOrganizationStatusResult.events should have size 1

      testKit.allEvents should have size 2

      val organizationStatusUpdated =
        updateOrganizationStatusResult.nextEvent[OrganizationStatusUpdated]

      organizationStatusUpdated.newStatus shouldBe OrganizationStatus.ORGANIZATION_STATUS_SUSPENDED

      testKit.currentState.organization.map(_.status) shouldBe Some(
        OrganizationStatus.ORGANIZATION_STATUS_SUSPENDED
      )
    }

    "return empty result from update organization status with invalid state's status" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiOrganizationStatusUpdated =
        ApiOrganizationStatusUpdated(
          Some(testOrgId),
          ApiOrganizationStatus.API_ORGANIZATION_STATUS_SUSPENDED
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
      val apiGetOrganizationInfo =
        ApiGetOrganizationInfo(orgId.map(id => ApiOrganizationId(id.id)))

      val getOrganizationInfoResult =
        testKit.getOrganizationInfo(apiGetOrganizationInfo)

      getOrganizationInfoResult.events should have size 1

      apiEstablishOrganization.info.contains(
        getOrganizationInfoResult.reply
      ) shouldBe true
    }

    "return empty result from getOrganizationInfo with invalid state" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val apiGetOrganizationInfo = ApiGetOrganizationInfo(Some(testOrgId))

      val getOrganizationInfoResult =
        testKit.getOrganizationInfo(apiGetOrganizationInfo)

      getOrganizationInfoResult.events should have size 0

    }

    "fail to leave Draft state if requirements aren't met" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))
      val missingMembersEstablingOrganization =
        apiEstablishOrganization.copy(members = Seq.empty[ApiMemberId])

      testKit.establishOrganization(missingMembersEstablingOrganization)
      val result = testKit.updateOrganizationStatus(
        ApiOrganizationStatusUpdated(
          orgId = Some(testOrgId),
          newStatus = ApiOrganizationStatus.API_ORGANIZATION_STATUS_ACTIVE,
          updatingMember = Some(ApiMemberId("member36"))
        )
      )
      result.isError shouldBe true
      result.errorDescription shouldBe "Cannot leave state ORGANIZATION_STATUS_DRAFT without required fields. The state is missing: Members"
    }

    "succeed in draft state update if already in draft state (even if required fields are missing in the state)" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))
      val missingMembersEstablingOrganization =
        apiEstablishOrganization.copy(members = Seq.empty[ApiMemberId])

      testKit.establishOrganization(missingMembersEstablingOrganization)
      val result = testKit.updateOrganizationStatus(
        ApiOrganizationStatusUpdated(
          orgId = Some(testOrgId),
          newStatus = ApiOrganizationStatus.API_ORGANIZATION_STATUS_DRAFT,
          updatingMember = Some(ApiMemberId("member36"))
        )
      )
      result.isReply shouldBe true
      result.reply shouldBe Empty.defaultInstance
    }

    "fail to return to draft state when not in draft state" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      testKit.establishOrganization(apiEstablishOrganization)
      val result = testKit.updateOrganizationStatus(
        ApiOrganizationStatusUpdated(
          orgId = Some(testOrgId),
          newStatus = ApiOrganizationStatus.API_ORGANIZATION_STATUS_ACTIVE,
          updatingMember = Some(ApiMemberId("member36"))
        )
      )
      result.isReply shouldBe true
      result.reply shouldBe Empty.defaultInstance
      result.updatedState
        .asInstanceOf[OrganizationState]
        .organization
        .map(_.status) shouldBe Some(
        OrganizationStatus.ORGANIZATION_STATUS_ACTIVE
      )
    }

    "correctly add members to the organization" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1
      val orgId = testKit.currentState.organization
        .flatMap(_.oid)
      val apiAddMembersToOrganization = ApiAddMembersToOrganization(
        orgId.map(id => ApiOrganizationId(id.id)),
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
        Some(testOrgId),
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
      val apiRemoveMembersFromOrganization = ApiRemoveMembersFromOrganization(
        orgId.map(id => ApiOrganizationId(id.id)),
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
        Some(testOrgId),
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
      val apiAddOwnersToOrganization = ApiAddOwnersToOrganization(
        orgId.map(id => ApiOrganizationId(id.id)),
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
        Some(testOrgId),
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
      val apiRemoveOwnersFromOrganization = ApiRemoveOwnersFromOrganization(
        orgId.map(id => ApiOrganizationId(id.id)),
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
        Some(testOrgId),
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
