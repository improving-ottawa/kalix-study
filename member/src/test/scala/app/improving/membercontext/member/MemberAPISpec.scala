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

//    "register member should work correctly" in {
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
//      val memberOpt = testKit.currentState.member
//
//      memberOpt shouldBe defined
//
//      memberOpt.map(_.status) shouldBe Some(MemberStatus.MEMBER_STATUS_DRAFT)
//
//      memberOpt.flatMap(_.info) shouldBe Some(convertApiInfoToInfo(apiInfo))
//    }
//
//    "member status update should work correctly" in {
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
//      val memberOpt = testKit.currentState.member
//
//      memberOpt shouldBe defined
//
//      memberOpt.map(_.status) shouldBe Some(MemberStatus.MEMBER_STATUS_DRAFT)
//
//      val testMember = testKit.currentState.member
//        .flatMap(_.memberId)
//        .map(_.id)
//        .getOrElse("MemberId not found.")
//      val updateCommand = ApiUpdateMemberStatus(
//        testMember,
//        Some(ApiMemberId(testMemberId2)),
//        ApiMemberStatus.API_MEMBER_STATUS_INACTIVE
//      )
//
//      val updatedResult = testKit.updateMemberStatus(updateCommand)
//
//      updatedResult.events should have size 1
//
//      val memberOpt2 = testKit.currentState.member
//
//      memberOpt2 shouldBe defined
//
//      memberOpt2.map(_.status) shouldBe Some(
//        MemberStatus.MEMBER_STATUS_INACTIVE
//      )
//
//      val updateCommandSuspendedStatus = ApiUpdateMemberStatus(
//        testMember,
//        Some(ApiMemberId(testMemberId2)),
//        ApiMemberStatus.API_MEMBER_STATUS_SUSPENDED
//      )
//
//      val updatedSuspendedStatusResult =
//        testKit.updateMemberStatus(updateCommandSuspendedStatus)
//
//      updatedSuspendedStatusResult.events should have size 0
//
//      val memberOpt3 = testKit.currentState.member
//
//      memberOpt3 shouldBe defined
//
//      memberOpt3.map(_.status) shouldBe Some(
//        MemberStatus.MEMBER_STATUS_INACTIVE
//      )
//    }
//
//    "member info update should work correctly" in {
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
//      val memberOpt = testKit.currentState.member
//
//      memberOpt shouldBe defined
//
//      val testMember = testKit.currentState.member
//        .flatMap(_.memberId)
//        .map(_.id)
//        .getOrElse("MemberId not found.")
//
//      val partialUpdateCommand = ApiUpdateMemberInfo(
//        testMember,
//        Some(apiPartialUpdateInfo),
//        Some(ApiMemberId(testMemberId2))
//      )
//
//      val partiallyUpdatedResult =
//        testKit.updateMemberInfo(partialUpdateCommand)
//
//      partiallyUpdatedResult.events should have size 1
//
//      val memberOptPartial = testKit.currentState.member
//
//      memberOptPartial shouldBe defined
//
//      val partialUpdateInfoOpt = memberOptPartial.flatMap(_.info)
//
//      partialUpdateInfoOpt shouldBe defined
//
//      partialUpdateInfoOpt.get.firstName shouldBe "member-name"
//
//      partialUpdateInfoOpt.get.lastName shouldBe "new-lastname"
//
//      val apiOrganizationIdPartialOpt =
//        partialUpdateInfoOpt.map(_.organizationMembership)
//
//      apiOrganizationIdPartialOpt shouldBe defined
//
//      val tenantIdPartialOpt = partialUpdateInfoOpt.flatMap(_.tenant)
//
//      tenantIdPartialOpt shouldBe defined
//
//      val updateCommand = ApiUpdateMemberInfo(
//        testMember,
//        Some(apiUpdateInfo),
//        Some(ApiMemberId(testMemberId2))
//      )
//
//      val updatedResult = testKit.updateMemberInfo(updateCommand)
//
//      updatedResult.events should have size 1
//
//      val memberOpt2 = testKit.currentState.member
//
//      memberOpt2 shouldBe defined
//
//      val infoOpt = memberOpt2.flatMap(_.info)
//
//      val contactOpt = infoOpt.flatMap(_.contact)
//
//      contactOpt.map(_.firstName) shouldBe Some("new-firstname")
//
//      contactOpt.map(_.lastName) shouldBe Some("new-lastname")
//
//      contactOpt.flatMap(_.emailAddress) shouldBe Some(
//        EmailAddress("newemail@member.com")
//      )
//
//      contactOpt.flatMap(_.phone) shouldBe Some(
//        MobileNumber("898-000-9876")
//      )
//
//    }
//
//    "get member data should work correctly" in {
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
//      val testMember = testKit.currentState.member
//        .flatMap(_.memberId)
//        .map(_.id)
//        .getOrElse("MemberId not found.")
//
//      val getMemberDataCommand = ApiGetMemberData(
//        testMember
//      )
//
//      val getMemberDataResult = testKit.getMemberData(getMemberDataCommand)
//
//      getMemberDataResult.reply.getMeta.lastModifiedBy
//        .map(_.memberId) shouldBe testKit.currentState.member
//        .flatMap(_.meta)
//        .flatMap(_.lastModifiedBy)
//        .map(_.id)
//    }

    "correctly change member statuses" in {
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

      val activateMember = ApiActivateMember(
        memberOpt
          .flatMap(_.memberId)
          .map(_.id)
          .getOrElse("memberId is NOT FOUND."),
        Some(ApiMemberId(testMemberId))
      )

      val activateMemberResult = testKit.activateMember(activateMember)

      activateMemberResult.events should have size 1

      testKit.currentState.getMember.status shouldBe MemberStatus.MEMBER_STATUS_ACTIVE

      val inactivateMember = ApiInactivateMember(
        memberOpt
          .flatMap(_.memberId)
          .map(_.id)
          .getOrElse("memberId is NOT FOUND."),
        Some(ApiMemberId(testMemberId))
      )

      val inactivateMemberResult = testKit.inactivateMember(inactivateMember)

      inactivateMemberResult.events should have size 1

      testKit.currentState.getMember.status shouldBe MemberStatus.MEMBER_STATUS_INACTIVE

      val activateMemberResult1 = testKit.activateMember(activateMember)

      activateMemberResult1.events should have size 1

      testKit.currentState.getMember.status shouldBe MemberStatus.MEMBER_STATUS_ACTIVE

      val suspendMember = ApiSuspendMember(
        memberOpt
          .flatMap(_.memberId)
          .map(_.id)
          .getOrElse("memberId is NOT FOUND."),
        Some(ApiMemberId(testMemberId))
      )

      val suspendMemberResult = testKit.suspendMember(suspendMember)

      suspendMemberResult.events should have size 1

      testKit.currentState.getMember.status shouldBe MemberStatus.MEMBER_STATUS_SUSPENDED

      val terminateMember = ApiTerminateMember(
        memberOpt
          .flatMap(_.memberId)
          .map(_.id)
          .getOrElse("memberId is NOT FOUND."),
        Some(ApiMemberId(testMemberId))
      )

      val terminateMemberResult = testKit.terminateMember(terminateMember)

      terminateMemberResult.events should have size 1

      testKit.currentState.getMember.status shouldBe MemberStatus.MEMBER_STATUS_TERMINATED
    }

    "correctly not change member statuses" in {
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

      val suspendMember = ApiSuspendMember(
        memberOpt
          .flatMap(_.memberId)
          .map(_.id)
          .getOrElse("memberId is NOT FOUND."),
        Some(ApiMemberId(testMemberId))
      )

      val suspendMemberResult = testKit.suspendMember(suspendMember)

      suspendMemberResult.events should have size 0

      val inactivateMember = ApiInactivateMember(
        memberOpt
          .flatMap(_.memberId)
          .map(_.id)
          .getOrElse("memberId is NOT FOUND."),
        Some(ApiMemberId(testMemberId))
      )

      val inactivateMemberResult = testKit.inactivateMember(inactivateMember)

      inactivateMemberResult.events should have size 0

      val activateMember = ApiActivateMember(
        memberOpt
          .flatMap(_.memberId)
          .map(_.id)
          .getOrElse("memberId is NOT FOUND."),
        Some(ApiMemberId(testMemberId))
      )

      val activateMemberResult = testKit.activateMember(activateMember)

      activateMemberResult.events should have size 1

      testKit.currentState.getMember.status shouldBe MemberStatus.MEMBER_STATUS_ACTIVE

      val inactivateMemberResult1 = testKit.inactivateMember(inactivateMember)

      inactivateMemberResult1.events should have size 1

      val inactivateMemberResult2 = testKit.inactivateMember(inactivateMember)

      inactivateMemberResult2.events should have size 0

      val suspendMemberResult1 = testKit.suspendMember(suspendMember)

      suspendMemberResult1.events should have size 0
    }
  }
}
