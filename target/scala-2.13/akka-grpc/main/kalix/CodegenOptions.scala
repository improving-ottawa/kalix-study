// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package kalix

@SerialVersionUID(0L)
final case class CodegenOptions(
    codegen: kalix.CodegenOptions.Codegen = kalix.CodegenOptions.Codegen.Empty,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[CodegenOptions] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      if (codegen.eventSourcedEntity.isDefined) {
        val __value = codegen.eventSourcedEntity.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (codegen.valueEntity.isDefined) {
        val __value = codegen.valueEntity.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (codegen.replicatedEntity.isDefined) {
        val __value = codegen.replicatedEntity.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (codegen.action.isDefined) {
        val __value = codegen.action.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (codegen.view.isDefined) {
        val __value = codegen.view.get
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
      codegen.eventSourcedEntity.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      codegen.valueEntity.foreach { __v =>
        val __m = __v
        _output__.writeTag(2, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      codegen.replicatedEntity.foreach { __v =>
        val __m = __v
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      codegen.action.foreach { __v =>
        val __m = __v
        _output__.writeTag(4, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      codegen.view.foreach { __v =>
        val __m = __v
        _output__.writeTag(5, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def getEventSourcedEntity: kalix.EventSourcedEntityDef = codegen.eventSourcedEntity.getOrElse(kalix.EventSourcedEntityDef.defaultInstance)
    def withEventSourcedEntity(__v: kalix.EventSourcedEntityDef): CodegenOptions = copy(codegen = kalix.CodegenOptions.Codegen.EventSourcedEntity(__v))
    def getValueEntity: kalix.ValueEntityDef = codegen.valueEntity.getOrElse(kalix.ValueEntityDef.defaultInstance)
    def withValueEntity(__v: kalix.ValueEntityDef): CodegenOptions = copy(codegen = kalix.CodegenOptions.Codegen.ValueEntity(__v))
    def getReplicatedEntity: kalix.ReplicatedEntityDef = codegen.replicatedEntity.getOrElse(kalix.ReplicatedEntityDef.defaultInstance)
    def withReplicatedEntity(__v: kalix.ReplicatedEntityDef): CodegenOptions = copy(codegen = kalix.CodegenOptions.Codegen.ReplicatedEntity(__v))
    def getAction: kalix.ActionDef = codegen.action.getOrElse(kalix.ActionDef.defaultInstance)
    def withAction(__v: kalix.ActionDef): CodegenOptions = copy(codegen = kalix.CodegenOptions.Codegen.Action(__v))
    def getView: kalix.ViewDef = codegen.view.getOrElse(kalix.ViewDef.defaultInstance)
    def withView(__v: kalix.ViewDef): CodegenOptions = copy(codegen = kalix.CodegenOptions.Codegen.View(__v))
    def clearCodegen: CodegenOptions = copy(codegen = kalix.CodegenOptions.Codegen.Empty)
    def withCodegen(__v: kalix.CodegenOptions.Codegen): CodegenOptions = copy(codegen = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => codegen.eventSourcedEntity.orNull
        case 2 => codegen.valueEntity.orNull
        case 3 => codegen.replicatedEntity.orNull
        case 4 => codegen.action.orNull
        case 5 => codegen.view.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => codegen.eventSourcedEntity.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 2 => codegen.valueEntity.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 3 => codegen.replicatedEntity.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 4 => codegen.action.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 5 => codegen.view.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: kalix.CodegenOptions.type = kalix.CodegenOptions
    // @@protoc_insertion_point(GeneratedMessage[kalix.CodegenOptions])
}

object CodegenOptions extends scalapb.GeneratedMessageCompanion[kalix.CodegenOptions] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[kalix.CodegenOptions] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): kalix.CodegenOptions = {
    var __codegen: kalix.CodegenOptions.Codegen = kalix.CodegenOptions.Codegen.Empty
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __codegen = kalix.CodegenOptions.Codegen.EventSourcedEntity(__codegen.eventSourcedEntity.fold(_root_.scalapb.LiteParser.readMessage[kalix.EventSourcedEntityDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 18 =>
          __codegen = kalix.CodegenOptions.Codegen.ValueEntity(__codegen.valueEntity.fold(_root_.scalapb.LiteParser.readMessage[kalix.ValueEntityDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 26 =>
          __codegen = kalix.CodegenOptions.Codegen.ReplicatedEntity(__codegen.replicatedEntity.fold(_root_.scalapb.LiteParser.readMessage[kalix.ReplicatedEntityDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 34 =>
          __codegen = kalix.CodegenOptions.Codegen.Action(__codegen.action.fold(_root_.scalapb.LiteParser.readMessage[kalix.ActionDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 42 =>
          __codegen = kalix.CodegenOptions.Codegen.View(__codegen.view.fold(_root_.scalapb.LiteParser.readMessage[kalix.ViewDef](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    kalix.CodegenOptions(
        codegen = __codegen,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[kalix.CodegenOptions] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      kalix.CodegenOptions(
        codegen = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).flatMap(_.as[_root_.scala.Option[kalix.EventSourcedEntityDef]]).map(kalix.CodegenOptions.Codegen.EventSourcedEntity(_))
            .orElse[kalix.CodegenOptions.Codegen](__fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).flatMap(_.as[_root_.scala.Option[kalix.ValueEntityDef]]).map(kalix.CodegenOptions.Codegen.ValueEntity(_)))
            .orElse[kalix.CodegenOptions.Codegen](__fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[_root_.scala.Option[kalix.ReplicatedEntityDef]]).map(kalix.CodegenOptions.Codegen.ReplicatedEntity(_)))
            .orElse[kalix.CodegenOptions.Codegen](__fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).flatMap(_.as[_root_.scala.Option[kalix.ActionDef]]).map(kalix.CodegenOptions.Codegen.Action(_)))
            .orElse[kalix.CodegenOptions.Codegen](__fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).flatMap(_.as[_root_.scala.Option[kalix.ViewDef]]).map(kalix.CodegenOptions.Codegen.View(_)))
            .getOrElse(kalix.CodegenOptions.Codegen.Empty)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = AnnotationsProto.javaDescriptor.getMessageTypes().get(5)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = AnnotationsProto.scalaDescriptor.messages(5)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 1 => __out = kalix.EventSourcedEntityDef
      case 2 => __out = kalix.ValueEntityDef
      case 3 => __out = kalix.ReplicatedEntityDef
      case 4 => __out = kalix.ActionDef
      case 5 => __out = kalix.ViewDef
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = kalix.CodegenOptions(
    codegen = kalix.CodegenOptions.Codegen.Empty
  )
  sealed trait Codegen extends _root_.scalapb.GeneratedOneof {
    def isEmpty: _root_.scala.Boolean = false
    def isDefined: _root_.scala.Boolean = true
    def isEventSourcedEntity: _root_.scala.Boolean = false
    def isValueEntity: _root_.scala.Boolean = false
    def isReplicatedEntity: _root_.scala.Boolean = false
    def isAction: _root_.scala.Boolean = false
    def isView: _root_.scala.Boolean = false
    def eventSourcedEntity: _root_.scala.Option[kalix.EventSourcedEntityDef] = _root_.scala.None
    def valueEntity: _root_.scala.Option[kalix.ValueEntityDef] = _root_.scala.None
    def replicatedEntity: _root_.scala.Option[kalix.ReplicatedEntityDef] = _root_.scala.None
    def action: _root_.scala.Option[kalix.ActionDef] = _root_.scala.None
    def view: _root_.scala.Option[kalix.ViewDef] = _root_.scala.None
  }
  object Codegen {
    @SerialVersionUID(0L)
    case object Empty extends kalix.CodegenOptions.Codegen {
      type ValueType = _root_.scala.Nothing
      override def isEmpty: _root_.scala.Boolean = true
      override def isDefined: _root_.scala.Boolean = false
      override def number: _root_.scala.Int = 0
      override def value: _root_.scala.Nothing = throw new java.util.NoSuchElementException("Empty.value")
    }
  
    @SerialVersionUID(0L)
    final case class EventSourcedEntity(value: kalix.EventSourcedEntityDef) extends kalix.CodegenOptions.Codegen {
      type ValueType = kalix.EventSourcedEntityDef
      override def isEventSourcedEntity: _root_.scala.Boolean = true
      override def eventSourcedEntity: _root_.scala.Option[kalix.EventSourcedEntityDef] = Some(value)
      override def number: _root_.scala.Int = 1
    }
    @SerialVersionUID(0L)
    final case class ValueEntity(value: kalix.ValueEntityDef) extends kalix.CodegenOptions.Codegen {
      type ValueType = kalix.ValueEntityDef
      override def isValueEntity: _root_.scala.Boolean = true
      override def valueEntity: _root_.scala.Option[kalix.ValueEntityDef] = Some(value)
      override def number: _root_.scala.Int = 2
    }
    @SerialVersionUID(0L)
    final case class ReplicatedEntity(value: kalix.ReplicatedEntityDef) extends kalix.CodegenOptions.Codegen {
      type ValueType = kalix.ReplicatedEntityDef
      override def isReplicatedEntity: _root_.scala.Boolean = true
      override def replicatedEntity: _root_.scala.Option[kalix.ReplicatedEntityDef] = Some(value)
      override def number: _root_.scala.Int = 3
    }
    @SerialVersionUID(0L)
    final case class Action(value: kalix.ActionDef) extends kalix.CodegenOptions.Codegen {
      type ValueType = kalix.ActionDef
      override def isAction: _root_.scala.Boolean = true
      override def action: _root_.scala.Option[kalix.ActionDef] = Some(value)
      override def number: _root_.scala.Int = 4
    }
    @SerialVersionUID(0L)
    final case class View(value: kalix.ViewDef) extends kalix.CodegenOptions.Codegen {
      type ValueType = kalix.ViewDef
      override def isView: _root_.scala.Boolean = true
      override def view: _root_.scala.Option[kalix.ViewDef] = Some(value)
      override def number: _root_.scala.Int = 5
    }
  }
  implicit class CodegenOptionsLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, kalix.CodegenOptions]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, kalix.CodegenOptions](_l) {
    def eventSourcedEntity: _root_.scalapb.lenses.Lens[UpperPB, kalix.EventSourcedEntityDef] = field(_.getEventSourcedEntity)((c_, f_) => c_.copy(codegen = kalix.CodegenOptions.Codegen.EventSourcedEntity(f_)))
    def valueEntity: _root_.scalapb.lenses.Lens[UpperPB, kalix.ValueEntityDef] = field(_.getValueEntity)((c_, f_) => c_.copy(codegen = kalix.CodegenOptions.Codegen.ValueEntity(f_)))
    def replicatedEntity: _root_.scalapb.lenses.Lens[UpperPB, kalix.ReplicatedEntityDef] = field(_.getReplicatedEntity)((c_, f_) => c_.copy(codegen = kalix.CodegenOptions.Codegen.ReplicatedEntity(f_)))
    def action: _root_.scalapb.lenses.Lens[UpperPB, kalix.ActionDef] = field(_.getAction)((c_, f_) => c_.copy(codegen = kalix.CodegenOptions.Codegen.Action(f_)))
    def view: _root_.scalapb.lenses.Lens[UpperPB, kalix.ViewDef] = field(_.getView)((c_, f_) => c_.copy(codegen = kalix.CodegenOptions.Codegen.View(f_)))
    def codegen: _root_.scalapb.lenses.Lens[UpperPB, kalix.CodegenOptions.Codegen] = field(_.codegen)((c_, f_) => c_.copy(codegen = f_))
  }
  final val EVENT_SOURCED_ENTITY_FIELD_NUMBER = 1
  final val VALUE_ENTITY_FIELD_NUMBER = 2
  final val REPLICATED_ENTITY_FIELD_NUMBER = 3
  final val ACTION_FIELD_NUMBER = 4
  final val VIEW_FIELD_NUMBER = 5
  def of(
    codegen: kalix.CodegenOptions.Codegen
  ): _root_.kalix.CodegenOptions = _root_.kalix.CodegenOptions(
    codegen
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[kalix.CodegenOptions])
}
