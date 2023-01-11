package app.improving.organizationcontext

import app.improving.{
  Address,
  ApiAddress,
  ApiCAPostalCode,
  ApiMemberId,
  ApiUSPostalCode
}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationByOwnerViewImpl(context: ViewContext)
    extends AbstractOrganizationByOwnerView {

  override def emptyState: ApiOrganization = ApiOrganization.defaultInstance

  override def processOrganizationEstablished(
      state: ApiOrganization,
      organizationEstablished: OrganizationEstablished
  ): UpdateEffect[ApiOrganization] = {
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        convertOrganizationEstablishedToOrganization(organizationEstablished)
      )
  }

  override def processMembersAddedToOrganization(
      state: ApiOrganization,
      membersAddedToOrganization: MembersAddedToOrganization
  ): UpdateEffect[ApiOrganization] = {
    val members = state.memberIds
    effects.updateState(
      state.copy(memberIds =
        (members ++ membersAddedToOrganization.newMembers.map(member =>
          member.id
        )).distinct
      )
    )
  }

  override def processMembersRemovedFromOrganization(
      state: ApiOrganization,
      membersRemovedFromOrganization: MembersRemovedFromOrganization
  ): UpdateEffect[ApiOrganization] = {
    val members = state.memberIds
    effects.updateState(
      state.copy(memberIds =
        members.filterNot(member =>
          membersRemovedFromOrganization.removedMembers
            .map(member => member.id)
            .contains(member)
        )
      )
    )
  }

  override def processOrganizationAccountsUpdated(
      state: ApiOrganization,
      organizationAccountsUpdated: OrganizationAccountsUpdated
  ): UpdateEffect[ApiOrganization] = {
    effects.updateState(
      state.copy(
        info = organizationAccountsUpdated.info.map(convertInfoToApiInfo),
        orgMeta =
          organizationAccountsUpdated.meta.map(convertMetaInfoToApiMetaInfo)
      )
    )
  }

  override def processOrganizationContactsUpdated(
      state: ApiOrganization,
      organizationContactsUpdated: OrganizationContactsUpdated
  ): UpdateEffect[ApiOrganization] = {
    effects.updateState(
      state.copy(contacts =
        organizationContactsUpdated.contacts.map(contact =>
          ApiContacts(
            contact.primaryContacts.map(member => ApiMemberId(member.id)),
            contact.billingContacts.map(member => ApiMemberId(member.id)),
            contact.distributionContacts.map(member => ApiMemberId(member.id))
          )
        )
      )
    )
  }

  override def processOrganizationInfoUpdated(
      state: ApiOrganization,
      organizationInfoUpdated: OrganizationInfoUpdated
  ): UpdateEffect[ApiOrganization] = {
    effects.updateState(
      state.copy(info = organizationInfoUpdated.info.map(convertInfoToApiInfo))
    )
  }

  override def processOrganizationStatusUpdated(
      state: ApiOrganization,
      organizationStatusUpdated: OrganizationStatusUpdated
  ): UpdateEffect[ApiOrganization] = {
    effects.updateState(
      state.copy(status =
        convertOrganizationStatusToApiOrganizationStatus(
          organizationStatusUpdated.newStatus
        )
      )
    )
  }

  override def processOwnersAddedToOrganization(
      state: ApiOrganization,
      ownersAddedToOrganization: OwnersAddedToOrganization
  ): UpdateEffect[ApiOrganization] = {
    val owners = state.ownerIds
    effects.updateState(
      state.copy(ownerIds =
        (owners ++ ownersAddedToOrganization.newOwners.map(member =>
          member.id
        )).distinct
      )
    )
  }

  override def processOwnersRemovedFromOrganization(
      state: ApiOrganization,
      ownersRemovedFromOrganization: OwnersRemovedFromOrganization
  ): UpdateEffect[ApiOrganization] = {
    val owners = state.ownerIds
    effects.updateState(
      state.copy(ownerIds =
        owners.filterNot(owner =>
          ownersRemovedFromOrganization.removedOwners
            .map(member => member.id)
            .contains(owner)
        )
      )
    )
  }
}
