package com.improving.organizationcontext.organization

import com.google.protobuf.empty.Empty
import com.improving.organization
import com.improving.organizationcontext.{
  ContactList,
  Contacts,
  FindOrganizationsByMember,
  FindOrganizationsByOwner,
  GetOrganizationInfo,
  Info,
  MemberList,
  MembersAddedToOrganization,
  MembersRemovedFromOrganization,
  MetaInfo,
  OrganizationAccountsUpdated,
  OrganizationContactsUpdated,
  OrganizationEstablished,
  OrganizationInfoUpdated,
  OrganizationStatus,
  OrganizationStatusUpdated,
  OwnerList,
  OwnersAddedToOrganization,
  OwnersRemovedFromOrganization,
  Parent,
  ParentUpdated
}
import com.improving.organization.ApiAddress.PostalCode
import com.improving.organization.{
  ApiInfo,
  ApiMetaInfo,
  ApiOrganizationStatus,
  ApiParent
}
import com.improving.{Address, MemberId, OrganizationId}
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
          if org.status == OrganizationStatus.DRAFT || org.status == OrganizationStatus.ACTIVE => {
        org.copy(info =
          apiEditOrganizationInfo.newInfo.map(convertUpdateInfoToInfo(_))
        )

        val event =
          OrganizationInfoUpdated(org.oid, org.info, org.orgMeta)

        effects
          .emitEvent(event)
          .thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
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
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org) =>
        effects.error(
          s"The current organization is already established.  Please update the organization instead of establishing new one. - ${org.toString}"
        )
      case _ => {
        val event = OrganizationEstablished(
          Some(OrganizationId(apiEstablishOrganization.orgId)),
          apiEstablishOrganization.info.map(convertApiInfoToInfo(_)),
          apiEstablishOrganization.parent.map(convertApiParentToParent(_)),
          Some(
            MemberList(
              apiEstablishOrganization.members.map(member =>
                MemberId(member.memberId)
              )
            )
          ),
          Some(
            OwnerList(
              apiEstablishOrganization.owners.map(owner =>
                MemberId(owner.memberId)
              )
            )
          ),
          Some(
            ContactList(
              apiEstablishOrganization.contacts.map(contact =>
                Contacts(
                  contact.primaryContacts.map(contact =>
                    MemberId(contact.memberId)
                  ),
                  contact.billingContacts.map(contact =>
                    MemberId(contact.memberId)
                  ),
                  contact.distributionContacts.map(contact =>
                    MemberId(contact.memberId)
                  )
                )
              )
            )
          ),
          apiEstablishOrganization.meta.map(convertApiMetaInfoToMetaInfo(_))
        )

        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
    }
  }

  private def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.name,
      apiInfo.shortName,
      apiInfo.address.map(convertApiAdressToAddress(_)),
      apiInfo.isPrivate,
      apiInfo.url,
      apiInfo.logo
    )
  }

  private def convertApiParentToParent(apiParent: ApiParent): Parent = {
    Parent(
      Some(OrganizationId(apiParent.orgId))
    )
  }

  private def convertApiMetaInfoToMetaInfo(
      apiMetaInfo: ApiMetaInfo
  ): MetaInfo = {
    MetaInfo(
      apiMetaInfo.createdOn,
      apiMetaInfo.createdBy.map(member => MemberId(member.memberId)),
      apiMetaInfo.lastUpdated,
      apiMetaInfo.lastUpdatedBy.map(member => MemberId(member.memberId)),
      convertApiOrganizationStatusToOrganizationStatus(
        apiMetaInfo.currentStatus
      ),
      apiMetaInfo.children.map(child => OrganizationId(child.id))
    )
  }

  private def convertApiOrganizationStatusToOrganizationStatus(
      apiOrganizationStatus: ApiOrganizationStatus
  ): OrganizationStatus = {
    apiOrganizationStatus match {
      case ApiOrganizationStatus.DRAFT      => OrganizationStatus.DRAFT
      case ApiOrganizationStatus.ACTIVE     => OrganizationStatus.ACTIVE
      case ApiOrganizationStatus.SUSPENDED  => OrganizationStatus.SUSPENDED
      case ApiOrganizationStatus.TERMINATED => OrganizationStatus.TERMINATED
      case ApiOrganizationStatus.Unrecognized(unrecognizedValue) =>
        OrganizationStatus.Unrecognized(unrecognizedValue)
    }
  }
  override def updateParent(
      currentState: OrganizationState,
      apiUpdateParent: organization.ApiUpdateParent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if org.status != OrganizationStatus.TERMINATED && org.oid.getOrElse(
            OrganizationId("*****")
          ) == OrganizationId(apiUpdateParent.orgId) => {

        val event = ParentUpdated(
          Some(OrganizationId(apiUpdateParent.orgId)),
          apiUpdateParent.newParent.map(parent => OrganizationId(parent.id))
        )

        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def updateOrganizationStatus(
      currentState: OrganizationState,
      apiOrganizationStatusUpdated: organization.ApiOrganizationStatusUpdated
  ): EventSourcedEntity.Effect[Empty] =
    currentState.organization match {
      case Some(org)
          if org.oid == apiOrganizationStatusUpdated.orgId
            .map(oid => OrganizationId(oid.id)) => {
        val event = OrganizationStatusUpdated(
          org.oid,
          convertApiOrganizationStatusToOrganizationStatus(
            apiOrganizationStatusUpdated.newStatus
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }

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
  ): OrganizationState = {
    currentState.organization match {
      case Some(org) => {
        effects.error(
          s"The current organization is already established.  Please update the organization instead of establishing new one. - ${org.toString}"
        )
        currentState
      }
      case _ => {
        val organization = Organization(
          organizationEstablished.orgId,
          organizationEstablished.info,
          organizationEstablished.parent.flatMap(_.id),
          organizationEstablished.members.toSeq.flatMap(member =>
            member.memberId
          ),
          organizationEstablished.owners.toSeq.flatMap(owner => owner.owners),
          organizationEstablished.contacts.toSeq.flatMap(contacts =>
            contacts.contacts
          ),
          organizationEstablished.meta,
          "name is missing",
          OrganizationStatus.ACTIVE
        )

        currentState.withOrganization(organization)
      }
    }
  }

  override def organizationInfoUpdated(
      currentState: OrganizationState,
      organizationInfoUpdated: OrganizationInfoUpdated
  ): OrganizationState = {
    currentState.organization match {
      case Some(org)
          if org.status == OrganizationStatus.DRAFT || org.status == OrganizationStatus.ACTIVE => {
        org.copy(info = organizationInfoUpdated.info)
        currentState.withOrganization(org)
      }
      case _ => currentState
    }
  }

  override def organizationStatusUpdated(
      currentState: OrganizationState,
      organizationStatusUpdated: OrganizationStatusUpdated
  ): OrganizationState = currentState.organization match {
    case Some(org)
        if org.status != OrganizationStatus.TERMINATED && org.oid == organizationStatusUpdated.orgId => {
      currentState.withOrganization(
        org.copy(status = organizationStatusUpdated.newStatus)
      )
    }
    case _ => currentState
  }

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
  ): OrganizationState = currentState.organization match {
    case Some(org)
        if org.status != OrganizationStatus.TERMINATED && org.oid == parentUpdated.id => {
      currentState.withOrganization(
        org.copy(parent = parentUpdated.newParent)
      )
    }
    case _ => currentState
  }

}
