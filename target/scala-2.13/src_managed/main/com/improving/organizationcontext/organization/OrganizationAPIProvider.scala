package com.improving.organizationcontext.organization

import com.google.protobuf.Descriptors
import com.google.protobuf.empty.EmptyProto
import com.improving.organization
import com.improving.organizationcontext.OrganizationContextDomainProto
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityOptions
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityProvider

import scala.collection.immutable.Seq

// This code is managed by Kalix tooling.
// It will be re-generated to reflect any changes to your protobuf definitions.
// DO NOT EDIT

object OrganizationAPIProvider {
  def apply(entityFactory: EventSourcedEntityContext => OrganizationAPI): OrganizationAPIProvider =
    new OrganizationAPIProvider(entityFactory, EventSourcedEntityOptions.defaults)
}
class OrganizationAPIProvider private(entityFactory: EventSourcedEntityContext => OrganizationAPI, override val options: EventSourcedEntityOptions)
  extends EventSourcedEntityProvider[OrganizationState, OrganizationAPI] {

  def withOptions(newOptions: EventSourcedEntityOptions): OrganizationAPIProvider =
    new OrganizationAPIProvider(entityFactory, newOptions)

  override final val serviceDescriptor: Descriptors.ServiceDescriptor =
    organization.OrganizationApiProto.javaDescriptor.findServiceByName("OrganizationService")

  override final val entityType: String = "organization"

  override final def newRouter(context: EventSourcedEntityContext): OrganizationAPIRouter =
    new OrganizationAPIRouter(entityFactory(context))

  override final val additionalDescriptors: Seq[Descriptors.FileDescriptor] =
    organization.OrganizationApiProto.javaDescriptor ::
    EmptyProto.javaDescriptor ::
    OrganizationContextDomainProto.javaDescriptor ::
    OrganizationDomainProto.javaDescriptor :: Nil
}

