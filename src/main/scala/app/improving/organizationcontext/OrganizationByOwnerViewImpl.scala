package app.improving.organizationcontext

import app.improving.{
  Address,
  ApiAddress,
  ApiCAPostalCode,
  ApiMemberId,
  ApiUSPostalCode
}
import app.improving.organization.{
  ApiContacts,
  ApiInfo,
  ApiMetaInfo,
  ApiOrganization,
  ApiOrganizationId,
  ApiOrganizationStatus
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

  private def convertOrganizationEstablishedToOrganization(
      organizationEstablished: OrganizationEstablished
  ): ApiOrganization = {
    ApiOrganization(
      organizationEstablished.orgId.map(org => ApiOrganizationId(org.id)),
      organizationEstablished.info.map(convertInfoToApiInfo),
      organizationEstablished.parent.flatMap(
        _.id.map(org => ApiOrganizationId(org.id))
      ),
      organizationEstablished.members.toList
        .flatMap(_.memberId.map(member => member.id)),
      organizationEstablished.owners.toList
        .flatMap(_.owners.map(owner => owner.id)),
      organizationEstablished.contacts.toList.flatMap(
        _.contacts.map(contact =>
          ApiContacts(
            contact.primaryContacts.map(ct => ApiMemberId(ct.id)),
            contact.billingContacts.map(ct => ApiMemberId(ct.id)),
            contact.distributionContacts.map(ct => ApiMemberId(ct.id))
          )
        )
      ),
      organizationEstablished.meta.map(convertMetaInfoToApiMetaInfo),
      organizationEstablished.info
        .map(_.name)
        .getOrElse("Name is not provided."),
      ApiOrganizationStatus.DRAFT
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

  private def convertAddressToApiAdress(address: Address): ApiAddress = {
    ApiAddress(
      address.line1,
      address.line2,
      address.city,
      address.stateProvince,
      address.country,
      address.postalCode match {
        case Address.PostalCode.UsPostalCode(_) =>
          ApiAddress.PostalCode.UsPostalCode(ApiUSPostalCode.defaultInstance)
        case Address.PostalCode.CaPostalCode(_) =>
          ApiAddress.PostalCode.CaPostalCode(
            ApiCAPostalCode.defaultInstance
          )
      }
    )
  }

  private def convertInfoToApiInfo(info: Info): ApiInfo = {
    ApiInfo(
      info.name,
      info.shortName,
      info.address.map(convertAddressToApiAdress(_)),
      info.isPrivate,
      info.url,
      info.logo
    )
  }

  private def convertMetaInfoToApiMetaInfo(
      metaInfo: MetaInfo
  ): ApiMetaInfo = {
    ApiMetaInfo(
      metaInfo.createdOn,
      metaInfo.createdBy.map(member => ApiMemberId(member.id)),
      metaInfo.lastUpdated,
      metaInfo.lastUpdatedBy.map(member => ApiMemberId(member.id)),
      convertOrganizationStatusToApiOrganizationStatus(
        metaInfo.currentStatus
      ),
      metaInfo.children.map(child => ApiOrganizationId(child.id))
    )
  }

  private def convertOrganizationStatusToApiOrganizationStatus(
      organizationStatus: OrganizationStatus
  ): ApiOrganizationStatus = {
    organizationStatus match {
      case OrganizationStatus.DRAFT      => ApiOrganizationStatus.DRAFT
      case OrganizationStatus.ACTIVE     => ApiOrganizationStatus.ACTIVE
      case OrganizationStatus.SUSPENDED  => ApiOrganizationStatus.SUSPENDED
      case OrganizationStatus.TERMINATED => ApiOrganizationStatus.TERMINATED
      case OrganizationStatus.Unrecognized(unrecognizedValue) =>
        ApiOrganizationStatus.Unrecognized(unrecognizedValue)
    }
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
