package app.improving.organizationcontext.organization

import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{MemberId, OrganizationId}
import app.improving.organizationcontext.{
  ContactList,
  Contacts,
  FindOrganizationsByMember,
  FindOrganizationsByOwner,
  GetOrganizationInfo,
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
  ParentUpdated
}
import app.improving.organizationcontext.infrastructure.util._
import app.improving.organizationcontext.organization.{
  ApiAddMembersToOrganization,
  ApiAddOwnersToOrganization,
  ApiEditOrganizationInfo,
  ApiEstablishOrganization,
  ApiGetOrganizationInfo,
  ApiInfo,
  ApiOrganizationStatusUpdated,
  ApiRemoveMembersFromOrganization,
  ApiRemoveOwnersFromOrganization,
  ApiUpdateParent
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

  override def getOrganization(
      currentState: OrganizationState,
      apiGetOrganizationById: ApiGetOrganizationById
  ): EventSourcedEntity.Effect[ApiOrganization] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiGetOrganizationById.orgId)
          ) => {
        effects.reply(convertOrganizationToApiOrganization(org))
      }
      case _ => effects.reply(ApiOrganization.defaultInstance)
    }
  }

  override def getOrganizationInfo(
      currentState: OrganizationState,
      apiGetOrganizationInfo: ApiGetOrganizationInfo
  ): EventSourcedEntity.Effect[ApiInfo] = {
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
            val apiInfoOpt = infoOpt.map(convertInfoToApiInfo)
            apiInfoOpt.getOrElse(ApiInfo.defaultInstance)
          })
      }
      case _ => effects.reply(ApiInfo.defaultInstance)
    }
  }
  override def removeMembersFromOrganization(
      currentState: OrganizationState,
      apiRemoveMembersFromOrganization: ApiRemoveMembersFromOrganization
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
      apiRemoveOwnersFromOrganization: ApiRemoveOwnersFromOrganization
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
      apiAddMembersToOrganization: ApiAddMembersToOrganization
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
      apiAddOwnersToOrganization: ApiAddOwnersToOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiAddOwnersToOrganization.orgId)
          ) =>
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
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
              Some(timestamp),
              updatingMember,
              Some(timestamp),
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
      apiEditOrganizationInfo: ApiEditOrganizationInfo
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if org.status == OrganizationStatus.DRAFT || org.status == OrganizationStatus.ACTIVE => {
        org.copy(info =
          apiEditOrganizationInfo.newInfo.map(convertApiUpdateInfoToInfo(_))
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

  override def establishOrganization(
      currentState: OrganizationState,
      apiEstablishOrganization: ApiEstablishOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org) =>
        effects.error(
          s"The current organization is already established.  Please update the organization instead of establishing new one. - ${org.toString}"
        )
      case _ => {
        val event = OrganizationEstablished(
          Some(OrganizationId(java.util.UUID.randomUUID().toString)),
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

  override def updateParent(
      currentState: OrganizationState,
      apiUpdateParent: ApiUpdateParent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if org.status != OrganizationStatus.TERMINATED && org.oid
            == Some(OrganizationId(apiUpdateParent.orgId)) => {

        val event = ParentUpdated(
          Some(OrganizationId(apiUpdateParent.orgId)),
          apiUpdateParent.newParent.map(parent =>
            OrganizationId(parent.organizationId)
          )
        )

        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def updateOrganizationStatus(
      currentState: OrganizationState,
      apiOrganizationStatusUpdated: ApiOrganizationStatusUpdated
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
          organizationEstablished.parent.flatMap(_.orgId),
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
        if org.status != OrganizationStatus.TERMINATED && org.oid == parentUpdated.orgId => {
      currentState.withOrganization(
        org.copy(parent = parentUpdated.newParent)
      )
    }
    case _ => currentState
  }
}
