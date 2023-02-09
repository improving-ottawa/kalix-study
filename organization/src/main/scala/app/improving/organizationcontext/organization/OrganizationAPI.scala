package app.improving.organizationcontext.organization

import app.improving.organizationcontext.OrganizationStatus.{ORGANIZATION_STATUS_DRAFT, ORGANIZATION_STATUS_TERMINATED}
import app.improving.organizationcontext.infrastructure.util._
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import app.improving.{ApiMemberId, ApiOrganizationId, MemberId, OrganizationId}
import app.improving.organizationcontext.{ContactList, Contacts, FindOrganizationsByMember, FindOrganizationsByOwner, GetOrganizationInfo, MemberList, MembersAddedToOrganization, MembersRemovedFromOrganization, MetaInfo, OrganizationAccountsUpdated, OrganizationContactsUpdated, OrganizationEstablished, OrganizationInfoUpdated, OrganizationStatus, OrganizationStatusUpdated, OwnerList, OwnersAddedToOrganization, OwnersRemovedFromOrganization, ParentUpdated}
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
              meta = org.orgMeta.map(_.copy(lastUpdated = Some(nowTs), lastUpdatedBy = Some(updatingMember)))
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
            val newInfo = buildNewInfoFromApiUpdateInfo(updateInfo, org.info)

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
        val maybeMember = apiEstablishOrganization.establishingMember

        (maybeInfo, maybeMember) match {
          case (Some(apiInfo), Some(apiEstablishingMember)) if verifyInfo(apiInfo) =>
            val orgId = apiEstablishOrganization.orgId
            val establishingMember = convertApiMemberIdToMemberId(apiEstablishingMember)
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
              meta = Some(MetaInfo(
                createdOn = Some(nowTs),
                createdBy = Some(establishingMember),
                lastUpdated = Some(nowTs),
                lastUpdatedBy = Some(establishingMember),
                currentStatus = OrganizationStatus.ORGANIZATION_STATUS_DRAFT
              )
            ))

            effects.emitEvent(event).thenReply(_ => ApiOrganizationId(orgId))
          case (_,  _) =>
              effects.error(getMissingFieldsError(Map("Info" -> maybeInfo, "Members" -> maybeMember) ++ getInfoFields(maybeInfo)))
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

        (apiUpdateParent.newParent, apiUpdateParent.updatingMember) match {
          case (Some(parent), Some(updatingMember)) =>
            val event = ParentUpdated(
              orgId = Some(OrganizationId(apiUpdateParent.orgId)),
              newParent = Some(OrganizationId(parent.organizationId)),
              meta = org.orgMeta.map(_.copy(lastUpdated = Some(nowTs), lastUpdatedBy = Some(convertApiMemberIdToMemberId(updatingMember))))
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
          case (Some(apiUpdatingMember)) =>

            val newStatus = convertApiOrganizationStatusToOrganizationStatus(
              apiOrganizationStatusUpdated.newStatus
            )

            val statusDirectionOkayOrError: Either[String, Unit] = verifyStatusDirection(org.status, newStatus)
            val statusChangeOkayOrError: Either[String, Unit] = if(org.status == newStatus) Right(()) else verifyStatusStateBeforeStatusChange(org.status, currentState)

            statusDirectionOkayOrError.orElse(statusChangeOkayOrError) match {
              case Left(error) => effects.error(error)
              case Right(_) =>
                val event = OrganizationStatusUpdated(
                  orgId = org.oid,
                  newStatus = newStatus,
                  meta = org.orgMeta.map(_.copy(lastUpdated = Some(nowTs), lastUpdatedBy = Some(convertApiMemberIdToMemberId(apiUpdatingMember))))
                )
                effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
            }


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

        currentState.withOrganization(
          org.copy(
            members = org.members.filterNot(
              membersRemovedFromOrganization.removedMembers.contains(_)
            ),
            orgMeta = membersRemovedFromOrganization.meta
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

      currentState.withOrganization(
        org.copy(
          orgMeta = org.orgMeta.map(
            _.copy(
              currentStatus = organizationStatusUpdated.newStatus,
              lastUpdated = organizationStatusUpdated.meta.flatMap(_.lastUpdated),
              lastUpdatedBy = organizationStatusUpdated.meta.flatMap(_.lastUpdatedBy)
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
      requiredFields: Map[String, Option[Any]],
      container: String = "Message"
  ): String = {
    val missingFields = requiredFields.filter(_._2.isEmpty)
    s"$container is missing the following fields: ${missingFields.keySet.mkString(", ")}"
  }


  def verifyInfo(apiInfo: ApiInfo): Boolean = {
    apiInfo.tenant.isDefined && apiInfo.name.nonEmpty
  }

  private def getInfoFields(maybeApiInfo: Option[ApiInfo]): Map[String, Option[Any]] = {
    maybeApiInfo.map(apiInfo => Map("Info.Tenant" -> apiInfo.tenant, "Info.Name" -> Some(apiInfo.name).filter(_.nonEmpty)))
      .getOrElse(Map.empty[String, Option[Any]])
  }

  private def getMetaFields(maybeMetaInfo: Option[MetaInfo]): Map[String, Option[Any]] = {
    maybeMetaInfo.map(meta => Map("Meta.CreatedOn" -> meta.createdOn, "Meta.CreatedBy" -> meta.createdBy, "Meta.LastUpdated" -> meta.lastUpdated, "Meta.LastUpdatedBy" -> meta.lastUpdatedBy)
    .filter(_._2.isEmpty))
      .getOrElse(Map.empty[String, Option[Any]])
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

  private def verifyStatusDirection(oldStatus: OrganizationStatus, newStatus: OrganizationStatus): Either[String, Unit] = {
    (oldStatus, newStatus) match {
      case (ORGANIZATION_STATUS_TERMINATED, _) if newStatus != ORGANIZATION_STATUS_TERMINATED => Left("You cannot exit Terminated state.")
      case (_, ORGANIZATION_STATUS_DRAFT) if oldStatus != ORGANIZATION_STATUS_DRAFT => Left("You cannot return to Draft state once Draft state has been exited.")
      case (_, _) => Right(())
    }
  }

  private def verifyStatusStateBeforeStatusChange(oldStatus: OrganizationStatus, currentState: OrganizationState): Either[String, Unit] = {
    val missingFields: Map[String, Option[Any]] = oldStatus match {
      case ORGANIZATION_STATUS_TERMINATED => Map.empty[String, Option[Any]]
      case _ => currentState.organization.map(verifyState).getOrElse(Map.empty[String, Option[Any]]) //all three of them have the same required fields
    }
    if(missingFields.isEmpty) Right(())
    else Left(missingFields.mkString(s"Cannot leave state $oldStatus without required fields. The state is missing ${missingFields.mkString(", ")}"))
  }


  private def verifyState(currentOrganization: Organization): Map[String, Option[Any]]
  = {
    (Map("Info" -> currentOrganization.info, "Members" -> currentOrganization.members.headOption, "Owners" -> currentOrganization.owners.headOption, "Meta" -> currentOrganization.orgMeta) ++
      getInfoFields(currentOrganization.info.map(convertInfoToApiInfo)) ++ getMetaFields(currentOrganization.orgMeta))
      .filter(_._2.isEmpty)
  }

}
