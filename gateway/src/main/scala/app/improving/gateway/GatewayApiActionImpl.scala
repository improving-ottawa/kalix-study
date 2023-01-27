package app.improving.gateway

import app.improving.ApiTenantId
import app.improving.tenantcontext.tenant.{ApiEstablishTenant, TenantService}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

import java.util.UUID
import scala.concurrent.Future

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class GatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractGatewayApiAction {
  override def handleEstablishTenant(
      establishTenants: CreateTenants
  ): Action.Effect[TenantsCreated] = {
    val tenantService =
      actionContext.getGrpcClient(classOf[TenantService], "tenant")

    effects.asyncReply(
      Future
        .sequence(
          establishTenants.tenantInfos.map(info =>
            tenantService.establishTenant(
              ApiEstablishTenant(
                UUID.randomUUID().toString,
                Some(info)
              )
            )
          )
        )
        .map(TenantsCreated(_))
    )
  }

}
