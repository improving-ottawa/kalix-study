// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.improving

/** *
  * Pattern of:
  * (?:[a-z0-9!#$%&amp;'*+/=?^_{|}~-](?:.[a-z0-9!#$%&amp;’*+/=?^_{|}~-])|&92;"(?:[&92;x01-&92;x08&92;x0b&92;x0c&92;x0e-&92;x1f&92;x21&92;x23-&92;x5b&92;x5d-&92;x7f]|&92;&92;[&92;x01-&92;x09&92;x0b&92;x0c&92;x0e-&92;x7f])&92;")&#64;(?:(?:[a-z0-9](?:[a-z0-9-][a-z0-9])?&92;.)+[a-z0-9](?:[a-z0-9-][a-z0-9])?|[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)&92;.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[&92;x01-&92;x08&92;x0b&92;x0c&92;x0e-&92;x1f&92;x21-&92;x5a&92;x53-&92;x7f]|&92;&92;[&92;x01-&92;x09&92;x0b&92;x0c&92;x0e-&92;x7f])+)])
  */
@SerialVersionUID(0L)
final case class EmailAddress(
    value: _root_.scala.Predef.String = "",
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[EmailAddress] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      
      {
        val __value = value
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, __value)
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
        val __v = value
        if (!__v.isEmpty) {
          _output__.writeString(1, __v)
        }
      };
      unknownFields.writeTo(_output__)
    }
    def withValue(__v: _root_.scala.Predef.String): EmailAddress = copy(value = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = value
          if (__t != "") __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(value)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: com.improving.EmailAddress.type = com.improving.EmailAddress
    // @@protoc_insertion_point(GeneratedMessage[com.improving.EmailAddress])
}

object EmailAddress extends scalapb.GeneratedMessageCompanion[com.improving.EmailAddress] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.improving.EmailAddress] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.improving.EmailAddress = {
    var __value: _root_.scala.Predef.String = ""
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __value = _input__.readStringRequireUtf8()
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    com.improving.EmailAddress(
        value = __value,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.improving.EmailAddress] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      com.improving.EmailAddress(
        value = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse("")
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = CommonDomainProto.javaDescriptor.getMessageTypes().get(3)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = CommonDomainProto.scalaDescriptor.messages(3)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.improving.EmailAddress(
    value = ""
  )
  implicit class EmailAddressLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.improving.EmailAddress]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.improving.EmailAddress](_l) {
    def value: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.value)((c_, f_) => c_.copy(value = f_))
  }
  final val VALUE_FIELD_NUMBER = 1
  def of(
    value: _root_.scala.Predef.String
  ): _root_.com.improving.EmailAddress = _root_.com.improving.EmailAddress(
    value
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[com.improving.EmailAddress])
}
