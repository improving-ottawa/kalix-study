package app.improving.gateway

import app.improving.ApiTenantId
import app.improving.eventcontext.event.{ApiScheduleEvent, EventService}
import app.improving.organizationcontext.organization.{
  ApiEstablishOrganization,
  OrganizationService
}
import app.improving.tenantcontext.tenant.{ApiEstablishTenant, TenantService}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success}

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class GatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractGatewayApiAction {

  private val log = LoggerFactory.getLogger(this.getClass)
  override def handleEstablishTenant(
      establishTenant: CreateTenant
  ): Action.Effect[TenantCreated] = {

    log.info("in handleEstablishTenant")

    val tenantService =
      actionContext.getGrpcClient(
        classOf[TenantService],
        "kalix-study-tenant"
      )

    effects.asyncReply(
      tenantService
        .establishTenant(
          ApiEstablishTenant(
            UUID.randomUUID().toString,
            establishTenant.tenantInfo
          )
        )
        .map(id => TenantCreated(Some(id)))
    )
  }

  override def handleEstablishOrganization(
      createOrganization: CreateOrganization
  ): Action.Effect[OrganizationCreated] = {

    log.info("in handleEstablishOrganization")

    val organizationService = actionContext.getGrpcClient(
      classOf[OrganizationService],
      "kalix-study-org"
    )

    effects.asyncReply(
      organizationService
        .establishOrganization(
          ApiEstablishOrganization(
            UUID.randomUUID().toString,
            createOrganization.establishOrganization.flatMap(_.info),
            createOrganization.establishOrganization.flatMap(_.parent),
            createOrganization.establishOrganization
              .map(_.members)
              .toSeq
              .flatten,
            createOrganization.establishOrganization
              .map(_.owners)
              .toSeq
              .flatten,
            createOrganization.establishOrganization
              .map(_.contacts)
              .toSeq
              .flatten,
            createOrganization.establishOrganization.flatMap(
              _.establishingMember
            ),
            createOrganization.establishOrganization.flatMap(_.meta)
          )
        )
        .map(id => OrganizationCreated(Some(id)))
    )
  }

  override def handleScheduleEvent(
      createEvent: CreateEvent
  ): Action.Effect[EventCreated] = {

    log.info("in handleScheduleEvent")

    val eventService = actionContext.getGrpcClient(
      classOf[EventService],
      "kalix-study-event"
    )

    effects.asyncReply(
      eventService
        .scheduleEvent(
          ApiScheduleEvent(
            UUID.randomUUID().toString,
            createEvent.scheduleEvent.flatMap(_.info),
            createEvent.scheduleEvent.flatMap(_.schedulingMember)
          )
        )
        .map(id => EventCreated(Some(id)))
    )
  }
}
