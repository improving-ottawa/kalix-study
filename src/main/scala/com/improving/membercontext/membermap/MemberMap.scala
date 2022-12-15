package com.improving.membercontext.membermap

import com.google.protobuf.empty.Empty
import com.improving.membercontext.membermap
import kalix.scalasdk.replicatedentity.ReplicatedEntity
import kalix.scalasdk.replicatedentity.ReplicatedEntityContext
import kalix.scalasdk.replicatedentity.ReplicatedRegisterMap

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberMap(context: ReplicatedEntityContext) extends AbstractMemberMap {


  def set(currentData: ReplicatedRegisterMap[MemberId, Info], setValue: SetValue): ReplicatedEntity.Effect[Empty] =
    effects.error("The command handler for `Set` is not implemented, yet")

  def remove(currentData: ReplicatedRegisterMap[MemberId, Info], removeValue: RemoveValue): ReplicatedEntity.Effect[Empty] =
    effects.error("The command handler for `Remove` is not implemented, yet")

  def get(currentData: ReplicatedRegisterMap[MemberId, Info], getValue: GetValue): ReplicatedEntity.Effect[CurrentValue] =
    effects.error("The command handler for `Get` is not implemented, yet")

  def getAll(currentData: ReplicatedRegisterMap[MemberId, Info], getAllValues: GetAllValues): ReplicatedEntity.Effect[CurrentValues] =
    effects.error("The command handler for `GetAll` is not implemented, yet")

}
