package com.improving.organizationcontext.organization

import com.google.protobuf.timestamp.Timestamp
import com.improving._
import com.improving.organization.{
  ApiAddMembersToOrganization,
  ApiAddOwnersToOrganization,
  ApiAddress,
  ApiCAPostalCode,
  ApiEditOrganizationInfo,
  ApiEstablishOrganization,
  ApiGetOrganizationInfo,
  ApiInfo,
  ApiMemberId,
  ApiMetaInfo,
  ApiOrganizationId,
  ApiOrganizationStatus,
  ApiOrganizationStatusUpdated,
  ApiParent,
  ApiRemoveMembersFromOrganization,
  ApiRemoveOwnersFromOrganization,
  ApiUpdateInfo,
  ApiUpdateParent
}
import com.improving.organizationcontext.{
  GetOrganizationInfo,
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
            )
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

      val apiUpdateParent =
        ApiUpdateParent(testOrgId, Some(ApiOrganizationId(newParentId)))

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

      val apiOrganizationStatusUpdated =
        ApiOrganizationStatusUpdated(
          testOrgId,
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

      val apiGetOrganizationInfo = ApiGetOrganizationInfo(testOrgId)

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

      getOrganizationInfoResult.reply shouldEqual ApiInfo.defaultInstance
    }

    "correctly add members to the organization" in {
      val testKit = OrganizationAPITestKit(new OrganizationAPI(_))

      val establishOrganizationResult =
        testKit.establishOrganization(apiEstablishOrganization)

      establishOrganizationResult.events should have size 1

      val apiAddMembersToOrganization = ApiAddMembersToOrganization(
        testOrgId,
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
      members should have size 4

      members shouldEqual Seq(
        MemberId("member1"),
        MemberId("member2"),
        MemberId("member3"),
        MemberId("member4")
      )

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
          ApiMemberId("member1"),
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
        testKit.removeMembersFromOrganization(
          apiRemoveMembersFromOrganization
        )

      apiRemoveMembersFromOrganizationResult.events should have size 1

      val members = testKit.currentState.organization
        .map(_.members)
        .getOrElse(Seq.empty[MemberId])
      members should have size 1

      members shouldEqual Seq(
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

      val apiAddOwnersToOrganization = ApiAddOwnersToOrganization(
        testOrgId,
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

      val apiRemoveOwnersFromOrganization = ApiRemoveOwnersFromOrganization(
        testOrgId,
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
