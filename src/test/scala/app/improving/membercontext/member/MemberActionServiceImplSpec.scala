package app.improving.membercontext.member

import app.improving.{
  ApiContact,
  ApiEmailAddress,
  ApiMemberId,
  ApiMobileNumber,
  ApiTenantId,
  Contact,
  EmailAddress,
  MobileNumber
}
import app.improving.organizationcontext.organization.ApiOrganizationId
import com.google.protobuf.empty.Empty
import kalix.scalasdk.testkit.MockRegistry
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberActionServiceImplSpec
    extends AnyWordSpec
    with Matchers
    with AsyncMockFactory {

  "MemberActionServiceImpl" must {

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

    "handle command RegisterMemberList" in {

      val mockMemberService = mock[MemberService]
      (mockMemberService.registerMember _)
        .when(*)
        .returns(Future.successful(Empty.defaultInstance))

      val mockRegistry = MockRegistry.withMock(mockMemberService)

      val service =
        MemberActionServiceImplTestKit(
          new MemberActionServiceImpl(_),
          mockRegistry
        )

      val memberMap =
        ApiMemberMap(Map[String, ApiInfo](testMemberId -> apiInfo))

      val apiRegisterMemberList = ApiRegisterMemberList(
        testMemberId,
        Some(memberMap),
        Some(ApiMemberId(testMemberId2))
      )

      val apiRegisterMemberListResult =
        service.registerMemberList(apiRegisterMemberList)

      apiRegisterMemberListResult.reply shouldBe com.google.protobuf.empty.Empty.defaultInstance
    }
  }
}
