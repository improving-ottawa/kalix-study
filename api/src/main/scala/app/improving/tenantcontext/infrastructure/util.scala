package app.improving.tenantcontext.infrastructure

import app.improving._
import app.improving.common.infrastructure.util._
import app.improving.tenantcontext.{Info, MetaInfo, TenantStatus}
import app.improving.tenantcontext.tenant._

object util {

  def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.name,
      apiInfo.primaryContact.map(convertApiContactToContact),
      apiInfo.address.map(convertApiAddressToAddress)
    )
  }

  def convertInfoToApiInfo(info: Info): ApiInfo = {
    ApiInfo(
      info.name,
      info.primaryContact.map(convertContactToApiContact),
      info.address.map(convertAddressToApiAddress)
    )
  }

  def convertApiContactToContact(apiContact: ApiContact): Contact = {
    Contact(
      apiContact.firstName,
      apiContact.lastName,
      apiContact.emailAddress.map(email => EmailAddress(email.value)),
      apiContact.phone.map(mobile => MobileNumber(mobile.value)),
      apiContact.userName
    )
  }

  def convertContactToApiContact(contact: Contact): ApiContact = {
    ApiContact(
      contact.firstName,
      contact.lastName,
      contact.emailAddress.map(email => ApiEmailAddress(email.value)),
      contact.phone.map(mobile => ApiMobileNumber(mobile.value)),
      contact.userName
    )
  }

  def convertMetaInfoToApiMetaInfo(metaInfo: MetaInfo): ApiMetaInfo = {
    ApiMetaInfo(
      metaInfo.createdOn,
      metaInfo.createdBy.map(convertContactToApiContact),
      metaInfo.lastUpdated,
      metaInfo.lastUpdatedBy.map(member => ApiMemberId(member.id)),
      convertTenantStatusToApiTenantStatus(metaInfo.currentStatus)
    )
  }

  def convertApiMetaInfoToMetaInfo(apiMetaInfo: ApiMetaInfo): MetaInfo = {
    MetaInfo(
      apiMetaInfo.createdOn,
      apiMetaInfo.createdBy.map(convertApiContactToContact),
      apiMetaInfo.lastUpdated,
      apiMetaInfo.lastUpdatedBy.map(member => MemberId(member.memberId)),
      convertApiTenantStatusToTenantStatus(apiMetaInfo.currentStatus)
    )
  }
  def convertTenantStatusToApiTenantStatus(
      status: TenantStatus
  ): ApiTenantStatus = {
    status match {
      case TenantStatus.DRAFT     => ApiTenantStatus.DRAFT
      case TenantStatus.ACTIVE    => ApiTenantStatus.ACTIVE
      case TenantStatus.SUSPENDED => ApiTenantStatus.SUSPENDED
      case TenantStatus.Unrecognized(unrecognizedValue) =>
        ApiTenantStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertApiTenantStatusToTenantStatus(
      apiStatus: ApiTenantStatus
  ): TenantStatus = {
    apiStatus match {
      case ApiTenantStatus.DRAFT     => TenantStatus.DRAFT
      case ApiTenantStatus.ACTIVE    => TenantStatus.ACTIVE
      case ApiTenantStatus.SUSPENDED => TenantStatus.SUSPENDED
      case ApiTenantStatus.Unrecognized(unrecognizedValue) =>
        TenantStatus.Unrecognized(unrecognizedValue)
    }
  }
  def convertTenantToApiTenant(tenant: Tenant): ApiTenant = {
    ApiTenant(
      tenant.tenantId.map(_.id).getOrElse(""),
      tenant.name,
      tenant.primaryContact.map(convertContactToApiContact),
      tenant.info.map(convertInfoToApiInfo),
      tenant.meta.map(convertMetaInfoToApiMetaInfo)
    )
  }

  def convertApiTenantToTenant(apiTenant: ApiTenant): Tenant = {
    Tenant(
      Some(TenantId(apiTenant.tenantId)),
      apiTenant.name,
      apiTenant.primaryContact.map(convertApiContactToContact),
      apiTenant.info.map(convertApiInfoToInfo),
      apiTenant.meta.map(convertApiMetaInfoToMetaInfo)
    )
  }
}
