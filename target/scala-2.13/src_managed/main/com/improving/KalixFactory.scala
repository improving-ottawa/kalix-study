package com.improving

import com.improving.organizationcontext.organization.OrganizationAPI
import com.improving.organizationcontext.organization.OrganizationAPIProvider
import kalix.scalasdk.Kalix
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This code is managed by Kalix tooling.
// It will be re-generated to reflect any changes to your protobuf definitions.
// DO NOT EDIT

object KalixFactory {

  def withComponents(
      createOrganizationAPI: EventSourcedEntityContext => OrganizationAPI): Kalix = {
    val kalix = Kalix()
    kalix
      .register(OrganizationAPIProvider(createOrganizationAPI))
  }
}
