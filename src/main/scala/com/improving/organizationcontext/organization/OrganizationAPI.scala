package com.improving.organizationcontext.organization

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import com.improving.Address.PostalCode
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
  ApiAddOwnersToOrganization,
  ApiAddress,
  ApiCAPostalCode,
  ApiInfo,
  ApiMetaInfo,
  ApiOrganizationStatus,
  ApiParent,
  ApiUSPostalCode
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

  override def findOrganizationsByOwner(
      currentState: OrganizationState,
      apiMemberId: organization.ApiMemberId
  ): EventSourcedEntity.Effect[organization.ApiOrganizationListByOwner] =
    effects.error(
      "The command handler for `FindOrganizationsByOwner` is not implemented, yet"
    )

  override def findOrganizationsByMember(
      currentState: OrganizationState,
      apiMemberId: organization.ApiMemberId
  ): EventSourcedEntity.Effect[organization.ApiOrganizationListByMember] =
    effects.error(
      "The command handler for `FindOrganizationsByMember` is not implemented, yet"
    )

  override def getOrganizationInfo(
      currentState: OrganizationState,
      apiGetOrganizationInfo: organization.ApiGetOrganizationInfo
  ): EventSourcedEntity.Effect[organization.ApiInfo] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiGetOrganizationInfo.orgId)
          ) => {
        val event = GetOrganizationInfo(
          Some(OrganizationId(apiGetOrganizationInfo.orgId))
        )
        effects
          .emitEvent(event)
          .thenReply(state => {
            val infoOpt = state.organization.flatMap(_.info)
            val apiInfoOpt = infoOpt.map(info => {
              ApiInfo(
                info.name,
                info.shortName,
                info.address.map(addr => {
                  convertAddressToApiAdress(addr)
                }),
                info.isPrivate,
                info.url,
                info.logo
              )
            })
            apiInfoOpt.getOrElse(ApiInfo.defaultInstance)
          })
      }
      case _ => effects.reply(ApiInfo.defaultInstance)
    }
  }
  override def removeMembersFromOrganization(
      currentState: OrganizationState,
      apiRemoveMembersFromOrganization: organization.ApiRemoveMembersFromOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiRemoveMembersFromOrganization.orgId)
          ) => {
        val event = MembersRemovedFromOrganization(
          Some(OrganizationId(apiRemoveMembersFromOrganization.orgId)),
          apiRemoveMembersFromOrganization.membersToRemove.map(member =>
            MemberId(member.memberId)
          ),
          apiRemoveMembersFromOrganization.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def removeOwnersFromOrganization(
      currentState: OrganizationState,
      apiRemoveOwnersFromOrganization: organization.ApiRemoveOwnersFromOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiRemoveOwnersFromOrganization.orgId)
          ) =>
        val now = java.time.Instant.now()
        val updatingMember =
          apiRemoveOwnersFromOrganization.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        val event = OwnersRemovedFromOrganization(
          Some(OrganizationId(apiRemoveOwnersFromOrganization.orgId)),
          apiRemoveOwnersFromOrganization.ownersToRemove.map(member =>
            MemberId(member.memberId)
          ),
          Some(
            MetaInfo(
              Some(Timestamp.of(now.getEpochSecond, now.getNano)),
              updatingMember,
              Some(Timestamp.of(now.getEpochSecond, now.getNano)),
              updatingMember,
              org.status,
              Seq.empty[OrganizationId]
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def addMembersToOrganization(
      currentState: OrganizationState,
      apiAddMembersToOrganization: organization.ApiAddMembersToOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiAddMembersToOrganization.orgId)
          ) =>
        val now = java.time.Instant.now()
        val updatingMember =
          apiAddMembersToOrganization.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        val event = MembersAddedToOrganization(
          Some(OrganizationId(apiAddMembersToOrganization.orgId)),
          apiAddMembersToOrganization.membersToAdd.map(member =>
            MemberId(member.memberId)
          ),
          Some(
            MetaInfo(
              Some(Timestamp.of(now.getEpochSecond, now.getNano)),
              updatingMember,
              Some(Timestamp.of(now.getEpochSecond, now.getNano)),
              updatingMember,
              org.status,
              Seq.empty[OrganizationId]
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def addOwnersToOrganization(
      currentState: OrganizationState,
      apiAddOwnersToOrganization: organization.ApiAddOwnersToOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiAddOwnersToOrganization.orgId)
          ) =>
        val now = java.time.Instant.now()
        val updatingMember =
          apiAddOwnersToOrganization.updatingMember.map(member =>
            MemberId(member.memberId)
          )
        val event = OwnersAddedToOrganization(
          Some(OrganizationId(apiAddOwnersToOrganization.orgId)),
          apiAddOwnersToOrganization.ownersToAdd.map(member =>
            MemberId(member.memberId)
          ),
          Some(
            MetaInfo(
              Some(Timestamp.of(now.getEpochSecond, now.getNano)),
              updatingMember,
              Some(Timestamp.of(now.getEpochSecond, now.getNano)),
              updatingMember,
              org.status,
              Seq.empty[OrganizationId]
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

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
        case ApiAddress.PostalCode.Empty => Address.PostalCode.Empty
        case ApiAddress.PostalCode.UsPostalCode(_) =>
          Address.PostalCode.UsPostalCode(
            com.improving.USPostalCode.defaultInstance
          )
        case ApiAddress.PostalCode.CaPostalCode(_) =>
          Address.PostalCode.CaPostalCode(
            com.improving.CAPostalCode.defaultInstance
          )
      }
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
          if org.status != OrganizationStatus.TERMINATED && org.oid
            == Some(OrganizationId(apiUpdateParent.orgId)) => {

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
          if org.oid == Some(
            OrganizationId(apiOrganizationStatusUpdated.orgId)
          ) => {
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
  ): OrganizationState = currentState.organization match {
    case Some(org)
        if org.members
          .exists(id => Some(id) == findOrganizationsByMember.member) =>
      currentState
    case _ => OrganizationState.defaultInstance
  }

  override def findOrganizationsByOwner(
      currentState: OrganizationState,
      findOrganizationsByOwner: FindOrganizationsByOwner
  ): OrganizationState = currentState.organization match {
    case Some(org)
        if org.owners
          .exists(id => Some(id) == findOrganizationsByOwner.owner) =>
      currentState
    case _ => OrganizationState.defaultInstance
  }

  override def getOrganizationInfo(
      currentState: OrganizationState,
      getOrganizationInfo: GetOrganizationInfo
  ): OrganizationState = currentState.organization match {
    case Some(org) if org.oid == getOrganizationInfo.orgId =>
      currentState
    case _ => OrganizationState.defaultInstance
  }

  override def membersAddedToOrganization(
      currentState: OrganizationState,
      membersAddedToOrganization: MembersAddedToOrganization
  ): OrganizationState = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == membersAddedToOrganization.orgId =>
        currentState.withOrganization(
          org.copy(
            members =
              (org.members ++ membersAddedToOrganization.newMembers).distinct,
            orgMeta = membersAddedToOrganization.meta
          )
        )
      case _ => currentState
    }
  }

  override def membersRemovedFromOrganization(
      currentState: OrganizationState,
      membersRemovedFromOrganization: MembersRemovedFromOrganization
  ): OrganizationState = {
    val now = java.time.Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == membersRemovedFromOrganization.orgId => {
        val meta = org.orgMeta.map(meta => {
          meta.copy(
            lastUpdated = Some(timestamp),
            lastUpdatedBy = membersRemovedFromOrganization.updatingMember
          )
        })
        currentState.withOrganization(
          org.copy(
            members = org.members.filterNot(
              membersRemovedFromOrganization.removedMembers.contains(_)
            ),
            orgMeta = meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def organizationAccountsUpdated(
      currentState: OrganizationState,
      organizationAccountsUpdated: OrganizationAccountsUpdated
  ): OrganizationState = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == organizationAccountsUpdated.orgId => {
        currentState.withOrganization(
          org.copy(
            info = organizationAccountsUpdated.info,
            orgMeta = organizationAccountsUpdated.meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def organizationContactsUpdated(
      currentState: OrganizationState,
      organizationContactsUpdated: OrganizationContactsUpdated
  ): OrganizationState = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == organizationContactsUpdated.orgId => {
        currentState.withOrganization(
          org.copy(
            contacts = organizationContactsUpdated.contacts
          )
        )
      }
      case _ => currentState
    }
  }

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
          organizationEstablished.info
            .map(_.name)
            .getOrElse("Name is not available."),
          OrganizationStatus.DRAFT
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
  ): OrganizationState = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == ownersAddedToOrganization.orgId => {
        currentState.withOrganization(
          org.copy(
            owners =
              (org.owners ++ ownersAddedToOrganization.newOwners).distinct,
            orgMeta = ownersAddedToOrganization.meta
          )
        )
      }
      case _ => currentState
    }
  }

  override def ownersRemovedFromOrganization(
      currentState: OrganizationState,
      ownersRemovedFromOrganization: OwnersRemovedFromOrganization
  ): OrganizationState = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == ownersRemovedFromOrganization.orgId => {
        currentState.withOrganization(
          org.copy(
            owners = org.owners.filterNot(
              ownersRemovedFromOrganization.removedOwners.contains(_)
            ),
            orgMeta = ownersRemovedFromOrganization.meta
          )
        )
      }
      case _ => currentState
    }
  }

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
