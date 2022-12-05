package com.improving.organizationcontext.organization

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
import kalix.javasdk.impl.eventsourcedentity.EventSourcedEntityRouter.CommandHandlerNotFound
import kalix.javasdk.impl.eventsourcedentity.EventSourcedEntityRouter.EventHandlerNotFound
import kalix.scalasdk.eventsourcedentity.CommandContext
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.impl.eventsourcedentity.EventSourcedEntityRouter

// This code is managed by Kalix tooling.
// It will be re-generated to reflect any changes to your protobuf definitions.
// DO NOT EDIT

/**
 * An event sourced entity handler that is the glue between the Protobuf service <code>CounterService</code>
 * and the command handler methods in the <code>Counter</code> class.
 */
class OrganizationAPIRouter(entity: OrganizationAPI) extends EventSourcedEntityRouter[OrganizationState, OrganizationAPI](entity) {
  def handleCommand(commandName: String, state: OrganizationState, command: Any, context: CommandContext): EventSourcedEntity.Effect[_] = {
    commandName match {
      case "AddMembersToOrganization" =>
        entity.addMembersToOrganization(state, command.asInstanceOf[organization.ApiAddMembersToOrganization])

      case "AddOwnersToOrganization" =>
        entity.addOwnersToOrganization(state, command.asInstanceOf[organization.ApiAddOwnersToOrganization])

      case "EditOrganizationInfo" =>
        entity.editOrganizationInfo(state, command.asInstanceOf[organization.ApiEditOrganizationInfo])

      case "EstablishOrganization" =>
        entity.establishOrganization(state, command.asInstanceOf[organization.ApiEstablishOrganization])

      case _ =>
        throw new CommandHandlerNotFound(commandName)
    }
  }
  def handleEvent(state: OrganizationState, event: Any): OrganizationState = {
    event match {
      case evt: FindOrganizationsByMember =>
        entity.findOrganizationsByMember(state, evt)

      case evt: FindOrganizationsByOwner =>
        entity.findOrganizationsByOwner(state, evt)

      case evt: GetOrganizationInfo =>
        entity.getOrganizationInfo(state, evt)

      case evt: MembersAddedToOrganization =>
        entity.membersAddedToOrganization(state, evt)

      case evt: MembersRemovedFromOrganization =>
        entity.membersRemovedFromOrganization(state, evt)

      case evt: OrganizationAccountsUpdated =>
        entity.organizationAccountsUpdated(state, evt)

      case evt: OrganizationContactsUpdated =>
        entity.organizationContactsUpdated(state, evt)

      case evt: OrganizationEstablished =>
        entity.organizationEstablished(state, evt)

      case evt: OrganizationInfoUpdated =>
        entity.organizationInfoUpdated(state, evt)

      case evt: OrganizationStatusUpdated =>
        entity.organizationStatusUpdated(state, evt)

      case evt: OwnersAddedToOrganization =>
        entity.ownersAddedToOrganization(state, evt)

      case evt: OwnersRemovedFromOrganization =>
        entity.ownersRemovedFromOrganization(state, evt)

      case evt: ParentUpdated =>
        entity.parentUpdated(state, evt)

      case _ =>
        throw new EventHandlerNotFound(event.getClass)
    }
  }
}

