package com.improving.organizationcontext.organization

import com.google.protobuf.empty.Empty
import com.improving.Components
import com.improving.ComponentsImpl
import com.improving.organization
import com.improving.organizationcontext.FindOrganizationsByMember
import com.improving.organizationcontext.FindOrganizationsByOwner
import com.improving.organizationcontext.GetOrganizationInfo
import com.improving.organizationcontext.MembersAddedToOrganization
import com.improving.organizationcontext.MembersRemovedFromOrganization
import com.improving.organizationcontext.OrganizationAccountsUpdated
import com.improving.organizationcontext.OrganizationContactsUpdated
import com.improving.organizationcontext.OrganizationEstablished
import com.improving.organizationcontext.OrganizationInfoUpdated
import com.improving.organizationcontext.OrganizationStatusUpdated
import com.improving.organizationcontext.OwnersAddedToOrganization
import com.improving.organizationcontext.OwnersRemovedFromOrganization
import com.improving.organizationcontext.ParentUpdated
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity

// This code is managed by Kalix tooling.
// It will be re-generated to reflect any changes to your protobuf definitions.
// DO NOT EDIT

abstract class AbstractOrganizationAPI extends EventSourcedEntity[OrganizationState] {

  def components: Components =
    new ComponentsImpl(commandContext())

  def addMembersToOrganization(currentState: OrganizationState, apiAddMembersToOrganization: organization.ApiAddMembersToOrganization): EventSourcedEntity.Effect[Empty]

  def addOwnersToOrganization(currentState: OrganizationState, apiAddOwnersToOrganization: organization.ApiAddOwnersToOrganization): EventSourcedEntity.Effect[Empty]

  def editOrganizationInfo(currentState: OrganizationState, apiEditOrganizationInfo: organization.ApiEditOrganizationInfo): EventSourcedEntity.Effect[Empty]

  def establishOrganization(currentState: OrganizationState, apiEstablishOrganization: organization.ApiEstablishOrganization): EventSourcedEntity.Effect[Empty]

  def findOrganizationsByMember(currentState: OrganizationState, findOrganizationsByMember: FindOrganizationsByMember): OrganizationState
  def findOrganizationsByOwner(currentState: OrganizationState, findOrganizationsByOwner: FindOrganizationsByOwner): OrganizationState
  def getOrganizationInfo(currentState: OrganizationState, getOrganizationInfo: GetOrganizationInfo): OrganizationState
  def membersAddedToOrganization(currentState: OrganizationState, membersAddedToOrganization: MembersAddedToOrganization): OrganizationState
  def membersRemovedFromOrganization(currentState: OrganizationState, membersRemovedFromOrganization: MembersRemovedFromOrganization): OrganizationState
  def organizationAccountsUpdated(currentState: OrganizationState, organizationAccountsUpdated: OrganizationAccountsUpdated): OrganizationState
  def organizationContactsUpdated(currentState: OrganizationState, organizationContactsUpdated: OrganizationContactsUpdated): OrganizationState
  def organizationEstablished(currentState: OrganizationState, organizationEstablished: OrganizationEstablished): OrganizationState
  def organizationInfoUpdated(currentState: OrganizationState, organizationInfoUpdated: OrganizationInfoUpdated): OrganizationState
  def organizationStatusUpdated(currentState: OrganizationState, organizationStatusUpdated: OrganizationStatusUpdated): OrganizationState
  def ownersAddedToOrganization(currentState: OrganizationState, ownersAddedToOrganization: OwnersAddedToOrganization): OrganizationState
  def ownersRemovedFromOrganization(currentState: OrganizationState, ownersRemovedFromOrganization: OwnersRemovedFromOrganization): OrganizationState
  def parentUpdated(currentState: OrganizationState, parentUpdated: ParentUpdated): OrganizationState
}

