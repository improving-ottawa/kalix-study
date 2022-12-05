// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package kalix

/** Options to describe replicated entities
  *
  * @param name
  *   Optional name for the entity - if not defined, will follow the name of the service
  * @param entityType
  *   The entity type name used when replicating this entity
  */
@SerialVersionUID(0L)
final case class ReplicatedEntityDef(
    name: _root_.scala.Predef.String = "",
    entityType: _root_.scala.Predef.String = "",
    replicatedData: kalix.ReplicatedEntityDef.ReplicatedData = kalix.ReplicatedEntityDef.ReplicatedData.Empty,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[ReplicatedEntityDef] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      
      {
        val __value = name
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, __value)
        }
      };
      
      {
        val __value = entityType
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, __value)
        }
      };
      if (replicatedData.replicatedCounter.isDefined) {
        val __value = replicatedData.replicatedCounter.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (replicatedData.replicatedRegister.isDefined) {
        val __value = replicatedData.replicatedRegister.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (replicatedData.replicatedSet.isDefined) {
        val __value = replicatedData.replicatedSet.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (replicatedData.replicatedMap.isDefined) {
        val __value = replicatedData.replicatedMap.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (replicatedData.replicatedCounterMap.isDefined) {
        val __value = replicatedData.replicatedCounterMap.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (replicatedData.replicatedRegisterMap.isDefined) {
        val __value = replicatedData.replicatedRegisterMap.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (replicatedData.replicatedMultiMap.isDefined) {
        val __value = replicatedData.replicatedMultiMap.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (replicatedData.replicatedVote.isDefined) {
        val __value = replicatedData.replicatedVote.get
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
      {
        val __v = name
        if (!__v.isEmpty) {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = entityType
        if (!__v.isEmpty) {
          _output__.writeString(2, __v)
        }
      };
      replicatedData.replicatedCounter.foreach { __v =>
        val __m = __v
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      replicatedData.replicatedRegister.foreach { __v =>
        val __m = __v
        _output__.writeTag(4, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      replicatedData.replicatedSet.foreach { __v =>
        val __m = __v
        _output__.writeTag(5, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      replicatedData.replicatedMap.foreach { __v =>
        val __m = __v
        _output__.writeTag(6, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      replicatedData.replicatedCounterMap.foreach { __v =>
        val __m = __v
        _output__.writeTag(7, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      replicatedData.replicatedRegisterMap.foreach { __v =>
        val __m = __v
        _output__.writeTag(8, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      replicatedData.replicatedMultiMap.foreach { __v =>
        val __m = __v
        _output__.writeTag(9, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      replicatedData.replicatedVote.foreach { __v =>
        val __m = __v
        _output__.writeTag(10, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def withName(__v: _root_.scala.Predef.String): ReplicatedEntityDef = copy(name = __v)
    def withEntityType(__v: _root_.scala.Predef.String): ReplicatedEntityDef = copy(entityType = __v)
    def getReplicatedCounter: kalix.ReplicatedCounterDef = replicatedData.replicatedCounter.getOrElse(kalix.ReplicatedCounterDef.defaultInstance)
    def withReplicatedCounter(__v: kalix.ReplicatedCounterDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounter(__v))
    def getReplicatedRegister: kalix.ReplicatedRegisterDef = replicatedData.replicatedRegister.getOrElse(kalix.ReplicatedRegisterDef.defaultInstance)
    def withReplicatedRegister(__v: kalix.ReplicatedRegisterDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegister(__v))
    def getReplicatedSet: kalix.ReplicatedSetDef = replicatedData.replicatedSet.getOrElse(kalix.ReplicatedSetDef.defaultInstance)
    def withReplicatedSet(__v: kalix.ReplicatedSetDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedSet(__v))
    def getReplicatedMap: kalix.ReplicatedMapDef = replicatedData.replicatedMap.getOrElse(kalix.ReplicatedMapDef.defaultInstance)
    def withReplicatedMap(__v: kalix.ReplicatedMapDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMap(__v))
    def getReplicatedCounterMap: kalix.ReplicatedCounterMapDef = replicatedData.replicatedCounterMap.getOrElse(kalix.ReplicatedCounterMapDef.defaultInstance)
    def withReplicatedCounterMap(__v: kalix.ReplicatedCounterMapDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounterMap(__v))
    def getReplicatedRegisterMap: kalix.ReplicatedRegisterMapDef = replicatedData.replicatedRegisterMap.getOrElse(kalix.ReplicatedRegisterMapDef.defaultInstance)
    def withReplicatedRegisterMap(__v: kalix.ReplicatedRegisterMapDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegisterMap(__v))
    def getReplicatedMultiMap: kalix.ReplicatedMultiMapDef = replicatedData.replicatedMultiMap.getOrElse(kalix.ReplicatedMultiMapDef.defaultInstance)
    def withReplicatedMultiMap(__v: kalix.ReplicatedMultiMapDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMultiMap(__v))
    def getReplicatedVote: kalix.ReplicatedVoteDef = replicatedData.replicatedVote.getOrElse(kalix.ReplicatedVoteDef.defaultInstance)
    def withReplicatedVote(__v: kalix.ReplicatedVoteDef): ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedVote(__v))
    def clearReplicatedData: ReplicatedEntityDef = copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.Empty)
    def withReplicatedData(__v: kalix.ReplicatedEntityDef.ReplicatedData): ReplicatedEntityDef = copy(replicatedData = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = name
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = entityType
          if (__t != "") __t else null
        }
        case 3 => replicatedData.replicatedCounter.orNull
        case 4 => replicatedData.replicatedRegister.orNull
        case 5 => replicatedData.replicatedSet.orNull
        case 6 => replicatedData.replicatedMap.orNull
        case 7 => replicatedData.replicatedCounterMap.orNull
        case 8 => replicatedData.replicatedRegisterMap.orNull
        case 9 => replicatedData.replicatedMultiMap.orNull
        case 10 => replicatedData.replicatedVote.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(name)
        case 2 => _root_.scalapb.descriptors.PString(entityType)
        case 3 => replicatedData.replicatedCounter.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 4 => replicatedData.replicatedRegister.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 5 => replicatedData.replicatedSet.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 6 => replicatedData.replicatedMap.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 7 => replicatedData.replicatedCounterMap.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 8 => replicatedData.replicatedRegisterMap.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 9 => replicatedData.replicatedMultiMap.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 10 => replicatedData.replicatedVote.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: kalix.ReplicatedEntityDef.type = kalix.ReplicatedEntityDef
    // @@protoc_insertion_point(GeneratedMessage[kalix.ReplicatedEntityDef])
}

object ReplicatedEntityDef extends scalapb.GeneratedMessageCompanion[kalix.ReplicatedEntityDef] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[kalix.ReplicatedEntityDef] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): kalix.ReplicatedEntityDef = {
    var __name: _root_.scala.Predef.String = ""
    var __entityType: _root_.scala.Predef.String = ""
    var __replicatedData: kalix.ReplicatedEntityDef.ReplicatedData = kalix.ReplicatedEntityDef.ReplicatedData.Empty
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __name = _input__.readStringRequireUtf8()
        case 18 =>
          __entityType = _input__.readStringRequireUtf8()
        case 26 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounter(__replicatedData.replicatedCounter.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedCounterDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 34 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegister(__replicatedData.replicatedRegister.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedRegisterDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 42 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedSet(__replicatedData.replicatedSet.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedSetDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 50 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMap(__replicatedData.replicatedMap.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedMapDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 58 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounterMap(__replicatedData.replicatedCounterMap.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedCounterMapDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 66 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegisterMap(__replicatedData.replicatedRegisterMap.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedRegisterMapDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 74 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMultiMap(__replicatedData.replicatedMultiMap.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedMultiMapDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 82 =>
          __replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedVote(__replicatedData.replicatedVote.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedVoteDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    kalix.ReplicatedEntityDef(
        name = __name,
        entityType = __entityType,
        replicatedData = __replicatedData,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[kalix.ReplicatedEntityDef] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      kalix.ReplicatedEntityDef(
        name = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        entityType = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        replicatedData = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedCounterDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounter(_))
            .orElse[kalix.ReplicatedEntityDef.ReplicatedData](__fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedRegisterDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegister(_)))
            .orElse[kalix.ReplicatedEntityDef.ReplicatedData](__fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedSetDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedSet(_)))
            .orElse[kalix.ReplicatedEntityDef.ReplicatedData](__fieldsMap.get(scalaDescriptor.findFieldByNumber(6).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedMapDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMap(_)))
            .orElse[kalix.ReplicatedEntityDef.ReplicatedData](__fieldsMap.get(scalaDescriptor.findFieldByNumber(7).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedCounterMapDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounterMap(_)))
            .orElse[kalix.ReplicatedEntityDef.ReplicatedData](__fieldsMap.get(scalaDescriptor.findFieldByNumber(8).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedRegisterMapDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegisterMap(_)))
            .orElse[kalix.ReplicatedEntityDef.ReplicatedData](__fieldsMap.get(scalaDescriptor.findFieldByNumber(9).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedMultiMapDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMultiMap(_)))
            .orElse[kalix.ReplicatedEntityDef.ReplicatedData](__fieldsMap.get(scalaDescriptor.findFieldByNumber(10).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedVoteDef]]).map(kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedVote(_)))
            .getOrElse(kalix.ReplicatedEntityDef.ReplicatedData.Empty)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = ComponentProto.javaDescriptor.getMessageTypes().get(4)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = ComponentProto.scalaDescriptor.messages(4)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 3 => __out = kalix.ReplicatedCounterDef
      case 4 => __out = kalix.ReplicatedRegisterDef
      case 5 => __out = kalix.ReplicatedSetDef
      case 6 => __out = kalix.ReplicatedMapDef
      case 7 => __out = kalix.ReplicatedCounterMapDef
      case 8 => __out = kalix.ReplicatedRegisterMapDef
      case 9 => __out = kalix.ReplicatedMultiMapDef
      case 10 => __out = kalix.ReplicatedVoteDef
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = kalix.ReplicatedEntityDef(
    name = "",
    entityType = "",
    replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.Empty
  )
  sealed trait ReplicatedData extends _root_.scalapb.GeneratedOneof {
    def isEmpty: _root_.scala.Boolean = false
    def isDefined: _root_.scala.Boolean = true
    def isReplicatedCounter: _root_.scala.Boolean = false
    def isReplicatedRegister: _root_.scala.Boolean = false
    def isReplicatedSet: _root_.scala.Boolean = false
    def isReplicatedMap: _root_.scala.Boolean = false
    def isReplicatedCounterMap: _root_.scala.Boolean = false
    def isReplicatedRegisterMap: _root_.scala.Boolean = false
    def isReplicatedMultiMap: _root_.scala.Boolean = false
    def isReplicatedVote: _root_.scala.Boolean = false
    def replicatedCounter: _root_.scala.Option[kalix.ReplicatedCounterDef] = _root_.scala.None
    def replicatedRegister: _root_.scala.Option[kalix.ReplicatedRegisterDef] = _root_.scala.None
    def replicatedSet: _root_.scala.Option[kalix.ReplicatedSetDef] = _root_.scala.None
    def replicatedMap: _root_.scala.Option[kalix.ReplicatedMapDef] = _root_.scala.None
    def replicatedCounterMap: _root_.scala.Option[kalix.ReplicatedCounterMapDef] = _root_.scala.None
    def replicatedRegisterMap: _root_.scala.Option[kalix.ReplicatedRegisterMapDef] = _root_.scala.None
    def replicatedMultiMap: _root_.scala.Option[kalix.ReplicatedMultiMapDef] = _root_.scala.None
    def replicatedVote: _root_.scala.Option[kalix.ReplicatedVoteDef] = _root_.scala.None
  }
  object ReplicatedData {
    @SerialVersionUID(0L)
    case object Empty extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = _root_.scala.Nothing
      override def isEmpty: _root_.scala.Boolean = true
      override def isDefined: _root_.scala.Boolean = false
      override def number: _root_.scala.Int = 0
      override def value: _root_.scala.Nothing = throw new java.util.NoSuchElementException("Empty.value")
    }
  
    @SerialVersionUID(0L)
    final case class ReplicatedCounter(value: kalix.ReplicatedCounterDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedCounterDef
      override def isReplicatedCounter: _root_.scala.Boolean = true
      override def replicatedCounter: _root_.scala.Option[kalix.ReplicatedCounterDef] = Some(value)
      override def number: _root_.scala.Int = 3
    }
    @SerialVersionUID(0L)
    final case class ReplicatedRegister(value: kalix.ReplicatedRegisterDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedRegisterDef
      override def isReplicatedRegister: _root_.scala.Boolean = true
      override def replicatedRegister: _root_.scala.Option[kalix.ReplicatedRegisterDef] = Some(value)
      override def number: _root_.scala.Int = 4
    }
    @SerialVersionUID(0L)
    final case class ReplicatedSet(value: kalix.ReplicatedSetDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedSetDef
      override def isReplicatedSet: _root_.scala.Boolean = true
      override def replicatedSet: _root_.scala.Option[kalix.ReplicatedSetDef] = Some(value)
      override def number: _root_.scala.Int = 5
    }
    @SerialVersionUID(0L)
    final case class ReplicatedMap(value: kalix.ReplicatedMapDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedMapDef
      override def isReplicatedMap: _root_.scala.Boolean = true
      override def replicatedMap: _root_.scala.Option[kalix.ReplicatedMapDef] = Some(value)
      override def number: _root_.scala.Int = 6
    }
    @SerialVersionUID(0L)
    final case class ReplicatedCounterMap(value: kalix.ReplicatedCounterMapDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedCounterMapDef
      override def isReplicatedCounterMap: _root_.scala.Boolean = true
      override def replicatedCounterMap: _root_.scala.Option[kalix.ReplicatedCounterMapDef] = Some(value)
      override def number: _root_.scala.Int = 7
    }
    @SerialVersionUID(0L)
    final case class ReplicatedRegisterMap(value: kalix.ReplicatedRegisterMapDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedRegisterMapDef
      override def isReplicatedRegisterMap: _root_.scala.Boolean = true
      override def replicatedRegisterMap: _root_.scala.Option[kalix.ReplicatedRegisterMapDef] = Some(value)
      override def number: _root_.scala.Int = 8
    }
    @SerialVersionUID(0L)
    final case class ReplicatedMultiMap(value: kalix.ReplicatedMultiMapDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedMultiMapDef
      override def isReplicatedMultiMap: _root_.scala.Boolean = true
      override def replicatedMultiMap: _root_.scala.Option[kalix.ReplicatedMultiMapDef] = Some(value)
      override def number: _root_.scala.Int = 9
    }
    @SerialVersionUID(0L)
    final case class ReplicatedVote(value: kalix.ReplicatedVoteDef) extends kalix.ReplicatedEntityDef.ReplicatedData {
      type ValueType = kalix.ReplicatedVoteDef
      override def isReplicatedVote: _root_.scala.Boolean = true
      override def replicatedVote: _root_.scala.Option[kalix.ReplicatedVoteDef] = Some(value)
      override def number: _root_.scala.Int = 10
    }
  }
  implicit class ReplicatedEntityDefLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedEntityDef]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, kalix.ReplicatedEntityDef](_l) {
    def name: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.name)((c_, f_) => c_.copy(name = f_))
    def entityType: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.entityType)((c_, f_) => c_.copy(entityType = f_))
    def replicatedCounter: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedCounterDef] = field(_.getReplicatedCounter)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounter(f_)))
    def replicatedRegister: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedRegisterDef] = field(_.getReplicatedRegister)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegister(f_)))
    def replicatedSet: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedSetDef] = field(_.getReplicatedSet)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedSet(f_)))
    def replicatedMap: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedMapDef] = field(_.getReplicatedMap)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMap(f_)))
    def replicatedCounterMap: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedCounterMapDef] = field(_.getReplicatedCounterMap)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedCounterMap(f_)))
    def replicatedRegisterMap: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedRegisterMapDef] = field(_.getReplicatedRegisterMap)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedRegisterMap(f_)))
    def replicatedMultiMap: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedMultiMapDef] = field(_.getReplicatedMultiMap)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedMultiMap(f_)))
    def replicatedVote: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedVoteDef] = field(_.getReplicatedVote)((c_, f_) => c_.copy(replicatedData = kalix.ReplicatedEntityDef.ReplicatedData.ReplicatedVote(f_)))
    def replicatedData: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedEntityDef.ReplicatedData] = field(_.replicatedData)((c_, f_) => c_.copy(replicatedData = f_))
  }
  final val NAME_FIELD_NUMBER = 1
  final val ENTITY_TYPE_FIELD_NUMBER = 2
  final val REPLICATED_COUNTER_FIELD_NUMBER = 3
  final val REPLICATED_REGISTER_FIELD_NUMBER = 4
  final val REPLICATED_SET_FIELD_NUMBER = 5
  final val REPLICATED_MAP_FIELD_NUMBER = 6
  final val REPLICATED_COUNTER_MAP_FIELD_NUMBER = 7
  final val REPLICATED_REGISTER_MAP_FIELD_NUMBER = 8
  final val REPLICATED_MULTI_MAP_FIELD_NUMBER = 9
  final val REPLICATED_VOTE_FIELD_NUMBER = 10
  def of(
    name: _root_.scala.Predef.String,
    entityType: _root_.scala.Predef.String,
    replicatedData: kalix.ReplicatedEntityDef.ReplicatedData
  ): _root_.kalix.ReplicatedEntityDef = _root_.kalix.ReplicatedEntityDef(
    name,
    entityType,
    replicatedData
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[kalix.ReplicatedEntityDef])
}
