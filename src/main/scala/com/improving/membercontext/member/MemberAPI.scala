package com.improving.membercontext.member

import com.google.protobuf.empty.Empty
import com.improving.member
import com.improving.membercontext.MemberInfoUpdated
import com.improving.membercontext.MemberListRegistered
import com.improving.membercontext.MemberRegistered
import com.improving.membercontext.MemberStatusUpdated
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
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `RegisterMember` is not implemented, yet"
    )

  override def memberStatusUpdated(
      currentState: MemberState,
      apiUpdateMemberStatus: member.ApiUpdateMemberStatus
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `MemberStatusUpdated` is not implemented, yet"
    )

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
  ): MemberState =
    throw new RuntimeException(
      "The event handler for `MemberRegistered` is not implemented, yet"
    )

  override def memberStatusUpdated(
      currentState: MemberState,
      memberStatusUpdated: MemberStatusUpdated
  ): MemberState =
    throw new RuntimeException(
      "The event handler for `MemberStatusUpdated` is not implemented, yet"
    )

}
