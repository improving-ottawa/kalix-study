package app.improving.membercontext.member

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{ApiMemberId, MemberId}
import app.improving.membercontext.{
  MemberReleased,
  MemberInfoUpdated,
  MemberRegistered,
  MemberStatus,
  MemberStatusUpdated,
  MetaInfo
}
import app.improving.membercontext.infrastructure.util._
import io.grpc.Status
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberAPI(context: EventSourcedEntityContext) extends AbstractMemberAPI {
  override def emptyState: MemberState = MemberState.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

  override def registerMember(
      currentState: MemberState,
      apiRegisterMember: ApiRegisterMember
  ): EventSourcedEntity.Effect[ApiMemberId] = {
    currentState.member match {
      case Some(member) => {

        log.info(
          s"MemberAPI in registerMember - member already existed - ${member}"
        )

        effects.error(
          s"Member already registered for id ${apiRegisterMember.memberId}"
        ) // already registered so just return.
      }
      case _ => {

        log.info(
          s"MemberAPI in registerMember - apiRegisterMember - ${apiRegisterMember}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val memberIdOpt = apiRegisterMember.registeringMember.map(member =>
          MemberId(member.memberId)
        )
        val memberId = apiRegisterMember.memberId
        val event = MemberRegistered(
          Some(MemberId(memberId)),
          apiRegisterMember.info.map(convertApiInfoToInfo),
          Some(
            MetaInfo(
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              memberIdOpt,
              MemberStatus.MEMBER_STATUS_DRAFT
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => ApiMemberId(memberId))
      }
    }
  }

  override def updateMemberStatus(
      currentState: MemberState,
      apiUpdateMemberStatus: ApiUpdateMemberStatus
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(state)
          if state.memberId.contains(
            MemberId(apiUpdateMemberStatus.memberId)
          ) && isStateChangeValid(state, apiUpdateMemberStatus.newStatus) => {

        log.info(
          s"MemberAPI in updateMemberStatus - apiUpdateMemberStatus - ${apiUpdateMemberStatus}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = MemberStatusUpdated(
          state.memberId,
          Some(
            MetaInfo(
              Some(timestamp),
              state.memberId,
              Some(timestamp),
              state.memberId,
              convertMemberStatus(
                apiUpdateMemberStatus.newStatus
              )
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case other =>
        log.info(
          s"MemberAPI in updateMemberStatus - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)
    }
  }

  def isStateChangeValid(member: Member, status: ApiMemberStatus): Boolean = {
    member.status match {
      case MemberStatus.MEMBER_STATUS_DRAFT
          if member.getInfo.handle.nonEmpty &&
            member.getInfo.avatar.nonEmpty &&
            member.getInfo.firstName.nonEmpty &&
            member.getInfo.lastName.nonEmpty &&
            !status.isApiMemberStatusDraft =>
        true
      case MemberStatus.MEMBER_STATUS_ACTIVE
          if !status.isApiMemberStatusDraft &&
            !status.isApiMemberStatusActive =>
        true
      case MemberStatus.MEMBER_STATUS_INACTIVE
          if !status.isApiMemberStatusDraft &&
            !status.isApiMemberStatusInactive &&
            !status.isApiMemberStatusSuspended =>
        true
      case MemberStatus.MEMBER_STATUS_SUSPENDED
          if !status.isApiMemberStatusDraft &&
            !status.isApiMemberStatusInactive &&
            !status.isApiMemberStatusSuspended =>
        true
      case _ =>
        false
    }
  }

  override def updateMemberInfo(
      currentState: MemberState,
      apiUpdateMemberInfo: ApiUpdateMemberInfo
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(state) => {

        log.info(
          s"MemberAPI in updateMemberInfo - apiUpdateMemberInfo - ${apiUpdateMemberInfo}"
        )

        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val memberIdOpt = apiUpdateMemberInfo.actingMember.map(member =>
          MemberId(member.memberId)
        )
        val event = MemberInfoUpdated(
          Some(MemberId(apiUpdateMemberInfo.memberId)),
          apiUpdateMemberInfo.info.map(info =>
            convertApiUpdateInfoToInfo(info)
          ),
          Some(
            MetaInfo(
              Some(timestamp),
              memberIdOpt,
              Some(timestamp),
              memberIdOpt,
              state.status
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }

      case other => {
        log.info(
          s"MemberAPI in updateMemberInfo - other - $other"
        )
        effects.reply(Empty.defaultInstance)
      }
    }
  }

//  override def registerMemberList(
//      currentState: MemberState,
//      apiRegisterMemberList: member.ApiRegisterMemberList
//  ): EventSourcedEntity.Effect[Empty] = {
//    currentState.member match {
//      case Some(_) => {
//        val now = java.time.Instant.now()
//        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
//        val memberIdOpt = apiRegisterMemberList.registeringMember.map(member =>
//          MemberId(member.member_id)
//        )
//        val memberMap =
//          apiRegisterMemberList.member_list
//            .map(convertApiMemberMapToMemberMap)
//            .getOrElse(MemberMap.defaultInstance)
//            .map
//
//        memberMap.foreach {
//          case (member_id, info) => {
//            val event = MemberRegistered(
//              Some(MemberId(member_id)),
//              Some(info),
//              Some(
//                MetaInfo(
//                  Some(timestamp),
//                  memberIdOpt,
//                  Some(timestamp),
//                  memberIdOpt,
//                  MemberStatus.ACTIVE
//                )
//              )
//            )
//            effects.emitEvent(event)
//          }
//        }
//        effects.reply(Empty.defaultInstance)
//      }
//
//      case _ => effects.reply(Empty.defaultInstance)
//    }
//  }

  override def getMemberData(
      currentState: MemberState,
      apiGetMemberData: ApiGetMemberData
  ): EventSourcedEntity.Effect[ApiMemberData] =
    currentState.member match {
      case Some(state)
          if state.memberId.contains(MemberId(apiGetMemberData.memberId)) => {

        log.info(
          s"MemberAPI in getMemberData - apiGetMemberData - ${apiGetMemberData}"
        )

        val apiMemberData = ApiMemberData(
          apiGetMemberData.memberId,
          state.info.map(convertInfoToApiUpdateInfo),
          state.meta.map(convertMetaInfoToApiMetaInfo)
        )
        effects.reply(apiMemberData)
      }
      case other =>
        log.info(
          s"MemberAPI in getMemberData - other - ${other}"
        )

        effects.error(
          s"MemberData ID ${apiGetMemberData.memberId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )

    }

  override def memberInfoUpdated(
      currentState: MemberState,
      memberInfoUpdated: MemberInfoUpdated
  ): MemberState = {
    currentState.member match {
      case Some(state) if state.memberId == memberInfoUpdated.memberId => {

        log.info(
          s"MemberAPI in memberInfoUpdated - memberInfoUpdated - ${memberInfoUpdated}"
        )

        currentState.withMember(
          state.copy(
            info = memberInfoUpdated.info,
            meta = memberInfoUpdated.meta
          )
        )
      }
      case other =>
        log.info(
          s"MemberAPI in memberInfoUpdated - other - ${other}"
        )

        currentState
    }
  }

//  override def memberListRegistered(
//      currentState: MemberState,
//      memberListRegistered: MemberListRegistered
//  ): MemberState = {
//    currentState.member match {
//      case Some(state) => {
//        val map = memberListRegistered.member_list.getOrElse(MemberMap()).map
//
//        map.foreach {
//          case (id, info) => {
//            val value = Value(
//              info.contact,
//              info.handle,
//              info.avatar,
//              info.first_name,
//              info.last_name,
//              info.mobile_number,
//              info.email_address,
//              info.notification_preference,
//              info.organization_membership,
//              info.tenant
//            )
//            val setValue = SetValue(
//              id,
//              state.member_id.map(member => Key(member.id)),
//              Some(value)
//            )
//
//            components.memberMap.set(setValue)
//          }
//        }
//        currentState
//      }
//      case _ => currentState
//    }
//  }

  override def memberRegistered(
      currentState: MemberState,
      memberRegistered: MemberRegistered
  ): MemberState = {
    currentState.member match {
      case Some(member) => {

        log.info(
          s"MemberAPI in memberRegistered - member already existed - $member"
        )

        currentState
      } // already registered so just return.
      case _ => {

        log.info(
          s"MemberAPI in memberRegistered - memberRegistered - ${memberRegistered}"
        )

        val member = Member(
          memberRegistered.memberId,
          memberRegistered.info,
          memberRegistered.meta,
          MemberStatus.MEMBER_STATUS_DRAFT
        )
        currentState.withMember(member)
      }
    }
  }

  override def memberStatusUpdated(
      currentState: MemberState,
      memberStatusUpdated: MemberStatusUpdated
  ): MemberState =
    currentState.member match {
      case Some(state) if state.memberId == memberStatusUpdated.memberId => {

        log.info(
          s"MemberAPI in memberStatusUpdated - memberStatusUpdated - $memberStatusUpdated"
        )

        currentState.withMember(
          state.copy(
            status = memberStatusUpdated.meta
              .map(_.memberStatus)
              .getOrElse(MemberStatus.MEMBER_STATUS_UNKNOWN),
            meta = memberStatusUpdated.meta
          )
        )
      }
      case other => {
        log.info(
          s"MemberAPI in memberStatusUpdated - other - $other"
        )
        currentState
      }
    }

  override def releaseMember(
      currentState: MemberState,
      apiReleaseMember: ApiReleaseMember
  ): EventSourcedEntity.Effect[Empty] =
    effects
      .emitEvent(
        MemberReleased(
          Some(MemberId(apiReleaseMember.memberId)),
          apiReleaseMember.deletingMemberId.map(apiId =>
            MemberId(apiId.memberId)
          )
        )
      )
      .deleteEntity()
      .thenReply(_ => Empty.defaultInstance)

  override def memberReleased(
      currentState: MemberState,
      memberReleased: MemberReleased
  ): MemberState = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

    currentState.copy(member =
      currentState.member.map(
        _.copy(
          status = MemberStatus.MEMBER_STATUS_RELEASED,
          meta = currentState.member.flatMap(
            _.meta.map(
              _.copy(
                lastModifiedBy = memberReleased.releasingMember,
                lastModifiedOn = Some(timestamp)
              )
            )
          )
        )
      )
    )
  }
}
