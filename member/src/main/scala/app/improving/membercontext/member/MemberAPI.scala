package app.improving.membercontext.member

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{ApiMemberId, MemberId}
import app.improving.membercontext.{
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

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberAPI(context: EventSourcedEntityContext) extends AbstractMemberAPI {
  override def emptyState: MemberState = MemberState.defaultInstance

  override def registerMember(
      currentState: MemberState,
      apiRegisterMember: ApiRegisterMember
  ): EventSourcedEntity.Effect[ApiMemberId] = {
    currentState.member match {
      case Some(_) =>
        effects.reply(
          ApiMemberId.defaultInstance
        ) // already registered so just return.
      case _ => {
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
              MemberStatus.ACTIVE
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
          if state.memberId == Some(
            MemberId(apiUpdateMemberStatus.memberId)
          ) => {
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
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def updateMemberInfo(
      currentState: MemberState,
      apiUpdateMemberInfo: ApiUpdateMemberInfo
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(state)
          if state.memberId == Some(
            MemberId(apiUpdateMemberInfo.memberId)
          ) => {
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
      case _ => effects.reply(Empty.defaultInstance)
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
//          apiRegisterMemberList.memberList
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
  ): EventSourcedEntity.Effect[ApiMemberData] = {
    currentState.member match {
      case Some(state)
          if state.memberId == Some(MemberId(apiGetMemberData.memberId)) => {
        val apiMemberData = ApiMemberData(
          apiGetMemberData.memberId,
          state.info.map(convertInfoToApiUpdateInfo),
          state.meta.map(convertMetaInfoToApiMetaInfo)
        )
        effects.reply(apiMemberData)
      }
      case _ =>
        effects.error(
          s"MemberData ID ${apiGetMemberData.memberId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
    }
  }

  override def memberInfoUpdated(
      currentState: MemberState,
      memberInfoUpdated: MemberInfoUpdated
  ): MemberState = {
    currentState.member match {
      case Some(state) if state.memberId == memberInfoUpdated.memberId => {
        currentState.withMember(
          state.copy(
            info = memberInfoUpdated.info,
            meta = memberInfoUpdated.meta
          )
        )
      }
      case _ => currentState
    }
  }

//  override def memberListRegistered(
//      currentState: MemberState,
//      memberListRegistered: MemberListRegistered
//  ): MemberState = {
//    currentState.member match {
//      case Some(state) => {
//        val map = memberListRegistered.memberList.getOrElse(MemberMap()).map
//
//        map.foreach {
//          case (id, info) => {
//            val value = Value(
//              info.contact,
//              info.handle,
//              info.avatar,
//              info.firstName,
//              info.lastName,
//              info.mobileNumber,
//              info.emailAddress,
//              info.notificationPreference,
//              info.organizationMembership,
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
      case Some(_) => currentState // already registered so just return.
      case _ => {
        val member = Member(
          memberRegistered.memberId,
          memberRegistered.info,
          memberRegistered.meta,
          MemberStatus.ACTIVE
        )
        currentState.withMember(member)
      }
    }
  }
  override def memberStatusUpdated(
      currentState: MemberState,
      memberStatusUpdated: MemberStatusUpdated
  ): MemberState = {
    currentState.member match {
      case Some(state) if state.memberId == memberStatusUpdated.memberId => {
        currentState.withMember(
          state.copy(
            status = memberStatusUpdated.meta
              .map(_.memberStatus)
              .getOrElse(MemberStatus.UNKNOWN),
            meta = memberStatusUpdated.meta
          )
        )
      }
      case _ => currentState
    }
  }
}
