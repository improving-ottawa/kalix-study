package app.improving.membercontext.membermap

import com.google.protobuf.empty.Empty
import app.improving.membercontext.membermap
import kalix.scalasdk.replicatedentity.ReplicatedEntity
import kalix.scalasdk.replicatedentity.ReplicatedEntityContext
import kalix.scalasdk.replicatedentity.ReplicatedRegisterMap
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberMap(context: ReplicatedEntityContext) extends AbstractMemberMap {

  private val log = LoggerFactory.getLogger(this.getClass)

  def set(
      currentData: ReplicatedRegisterMap[MapMemberId, MapMemberInfo],
      setValue: SetValue
  ): ReplicatedEntity.Effect[Empty] = {

    log.info(
      s"MemberMap in set - setValue ${setValue}"
    )

    val key = MapMemberId(setValue.getKey.id)
    val value = MapMemberInfo(
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
      currentData: ReplicatedRegisterMap[MapMemberId, MapMemberInfo],
      removeValue: RemoveValue
  ): ReplicatedEntity.Effect[Empty] = {

    log.info(
      s"MemberMap in remove - removeValue ${removeValue}"
    )

    val key = MapMemberId(removeValue.getKey.id)
    effects
      .update(currentData.remove(key))
      .thenReply(Empty.defaultInstance)
  }

  def get(
      currentData: ReplicatedRegisterMap[MapMemberId, MapMemberInfo],
      getValue: GetValue
  ): ReplicatedEntity.Effect[CurrentValue] = {

    log.info(
      s"MemberMap in get - getValue ${getValue}"
    )

    val key = MapMemberId(getValue.getKey.id)
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
      currentData: ReplicatedRegisterMap[MapMemberId, MapMemberInfo],
      getAllValues: GetAllValues
  ): ReplicatedEntity.Effect[CurrentValues] = {

    log.info(
      s"MemberMap in getAll - getAllValues ${getAllValues}"
    )

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
