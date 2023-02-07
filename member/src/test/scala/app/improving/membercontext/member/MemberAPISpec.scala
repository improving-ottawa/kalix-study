package app.improving.membercontext.member

import TestData.{testMemberId, _}
import app.improving._
import app.improving.membercontext.MemberStatus
import app.improving.membercontext.infrastructure.util.convertApiInfoToInfo
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberAPISpec extends AnyWordSpec with Matchers {
  "The MemberAPI" should {

    "register member should work correctly" in {
      val testKit = MemberAPITestKit(new MemberAPI(_))

      val command = ApiRegisterMember(
        testMemberId,
        Some(apiInfo),
        Some(ApiMemberId(testMemberId))
      )

      val result = testKit.registerMember(command)

      result.events should have size 1

      val memberOpt = testKit.currentState.member

      memberOpt shouldBe defined

      memberOpt.map(_.status) shouldBe Some(MemberStatus.MEMBER_STATUS_ACTIVE)

      memberOpt.flatMap(_.info) shouldBe Some(convertApiInfoToInfo(apiInfo))
    }

    "member status update should work correctly" in {
      val testKit = MemberAPITestKit(new MemberAPI(_))

      val command = ApiRegisterMember(
        testMemberId,
        Some(apiInfo),
        Some(ApiMemberId(testMemberId))
      )

      val result = testKit.registerMember(command)

      result.events should have size 1

      val memberOpt = testKit.currentState.member

      memberOpt shouldBe defined

      memberOpt.map(_.status) shouldBe Some(MemberStatus.MEMBER_STATUS_ACTIVE)

      val testMember = testKit.currentState.member
        .flatMap(_.memberId)
        .map(_.id)
        .getOrElse("MemberId not found.")
      val updateCommand = ApiUpdateMemberStatus(
        testMember,
        Some(ApiMemberId(testMemberId2)),
        ApiMemberStatus.API_MEMBER_STATUS_INACTIVE
      )

      val updatedResult = testKit.updateMemberStatus(updateCommand)

      updatedResult.events should have size 1

      val memberOpt2 = testKit.currentState.member

      memberOpt2 shouldBe defined

      memberOpt2.map(_.status) shouldBe Some(
        MemberStatus.MEMBER_STATUS_INACTIVE
      )
    }

    "member info update should work correctly" in {
      val testKit = MemberAPITestKit(new MemberAPI(_))

      val command = ApiRegisterMember(
        testMemberId,
        Some(apiInfo),
        Some(ApiMemberId(testMemberId))
      )

      val result = testKit.registerMember(command)

      result.events should have size 1

      val memberOpt = testKit.currentState.member

      memberOpt shouldBe defined

      val testMember = testKit.currentState.member
        .flatMap(_.memberId)
        .map(_.id)
        .getOrElse("MemberId not found.")

      val partialUpdateCommand = ApiUpdateMemberInfo(
        testMember,
        Some(apiPartialUpdateInfo),
        Some(ApiMemberId(testMemberId2))
      )

      val partiallyUpdatedResult = testKit.updateMemberInfo(partialUpdateCommand)

      partiallyUpdatedResult.events should have size 1

      val memberOptPartial = testKit.currentState.member

      memberOptPartial shouldBe defined

      val partialUpdateInfoOpt = memberOptPartial.flatMap(_.info)

      val contactPartialOpt = partialUpdateInfoOpt.flatMap(_.contact)

      contactPartialOpt.map(_.firstName) shouldBe Some("firstname")

      contactPartialOpt.map(_.lastName) shouldBe Some("new-lastname")

      contactPartialOpt.flatMap(_.emailAddress) shouldBe Some(
        EmailAddress("newemail@member.com")
      )

      contactPartialOpt.flatMap(_.phone) shouldBe Some(
        MobileNumber("898-000-9876")
      )

      val apiOrganizationIdPartialOpt = partialUpdateInfoOpt.map(_.organizationMembership)

      apiOrganizationIdPartialOpt.nonEmpty shouldBe true

      val tenantIdPartialOpt = partialUpdateInfoOpt.flatMap(_.tenant)

      tenantIdPartialOpt shouldBe defined

      val updateCommand = ApiUpdateMemberInfo(
        testMember,
        Some(apiUpdateInfo),
        Some(ApiMemberId(testMemberId2))
      )

      val updatedResult = testKit.updateMemberInfo(updateCommand)

      updatedResult.events should have size 1

      val memberOpt2 = testKit.currentState.member

      memberOpt2 shouldBe defined

      val infoOpt = memberOpt2.flatMap(_.info)

      val contactOpt = infoOpt.flatMap(_.contact)

      contactOpt.map(_.firstName) shouldBe Some("new-firstname")

      contactOpt.map(_.lastName) shouldBe Some("new-lastname")

      contactOpt.flatMap(_.emailAddress) shouldBe Some(
        EmailAddress("newemail@member.com")
      )

      contactOpt.flatMap(_.phone) shouldBe Some(
        MobileNumber("898-000-9876")
      )

    }

    "get member data should work correctly" in {
      val testKit = MemberAPITestKit(new MemberAPI(_))

      val command = ApiRegisterMember(
        testMemberId,
        Some(apiInfo),
        Some(ApiMemberId(testMemberId))
      )

      val result = testKit.registerMember(command)

      result.events should have size 1

      val testMember = testKit.currentState.member
        .flatMap(_.memberId)
        .map(_.id)
        .getOrElse("MemberId not found.")

      val getMemberDataCommand = ApiGetMemberData(
        testMember
      )

      val getMemberDataResult = testKit.getMemberData(getMemberDataCommand)

      getMemberDataResult.reply.getMeta.lastModifiedBy
        .map(_.memberId) shouldBe testKit.currentState.member
        .flatMap(_.meta)
        .flatMap(_.lastModifiedBy)
        .map(_.id)
    }

//    "member_list registered should work correctly" in {
//      val testKit = MemberAPITestKit(new MemberAPI(_))
//
//      val command = ApiRegisterMember(
//        testMemberId,
//        Some(apiInfo),
//        Some(ApiMemberId(testMemberId))
//      )
//
//      val result = testKit.registerMember(command)
//
//      result.events should have size 1
//
//      val memberMap =
//        ApiMemberMap(Map[String, ApiInfo](testMemberId -> apiInfo))
//
//      val apiRegisterMemberList = ApiRegisterMemberList(
//        testMemberId,
//        Some(memberMap),
//        Some(ApiMemberId(testMemberId2))
//      )
//
//      val apiRegisterMemberListResult =
//        testKit.registerMemberList(apiRegisterMemberList)
//
//      apiRegisterMemberListResult.events should have size 1
//    }
  }
}
