package app.improving.gateway

import app.improving.ApiTenantId
import app.improving.tenantcontext.tenant.{ApiEstablishTenant, TenantService}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

import java.util.UUID

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class GatewayAPIActionImpl(creationContext: ActionCreationContext)
    extends AbstractGatewayAPIAction {
  override def handleEstablishTenant(
      establishTenant: EstablishTenant
  ): Action.Effect[ApiTenantId] = {
    val tenantService =
      actionContext.getGrpcClient(classOf[TenantService], "tenant")

    effects.asyncReply(
      tenantService.establishTenant(
        ApiEstablishTenant(
          UUID.randomUUID().toString,
          establishTenant.info
        )
      )
    )
  }

}
