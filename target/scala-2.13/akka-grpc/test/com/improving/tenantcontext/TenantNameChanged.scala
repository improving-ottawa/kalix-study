// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.improving.tenantcontext

@SerialVersionUID(0L)
final case class TenantNameChanged(
    tenantId: _root_.scala.Option[com.improving.TenantId] = _root_.scala.None,
    oldName: _root_.scala.Predef.String = "",
    newName: _root_.scala.Predef.String = "",
    meta: _root_.scala.Option[com.improving.tenantcontext.MetaInfo] = _root_.scala.None,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[TenantNameChanged] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      if (tenantId.isDefined) {
        val __value = tenantId.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      
      {
        val __value = oldName
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, __value)
        }
      };
      
      {
        val __value = newName
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(3, __value)
        }
      };
      if (meta.isDefined) {
        val __value = meta.get
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
      tenantId.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      {
        val __v = oldName
        if (!__v.isEmpty) {
          _output__.writeString(2, __v)
        }
      };
      {
        val __v = newName
        if (!__v.isEmpty) {
          _output__.writeString(3, __v)
        }
      };
      meta.foreach { __v =>
        val __m = __v
        _output__.writeTag(4, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def getTenantId: com.improving.TenantId = tenantId.getOrElse(com.improving.TenantId.defaultInstance)
    def clearTenantId: TenantNameChanged = copy(tenantId = _root_.scala.None)
    def withTenantId(__v: com.improving.TenantId): TenantNameChanged = copy(tenantId = Option(__v))
    def withOldName(__v: _root_.scala.Predef.String): TenantNameChanged = copy(oldName = __v)
    def withNewName(__v: _root_.scala.Predef.String): TenantNameChanged = copy(newName = __v)
    def getMeta: com.improving.tenantcontext.MetaInfo = meta.getOrElse(com.improving.tenantcontext.MetaInfo.defaultInstance)
    def clearMeta: TenantNameChanged = copy(meta = _root_.scala.None)
    def withMeta(__v: com.improving.tenantcontext.MetaInfo): TenantNameChanged = copy(meta = Option(__v))
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => tenantId.orNull
        case 2 => {
          val __t = oldName
          if (__t != "") __t else null
        }
        case 3 => {
          val __t = newName
          if (__t != "") __t else null
        }
        case 4 => meta.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => tenantId.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 2 => _root_.scalapb.descriptors.PString(oldName)
        case 3 => _root_.scalapb.descriptors.PString(newName)
        case 4 => meta.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: com.improving.tenantcontext.TenantNameChanged.type = com.improving.tenantcontext.TenantNameChanged
    // @@protoc_insertion_point(GeneratedMessage[com.improving.tenantcontext.TenantNameChanged])
}

object TenantNameChanged extends scalapb.GeneratedMessageCompanion[com.improving.tenantcontext.TenantNameChanged] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.improving.tenantcontext.TenantNameChanged] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.improving.tenantcontext.TenantNameChanged = {
    var __tenantId: _root_.scala.Option[com.improving.TenantId] = _root_.scala.None
    var __oldName: _root_.scala.Predef.String = ""
    var __newName: _root_.scala.Predef.String = ""
    var __meta: _root_.scala.Option[com.improving.tenantcontext.MetaInfo] = _root_.scala.None
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __tenantId = Option(__tenantId.fold(_root_.scalapb.LiteParser.readMessage[com.improving.TenantId](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 18 =>
          __oldName = _input__.readStringRequireUtf8()
        case 26 =>
          __newName = _input__.readStringRequireUtf8()
        case 34 =>
          __meta = Option(__meta.fold(_root_.scalapb.LiteParser.readMessage[com.improving.tenantcontext.MetaInfo](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    com.improving.tenantcontext.TenantNameChanged(
        tenantId = __tenantId,
        oldName = __oldName,
        newName = __newName,
        meta = __meta,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.improving.tenantcontext.TenantNameChanged] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      com.improving.tenantcontext.TenantNameChanged(
        tenantId = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).flatMap(_.as[_root_.scala.Option[com.improving.TenantId]]),
        oldName = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        newName = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        meta = __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).flatMap(_.as[_root_.scala.Option[com.improving.tenantcontext.MetaInfo]])
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = TenantContextDomainProto.javaDescriptor.getMessageTypes().get(9)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = TenantContextDomainProto.scalaDescriptor.messages(9)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 1 => __out = com.improving.TenantId
      case 4 => __out = com.improving.tenantcontext.MetaInfo
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.improving.tenantcontext.TenantNameChanged(
    tenantId = _root_.scala.None,
    oldName = "",
    newName = "",
    meta = _root_.scala.None
  )
  implicit class TenantNameChangedLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.improving.tenantcontext.TenantNameChanged]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.improving.tenantcontext.TenantNameChanged](_l) {
    def tenantId: _root_.scalapb.lenses.Lens[UpperPB, com.improving.TenantId] = field(_.getTenantId)((c_, f_) => c_.copy(tenantId = Option(f_)))
    def optionalTenantId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.TenantId]] = field(_.tenantId)((c_, f_) => c_.copy(tenantId = f_))
    def oldName: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.oldName)((c_, f_) => c_.copy(oldName = f_))
    def newName: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.newName)((c_, f_) => c_.copy(newName = f_))
    def meta: _root_.scalapb.lenses.Lens[UpperPB, com.improving.tenantcontext.MetaInfo] = field(_.getMeta)((c_, f_) => c_.copy(meta = Option(f_)))
    def optionalMeta: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.tenantcontext.MetaInfo]] = field(_.meta)((c_, f_) => c_.copy(meta = f_))
  }
  final val TENANTID_FIELD_NUMBER = 1
  final val OLDNAME_FIELD_NUMBER = 2
  final val NEWNAME_FIELD_NUMBER = 3
  final val META_FIELD_NUMBER = 4
  def of(
    tenantId: _root_.scala.Option[com.improving.TenantId],
    oldName: _root_.scala.Predef.String,
    newName: _root_.scala.Predef.String,
    meta: _root_.scala.Option[com.improving.tenantcontext.MetaInfo]
  ): _root_.com.improving.tenantcontext.TenantNameChanged = _root_.com.improving.tenantcontext.TenantNameChanged(
    tenantId,
    oldName,
    newName,
    meta
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[com.improving.tenantcontext.TenantNameChanged])
}
