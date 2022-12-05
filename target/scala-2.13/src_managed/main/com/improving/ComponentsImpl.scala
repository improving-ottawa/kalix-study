package com.improving

import kalix.scalasdk.Context
import kalix.scalasdk.DeferredCall
import kalix.scalasdk.Metadata
import kalix.scalasdk.impl.InternalContext
import kalix.scalasdk.impl.ScalaDeferredCallAdapter


// This code is managed by Kalix tooling.
// It will be re-generated to reflect any changes to your protobuf definitions.
// DO NOT EDIT

/**
 * Not intended for direct instantiation, called by generated code, use Action.components() to access
 */
final class ComponentsImpl(context: InternalContext) extends Components {

  def this(context: Context) =
    this(context.asInstanceOf[InternalContext])

  private def getGrpcClient[T](serviceClass: Class[T]): T =
    context.getComponentGrpcClient(serviceClass)

 @Override
 override def organizationAPI: Components.OrganizationAPICalls =
   new OrganizationAPICallsImpl();


 private final class OrganizationAPICallsImpl extends Components.OrganizationAPICalls {
   override def addMembersToOrganization(command: _root_.com.improving.organization.ApiAddMembersToOrganization): DeferredCall[_root_.com.improving.organization.ApiAddMembersToOrganization, _root_.com.google.protobuf.empty.Empty] =
     ScalaDeferredCallAdapter(
       command,
       Metadata.empty,
       "com.improving.organization.OrganizationService",
       "AddMembersToOrganization",
       () => getGrpcClient(classOf[_root_.com.improving.organization.OrganizationService]).addMembersToOrganization(command)
     )
   override def addOwnersToOrganization(command: _root_.com.improving.organization.ApiAddOwnersToOrganization): DeferredCall[_root_.com.improving.organization.ApiAddOwnersToOrganization, _root_.com.google.protobuf.empty.Empty] =
     ScalaDeferredCallAdapter(
       command,
       Metadata.empty,
       "com.improving.organization.OrganizationService",
       "AddOwnersToOrganization",
       () => getGrpcClient(classOf[_root_.com.improving.organization.OrganizationService]).addOwnersToOrganization(command)
     )
   override def editOrganizationInfo(command: _root_.com.improving.organization.ApiEditOrganizationInfo): DeferredCall[_root_.com.improving.organization.ApiEditOrganizationInfo, _root_.com.google.protobuf.empty.Empty] =
     ScalaDeferredCallAdapter(
       command,
       Metadata.empty,
       "com.improving.organization.OrganizationService",
       "EditOrganizationInfo",
       () => getGrpcClient(classOf[_root_.com.improving.organization.OrganizationService]).editOrganizationInfo(command)
     )
   override def establishOrganization(command: _root_.com.improving.organization.ApiEstablishOrganization): DeferredCall[_root_.com.improving.organization.ApiEstablishOrganization, _root_.com.google.protobuf.empty.Empty] =
     ScalaDeferredCallAdapter(
       command,
       Metadata.empty,
       "com.improving.organization.OrganizationService",
       "EstablishOrganization",
       () => getGrpcClient(classOf[_root_.com.improving.organization.OrganizationService]).establishOrganization(command)
     )
 }

}
