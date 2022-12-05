// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.improving.membercontext

@SerialVersionUID(0L)
final case class MetaInfo(
    createdOn: _root_.scala.Option[com.google.protobuf.timestamp.Timestamp] = _root_.scala.None,
    reatedBy: _root_.scala.Option[com.improving.MemberId] = _root_.scala.None,
    lastModifiedOn: _root_.scala.Option[com.google.protobuf.timestamp.Timestamp] = _root_.scala.None,
    lastModifiedBy: _root_.scala.Option[com.improving.MemberId] = _root_.scala.None,
    memberStatus: com.improving.membercontext.Status = com.improving.membercontext.Status.ACTIVE,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[MetaInfo] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      if (createdOn.isDefined) {
        val __value = createdOn.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (reatedBy.isDefined) {
        val __value = reatedBy.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (lastModifiedOn.isDefined) {
        val __value = lastModifiedOn.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (lastModifiedBy.isDefined) {
        val __value = lastModifiedBy.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      
      {
        val __value = memberStatus.value
        if (__value != 0) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeEnumSize(5, __value)
        }
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
      createdOn.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      reatedBy.foreach { __v =>
        val __m = __v
        _output__.writeTag(2, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      lastModifiedOn.foreach { __v =>
        val __m = __v
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      lastModifiedBy.foreach { __v =>
        val __m = __v
        _output__.writeTag(4, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      {
        val __v = memberStatus.value
        if (__v != 0) {
          _output__.writeEnum(5, __v)
        }
      };
      unknownFields.writeTo(_output__)
    }
    def getCreatedOn: com.google.protobuf.timestamp.Timestamp = createdOn.getOrElse(com.google.protobuf.timestamp.Timestamp.defaultInstance)
    def clearCreatedOn: MetaInfo = copy(createdOn = _root_.scala.None)
    def withCreatedOn(__v: com.google.protobuf.timestamp.Timestamp): MetaInfo = copy(createdOn = Option(__v))
    def getReatedBy: com.improving.MemberId = reatedBy.getOrElse(com.improving.MemberId.defaultInstance)
    def clearReatedBy: MetaInfo = copy(reatedBy = _root_.scala.None)
    def withReatedBy(__v: com.improving.MemberId): MetaInfo = copy(reatedBy = Option(__v))
    def getLastModifiedOn: com.google.protobuf.timestamp.Timestamp = lastModifiedOn.getOrElse(com.google.protobuf.timestamp.Timestamp.defaultInstance)
    def clearLastModifiedOn: MetaInfo = copy(lastModifiedOn = _root_.scala.None)
    def withLastModifiedOn(__v: com.google.protobuf.timestamp.Timestamp): MetaInfo = copy(lastModifiedOn = Option(__v))
    def getLastModifiedBy: com.improving.MemberId = lastModifiedBy.getOrElse(com.improving.MemberId.defaultInstance)
    def clearLastModifiedBy: MetaInfo = copy(lastModifiedBy = _root_.scala.None)
    def withLastModifiedBy(__v: com.improving.MemberId): MetaInfo = copy(lastModifiedBy = Option(__v))
    def withMemberStatus(__v: com.improving.membercontext.Status): MetaInfo = copy(memberStatus = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => createdOn.orNull
        case 2 => reatedBy.orNull
        case 3 => lastModifiedOn.orNull
        case 4 => lastModifiedBy.orNull
        case 5 => {
          val __t = memberStatus.javaValueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => createdOn.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 2 => reatedBy.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 3 => lastModifiedOn.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 4 => lastModifiedBy.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 5 => _root_.scalapb.descriptors.PEnum(memberStatus.scalaValueDescriptor)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: com.improving.membercontext.MetaInfo.type = com.improving.membercontext.MetaInfo
    // @@protoc_insertion_point(GeneratedMessage[com.improving.membercontext.MetaInfo])
}

object MetaInfo extends scalapb.GeneratedMessageCompanion[com.improving.membercontext.MetaInfo] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.improving.membercontext.MetaInfo] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.improving.membercontext.MetaInfo = {
    var __createdOn: _root_.scala.Option[com.google.protobuf.timestamp.Timestamp] = _root_.scala.None
    var __reatedBy: _root_.scala.Option[com.improving.MemberId] = _root_.scala.None
    var __lastModifiedOn: _root_.scala.Option[com.google.protobuf.timestamp.Timestamp] = _root_.scala.None
    var __lastModifiedBy: _root_.scala.Option[com.improving.MemberId] = _root_.scala.None
    var __memberStatus: com.improving.membercontext.Status = com.improving.membercontext.Status.ACTIVE
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __createdOn = Option(__createdOn.fold(_root_.scalapb.LiteParser.readMessage[com.google.protobuf.timestamp.Timestamp](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 18 =>
          __reatedBy = Option(__reatedBy.fold(_root_.scalapb.LiteParser.readMessage[com.improving.MemberId](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 26 =>
          __lastModifiedOn = Option(__lastModifiedOn.fold(_root_.scalapb.LiteParser.readMessage[com.google.protobuf.timestamp.Timestamp](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 34 =>
          __lastModifiedBy = Option(__lastModifiedBy.fold(_root_.scalapb.LiteParser.readMessage[com.improving.MemberId](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 40 =>
          __memberStatus = com.improving.membercontext.Status.fromValue(_input__.readEnum())
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    com.improving.membercontext.MetaInfo(
        createdOn = __createdOn,
        reatedBy = __reatedBy,
        lastModifiedOn = __lastModifiedOn,
        lastModifiedBy = __lastModifiedBy,
        memberStatus = __memberStatus,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.improving.membercontext.MetaInfo] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      com.improving.membercontext.MetaInfo(
        createdOn = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).flatMap(_.as[_root_.scala.Option[com.google.protobuf.timestamp.Timestamp]]),
        reatedBy = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).flatMap(_.as[_root_.scala.Option[com.improving.MemberId]]),
        lastModifiedOn = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[_root_.scala.Option[com.google.protobuf.timestamp.Timestamp]]),
        lastModifiedBy = __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).flatMap(_.as[_root_.scala.Option[com.improving.MemberId]]),
        memberStatus = com.improving.membercontext.Status.fromValue(__fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).map(_.as[_root_.scalapb.descriptors.EnumValueDescriptor]).getOrElse(com.improving.membercontext.Status.ACTIVE.scalaValueDescriptor).number)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = MemberContextDomainProto.javaDescriptor.getMessageTypes().get(9)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = MemberContextDomainProto.scalaDescriptor.messages(9)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 1 => __out = com.google.protobuf.timestamp.Timestamp
      case 2 => __out = com.improving.MemberId
      case 3 => __out = com.google.protobuf.timestamp.Timestamp
      case 4 => __out = com.improving.MemberId
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = {
    (__fieldNumber: @_root_.scala.unchecked) match {
      case 5 => com.improving.membercontext.Status
    }
  }
  lazy val defaultInstance = com.improving.membercontext.MetaInfo(
    createdOn = _root_.scala.None,
    reatedBy = _root_.scala.None,
    lastModifiedOn = _root_.scala.None,
    lastModifiedBy = _root_.scala.None,
    memberStatus = com.improving.membercontext.Status.ACTIVE
  )
  implicit class MetaInfoLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.improving.membercontext.MetaInfo]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.improving.membercontext.MetaInfo](_l) {
    def createdOn: _root_.scalapb.lenses.Lens[UpperPB, com.google.protobuf.timestamp.Timestamp] = field(_.getCreatedOn)((c_, f_) => c_.copy(createdOn = Option(f_)))
    def optionalCreatedOn: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.google.protobuf.timestamp.Timestamp]] = field(_.createdOn)((c_, f_) => c_.copy(createdOn = f_))
    def reatedBy: _root_.scalapb.lenses.Lens[UpperPB, com.improving.MemberId] = field(_.getReatedBy)((c_, f_) => c_.copy(reatedBy = Option(f_)))
    def optionalReatedBy: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.MemberId]] = field(_.reatedBy)((c_, f_) => c_.copy(reatedBy = f_))
    def lastModifiedOn: _root_.scalapb.lenses.Lens[UpperPB, com.google.protobuf.timestamp.Timestamp] = field(_.getLastModifiedOn)((c_, f_) => c_.copy(lastModifiedOn = Option(f_)))
    def optionalLastModifiedOn: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.google.protobuf.timestamp.Timestamp]] = field(_.lastModifiedOn)((c_, f_) => c_.copy(lastModifiedOn = f_))
    def lastModifiedBy: _root_.scalapb.lenses.Lens[UpperPB, com.improving.MemberId] = field(_.getLastModifiedBy)((c_, f_) => c_.copy(lastModifiedBy = Option(f_)))
    def optionalLastModifiedBy: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.MemberId]] = field(_.lastModifiedBy)((c_, f_) => c_.copy(lastModifiedBy = f_))
    def memberStatus: _root_.scalapb.lenses.Lens[UpperPB, com.improving.membercontext.Status] = field(_.memberStatus)((c_, f_) => c_.copy(memberStatus = f_))
  }
  final val CREATEDON_FIELD_NUMBER = 1
  final val REATEDBY_FIELD_NUMBER = 2
  final val LASTMODIFIEDON_FIELD_NUMBER = 3
  final val LASTMODIFIEDBY_FIELD_NUMBER = 4
  final val MEMBERSTATUS_FIELD_NUMBER = 5
  def of(
    createdOn: _root_.scala.Option[com.google.protobuf.timestamp.Timestamp],
    reatedBy: _root_.scala.Option[com.improving.MemberId],
    lastModifiedOn: _root_.scala.Option[com.google.protobuf.timestamp.Timestamp],
    lastModifiedBy: _root_.scala.Option[com.improving.MemberId],
    memberStatus: com.improving.membercontext.Status
  ): _root_.com.improving.membercontext.MetaInfo = _root_.com.improving.membercontext.MetaInfo(
    createdOn,
    reatedBy,
    lastModifiedOn,
    lastModifiedBy,
    memberStatus
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[com.improving.membercontext.MetaInfo])
}
