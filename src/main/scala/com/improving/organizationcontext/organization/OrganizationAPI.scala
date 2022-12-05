package com.improving.organizationcontext.organization

import com.google.protobuf.empty.Empty
import com.improving.organization.ApiAddress.PostalCode
import com.improving.{Address, organization}
import com.improving.organizationcontext.{
  FindOrganizationsByMember,
  FindOrganizationsByOwner,
  GetOrganizationInfo,
  Info,
  MembersAddedToOrganization,
  MembersRemovedFromOrganization,
  OrganizationAccountsUpdated,
  OrganizationContactsUpdated,
  OrganizationEstablished,
  OrganizationInfoUpdated,
  OrganizationStatusUpdated,
  OwnersAddedToOrganization,
  OwnersRemovedFromOrganization,
  ParentUpdated
}
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationAPI(context: EventSourcedEntityContext)
    extends AbstractOrganizationAPI {
  override def emptyState: OrganizationState = OrganizationState.defaultInstance

  override def addMembersToOrganization(
      currentState: OrganizationState,
      apiAddMembersToOrganization: organization.ApiAddMembersToOrganization
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `AddMembersToOrganization` is not implemented, yet"
    )

  override def addOwnersToOrganization(
      currentState: OrganizationState,
      apiAddOwnersToOrganization: organization.ApiAddOwnersToOrganization
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `AddOwnersToOrganization` is not implemented, yet"
    )

  override def editOrganizationInfo(
      currentState: OrganizationState,
      apiEditOrganizationInfo: organization.ApiEditOrganizationInfo
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if org.status == OrganizationStatus.Draft || org.status == OrganizationStatus.Active => {
        org.copy(info =
          apiEditOrganizationInfo.newInfo.map(convertUpdateInfoToInfo(_))
        )
        currentState.withOrganization(org)
        ()
      }
      case None => ()
    }
    effects.reply(Empty)
  }

  private def convertUpdateInfoToInfo(
      updateInfo: organization.ApiUpdateInfo
  ): Info = {
    Info(
      updateInfo.name,
      updateInfo.shortName,
      updateInfo.address.map(convertApiAdressToAddress(_)),
      updateInfo.isPrivate,
      updateInfo.url,
      updateInfo.logo
    )
  }

  private def convertApiAdressToAddress(
      apiAddress: organization.ApiAddress
  ): Address = {
    Address(
      apiAddress.line1,
      apiAddress.line2,
      apiAddress.city,
      apiAddress.stateProvince,
      apiAddress.country,
      apiAddress.postalCode match {
        case PostalCode.Empty => Address.PostalCode.Empty
        case PostalCode.UsPostalCode(_) =>
          Address.PostalCode.UsPostalCode(
            com.improving.USPostalCode.defaultInstance
          )
        case PostalCode.CaPostalCode(_) =>
          Address.PostalCode.CaPostalCode(
            com.improving.CAPostalCode.defaultInstance
          )
      }
    )
  }

  override def establishOrganization(
      currentState: OrganizationState,
      apiEstablishOrganization: organization.ApiEstablishOrganization
  ): EventSourcedEntity.Effect[Empty] =
    effects.error(
      "The command handler for `EstablishOrganization` is not implemented, yet"
    )

  override def findOrganizationsByMember(
      currentState: OrganizationState,
      findOrganizationsByMember: FindOrganizationsByMember
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `FindOrganizationsByMember` is not implemented, yet"
    )

  override def findOrganizationsByOwner(
      currentState: OrganizationState,
      findOrganizationsByOwner: FindOrganizationsByOwner
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `FindOrganizationsByOwner` is not implemented, yet"
    )

  override def getOrganizationInfo(
      currentState: OrganizationState,
      getOrganizationInfo: GetOrganizationInfo
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `GetOrganizationInfo` is not implemented, yet"
    )

  override def membersAddedToOrganization(
      currentState: OrganizationState,
      membersAddedToOrganization: MembersAddedToOrganization
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `MembersAddedToOrganization` is not implemented, yet"
    )

  override def membersRemovedFromOrganization(
      currentState: OrganizationState,
      membersRemovedFromOrganization: MembersRemovedFromOrganization
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `MembersRemovedFromOrganization` is not implemented, yet"
    )

  override def organizationAccountsUpdated(
      currentState: OrganizationState,
      organizationAccountsUpdated: OrganizationAccountsUpdated
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `OrganizationAccountsUpdated` is not implemented, yet"
    )

  override def organizationContactsUpdated(
      currentState: OrganizationState,
      organizationContactsUpdated: OrganizationContactsUpdated
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `OrganizationContactsUpdated` is not implemented, yet"
    )

  override def organizationEstablished(
      currentState: OrganizationState,
      organizationEstablished: OrganizationEstablished
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `OrganizationEstablished` is not implemented, yet"
    )

  override def organizationInfoUpdated(
      currentState: OrganizationState,
      organizationInfoUpdated: OrganizationInfoUpdated
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `OrganizationInfoUpdated` is not implemented, yet"
    )

  override def organizationStatusUpdated(
      currentState: OrganizationState,
      organizationStatusUpdated: OrganizationStatusUpdated
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `OrganizationStatusUpdated` is not implemented, yet"
    )

  override def ownersAddedToOrganization(
      currentState: OrganizationState,
      ownersAddedToOrganization: OwnersAddedToOrganization
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `OwnersAddedToOrganization` is not implemented, yet"
    )

  override def ownersRemovedFromOrganization(
      currentState: OrganizationState,
      ownersRemovedFromOrganization: OwnersRemovedFromOrganization
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `OwnersRemovedFromOrganization` is not implemented, yet"
    )

  override def parentUpdated(
      currentState: OrganizationState,
      parentUpdated: ParentUpdated
  ): OrganizationState =
    throw new RuntimeException(
      "The event handler for `ParentUpdated` is not implemented, yet"
    )

}
