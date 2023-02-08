package app.improving.membercontext.member

import app.improving.ApiMemberId
import kalix.scalasdk.testkit.MockRegistry
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import TestData._

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

    "handle command RegisterMemberList" in {

      val mockMemberService = mock[MemberService]
      (mockMemberService.registerMember _)
        .when(*)
        .returns(Future.successful(ApiMemberId.defaultInstance))

      val mockRegistry = MockRegistry.withMock(mockMemberService)

      val service =
        MemberActionServiceImplTestKit(
          new MemberActionServiceImpl(_),
          mockRegistry
        )

      val memberMap =
        ApiMemberMap(Map[String, ApiInfo](testMemberId -> apiInfo))

      val apiRegisterMemberList = ApiRegisterMemberList(
        Some(memberMap),
        Some(ApiMemberId(testMemberId2))
      )

      val apiRegisterMemberListResult =
        service.registerMemberList(apiRegisterMemberList)

      apiRegisterMemberListResult.reply shouldBe com.google.protobuf.empty.Empty.defaultInstance
    }
  }
}
