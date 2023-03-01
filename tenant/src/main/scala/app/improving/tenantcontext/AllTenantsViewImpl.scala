package app.improving.tenantcontext

import app.improving.tenantcontext.tenant.{ApiTenant, ApiTenantStatus}
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext
import app.improving.tenantcontext.infrastructure.util._

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class AllTenantsViewImpl(context: ViewContext) extends AbstractAllTenantsView {

  override def emptyState: ApiTenant = ApiTenant.defaultInstance

  override def processTenantEstablished(
      state: ApiTenant,
      tenantEstablished: TenantEstablished
  ): UpdateEffect[ApiTenant] = {
    if (state != emptyState) effects.ignore()
    else
      effects.updateState(
        ApiTenant(
          tenantEstablished.tenantId
            .map(_.id)
            .getOrElse("TenantId is NOT FOUND."),
          tenantEstablished.name,
          tenantEstablished.primaryContact.map(convertContactToApiContact),
          tenantEstablished.info.map(convertInfoToApiInfo),
          tenantEstablished.meta.map(convertMetaInfoToApiMetaInfo),
          ApiTenantStatus.API_TENANT_STATUS_DRAFT
        )
      )
  }

  override def processTenantActivated(
      state: ApiTenant,
      tenantActivated: TenantActivated
  ): UpdateEffect[ApiTenant] = {
    effects.updateState(
      state.copy(
        meta = tenantActivated.meta.map(meta => {
          convertMetaInfoToApiMetaInfo(
            meta.copy(currentStatus = TenantStatus.TENANT_STATUS_ACTIVE)
          )
        }),
        name = tenantActivated.name,
        status = ApiTenantStatus.API_TENANT_STATUS_ACTIVE
      )
    )
  }

  override def processTenantSuspended(
      state: ApiTenant,
      tenantSuspended: TenantSuspended
  ): UpdateEffect[ApiTenant] = {
    effects.updateState(
      state.copy(
        meta = tenantSuspended.meta.map(convertMetaInfoToApiMetaInfo),
        name = tenantSuspended.name,
        status = ApiTenantStatus.API_TENANT_STATUS_SUSPENDED
      )
    )
  }

  override def processPrimaryContactUpdated(
      state: ApiTenant,
      primaryContactUpdated: PrimaryContactUpdated
  ): UpdateEffect[ApiTenant] = {
    effects.updateState(
      state.copy(
        meta = primaryContactUpdated.meta.map(convertMetaInfoToApiMetaInfo),
        primaryContact =
          primaryContactUpdated.newContact.map(convertContactToApiContact)
      )
    )
  }

  override def processTenantNameChanged(
      state: ApiTenant,
      tenantNameChanged: TenantNameChanged
  ): UpdateEffect[ApiTenant] = {
    effects.updateState(
      state.copy(
        meta = tenantNameChanged.meta.map(convertMetaInfoToApiMetaInfo),
        name = tenantNameChanged.newName
      )
    )
  }

  override def processTenantReleased(
      state: ApiTenant,
      tenantReleased: TenantReleased
  ): UpdateEffect[ApiTenant] = effects.deleteState()
}
