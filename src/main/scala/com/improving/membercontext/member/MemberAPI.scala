package com.improving.membercontext.member

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import com.improving.member.{
  ApiInfo,
  ApiMemberStatus,
  ApiNotificationPreference
}
import com.improving.{
  ApiContact,
  Contact,
  EmailAddress,
  MemberId,
  MobileNumber,
  OrganizationId,
  TenantId,
  member
}
import com.improving.membercontext.{
  Info,
  MemberInfoUpdated,
  MemberListRegistered,
  MemberRegistered,
  MemberStatus,
  MemberStatusUpdated,
  MetaInfo,
  NotificationPreference
}
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
      apiRegisterMember: member.ApiRegisterMember
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.member match {
      case Some(_) =>
        effects.reply(
          Empty.defaultInstance
        ) // already registered so just return.
      case _ => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val memberIdOpt = apiRegisterMember.registeringMember.map(member =>
          MemberId(member.memberId)
        ) // ??? not sure if this is correct ???
        val event = MemberRegistered(
          Some(MemberId(apiRegisterMember.memberId)),
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
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
    }
  }

  private def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.contact.map(convertApiContactToContact),
      apiInfo.handle,
      apiInfo.avatar,
      apiInfo.firstName,
      apiInfo.lastName,
      apiInfo.mobileNumber.map(mobile => MobileNumber(mobile.value)),
      apiInfo.emailAddress.map(email => EmailAddress(email.value)),
      convertNotificationPreference(apiInfo.notificationPreference),
      apiInfo.organizationMembership.map(org => OrganizationId(org.orgId)),
      apiInfo.tenant.map(tenant => TenantId(tenant.tenantId))
    )
  }

  private def convertApiContactToContact(apiContact: ApiContact): Contact = {
    Contact(
      apiContact.firstName,
      apiContact.lastName,
      apiContact.emailAddress.map(email => EmailAddress(email.value)),
      apiContact.phone.map(mobile => MobileNumber(mobile.value)),
      apiContact.userName
    )
  }

  private def convertNotificationPreference(
      apiNotificationPreference: ApiNotificationPreference
  ): NotificationPreference = {
    apiNotificationPreference match {
      case ApiNotificationPreference.EMAIL => NotificationPreference.EMAIL
      case ApiNotificationPreference.SMS   => NotificationPreference.SMS
      case ApiNotificationPreference.APPLICATION =>
        NotificationPreference.APPLICATION
      case ApiNotificationPreference.Unrecognized(unrecognizedValue) =>
        NotificationPreference.Unrecognized(unrecognizedValue)
    }
  }

  private def convertApiMemberStatusToMemberStatus(
      apiMemberStatus: ApiMemberStatus
  ): MemberStatus = {
    apiMemberStatus match {
      case ApiMemberStatus.ACTIVE     => MemberStatus.ACTIVE
      case ApiMemberStatus.INACTIVE   => MemberStatus.INACTIVE
      case ApiMemberStatus.SUSPENDED  => MemberStatus.SUSPENDED
      case ApiMemberStatus.TERMINATED => MemberStatus.TERMINATED
      case ApiMemberStatus.Unrecognized(unrecognizedValue) =>
        MemberStatus.Unrecognized(unrecognizedValue)
    }
  }
  override def memberStatusUpdated(
      currentState: MemberState,
      apiUpdateMemberStatus: member.ApiUpdateMemberStatus
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
              state.memberId, // ??? not sure if this is correct ???
              Some(timestamp),
              state.memberId, // ??? not sure if this is correct ???
              convertApiMemberStatusToMemberStatus(
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
      apiUpdateMemberInfo: member.ApiUpdateMemberInfo
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `UpdateMemberInfo` is not implemented, yet"
    )

  override def registerMemberList(
      currentState: MemberState,
      apiRegisterMemberList: member.ApiRegisterMemberList
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `RegisterMemberList` is not implemented, yet"
    )

  override def memberInfoUpdated(
      currentState: MemberState,
      memberInfoUpdated: MemberInfoUpdated
  ): MemberState =
    throw new RuntimeException(
      "The event handler for `MemberInfoUpdated` is not implemented, yet"
    )

  override def memberListRegistered(
      currentState: MemberState,
      memberListRegistered: MemberListRegistered
  ): MemberState =
    throw new RuntimeException(
      "The event handler for `MemberListRegistered` is not implemented, yet"
    )

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
