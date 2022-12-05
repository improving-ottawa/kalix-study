// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.improving.eventcontext

@SerialVersionUID(0L)
final case class EventDelayed(
    id: _root_.scala.Option[com.improving.EventId] = _root_.scala.None,
    reason: _root_.scala.Predef.String = "",
    meta: _root_.scala.Option[com.improving.eventcontext.EventMetaInfo] = _root_.scala.None,
    expectedDuration: _root_.scala.Option[com.google.protobuf.duration.Duration] = _root_.scala.None,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[EventDelayed] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      if (id.isDefined) {
        val __value = id.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      
      {
        val __value = reason
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, __value)
        }
      };
      if (meta.isDefined) {
        val __value = meta.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (expectedDuration.isDefined) {
        val __value = expectedDuration.get
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
      id.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      {
        val __v = reason
        if (!__v.isEmpty) {
          _output__.writeString(2, __v)
        }
      };
      meta.foreach { __v =>
        val __m = __v
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      expectedDuration.foreach { __v =>
        val __m = __v
        _output__.writeTag(4, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def getId: com.improving.EventId = id.getOrElse(com.improving.EventId.defaultInstance)
    def clearId: EventDelayed = copy(id = _root_.scala.None)
    def withId(__v: com.improving.EventId): EventDelayed = copy(id = Option(__v))
    def withReason(__v: _root_.scala.Predef.String): EventDelayed = copy(reason = __v)
    def getMeta: com.improving.eventcontext.EventMetaInfo = meta.getOrElse(com.improving.eventcontext.EventMetaInfo.defaultInstance)
    def clearMeta: EventDelayed = copy(meta = _root_.scala.None)
    def withMeta(__v: com.improving.eventcontext.EventMetaInfo): EventDelayed = copy(meta = Option(__v))
    def getExpectedDuration: com.google.protobuf.duration.Duration = expectedDuration.getOrElse(com.google.protobuf.duration.Duration.defaultInstance)
    def clearExpectedDuration: EventDelayed = copy(expectedDuration = _root_.scala.None)
    def withExpectedDuration(__v: com.google.protobuf.duration.Duration): EventDelayed = copy(expectedDuration = Option(__v))
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => id.orNull
        case 2 => {
          val __t = reason
          if (__t != "") __t else null
        }
        case 3 => meta.orNull
        case 4 => expectedDuration.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => id.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 2 => _root_.scalapb.descriptors.PString(reason)
        case 3 => meta.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 4 => expectedDuration.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: com.improving.eventcontext.EventDelayed.type = com.improving.eventcontext.EventDelayed
    // @@protoc_insertion_point(GeneratedMessage[com.improving.eventcontext.EventDelayed])
}

object EventDelayed extends scalapb.GeneratedMessageCompanion[com.improving.eventcontext.EventDelayed] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.improving.eventcontext.EventDelayed] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.improving.eventcontext.EventDelayed = {
    var __id: _root_.scala.Option[com.improving.EventId] = _root_.scala.None
    var __reason: _root_.scala.Predef.String = ""
    var __meta: _root_.scala.Option[com.improving.eventcontext.EventMetaInfo] = _root_.scala.None
    var __expectedDuration: _root_.scala.Option[com.google.protobuf.duration.Duration] = _root_.scala.None
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __id = Option(__id.fold(_root_.scalapb.LiteParser.readMessage[com.improving.EventId](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 18 =>
          __reason = _input__.readStringRequireUtf8()
        case 26 =>
          __meta = Option(__meta.fold(_root_.scalapb.LiteParser.readMessage[com.improving.eventcontext.EventMetaInfo](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 34 =>
          __expectedDuration = Option(__expectedDuration.fold(_root_.scalapb.LiteParser.readMessage[com.google.protobuf.duration.Duration](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    com.improving.eventcontext.EventDelayed(
        id = __id,
        reason = __reason,
        meta = __meta,
        expectedDuration = __expectedDuration,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.improving.eventcontext.EventDelayed] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      com.improving.eventcontext.EventDelayed(
        id = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).flatMap(_.as[_root_.scala.Option[com.improving.EventId]]),
        reason = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        meta = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[_root_.scala.Option[com.improving.eventcontext.EventMetaInfo]]),
        expectedDuration = __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).flatMap(_.as[_root_.scala.Option[com.google.protobuf.duration.Duration]])
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = EventContextDomainProto.javaDescriptor.getMessageTypes().get(3)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = EventContextDomainProto.scalaDescriptor.messages(3)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 1 => __out = com.improving.EventId
      case 3 => __out = com.improving.eventcontext.EventMetaInfo
      case 4 => __out = com.google.protobuf.duration.Duration
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.improving.eventcontext.EventDelayed(
    id = _root_.scala.None,
    reason = "",
    meta = _root_.scala.None,
    expectedDuration = _root_.scala.None
  )
  implicit class EventDelayedLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.improving.eventcontext.EventDelayed]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.improving.eventcontext.EventDelayed](_l) {
    def id: _root_.scalapb.lenses.Lens[UpperPB, com.improving.EventId] = field(_.getId)((c_, f_) => c_.copy(id = Option(f_)))
    def optionalId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.EventId]] = field(_.id)((c_, f_) => c_.copy(id = f_))
    def reason: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.reason)((c_, f_) => c_.copy(reason = f_))
    def meta: _root_.scalapb.lenses.Lens[UpperPB, com.improving.eventcontext.EventMetaInfo] = field(_.getMeta)((c_, f_) => c_.copy(meta = Option(f_)))
    def optionalMeta: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.eventcontext.EventMetaInfo]] = field(_.meta)((c_, f_) => c_.copy(meta = f_))
    def expectedDuration: _root_.scalapb.lenses.Lens[UpperPB, com.google.protobuf.duration.Duration] = field(_.getExpectedDuration)((c_, f_) => c_.copy(expectedDuration = Option(f_)))
    def optionalExpectedDuration: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.google.protobuf.duration.Duration]] = field(_.expectedDuration)((c_, f_) => c_.copy(expectedDuration = f_))
  }
  final val ID_FIELD_NUMBER = 1
  final val REASON_FIELD_NUMBER = 2
  final val META_FIELD_NUMBER = 3
  final val EXPECTEDDURATION_FIELD_NUMBER = 4
  def of(
    id: _root_.scala.Option[com.improving.EventId],
    reason: _root_.scala.Predef.String,
    meta: _root_.scala.Option[com.improving.eventcontext.EventMetaInfo],
    expectedDuration: _root_.scala.Option[com.google.protobuf.duration.Duration]
  ): _root_.com.improving.eventcontext.EventDelayed = _root_.com.improving.eventcontext.EventDelayed(
    id,
    reason,
    meta,
    expectedDuration
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[com.improving.eventcontext.EventDelayed])
}
