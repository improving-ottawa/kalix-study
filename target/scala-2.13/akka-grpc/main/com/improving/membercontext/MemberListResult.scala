// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.improving.membercontext

@SerialVersionUID(0L)
final case class MemberListResult(
    members: _root_.scala.Option[com.improving.membercontext.MemberMap] = _root_.scala.None,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[MemberListResult] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      if (members.isDefined) {
        val __value = members.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      __size += unknownFields.serializedSize
      __size
    }
    override def serializedSize: _root_.scala.Int = {
      var __size = __serializedSizeMemoized
      if (__size == 0) {
        __size = __computeSerializedSize() + 1
        __serializedSizeMemoized = __size
      }
      __size - 1
      
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): _root_.scala.Unit = {
      members.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def getMembers: com.improving.membercontext.MemberMap = members.getOrElse(com.improving.membercontext.MemberMap.defaultInstance)
    def clearMembers: MemberListResult = copy(members = _root_.scala.None)
    def withMembers(__v: com.improving.membercontext.MemberMap): MemberListResult = copy(members = Option(__v))
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => members.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => members.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: com.improving.membercontext.MemberListResult.type = com.improving.membercontext.MemberListResult
    // @@protoc_insertion_point(GeneratedMessage[com.improving.membercontext.MemberListResult])
}

object MemberListResult extends scalapb.GeneratedMessageCompanion[com.improving.membercontext.MemberListResult] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.improving.membercontext.MemberListResult] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.improving.membercontext.MemberListResult = {
    var __members: _root_.scala.Option[com.improving.membercontext.MemberMap] = _root_.scala.None
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __members = Option(__members.fold(_root_.scalapb.LiteParser.readMessage[com.improving.membercontext.MemberMap](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    com.improving.membercontext.MemberListResult(
        members = __members,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.improving.membercontext.MemberListResult] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      com.improving.membercontext.MemberListResult(
        members = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).flatMap(_.as[_root_.scala.Option[com.improving.membercontext.MemberMap]])
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = MemberContextDomainProto.javaDescriptor.getMessageTypes().get(6)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = MemberContextDomainProto.scalaDescriptor.messages(6)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 1 => __out = com.improving.membercontext.MemberMap
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.improving.membercontext.MemberListResult(
    members = _root_.scala.None
  )
  implicit class MemberListResultLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.improving.membercontext.MemberListResult]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.improving.membercontext.MemberListResult](_l) {
    def members: _root_.scalapb.lenses.Lens[UpperPB, com.improving.membercontext.MemberMap] = field(_.getMembers)((c_, f_) => c_.copy(members = Option(f_)))
    def optionalMembers: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.membercontext.MemberMap]] = field(_.members)((c_, f_) => c_.copy(members = f_))
  }
  final val MEMBERS_FIELD_NUMBER = 1
  def of(
    members: _root_.scala.Option[com.improving.membercontext.MemberMap]
  ): _root_.com.improving.membercontext.MemberListResult = _root_.com.improving.membercontext.MemberListResult(
    members
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[com.improving.membercontext.MemberListResult])
}
