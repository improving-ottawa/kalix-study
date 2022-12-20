package app.improving.membercontext.membermap

import com.google.protobuf.empty.Empty
import app.improving.membercontext.membermap
import kalix.scalasdk.replicatedentity.ReplicatedEntity
import kalix.scalasdk.replicatedentity.ReplicatedEntityContext
import kalix.scalasdk.replicatedentity.ReplicatedRegisterMap

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberMap(context: ReplicatedEntityContext) extends AbstractMemberMap {

  def set(
      currentData: ReplicatedRegisterMap[MemberId, Info],
      setValue: SetValue
  ): ReplicatedEntity.Effect[Empty] = {
    val key = MemberId(setValue.getKey.id)
    val value = Info(
      setValue.getValue.contact,
      setValue.getValue.handle,
      setValue.getValue.avatar,
      setValue.getValue.firstName,
      setValue.getValue.lastName,
      setValue.getValue.mobileNumber,
      setValue.getValue.emailAddress,
      setValue.getValue.notificationPreference,
      setValue.getValue.organizationMembership,
      setValue.getValue.tenant
    )
    effects
      .update(currentData.setValue(key, value))
      .thenReply(Empty.defaultInstance)
  }

  def remove(
      currentData: ReplicatedRegisterMap[MemberId, Info],
      removeValue: RemoveValue
  ): ReplicatedEntity.Effect[Empty] = {
    val key = MemberId(removeValue.getKey.id)
    effects
      .update(currentData.remove(key))
      .thenReply(Empty.defaultInstance)
  }

  def get(
      currentData: ReplicatedRegisterMap[MemberId, Info],
      getValue: GetValue
  ): ReplicatedEntity.Effect[CurrentValue] = {
    val key = MemberId(getValue.getKey.id)
    val maybeValue = currentData.get(key)
    val currentValue = membermap.CurrentValue(
      getValue.key,
      maybeValue.map(v =>
        membermap.Value(
          v.contact,
          v.handle,
          v.avatar,
          v.firstName,
          v.lastName,
          v.mobileNumber,
          v.emailAddress,
          v.notificationPreference,
          v.organizationMembership,
          v.tenant
        )
      )
    )
    effects.reply(currentValue)
  }

  def getAll(
      currentData: ReplicatedRegisterMap[MemberId, Info],
      getAllValues: GetAllValues
  ): ReplicatedEntity.Effect[CurrentValues] = {
    val allData =
      currentData.keySet.map { key =>
        val value = currentData
          .get(key)
          .map(v =>
            membermap.Value(
              v.contact,
              v.handle,
              v.avatar,
              v.firstName,
              v.lastName,
              v.mobileNumber,
              v.emailAddress,
              v.notificationPreference,
              v.organizationMembership,
              v.tenant
            )
          )
        membermap.CurrentValue(Some(membermap.Key(key.id)), value)
      }.toSeq

    effects.reply(membermap.CurrentValues(allData))
  }

}
