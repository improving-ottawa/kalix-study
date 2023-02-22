package app.improving.membercontext

import app.improving.membercontext.infrastructure.util.{
  convertInfoToApiUpdateInfo,
  convertMemberRegisteredToApiMemberData,
  convertMetaInfoToApiMetaInfo
}
import app.improving.membercontext.member.ApiMemberData
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberByMetaInfoViewImpl(context: ViewContext)
    extends AbstractMemberByMetaInfoView {

  override def emptyState: ApiMemberData = ApiMemberData.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

  override def processRegisterMember(
      state: ApiMemberData,
      memberRegistered: MemberRegistered
  ): UpdateEffect[ApiMemberData] = {
    if (state != emptyState) {

      log.info(
        s"MemberByMetaInfoViewImpl in processRegisterMember - state already existed"
      )

      effects.ignore()
    } else {

      log.info(
        s"MemberByMetaInfoViewImpl in processRegisterMember - memberRegistered ${memberRegistered}"
      )

      effects.updateState(
        convertMemberRegisteredToApiMemberData(memberRegistered)
      )
    }
  }

  override def processUpdateMemberStatus(
      state: ApiMemberData,
      memberStatusUpdated: MemberStatusUpdated
  ): UpdateEffect[ApiMemberData] = {

    log.info(
      s"MemberByMetaInfoViewImpl in processUpdateMemberStatus - memberStatusUpdated ${memberStatusUpdated}"
    )

    val updatedMetaOpt =
      memberStatusUpdated.meta.map(convertMetaInfoToApiMetaInfo)
    effects.updateState(
      state.copy(meta = updatedMetaOpt)
    )
  }

  override def processUpdateMemberInfo(
      state: ApiMemberData,
      memberInfoUpdated: MemberInfoUpdated
  ): UpdateEffect[ApiMemberData] = {

    log.info(
      s"MemberByMetaInfoViewImpl in processUpdateMemberInfo - memberInfoUpdated ${memberInfoUpdated}"
    )

    val updatedInfoOpt =
      memberInfoUpdated.info.map(convertInfoToApiUpdateInfo)
    effects.updateState(
      state.copy(info = updatedInfoOpt)
    )
  }

  override def processRegisterMemberList(
      state: ApiMemberData,
      memberListRegistered: MemberListRegistered
  ): UpdateEffect[ApiMemberData] = {
    throw new UnsupportedOperationException(
      "Update handler for 'ProcessRegisterMemberList' not implemented yet"
    )
  }
}
