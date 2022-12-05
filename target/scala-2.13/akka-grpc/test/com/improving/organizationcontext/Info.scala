// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.improving.organizationcontext

/** @param url
  *   URL type?
  * @param logo
  *   URL type?
  */
@SerialVersionUID(0L)
final case class Info(
    name: _root_.scala.Predef.String = "",
    shortName: _root_.scala.Predef.String = "",
    address: _root_.scala.Option[com.improving.Address] = _root_.scala.None,
    isPrivate: _root_.scala.Boolean = false,
    url: _root_.scala.Predef.String = "",
    logo: _root_.scala.Predef.String = "",
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[Info] {
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
        val __value = shortName
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, __value)
        }
      };
      if (address.isDefined) {
        val __value = address.get
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      
      {
        val __value = isPrivate
        if (__value != false) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeBoolSize(4, __value)
        }
      };
      
      {
        val __value = url
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(5, __value)
        }
      };
      
      {
        val __value = logo
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(6, __value)
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
      {
        val __v = name
        if (!__v.isEmpty) {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = shortName
        if (!__v.isEmpty) {
          _output__.writeString(2, __v)
        }
      };
      address.foreach { __v =>
        val __m = __v
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      {
        val __v = isPrivate
        if (__v != false) {
          _output__.writeBool(4, __v)
        }
      };
      {
        val __v = url
        if (!__v.isEmpty) {
          _output__.writeString(5, __v)
        }
      };
      {
        val __v = logo
        if (!__v.isEmpty) {
          _output__.writeString(6, __v)
        }
      };
      unknownFields.writeTo(_output__)
    }
    def withName(__v: _root_.scala.Predef.String): Info = copy(name = __v)
    def withShortName(__v: _root_.scala.Predef.String): Info = copy(shortName = __v)
    def getAddress: com.improving.Address = address.getOrElse(com.improving.Address.defaultInstance)
    def clearAddress: Info = copy(address = _root_.scala.None)
    def withAddress(__v: com.improving.Address): Info = copy(address = Option(__v))
    def withIsPrivate(__v: _root_.scala.Boolean): Info = copy(isPrivate = __v)
    def withUrl(__v: _root_.scala.Predef.String): Info = copy(url = __v)
    def withLogo(__v: _root_.scala.Predef.String): Info = copy(logo = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = name
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = shortName
          if (__t != "") __t else null
        }
        case 3 => address.orNull
        case 4 => {
          val __t = isPrivate
          if (__t != false) __t else null
        }
        case 5 => {
          val __t = url
          if (__t != "") __t else null
        }
        case 6 => {
          val __t = logo
          if (__t != "") __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(name)
        case 2 => _root_.scalapb.descriptors.PString(shortName)
        case 3 => address.map(_.toPMessage).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 4 => _root_.scalapb.descriptors.PBoolean(isPrivate)
        case 5 => _root_.scalapb.descriptors.PString(url)
        case 6 => _root_.scalapb.descriptors.PString(logo)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: com.improving.organizationcontext.Info.type = com.improving.organizationcontext.Info
    // @@protoc_insertion_point(GeneratedMessage[com.improving.organizationcontext.Info])
}

object Info extends scalapb.GeneratedMessageCompanion[com.improving.organizationcontext.Info] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.improving.organizationcontext.Info] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.improving.organizationcontext.Info = {
    var __name: _root_.scala.Predef.String = ""
    var __shortName: _root_.scala.Predef.String = ""
    var __address: _root_.scala.Option[com.improving.Address] = _root_.scala.None
    var __isPrivate: _root_.scala.Boolean = false
    var __url: _root_.scala.Predef.String = ""
    var __logo: _root_.scala.Predef.String = ""
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __name = _input__.readStringRequireUtf8()
        case 18 =>
          __shortName = _input__.readStringRequireUtf8()
        case 26 =>
          __address = Option(__address.fold(_root_.scalapb.LiteParser.readMessage[com.improving.Address](_input__))(_root_.scalapb.LiteParser.readMessage(_input__, _)))
        case 32 =>
          __isPrivate = _input__.readBool()
        case 42 =>
          __url = _input__.readStringRequireUtf8()
        case 50 =>
          __logo = _input__.readStringRequireUtf8()
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    com.improving.organizationcontext.Info(
        name = __name,
        shortName = __shortName,
        address = __address,
        isPrivate = __isPrivate,
        url = __url,
        logo = __logo,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.improving.organizationcontext.Info] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      com.improving.organizationcontext.Info(
        name = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        shortName = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        address = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[_root_.scala.Option[com.improving.Address]]),
        isPrivate = __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).map(_.as[_root_.scala.Boolean]).getOrElse(false),
        url = __fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        logo = __fieldsMap.get(scalaDescriptor.findFieldByNumber(6).get).map(_.as[_root_.scala.Predef.String]).getOrElse("")
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = OrganizationContextDomainProto.javaDescriptor.getMessageTypes().get(2)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = OrganizationContextDomainProto.scalaDescriptor.messages(2)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 3 => __out = com.improving.Address
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.improving.organizationcontext.Info(
    name = "",
    shortName = "",
    address = _root_.scala.None,
    isPrivate = false,
    url = "",
    logo = ""
  )
  implicit class InfoLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.improving.organizationcontext.Info]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.improving.organizationcontext.Info](_l) {
    def name: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.name)((c_, f_) => c_.copy(name = f_))
    def shortName: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.shortName)((c_, f_) => c_.copy(shortName = f_))
    def address: _root_.scalapb.lenses.Lens[UpperPB, com.improving.Address] = field(_.getAddress)((c_, f_) => c_.copy(address = Option(f_)))
    def optionalAddress: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Option[com.improving.Address]] = field(_.address)((c_, f_) => c_.copy(address = f_))
    def isPrivate: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Boolean] = field(_.isPrivate)((c_, f_) => c_.copy(isPrivate = f_))
    def url: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.url)((c_, f_) => c_.copy(url = f_))
    def logo: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.logo)((c_, f_) => c_.copy(logo = f_))
  }
  final val NAME_FIELD_NUMBER = 1
  final val SHORTNAME_FIELD_NUMBER = 2
  final val ADDRESS_FIELD_NUMBER = 3
  final val ISPRIVATE_FIELD_NUMBER = 4
  final val URL_FIELD_NUMBER = 5
  final val LOGO_FIELD_NUMBER = 6
  def of(
    name: _root_.scala.Predef.String,
    shortName: _root_.scala.Predef.String,
    address: _root_.scala.Option[com.improving.Address],
    isPrivate: _root_.scala.Boolean,
    url: _root_.scala.Predef.String,
    logo: _root_.scala.Predef.String
  ): _root_.com.improving.organizationcontext.Info = _root_.com.improving.organizationcontext.Info(
    name,
    shortName,
    address,
    isPrivate,
    url,
    logo
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[com.improving.organizationcontext.Info])
}
