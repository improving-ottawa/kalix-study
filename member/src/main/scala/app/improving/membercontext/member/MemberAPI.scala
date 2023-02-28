package app.improving.membercontext.member

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{ApiMemberId, MemberId}
import app.improving.membercontext.{
  MemberActivated,
  MemberInactivated,
  MemberInfoUpdated,
  MemberRegistered,
  MemberReleased,
  MemberStatus,
  MemberStatusUpdated,
  MemberSuspended,
  MemberTerminated,
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
              memberIdOpt
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
              state.memberId
            )
          ),
          convertMemberStatus(apiUpdateMemberStatus.newStatus)
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
            !status.isApiMemberStatusDraft && !status.isApiMemberStatusSuspended &&
            !status.isApiMemberStatusInactive =>
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
      case MemberStatus.MEMBER_STATUS_TERMINATED
          if !status.isApiMemberStatusTerminated =>
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
              memberIdOpt
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
//        val memberIdOpt = apiRegisterMemberList.registering_member.map(member =>
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
          state.meta.map(convertMetaInfoToApiMetaInfo),
          convertMemberStatus(state.status)
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
            status = memberStatusUpdated.newStatus,
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
          apiReleaseMember.deletingMember.map(apiId => MemberId(apiId.memberId))
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

  override def activateMember(
      currentState: MemberState,
      apiActivateMember: ApiActivateMember
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_ACTIVE
          ) => {

        log.info(
          s"MemberAPI in activateMember - apiActivateMember - ${apiActivateMember}"
        )

        val activatedMember = MemberActivated(
          Some(MemberId(apiActivateMember.memberId)),
          apiActivateMember.activatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(activatedMember).thenReply(_ => Empty.defaultInstance)
      }
      case other =>
        log.info(
          s"MemberAPI in activateMember - not activated - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)

    }
  }

  override def inactivateMember(
      currentState: MemberState,
      apiInactivateMember: ApiInactivateMember
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_INACTIVE
          ) => {

        log.info(
          s"MemberAPI in inactivateMember - apiInactivateMember - ${apiInactivateMember}"
        )

        val inactivatedMember = MemberInactivated(
          Some(MemberId(apiInactivateMember.memberId)),
          apiInactivateMember.inactivatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects
          .emitEvent(inactivatedMember)
          .thenReply(_ => Empty.defaultInstance)
      }
      case other =>
        log.info(
          s"MemberAPI in inactivateMember - not inactivated - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)

    }
  }

  override def suspendMember(
      currentState: MemberState,
      apiSuspendMember: ApiSuspendMember
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_SUSPENDED
          ) => {

        log.info(
          s"MemberAPI in suspendMember - apiSuspendMember - ${apiSuspendMember}"
        )

        val suspendedMember = MemberSuspended(
          Some(MemberId(apiSuspendMember.memberId)),
          apiSuspendMember.suspendingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(suspendedMember).thenReply(_ => Empty.defaultInstance)
      }
      case other =>
        log.info(
          s"MemberAPI in suspendMember - not suspended - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)

    }
  }

  override def terminateMember(
      currentState: MemberState,
      apiTerminateMember: ApiTerminateMember
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_TERMINATED
          ) => {

        log.info(
          s"MemberAPI in terminateMember - apiTerminateMember - ${apiTerminateMember}"
        )

        val terminatedMember = MemberTerminated(
          Some(MemberId(apiTerminateMember.memberId)),
          apiTerminateMember.terminatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects
          .emitEvent(terminatedMember)
          .thenReply(_ => Empty.defaultInstance)
      }
      case other =>
        log.info(
          s"MemberAPI in terminateMember - not terminated - other - ${other}"
        )

        effects.reply(Empty.defaultInstance)

    }
  }

  override def memberActivated(
      currentState: MemberState,
      memberActivated: MemberActivated
  ): MemberState = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_ACTIVE
          ) => {

        log.info(
          s"MemberAPI in memberActivated - memberActivated - $memberActivated"
        )

        currentState.withMember(
          member.copy(
            status = MemberStatus.MEMBER_STATUS_ACTIVE
          )
        )
      }
      case other => {
        log.info(
          s"MemberAPI in memberActivated - not activated - other - $other"
        )
        currentState
      }
    }
  }

  override def memberInactivated(
      currentState: MemberState,
      memberInactivated: MemberInactivated
  ): MemberState = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_INACTIVE
          ) => {

        log.info(
          s"MemberAPI in memberInactivated - memberInactivated - $memberInactivated"
        )

        currentState.withMember(
          member.copy(
            status = MemberStatus.MEMBER_STATUS_INACTIVE
          )
        )
      }
      case other => {
        log.info(
          s"MemberAPI in memberInactivated - not inactivated - other - $other"
        )
        currentState
      }
    }
  }

  override def memberSuspended(
      currentState: MemberState,
      memberSuspended: MemberSuspended
  ): MemberState = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_SUSPENDED
          ) => {

        log.info(
          s"MemberAPI in memberSuspended - memberSuspended - $memberSuspended"
        )

        currentState.withMember(
          member.copy(
            status = MemberStatus.MEMBER_STATUS_SUSPENDED
          )
        )
      }
      case other => {
        log.info(
          s"MemberAPI in memberSuspended - not suspended - other - $other"
        )
        currentState
      }
    }
  }

  override def memberTerminated(
      currentState: MemberState,
      memberTerminated: MemberTerminated
  ): MemberState = {
    currentState.member match {
      case Some(member)
          if isStateChangeValid(
            member,
            ApiMemberStatus.API_MEMBER_STATUS_TERMINATED
          ) => {

        log.info(
          s"MemberAPI in memberTerminated - memberTerminated - $memberTerminated"
        )

        currentState.withMember(
          member.copy(
            status = MemberStatus.MEMBER_STATUS_TERMINATED
          )
        )
      }
      case other => {
        log.info(
          s"MemberAPI in memberTerminated - not terminated - other - $other"
        )
        currentState
      }
    }
  }
}
