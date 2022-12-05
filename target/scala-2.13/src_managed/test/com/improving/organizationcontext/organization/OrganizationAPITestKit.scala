package com.improving.organizationcontext.organization

import com.google.protobuf.empty.Empty
import com.improving.organization
import kalix.scalasdk.Metadata
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext
import kalix.scalasdk.testkit.EventSourcedResult
import kalix.scalasdk.testkit.impl.EventSourcedEntityEffectsRunner
import kalix.scalasdk.testkit.impl.EventSourcedResultImpl
import kalix.scalasdk.testkit.impl.TestKitEventSourcedEntityCommandContext
import kalix.scalasdk.testkit.impl.TestKitEventSourcedEntityContext
import kalix.scalasdk.testkit.impl.TestKitEventSourcedEntityEventContext

import scala.collection.immutable.Seq

// This code is managed by Kalix tooling.
// It will be re-generated to reflect any changes to your protobuf definitions.
// DO NOT EDIT

/**
 * TestKit for unit testing OrganizationAPI
 */
object OrganizationAPITestKit {
  /**
   * Create a testkit instance of OrganizationAPI
   * @param entityFactory A function that creates a OrganizationAPI based on the given EventSourcedEntityContext,
   *                      a default entity id is used.
   */
  def apply(entityFactory: EventSourcedEntityContext => OrganizationAPI): OrganizationAPITestKit =
    apply("testkit-entity-id", entityFactory)
  /**
   * Create a testkit instance of OrganizationAPI with a specific entity id.
   */
  def apply(entityId: String, entityFactory: EventSourcedEntityContext => OrganizationAPI): OrganizationAPITestKit =
    new OrganizationAPITestKit(entityFactory(new TestKitEventSourcedEntityContext(entityId)))
}
final class OrganizationAPITestKit private(entity: OrganizationAPI) extends EventSourcedEntityEffectsRunner[OrganizationState](entity: OrganizationAPI) {

  override protected def handleEvent(state: OrganizationState, event: Any): OrganizationState = {
    event match {
      case e: com.improving.organizationcontext.FindOrganizationsByMember =>
        entity.findOrganizationsByMember(state, e)

      case e: com.improving.organizationcontext.FindOrganizationsByOwner =>
        entity.findOrganizationsByOwner(state, e)

      case e: com.improving.organizationcontext.GetOrganizationInfo =>
        entity.getOrganizationInfo(state, e)

      case e: com.improving.organizationcontext.MembersAddedToOrganization =>
        entity.membersAddedToOrganization(state, e)

      case e: com.improving.organizationcontext.MembersRemovedFromOrganization =>
        entity.membersRemovedFromOrganization(state, e)

      case e: com.improving.organizationcontext.OrganizationAccountsUpdated =>
        entity.organizationAccountsUpdated(state, e)

      case e: com.improving.organizationcontext.OrganizationContactsUpdated =>
        entity.organizationContactsUpdated(state, e)

      case e: com.improving.organizationcontext.OrganizationEstablished =>
        entity.organizationEstablished(state, e)

      case e: com.improving.organizationcontext.OrganizationInfoUpdated =>
        entity.organizationInfoUpdated(state, e)

      case e: com.improving.organizationcontext.OrganizationStatusUpdated =>
        entity.organizationStatusUpdated(state, e)

      case e: com.improving.organizationcontext.OwnersAddedToOrganization =>
        entity.ownersAddedToOrganization(state, e)

      case e: com.improving.organizationcontext.OwnersRemovedFromOrganization =>
        entity.ownersRemovedFromOrganization(state, e)

      case e: com.improving.organizationcontext.ParentUpdated =>
        entity.parentUpdated(state, e)
    }
  }

  def addMembersToOrganization(command: organization.ApiAddMembersToOrganization, metadata: Metadata = Metadata.empty): EventSourcedResult[Empty] =
    interpretEffects(() => entity.addMembersToOrganization(currentState, command), metadata)

  def addOwnersToOrganization(command: organization.ApiAddOwnersToOrganization, metadata: Metadata = Metadata.empty): EventSourcedResult[Empty] =
    interpretEffects(() => entity.addOwnersToOrganization(currentState, command), metadata)

  def editOrganizationInfo(command: organization.ApiEditOrganizationInfo, metadata: Metadata = Metadata.empty): EventSourcedResult[Empty] =
    interpretEffects(() => entity.editOrganizationInfo(currentState, command), metadata)

  def establishOrganization(command: organization.ApiEstablishOrganization, metadata: Metadata = Metadata.empty): EventSourcedResult[Empty] =
    interpretEffects(() => entity.establishOrganization(currentState, command), metadata)
}
