package app.improving.organizationcontext.organization

import app.improving.organizationcontext.infrastructure.util._
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{ApiMemberId, ApiOrganizationId, MemberId, OrganizationId}
import app.improving.organizationcontext.{ContactList, Contacts, FindOrganizationsByMember, FindOrganizationsByOwner, GetOrganizationInfo, MemberList, MembersAddedToOrganization, MembersRemovedFromOrganization, OrganizationAccountsUpdated, OrganizationContactsUpdated, OrganizationEstablished, OrganizationInfoUpdated, OrganizationStatus, OrganizationStatusUpdated, OwnerList, OwnersAddedToOrganization, OwnersRemovedFromOrganization, ParentUpdated}
import io.grpc.Status
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationAPI(context: EventSourcedEntityContext)
    extends AbstractOrganizationAPI {
  override def emptyState: OrganizationState = OrganizationState.defaultInstance


  //TODO: there's an UpdateOrganizationContacts listed in the riddl but not present

  override def getOrganization(
      currentState: OrganizationState,
      apiGetOrganizationById: ApiGetOrganizationById
  ): EventSourcedEntity.Effect[ApiOrganization] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.ORGANIZATION_STATUS_TERMINATED
          ) && org.oid == Some(
            OrganizationId(apiGetOrganizationById.orgId)
          ) => {
        effects.reply(convertOrganizationToApiOrganization(org))
      }
      case _ =>
        effects.error(
          s"OrganizationBy ID ${apiGetOrganizationById.orgId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
    }
  }

  override def getOrganizationInfo(
      currentState: OrganizationState,
      apiGetOrganizationInfo: ApiGetOrganizationInfo
  ): EventSourcedEntity.Effect[ApiInfo] = {
    currentState.organization match {
      case Some(org)
          if currentState.organization.map(_.status) != Some(
            OrganizationStatus.ORGANIZATION_STATUS_TERMINATED
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
      case _ =>
        effects.error(
          s"OrganizationInfo By ID ${apiGetOrganizationInfo.orgId} IS NOT FOUND.",
          Status.Code.NOT_FOUND
        )
    }
  }

  //TODO why does MembersAddedToOrganization have a MetaInfo field, while MembersRemovedFromOrganization just have the updating member?
  override def removeMembersFromOrganization(
      currentState: OrganizationState,
      apiRemoveMembersFromOrganization: ApiRemoveMembersFromOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid.contains(OrganizationId(apiRemoveMembersFromOrganization.orgId)) =>

        simpleListUpdate(apiRemoveMembersFromOrganization.updatingMember, apiRemoveMembersFromOrganization.membersToRemove, "MembersToRemove"){
          (updatingApiMember, membersToRemove) => {
            val updatingMember = convertApiMemberIdToMemberId(updatingApiMember)

            val event = MembersRemovedFromOrganization(
              orgId = Some(OrganizationId(apiRemoveMembersFromOrganization.orgId)),
              removedMembers = membersToRemove.map(convertApiMemberIdToMemberId),
              updatingMember = Some(updatingMember)
            )
            effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
          }

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
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid.contains(OrganizationId(apiRemoveOwnersFromOrganization.orgId)) =>

        simpleListUpdate(apiRemoveOwnersFromOrganization.updatingMember, apiRemoveOwnersFromOrganization.ownersToRemove, "OwnersToRemove") {
          (updatingMember, ownersToRemove) => {
            val event = OwnersRemovedFromOrganization(
              orgId = Some(OrganizationId(apiRemoveOwnersFromOrganization.orgId)),
              removedOwners = ownersToRemove.map(convertApiMemberIdToMemberId),
              meta = org.orgMeta.map(_.copy(lastUpdated = Some(nowTs), lastUpdatedBy = Some(convertApiMemberIdToMemberId(updatingMember))))
            )
            effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
          }
        }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def addMembersToOrganization(
      currentState: OrganizationState,
      apiAddMembersToOrganization: ApiAddMembersToOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid.contains(OrganizationId(apiAddMembersToOrganization.orgId)) =>

        simpleListUpdate(apiAddMembersToOrganization.updatingMember,apiAddMembersToOrganization.membersToAdd, "MembersToAdd"){
          (apiUpdatingMember, apiMembersToAdd) => {
            val updatingMember = convertApiMemberIdToMemberId(apiUpdatingMember)
            val newMeta = org.orgMeta.map(_.copy(lastUpdated = Some(nowTs), lastUpdatedBy = Some(updatingMember)))

            val event = MembersAddedToOrganization(
              orgId = Some(OrganizationId(apiAddMembersToOrganization.orgId)),
              newMembers = apiMembersToAdd.map(convertApiMemberIdToMemberId),
              meta = newMeta
            )
            effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
          }
        }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def addOwnersToOrganization(
      currentState: OrganizationState,
      apiAddOwnersToOrganization: ApiAddOwnersToOrganization
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid.contains(OrganizationId(apiAddOwnersToOrganization.orgId)) =>

        simpleListUpdate(apiAddOwnersToOrganization.updatingMember, apiAddOwnersToOrganization.ownersToAdd, "OwnersToAdd"){
          (updatingMember, ownersToAdd) => {
            val event = OwnersAddedToOrganization(
              orgId = Some(OrganizationId(apiAddOwnersToOrganization.orgId)),
              newOwners = ownersToAdd.map(convertApiMemberIdToMemberId),
              meta = org.orgMeta.map(_.copy(lastUpdated = Some(nowTs), lastUpdatedBy = Some(convertApiMemberIdToMemberId(updatingMember))))
            )

            effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
          }
        }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def editOrganizationInfo(
      currentState: OrganizationState,
      apiEditOrganizationInfo: ApiEditOrganizationInfo
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org) if org.status == OrganizationStatus.ORGANIZATION_STATUS_DRAFT || org.status == OrganizationStatus.ORGANIZATION_STATUS_ACTIVE  =>
        val maybeUpdateInfo = apiEditOrganizationInfo.newInfo
        val maybeMember = apiEditOrganizationInfo.editingMember
        (maybeUpdateInfo, maybeMember) match {
          case (Some(updateInfo), Some(editingMember)) =>
            val newInfo = convertApiUpdateInfoToInfo(updateInfo, org.info)

            val newMeta = org.orgMeta.map(meta =>
            meta.copy(
              lastUpdated = Some(nowTs),
              lastUpdatedBy = Some(convertApiMemberIdToMemberId(editingMember))
            ))

            val event =
              OrganizationInfoUpdated(org.oid, newInfo, newMeta)

            effects
              .emitEvent(event)
              .thenReply(_ => Empty.defaultInstance)
          case _ => effects.error(getMissingFieldsError(Map("NewInfo" -> apiEditOrganizationInfo.newInfo, "EditingMember" -> maybeMember)))
        }


      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  //TODO in the incoming command, members & owners aren't marked as required in the riddl (see organizationTypeDefinitions/Info). But the draft state requires at least one member and at least one owner. Should we just use the establishing member if members & owners is empty? Or verify their non-emptiness & mark them as required?
  override def establishOrganization(
      currentState: OrganizationState,
      apiEstablishOrganization: ApiEstablishOrganization
  ): EventSourcedEntity.Effect[ApiOrganizationId] = {
    currentState.organization match {
      case Some(org) =>
        effects.error(
          s"The current organization is already established.  Please update the organization instead of establishing new one. - ${org.toString}"
        )
      case _ =>

        val maybeInfo = apiEstablishOrganization.info
        val maybeMeta = apiEstablishOrganization.meta
        val maybeMember = apiEstablishOrganization.establishingMember

        (maybeInfo, maybeMeta, maybeMember) match {
          case (Some(apiInfo), Some(metaInfo), Some(establishingMember)) if verifyInfo(apiInfo) && verifyMeta(metaInfo) =>
            val orgId = apiEstablishOrganization.orgId
            val now = java.time.Instant.now()
            val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
            val event = OrganizationEstablished(
              orgId = Some(OrganizationId(orgId)),
              info = Some(convertApiInfoToInfo(apiInfo)),
              parent = apiEstablishOrganization.parent.map(convertApiParentToParent),
              members = Some(
                MemberList(
                  apiEstablishOrganization.members.map(member =>
                    MemberId(member.memberId)
                  )
                )
              ),
              owners = Some(
                OwnerList(
                  apiEstablishOrganization.owners.map(owner =>
                    MemberId(owner.memberId)
                  )
                )
              ),
              contacts = Some(
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
              //TODO Do we actually need the meta info coming in? The previous code was doing a map so I've run with that, but wanted to check
              meta = Some(
                convertApiMetaInfoToMetaInfo(
                  metaInfo.copy(
                    createdOn = Some(timestamp),
                    createdBy = Some(establishingMember),
                    lastUpdated = Some(timestamp),
                    lastUpdatedBy = Some(establishingMember),
                    currentStatus =
                      ApiOrganizationStatus.API_ORGANIZATION_STATUS_DRAFT
                  )
                )
              )
            )

            effects.emitEvent(event).thenReply(_ => ApiOrganizationId(orgId))
          case (_, _, _) =>
              effects.error(getMissingFieldsError(Map("Info" -> maybeInfo, "Meta" -> maybeMeta, "Members" -> maybeMember) ++ getInfoFields(maybeInfo) ++ getMetaFields(maybeMeta)))
        }
    }
  }

  //TODO we can't provide a lastUpdatedBy for the meta; we don't have a member id. Should it be added to the ApiUpdateParent message? Alternately, should it be marked as optional for MetaInfo?
  override def updateParent(
      currentState: OrganizationState,
      apiUpdateParent: ApiUpdateParent
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.organization match {
      case Some(org)
          if org.status != OrganizationStatus.ORGANIZATION_STATUS_TERMINATED && org.oid.contains(OrganizationId(apiUpdateParent.orgId)) => {

        apiUpdateParent.newParent match {
          case Some(parent) =>
            val event = ParentUpdated(
              orgId = Some(OrganizationId(apiUpdateParent.orgId)),
              newParent = Some(OrganizationId(parent.organizationId)),
              meta = org.orgMeta.map(_.copy(lastUpdated = Some(nowTs))) //TODO since we don't have a last updated by, the last updated by is going to be incorrect
            )

            effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
          case _ => effects.error(getMissingFieldsError(Map("Parent" -> apiUpdateParent.newParent)))
        }

      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  //TODO: in riddl, the UpdateOrganizationStatus has an updatingMember, but the OrganizationStatusUpdated doesn't have an updatingMember or a MetaInfo. Should we add one there + here?
  override def updateOrganizationStatus(
      currentState: OrganizationState,
      apiOrganizationStatusUpdated: ApiOrganizationStatusUpdated
  ): EventSourcedEntity.Effect[Empty] =
    currentState.organization match {
      case Some(org)
          if org.oid.contains(OrganizationId(apiOrganizationStatusUpdated.orgId)) => {

        val maybeUpdatingMember = apiOrganizationStatusUpdated.updatingMember

        (maybeUpdatingMember) match {
          case (Some(_)) =>
            val event = OrganizationStatusUpdated(
              orgId = org.oid,
              newStatus = convertApiOrganizationStatusToOrganizationStatus(
                apiOrganizationStatusUpdated.newStatus
              )
            )
            effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
          case _ => effects.error(getMissingFieldsError(Map("UpdatingMember" -> maybeUpdatingMember)))
        }


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
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid == membersAddedToOrganization.orgId =>
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
    currentState.organization match {
      case Some(org)
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid == membersRemovedFromOrganization.orgId => {

        val meta = org.orgMeta.map(meta => {
          meta.copy(
            lastUpdated = Some(nowTs),
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
            OrganizationStatus.ORGANIZATION_STATUS_TERMINATED
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
            OrganizationStatus.ORGANIZATION_STATUS_TERMINATED
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
          oid = organizationEstablished.orgId,
          info = organizationEstablished.info,
          parent = organizationEstablished.parent.flatMap(_.orgId),
          members = organizationEstablished.members.toSeq.flatMap(member =>
            member.memberId
          ),
          owners = organizationEstablished.owners.toSeq.flatMap(owner => owner.owners),
          contacts = organizationEstablished.contacts.toSeq.flatMap(contacts =>
            contacts.contacts
          ),
          orgMeta = organizationEstablished.meta,
          name = organizationEstablished.info
            .map(_.name)
            .getOrElse("Name is not available."),
          status = OrganizationStatus.ORGANIZATION_STATUS_DRAFT
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
          if org.status == OrganizationStatus.ORGANIZATION_STATUS_DRAFT || org.status == OrganizationStatus.ORGANIZATION_STATUS_ACTIVE => {
        org.copy(info = organizationInfoUpdated.info, orgMeta = organizationInfoUpdated.meta)
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
        if org.status != OrganizationStatus.ORGANIZATION_STATUS_TERMINATED && org.oid == organizationStatusUpdated.orgId => {
      val timestamp = nowTs
      currentState.withOrganization(
        org.copy(
          orgMeta = org.orgMeta.map(
            _.copy(
              currentStatus = organizationStatusUpdated.newStatus,
              lastUpdated = Some(timestamp)
            )
          ),
          status = organizationStatusUpdated.newStatus
        )
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
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid == ownersAddedToOrganization.orgId => {
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
          if !currentState.organization.map(_.status).contains(OrganizationStatus.ORGANIZATION_STATUS_TERMINATED) && org.oid == ownersRemovedFromOrganization.orgId => {
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
        if org.status != OrganizationStatus.ORGANIZATION_STATUS_TERMINATED && org.oid == parentUpdated.orgId => {
      currentState.withOrganization(
        org.copy(parent = parentUpdated.newParent, orgMeta = parentUpdated.meta)
      )
    }
    case _ => currentState
  }

  private def getMissingFieldsError(
      requiredFields: Map[String, Option[Any]]
  ): String = {
    val missingFields = requiredFields.filter(_._2.isDefined)
    s"Message is missing the following fields: ${missingFields.keySet.mkString(", ")}"
  }


  def verifyInfo(apiInfo: ApiInfo): Boolean = {
    apiInfo.tenant.isDefined && apiInfo.name.nonEmpty
  }

  private def getInfoFields(maybeApiInfo: Option[ApiInfo]): Map[String, Option[Any]] = {
    maybeApiInfo.map(apiInfo => Map("Info.Tenant" -> apiInfo.tenant, "Info.Name" -> Some(apiInfo.name).filter(_.nonEmpty)))
      .getOrElse(Map.empty[String, Option[Any]])
  }

  private def getMetaFields(maybeApiMetaInfo: Option[ApiMetaInfo]): Map[String, Option[Any]] = {
    maybeApiMetaInfo.map(apiMetaInfo => Map("MetaInfo.CreatedOn" -> apiMetaInfo.createdOn, "MetaInfo.CreatedBy" -> apiMetaInfo.createdBy,
    "MetaInfo.LastUpdated" -> apiMetaInfo.lastUpdated, "MetaInfo.LastUpdatedBy" -> apiMetaInfo.lastUpdatedBy))
      .getOrElse(Map.empty[String, Option[Any]])
  }

  private def verifyMeta(apiMetaInfo: ApiMetaInfo): Boolean = {
    apiMetaInfo.createdOn.isDefined && apiMetaInfo.createdBy.isDefined && apiMetaInfo.lastUpdated.isDefined && apiMetaInfo.lastUpdatedBy.isDefined
  }

  private def nowTs: Timestamp = {
    val now = java.time.Instant.now()
    Timestamp.of(now.getEpochSecond, now.getNano)
  }

  private def simpleListUpdate(maybeUpdatingMember: Option[ApiMemberId], itemsToAdd: Seq[ApiMemberId], label: String)(block: (ApiMemberId, Seq[ApiMemberId]) => EventSourcedEntity.Effect[Empty]): EventSourcedEntity.Effect[Empty] = {
    (maybeUpdatingMember, itemsToAdd.headOption) match {
      case (Some(updatingMember), Some(_)) => block(updatingMember, itemsToAdd)
      case _ => effects.error(getMissingFieldsError(Map("UpdatingMember" -> maybeUpdatingMember, label -> itemsToAdd.headOption)))
    }
  }

}
