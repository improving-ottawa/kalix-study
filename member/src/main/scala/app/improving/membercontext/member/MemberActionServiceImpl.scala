package app.improving.membercontext.member

import app.improving.ApiMemberId
import com.google.protobuf.empty.Empty
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberActionServiceImpl(creationContext: ActionCreationContext)
    extends AbstractMemberActionServiceAction {

  val memberService: MemberService =
    creationContext.getGrpcClient(classOf[MemberService], "member")

  override def registerMemberList(
      apiRegisterMemberList: ApiRegisterMemberList
  ): Action.Effect[ApiMemberIds] = {
    val memberIdOpt = apiRegisterMemberList.registeringMember.map(member =>
      ApiMemberId(member.memberId)
    )
    val apiMemberMap =
      apiRegisterMemberList.memberList
        .getOrElse(ApiMemberMap.defaultInstance)
        .map

    apiMemberMap.foreach { case (memberId, info) =>
      val register = ApiRegisterMember(
        memberId,
        Some(info),
        memberIdOpt
      )
      memberService.registerMember(register)

    }

    effects.reply(
      ApiMemberIds(apiMemberMap.keys.toSeq.map(ApiMemberId(_)))
    )
  }
}
