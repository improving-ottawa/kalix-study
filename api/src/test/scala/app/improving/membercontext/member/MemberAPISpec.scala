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

      memberOpt.map(_.status) shouldBe Some(MemberStatus.ACTIVE)

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

      memberOpt.map(_.status) shouldBe Some(MemberStatus.ACTIVE)

      val testMember = testKit.currentState.member
        .flatMap(_.memberId)
        .map(_.id)
        .getOrElse("MemberId not found.")
      val updateCommand = ApiUpdateMemberStatus(
        testMember,
        Some(ApiMemberId(testMemberId2)),
        ApiMemberStatus.INACTIVE
      )

      val updatedResult = testKit.updateMemberStatus(updateCommand)

      updatedResult.events should have size 1

      val memberOpt2 = testKit.currentState.member

      memberOpt2 shouldBe defined

      memberOpt2.map(_.status) shouldBe Some(MemberStatus.INACTIVE)
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

      infoOpt.flatMap(_.emailAddress) shouldBe Some(
        EmailAddress("newemail@member.com")
      )

      infoOpt.flatMap(_.mobileNumber) shouldBe Some(
        MobileNumber("898-000-9876")
      )

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

      getMemberDataResult.reply.getInfo.emailAddress
        .map(_.value) shouldBe testKit.currentState.member
        .flatMap(_.info)
        .flatMap(_.emailAddress)
        .map(_.value)

      getMemberDataResult.reply.getInfo.mobileNumber
        .map(_.value) shouldBe testKit.currentState.member
        .flatMap(_.info)
        .flatMap(_.mobileNumber)
        .map(_.value)

      getMemberDataResult.reply.getMeta.lastModifiedBy
        .map(_.memberId) shouldBe testKit.currentState.member
        .flatMap(_.meta)
        .flatMap(_.lastModifiedBy)
        .map(_.id)
    }

//    "memberList registered should work correctly" in {
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
