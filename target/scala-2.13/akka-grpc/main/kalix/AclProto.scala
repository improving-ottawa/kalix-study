// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package kalix

object AclProto extends _root_.scalapb.GeneratedFileObject {
  lazy val dependencies: Seq[_root_.scalapb.GeneratedFileObject] = Seq.empty
  lazy val messagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] =
    Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]](
      kalix.Acl,
      kalix.PrincipalMatcher
    )
  private lazy val ProtoBytes: _root_.scala.Array[Byte] =
      scalapb.Encoding.fromBase64(scala.collection.immutable.Seq(
  """Cg9rYWxpeC9hY2wucHJvdG8SBWthbGl4IqQBCgNBY2wSOQoFYWxsb3cYASADKAsyFy5rYWxpeC5QcmluY2lwYWxNYXRjaGVyQ
  griPwcSBWFsbG93UgVhbGxvdxI2CgRkZW55GAIgAygLMhcua2FsaXguUHJpbmNpcGFsTWF0Y2hlckIJ4j8GEgRkZW55UgRkZW55E
  ioKCWRlbnlfY29kZRgDIAEoDUIN4j8KEghkZW55Q29kZVIIZGVueUNvZGUi+gEKEFByaW5jaXBhbE1hdGNoZXISUQoJcHJpbmNpc
  GFsGAEgASgOMiEua2FsaXguUHJpbmNpcGFsTWF0Y2hlci5QcmluY2lwYWxCDuI/CxIJcHJpbmNpcGFsSABSCXByaW5jaXBhbBIoC
  gdzZXJ2aWNlGAIgASgJQgziPwkSB3NlcnZpY2VIAFIHc2VydmljZSJeCglQcmluY2lwYWwSIQoLVU5TUEVDSUZJRUQQABoQ4j8NE
  gtVTlNQRUNJRklFRBIRCgNBTEwQARoI4j8FEgNBTEwSGwoISU5URVJORVQQAhoN4j8KEghJTlRFUk5FVEIJCgdtYXRjaGVyQkIKB
  WthbGl4QghBY2xQcm90b1ABWi1naXRodWIuY29tL2xpZ2h0YmVuZC9rYWxpeC1nby1zZGsva2FsaXg7a2FsaXhiBnByb3RvMw=="""
      ).mkString)
  lazy val scalaDescriptor: _root_.scalapb.descriptors.FileDescriptor = {
    val scalaProto = com.google.protobuf.descriptor.FileDescriptorProto.parseFrom(ProtoBytes)
    _root_.scalapb.descriptors.FileDescriptor.buildFrom(scalaProto, dependencies.map(_.scalaDescriptor))
  }
  lazy val javaDescriptor: com.google.protobuf.Descriptors.FileDescriptor = {
    val javaProto = com.google.protobuf.DescriptorProtos.FileDescriptorProto.parseFrom(ProtoBytes)
    com.google.protobuf.Descriptors.FileDescriptor.buildFrom(javaProto, _root_.scala.Array(
    ))
  }
  @deprecated("Use javaDescriptor instead. In a future version this will refer to scalaDescriptor.", "ScalaPB 0.5.47")
  def descriptor: com.google.protobuf.Descriptors.FileDescriptor = javaDescriptor
}