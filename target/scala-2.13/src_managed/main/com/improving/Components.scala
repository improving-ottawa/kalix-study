package com.improving

import kalix.scalasdk.DeferredCall


// This code is managed by Kalix tooling.
// It will be re-generated to reflect any changes to your protobuf definitions.
// DO NOT EDIT

/**
 * Not intended for user extension, provided through generated implementation
 */
trait Components {
 import Components._

 def organizationAPI: OrganizationAPICalls

}

object Components{

 trait OrganizationAPICalls {
   def addMembersToOrganization(command: _root_.com.improving.organization.ApiAddMembersToOrganization): DeferredCall[_root_.com.improving.organization.ApiAddMembersToOrganization, _root_.com.google.protobuf.empty.Empty]

   def addOwnersToOrganization(command: _root_.com.improving.organization.ApiAddOwnersToOrganization): DeferredCall[_root_.com.improving.organization.ApiAddOwnersToOrganization, _root_.com.google.protobuf.empty.Empty]

   def editOrganizationInfo(command: _root_.com.improving.organization.ApiEditOrganizationInfo): DeferredCall[_root_.com.improving.organization.ApiEditOrganizationInfo, _root_.com.google.protobuf.empty.Empty]

   def establishOrganization(command: _root_.com.improving.organization.ApiEstablishOrganization): DeferredCall[_root_.com.improving.organization.ApiEstablishOrganization, _root_.com.google.protobuf.empty.Empty]

 }

}
