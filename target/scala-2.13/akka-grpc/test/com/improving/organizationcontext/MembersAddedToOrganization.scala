// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.improving.organizationcontext

@SerialVersionUID(0L)
final case class MembersAddedToOrganization(
    orgId: _root_.scala.Option[com.improving.OrganizationId] = _root_.scala.None,
    newMembers: _root_.scala.Seq[com.improving.MemberId] = _root_.scala.Seq.empty,
    meta: _root_.scala.Option[com.improving.organizationcontext.MetaInfo] = _root_.scala.None,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[MembersAddedToOrganization] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      if (orgId.isDefined) {
        val __value = orgId.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      newMembers.foreach { __item =>
        val __value = __item
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      }
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
      orgId.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      newMembers.foreach { __v =>
        val __m = __v
        _output__.writeTag(2, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      meta.foreach { __v =>
        val __m = __v
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def getOrgId: com.improving.OrganizationId = orgId.getOrElse(com.improving.OrganizationId.defaultInstance)
    def clearOrgId: MembersAddedToOrganization = copy(orgId = _root_.scala.None)
    def withOrgId(__v: com.improving.OrganizationId): MembersAddedToOrganization = copy(orgId = Option(__v))
    def clearNewMembers = copy(newMembers = _root_.scala.Seq.empty)
    def addNewMembers(__vs: com.improving.MemberId *): MembersAddedToOrganization = addAllNewMembers(__vs)
    def addAllNewMembers(__vs: Iterable[com.improving.MemberId]): MembersAddedToOrganization = copy(newMembers = newMembers ++ __vs)
    def withNewMembers(__v: _root_.scala.Seq[com.improving.MemberId]): MembersAddedToOrganization = copy(newMembers = __v)
    def getMeta: com.improving.organizationcontext.MetaInfo = meta.getOrElse(com.improving.organizationcontext.MetaInfo.defaultInstance)
    def clearMeta: MembersAddedToOrganization = copy(meta = _root_.scala.None)
    def withMeta(__v: com.improving.organizationcontext.MetaInfo): MembersAddedToOrganization = copy(meta = Option(__v))
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => orgId.orNull
        case 2 => newMembers
        case 3 => meta.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => orgId.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 2 => _root_.scalapb.descriptors.PRepeated(newMembers.iterator.map(_.toPMessage).toVector)
        case 3 => meta.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: com.improving.organizationcontext.MembersAddedToOrganization.type = com.improving.organizationcontext.MembersAddedToOrganization
    // @@protoc_insertion_point(GeneratedMessage[com.improving.organizationcontext.MembersAddedToOrganization])
}

object MembersAddedToOrganization extends scalapb.GeneratedMessageCompanion[com.improving.organizationcontext.MembersAddedToOrganization] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.improving.organizationcontext.MembersAddedToOrganization] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.improving.organizationcontext.MembersAddedToOrganization = {
    var __orgId: _root_.scala.Option[com.improving.OrganizationId] = _root_.scala.None
    val __newMembers: _root_.scala.collection.immutable.VectorBuilder[com.improving.MemberId] = new _root_.scala.collection.immutable.VectorBuilder[com.improving.MemberId]
    var __meta: _root_.scala.Option[com.improving.organizationcontext.MetaInfo] = _root_.scala.None
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __orgId = Option(__orgId.fold(_root_.scalapb.LiteParser.readMessage[com.improving.OrganizationId](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 18 =>
          __newMembers += _root_.scalapb.LiteParser.readMessage[com.improving.MemberId](_input__)
        case 26 =>
          __meta = Option(__meta.fold(_root_.scalapb.LiteParser.readMessage[com.improving.organizationcontext.MetaInfo](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    com.improving.organizationcontext.MembersAddedToOrganization(
        orgId = __orgId,
        newMembers = __newMembers.result(),
        meta = __meta,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.improving.organizationcontext.MembersAddedToOrganization] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      com.improving.organizationcontext.MembersAddedToOrganization(
        orgId = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).flatMap(_.as[_root_.scala.Option[com.improving.OrganizationId]]),
        newMembers = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Seq[com.improving.MemberId]]).getOrElse(_root_.scala.Seq.empty),
        meta = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[_root_.scala.Option[com.improving.organizationcontext.MetaInfo]])
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = OrganizationContextDomainProto.javaDescriptor.getMessageTypes().get(12)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = OrganizationContextDomainProto.scalaDescriptor.messages(12)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 1 => __out = com.improving.OrganizationId
      case 2 => __out = com.improving.MemberId
      case 3 => __out = com.improving.organizationcontext.MetaInfo
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.improving.organizationcontext.MembersAddedToOrganization(
    orgId = _root_.scala.None,
    newMembers = _root_.scala.Seq.empty,
    meta = _root_.scala.None
  )
  implicit class MembersAddedToOrganizationLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.improving.organizationcontext.MembersAddedToOrganization]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.improving.organizationcontext.MembersAddedToOrganization](_l) {
    def orgId: _root_.scalapb.lenses.Lens[UpperPB, com.improving.OrganizationId] = field(_.getOrgId)((c_, f_) => c_.copy(orgId = Option(f_)))
    def optionalOrgId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.OrganizationId]] = field(_.orgId)((c_, f_) => c_.copy(orgId = f_))
    def newMembers: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Seq[com.improving.MemberId]] = field(_.newMembers)((c_, f_) => c_.copy(newMembers = f_))
    def meta: _root_.scalapb.lenses.Lens[UpperPB, com.improving.organizationcontext.MetaInfo] = field(_.getMeta)((c_, f_) => c_.copy(meta = Option(f_)))
    def optionalMeta: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.organizationcontext.MetaInfo]] = field(_.meta)((c_, f_) => c_.copy(meta = f_))
  }
  final val ORGID_FIELD_NUMBER = 1
  final val NEWMEMBERS_FIELD_NUMBER = 2
  final val META_FIELD_NUMBER = 3
  def of(
    orgId: _root_.scala.Option[com.improving.OrganizationId],
    newMembers: _root_.scala.Seq[com.improving.MemberId],
    meta: _root_.scala.Option[com.improving.organizationcontext.MetaInfo]
  ): _root_.com.improving.organizationcontext.MembersAddedToOrganization = _root_.com.improving.organizationcontext.MembersAddedToOrganization(
    orgId,
    newMembers,
    meta
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[com.improving.organizationcontext.MembersAddedToOrganization])
}
