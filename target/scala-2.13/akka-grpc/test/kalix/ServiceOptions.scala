// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package kalix

/** @param type
  *   This field is mandatory
  * @param component
  *   A path to a component - required for SERVICE_TYPE_ENTITY types
  * @param acl
  *   ACL options
  */
@SerialVersionUID(0L)
final case class ServiceOptions(
    `type`: kalix.ServiceOptions.ServiceType = kalix.ServiceOptions.ServiceType.SERVICE_TYPE_UNSPECIFIED,
    component: _root_.scala.Predef.String = "",
    acl: _root_.scala.Option[kalix.Acl] = _root_.scala.None,
    eventing: _root_.scala.Option[kalix.ServiceEventing] = _root_.scala.None,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[ServiceOptions] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      
      {
        val __value = `type`.value
        if (__value != 0) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeEnumSize(1, __value)
        }
      };
      
      {
        val __value = component
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, __value)
        }
      };
      if (acl.isDefined) {
        val __value = acl.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      if (eventing.isDefined) {
        val __value = eventing.get
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
        val __v = `type`.value
        if (__v != 0) {
          _output__.writeEnum(1, __v)
        }
      };
      {
        val __v = component
        if (!__v.isEmpty) {
          _output__.writeString(2, __v)
        }
      };
      acl.foreach { __v =>
        val __m = __v
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      eventing.foreach { __v =>
        val __m = __v
        _output__.writeTag(4, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def withType(__v: kalix.ServiceOptions.ServiceType): ServiceOptions = copy(`type` = __v)
    def withComponent(__v: _root_.scala.Predef.String): ServiceOptions = copy(component = __v)
    def getAcl: kalix.Acl = acl.getOrElse(kalix.Acl.defaultInstance)
    def clearAcl: ServiceOptions = copy(acl = _root_.scala.None)
    def withAcl(__v: kalix.Acl): ServiceOptions = copy(acl = Option(__v))
    def getEventing: kalix.ServiceEventing = eventing.getOrElse(kalix.ServiceEventing.defaultInstance)
    def clearEventing: ServiceOptions = copy(eventing = _root_.scala.None)
    def withEventing(__v: kalix.ServiceEventing): ServiceOptions = copy(eventing = Option(__v))
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = `type`.javaValueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
        case 2 => {
          val __t = component
          if (__t != "") __t else null
        }
        case 3 => acl.orNull
        case 4 => eventing.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PEnum(`type`.scalaValueDescriptor)
        case 2 => _root_.scalapb.descriptors.PString(component)
        case 3 => acl.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 4 => eventing.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: kalix.ServiceOptions.type = kalix.ServiceOptions
    // @@protoc_insertion_point(GeneratedMessage[kalix.ServiceOptions])
}

object ServiceOptions extends scalapb.GeneratedMessageCompanion[kalix.ServiceOptions] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[kalix.ServiceOptions] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): kalix.ServiceOptions = {
    var __type: kalix.ServiceOptions.ServiceType = kalix.ServiceOptions.ServiceType.SERVICE_TYPE_UNSPECIFIED
    var __component: _root_.scala.Predef.String = ""
    var __acl: _root_.scala.Option[kalix.Acl] = _root_.scala.None
    var __eventing: _root_.scala.Option[kalix.ServiceEventing] = _root_.scala.None
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 8 =>
          __type = kalix.ServiceOptions.ServiceType.fromValue(_input__.readEnum())
        case 18 =>
          __component = _input__.readStringRequireUtf8()
        case 26 =>
          __acl = Option(__acl.fold(_root_.scalapb.LiteParser.readMessage[kalix.Acl](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 34 =>
          __eventing = Option(__eventing.fold(_root_.scalapb.LiteParser.readMessage[kalix.ServiceEventing](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    kalix.ServiceOptions(
        `type` = __type,
        component = __component,
        acl = __acl,
        eventing = __eventing,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[kalix.ServiceOptions] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      kalix.ServiceOptions(
        `type` = kalix.ServiceOptions.ServiceType.fromValue(__fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scalapb.descriptors.EnumValueDescriptor]).getOrElse(kalix.ServiceOptions.ServiceType.SERVICE_TYPE_UNSPECIFIED.scalaValueDescriptor).number),
        component = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        acl = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[_root_.scala.Option[kalix.Acl]]),
        eventing = __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).flatMap(_.as[_root_.scala.Option[kalix.ServiceEventing]])
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = AnnotationsProto.javaDescriptor.getMessageTypes().get(4)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = AnnotationsProto.scalaDescriptor.messages(4)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 3 => __out = kalix.Acl
      case 4 => __out = kalix.ServiceEventing
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = {
    (__fieldNumber: @_root_.scala.unchecked) match {
      case 1 => kalix.ServiceOptions.ServiceType
    }
  }
  lazy val defaultInstance = kalix.ServiceOptions(
    `type` = kalix.ServiceOptions.ServiceType.SERVICE_TYPE_UNSPECIFIED,
    component = "",
    acl = _root_.scala.None,
    eventing = _root_.scala.None
  )
  sealed abstract class ServiceType(val value: _root_.scala.Int) extends _root_.scalapb.GeneratedEnum {
    type EnumType = ServiceType
    def isServiceTypeUnspecified: _root_.scala.Boolean = false
    def isServiceTypeAction: _root_.scala.Boolean = false
    def isServiceTypeEntity: _root_.scala.Boolean = false
    def isServiceTypeView: _root_.scala.Boolean = false
    def companion: _root_.scalapb.GeneratedEnumCompanion[ServiceType] = kalix.ServiceOptions.ServiceType
    final def asRecognized: _root_.scala.Option[kalix.ServiceOptions.ServiceType.Recognized] = if (isUnrecognized) _root_.scala.None else _root_.scala.Some(this.asInstanceOf[kalix.ServiceOptions.ServiceType.Recognized])
  }
  
  object ServiceType extends _root_.scalapb.GeneratedEnumCompanion[ServiceType] {
    sealed trait Recognized extends ServiceType
    implicit def enumCompanion: _root_.scalapb.GeneratedEnumCompanion[ServiceType] = this
    
    /** Will be ignored for processing - same as omitting the service type
      */
    @SerialVersionUID(0L)
    case object SERVICE_TYPE_UNSPECIFIED extends ServiceType(0) with ServiceType.Recognized {
      val index = 0
      val name = "SERVICE_TYPE_UNSPECIFIED"
      override def isServiceTypeUnspecified: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    case object SERVICE_TYPE_ACTION extends ServiceType(1) with ServiceType.Recognized {
      val index = 1
      val name = "SERVICE_TYPE_ACTION"
      override def isServiceTypeAction: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    case object SERVICE_TYPE_ENTITY extends ServiceType(2) with ServiceType.Recognized {
      val index = 2
      val name = "SERVICE_TYPE_ENTITY"
      override def isServiceTypeEntity: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    case object SERVICE_TYPE_VIEW extends ServiceType(3) with ServiceType.Recognized {
      val index = 3
      val name = "SERVICE_TYPE_VIEW"
      override def isServiceTypeView: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    final case class Unrecognized(unrecognizedValue: _root_.scala.Int) extends ServiceType(unrecognizedValue) with _root_.scalapb.UnrecognizedEnum
    lazy val values = scala.collection.immutable.Seq(SERVICE_TYPE_UNSPECIFIED, SERVICE_TYPE_ACTION, SERVICE_TYPE_ENTITY, SERVICE_TYPE_VIEW)
    def fromValue(__value: _root_.scala.Int): ServiceType = __value match {
      case 0 => SERVICE_TYPE_UNSPECIFIED
      case 1 => SERVICE_TYPE_ACTION
      case 2 => SERVICE_TYPE_ENTITY
      case 3 => SERVICE_TYPE_VIEW
      case __other => Unrecognized(__other)
    }
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.EnumDescriptor = kalix.ServiceOptions.javaDescriptor.getEnumTypes().get(0)
    def scalaDescriptor: _root_.scalapb.descriptors.EnumDescriptor = kalix.ServiceOptions.scalaDescriptor.enums(0)
  }
  implicit class ServiceOptionsLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, kalix.ServiceOptions]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, kalix.ServiceOptions](_l) {
    def `type`: _root_.scalapb.lenses.Lens[UpperPB, kalix.ServiceOptions.ServiceType] = field(_.`type`)((c_, f_) => c_.copy(`type` = f_))
    def component: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.component)((c_, f_) => c_.copy(component = f_))
    def acl: _root_.scalapb.lenses.Lens[UpperPB, kalix.Acl] = field(_.getAcl)((c_, f_) => c_.copy(acl = Option(f_)))
    def optionalAcl: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[kalix.Acl]] = field(_.acl)((c_, f_) => c_.copy(acl = f_))
    def eventing: _root_.scalapb.lenses.Lens[UpperPB, kalix.ServiceEventing] = field(_.getEventing)((c_, f_) => c_.copy(eventing = Option(f_)))
    def optionalEventing: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[kalix.ServiceEventing]] = field(_.eventing)((c_, f_) => c_.copy(eventing = f_))
  }
  final val TYPE_FIELD_NUMBER = 1
  final val COMPONENT_FIELD_NUMBER = 2
  final val ACL_FIELD_NUMBER = 3
  final val EVENTING_FIELD_NUMBER = 4
  def of(
    `type`: kalix.ServiceOptions.ServiceType,
    component: _root_.scala.Predef.String,
    acl: _root_.scala.Option[kalix.Acl],
    eventing: _root_.scala.Option[kalix.ServiceEventing]
  ): _root_.kalix.ServiceOptions = _root_.kalix.ServiceOptions(
    `type`,
    component,
    acl,
    eventing
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[kalix.ServiceOptions])
}
