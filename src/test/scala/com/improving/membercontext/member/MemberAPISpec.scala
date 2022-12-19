package com.improving.membercontext.member

import com.improving.{
  ApiContact,
  ApiEmailAddress,
  ApiMemberId,
  ApiMobileNumber,
  ApiTenantId,
  Contact,
  EmailAddress,
  MobileNumber,
  OrganizationId,
  TenantId
}
import com.improving.member.{
  ApiGetMemberData,
  ApiInfo,
  ApiMemberStatus,
  ApiNotificationPreference,
  ApiRegisterMember,
  ApiUpdateInfo,
  ApiUpdateMemberInfo,
  ApiUpdateMemberStatus
}
import com.improving.membercontext.{Info, MemberStatus, NotificationPreference}
import com.improving.organization.ApiOrganizationId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberAPISpec extends AnyWordSpec with Matchers {
  "The MemberAPI" should {
    val testMemberId = "test-member-id"
    val testMemberId2 = "test-member-id2"
    val testOrganizationId = "test-organization-id"
    val testTenantId = "test-tenant-id"
    val apiContact = ApiContact(
      "member-first-name",
      "member-last-name",
      Some(ApiEmailAddress("member@memberapi.com")),
      Some(ApiMobileNumber("987-878-0987")),
      "user-name"
    )
    val apiInfo = ApiInfo(
      Some(apiContact),
      "handle",
      "avartar",
      "member-name",
      "short-name",
      Some(ApiMobileNumber("987-878-0987")),
      Some(ApiEmailAddress("member@memberapi.com")),
      ApiNotificationPreference.SMS,
      Seq[ApiOrganizationId](ApiOrganizationId(testMemberId)),
      Some(ApiTenantId(testTenantId))
    )
    val apiUpdateInfo = ApiUpdateInfo(
      "new-handle",
      "new-avatar",
      "new-firstname",
      "new-lastname",
      Some(ApiMobileNumber("898-000-9876")),
      Some(ApiEmailAddress("newemail@member.com")),
      ApiNotificationPreference.EMAIL,
      Seq[ApiOrganizationId](ApiOrganizationId(testOrganizationId))
    )
    val contact = Contact(
      "member-first-name",
      "member-last-name",
      Some(EmailAddress("member@memberapi.com")),
      Some(MobileNumber("987-878-0987")),
      "user-name"
    )
    val info = Info(
      Some(contact),
      "handle",
      "avartar",
      "member-name",
      "short-name",
      Some(MobileNumber("987-878-0987")),
      Some(EmailAddress("member@memberapi.com")),
      NotificationPreference.SMS,
      Seq[OrganizationId](OrganizationId(testMemberId)),
      Some(TenantId(testTenantId))
    )

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

      memberOpt.flatMap(_.info) shouldBe Some(info)
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

      val updateCommand = ApiUpdateMemberStatus(
        testMemberId,
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

      memberOpt.flatMap(_.info) shouldBe Some(info)

      val updateCommand = ApiUpdateMemberInfo(
        testMemberId,
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

      val getMemberDataCommand = ApiGetMemberData(
        testMemberId
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
  }
}
