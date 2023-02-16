package app.improving.organizationcontext

import app.improving.ApiMemberId
import app.improving.organizationcontext.infrastructure.util._
import app.improving.organizationcontext.organization.{
  ApiContacts,
  ApiOrganization
}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllOrganizationsViewImpl(context: ViewContext)
    extends AbstractAllOrganizationsView {

  override def emptyState: ApiOrganization = ApiOrganization.defaultInstance

  private val log = LoggerFactory.getLogger(this.getClass)

  override def processOrganizationEstablished(
      state: ApiOrganization,
      organizationEstablished: OrganizationEstablished
  ): UpdateEffect[ApiOrganization] = {
    if (state != emptyState) {

      log.info(
        s"AllOrganizationsViewImpl in processOrganizationEstablished - state already existed"
      )

      effects.ignore()
    } else {

      log.info(
        s"AllOrganizationsViewImpl in processOrganizationEstablished - organizationEstablished ${organizationEstablished}"
      )

      effects.updateState(
        convertOrganizationEstablishedToApiOrganization(organizationEstablished)
      )
    }
  }

  override def processMembersAddedToOrganization(
      state: ApiOrganization,
      membersAddedToOrganization: MembersAddedToOrganization
  ): UpdateEffect[ApiOrganization] = {

    log.info(
      s"AllOrganizationsViewImpl in processMembersAddedToOrganization - membersAddedToOrganization ${membersAddedToOrganization}"
    )

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

    log.info(
      s"AllOrganizationsViewImpl in processMembersRemovedFromOrganization - membersRemovedFromOrganization ${membersRemovedFromOrganization}"
    )

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

    log.info(
      s"AllOrganizationsViewImpl in processOrganizationAccountsUpdated - organizationAccountsUpdated ${organizationAccountsUpdated}"
    )

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

    log.info(
      s"AllOrganizationsViewImpl in processOrganizationContactsUpdated - organizationContactsUpdated ${organizationContactsUpdated}"
    )

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

    log.info(
      s"AllOrganizationsViewImpl in processOrganizationInfoUpdated - organizationInfoUpdated ${organizationInfoUpdated}"
    )

    effects.updateState(
      state.copy(info = organizationInfoUpdated.info.map(convertInfoToApiInfo))
    )
  }

  override def processOrganizationStatusUpdated(
      state: ApiOrganization,
      organizationStatusUpdated: OrganizationStatusUpdated
  ): UpdateEffect[ApiOrganization] = {

    log.info(
      s"AllOrganizationsViewImpl in processOrganizationStatusUpdated - organizationStatusUpdated ${organizationStatusUpdated}"
    )

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

    log.info(
      s"AllOrganizationsViewImpl in processOwnersAddedToOrganization - ownersAddedToOrganization ${ownersAddedToOrganization}"
    )

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

    log.info(
      s"AllOrganizationsViewImpl in processOwnersRemovedFromOrganization - ownersRemovedFromOrganization ${ownersRemovedFromOrganization}"
    )

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
