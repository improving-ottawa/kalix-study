package app.improving.tenantcontext.tenant

import app.improving.{MemberId, TenantId}
import app.improving.tenantcontext.infrastructure.util.{
  convertApiContactToContact,
  convertApiInfoToInfo
}
import app.improving.tenantcontext.{
  MetaInfo,
  PrimaryContactUpdated,
  TenantActivated,
  TenantEstablished,
  TenantNameChanged,
  TenantStatus,
  TenantSuspended
}
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TenantAPI(context: EventSourcedEntityContext) extends AbstractTenantAPI {
  override def emptyState: TenantState = TenantState.defaultInstance

  override def establishTenant(
      currentState: TenantState,
      apiEstablishTenant: ApiEstablishTenant
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.tenant match {
      case Some(_) => effects.reply(Empty.defaultInstance)
      case _ => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = TenantEstablished(
          Some(TenantId(apiEstablishTenant.tenantId)),
          apiEstablishTenant.info.map(convertApiInfoToInfo),
          Some(
            MetaInfo(
              Some(timestamp),
              apiEstablishTenant.info
                .flatMap(_.primaryContact)
                .map(convertApiContactToContact),
              Some(timestamp),
              None,
              TenantStatus.DRAFT
            )
          ),
          apiEstablishTenant.info.map(_.name).getOrElse(""),
          apiEstablishTenant.info
            .flatMap(_.primaryContact)
            .map(convertApiContactToContact)
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
    }
  }

  override def activateTenant(
      currentState: TenantState,
      apiActivateTenant: ApiActivateTenant
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.tenant match {
      case Some(tenant)
          if tenant.tenantId == Some(TenantId(apiActivateTenant.tenantId)) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = TenantActivated(
          tenant.tenantId,
          tenant.name,
          tenant.meta.map(
            _.copy(
              currentStatus = TenantStatus.ACTIVE,
              lastUpdated = Some(timestamp),
              lastUpdatedBy = Some(MemberId(apiActivateTenant.updatingUser))
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def suspendTenant(
      currentState: TenantState,
      apiSuspendTenant: ApiSuspendTenant
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.tenant match {
      case Some(tenant)
          if tenant.tenantId == Some(TenantId(apiSuspendTenant.tenantId)) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = TenantSuspended(
          tenant.tenantId,
          tenant.name,
          tenant.meta.map(
            _.copy(
              currentStatus = TenantStatus.SUSPENDED,
              lastUpdated = Some(timestamp),
              lastUpdatedBy = Some(MemberId(apiSuspendTenant.updatingUser))
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def updatePrimaryContact(
      currentState: TenantState,
      apiUpdatePrimaryContact: ApiUpdatePrimaryContact
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.tenant match {
      case Some(tenant)
          if tenant.tenantId == Some(
            TenantId(apiUpdatePrimaryContact.tenantId)
          ) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = PrimaryContactUpdated(
          tenant.tenantId,
          tenant.primaryContact,
          apiUpdatePrimaryContact.newContact.map(convertApiContactToContact),
          tenant.meta.map(
            _.copy(
              lastUpdated = Some(timestamp),
              lastUpdatedBy =
                Some(MemberId(apiUpdatePrimaryContact.updatingUser))
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def changeTenantName(
      currentState: TenantState,
      apiChangeTenantName: ApiChangeTenantName
  ): EventSourcedEntity.Effect[Empty] = {
    currentState.tenant match {
      case Some(tenant)
          if tenant.tenantId == Some(
            TenantId(apiChangeTenantName.tenantId)
          ) => {
        val now = java.time.Instant.now()
        val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
        val event = TenantNameChanged(
          tenant.tenantId,
          tenant.name,
          apiChangeTenantName.newName,
          tenant.meta.map(
            _.copy(
              lastUpdated = Some(timestamp),
              lastUpdatedBy = Some(MemberId(apiChangeTenantName.updatingUser))
            )
          )
        )
        effects.emitEvent(event).thenReply(_ => Empty.defaultInstance)
      }
      case _ => effects.reply(Empty.defaultInstance)
    }
  }

  override def tenantEstablished(
      currentState: TenantState,
      tenantEstablished: TenantEstablished
  ): TenantState = {
    currentState.tenant match {
      case Some(_) => currentState
      case _ => {
        currentState.withTenant(
          Tenant(
            tenantEstablished.tenantId,
            tenantEstablished.name,
            tenantEstablished.primaryContact,
            tenantEstablished.info,
            tenantEstablished.meta,
            TenantStatus.DRAFT
          )
        )
      }
    }
  }

  override def tenantActivated(
      currentState: TenantState,
      tenantActivated: TenantActivated
  ): TenantState = {
    currentState.tenant match {
      case Some(tenant) if tenant.tenantId == tenantActivated.tenantId => {
        currentState.withTenant(
          tenant.copy(
            meta = tenantActivated.meta,
            status = TenantStatus.ACTIVE
          )
        )
      }
      case _ => currentState
    }
  }

  override def tenantSuspended(
      currentState: TenantState,
      tenantSuspended: TenantSuspended
  ): TenantState = {
    currentState.tenant match {
      case Some(tenant) if tenant.tenantId == tenantSuspended.tenantId => {
        currentState.withTenant(
          tenant.copy(
            meta = tenantSuspended.meta,
            status = TenantStatus.SUSPENDED
          )
        )
      }
      case _ => currentState
    }
  }

  override def primaryContactUpdated(
      currentState: TenantState,
      primaryContactUpdated: PrimaryContactUpdated
  ): TenantState = {
    currentState.tenant match {
      case Some(tenant)
          if tenant.tenantId == primaryContactUpdated.tenantId && tenant.primaryContact == primaryContactUpdated.oldContact => {
        currentState.withTenant(
          tenant.copy(
            meta = primaryContactUpdated.meta,
            primaryContact = primaryContactUpdated.newContact
          )
        )
      }
      case _ => currentState
    }
  }

  override def tenantNameChanged(
      currentState: TenantState,
      tenantNameChanged: TenantNameChanged
  ): TenantState = {
    currentState.tenant match {
      case Some(tenant)
          if tenant.tenantId == tenantNameChanged.tenantId && tenant.name == tenantNameChanged.oldName => {
        currentState.withTenant(
          tenant.copy(
            meta = tenantNameChanged.meta,
            name = tenantNameChanged.newName
          )
        )
      }
      case _ => currentState
    }
  }
}
