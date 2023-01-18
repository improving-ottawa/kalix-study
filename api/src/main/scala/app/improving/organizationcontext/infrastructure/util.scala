package app.improving.organizationcontext.infrastructure

import app.improving.common.infrastructure.util.convertApiAddressToAddress

import app.improving.{
  Address,
  ApiAddress,
  ApiCAPostalCode,
  ApiContact,
  ApiEmailAddress,
  ApiMemberId,
  ApiMobileNumber,
  ApiOrganizationId,
  ApiTenantId,
  ApiUSPostalCode,
  Contact,
  MemberId,
  OrganizationId,
  TenantId
}
import app.improving.organizationcontext.{
  Contacts,
  Info,
  MetaInfo,
  OrganizationEstablished,
  OrganizationStatus,
  Parent
}
import app.improving.organizationcontext.organization._

object util {

  def convertApiUpdateInfoToInfo(
      updateInfo: ApiUpdateInfo
  ): Info = {
    Info(
      updateInfo.name,
      updateInfo.shortName,
      updateInfo.address.map(convertApiAddressToAddress(_)),
      updateInfo.isPrivate,
      updateInfo.url,
      updateInfo.logo,
      updateInfo.tenant.map(tenant => TenantId(tenant.tenantId))
    )
  }

  def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.name,
      apiInfo.shortName,
      apiInfo.address.map(convertApiAddressToAddress(_)),
      apiInfo.isPrivate,
      apiInfo.url,
      apiInfo.logo,
      apiInfo.tenant.map(tenant => TenantId(tenant.tenantId))
    )
  }

  def convertInfoToApiInfo(info: Info): ApiInfo = {
    ApiInfo(
      info.name,
      info.shortName,
      info.address.map(addr => {
        convertAddressToApiAddress(addr)
      }),
      info.isPrivate,
      info.url,
      info.logo,
      info.tenant.map(tenant => ApiTenantId(tenant.id))
    )
  }
  def convertApiParentToParent(apiParent: ApiParent): Parent = {
    Parent(
      Some(OrganizationId(apiParent.orgId))
    )
  }

  def convertApiMetaInfoToMetaInfo(
      apiMetaInfo: ApiMetaInfo
  ): MetaInfo = {
    MetaInfo(
      apiMetaInfo.createdOn,
      apiMetaInfo.createdBy.map(member => MemberId(member.memberId)),
      apiMetaInfo.lastUpdated,
      apiMetaInfo.lastUpdatedBy.map(member => MemberId(member.memberId)),
      convertApiOrganizationStatusToOrganizationStatus(
        apiMetaInfo.currentStatus
      ),
      apiMetaInfo.children.map(child => OrganizationId(child.organizationId))
    )
  }

  def convertApiOrganizationStatusToOrganizationStatus(
      apiOrganizationStatus: ApiOrganizationStatus
  ): OrganizationStatus = {
    apiOrganizationStatus match {
      case ApiOrganizationStatus.DRAFT      => OrganizationStatus.DRAFT
      case ApiOrganizationStatus.ACTIVE     => OrganizationStatus.ACTIVE
      case ApiOrganizationStatus.SUSPENDED  => OrganizationStatus.SUSPENDED
      case ApiOrganizationStatus.TERMINATED => OrganizationStatus.TERMINATED
      case ApiOrganizationStatus.Unrecognized(unrecognizedValue) =>
        OrganizationStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertOrganizationStatusToApiOrganizationStatus(
      organizationStatus: OrganizationStatus
  ): ApiOrganizationStatus = {
    organizationStatus match {
      case OrganizationStatus.DRAFT      => ApiOrganizationStatus.DRAFT
      case OrganizationStatus.ACTIVE     => ApiOrganizationStatus.ACTIVE
      case OrganizationStatus.SUSPENDED  => ApiOrganizationStatus.SUSPENDED
      case OrganizationStatus.TERMINATED => ApiOrganizationStatus.TERMINATED
      case OrganizationStatus.Unrecognized(unrecognizedValue) =>
        ApiOrganizationStatus.Unrecognized(unrecognizedValue)
    }
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

  def convertContactsToApiContacts(contacts: Contacts): ApiContacts = {
    ApiContacts(
      contacts.primaryContacts.map(id => ApiMemberId(id.id)),
      contacts.billingContacts.map(id => ApiMemberId(id.id)),
      contacts.distributionContacts.map(id => ApiMemberId(id.id))
    )
  }

  def convertMetaInfoToApiMetaInfo(metaInfo: MetaInfo): ApiMetaInfo = {
    ApiMetaInfo(
      metaInfo.createdOn,
      metaInfo.createdBy.map(id => ApiMemberId(id.id)),
      metaInfo.lastUpdated,
      metaInfo.lastUpdatedBy.map(id => ApiMemberId(id.id)),
      convertOrganizationStatusToApiOrganizationStatus(metaInfo.currentStatus),
      metaInfo.children.map(id => ApiOrganizationId(id.id))
    )
  }
  def convertOrganizationToApiOrganization(
      organization: Organization
  ): ApiOrganization = {
    ApiOrganization(
      organization.oid.map(id => ApiOrganizationId(id.id)),
      organization.info.map(convertInfoToApiInfo),
      organization.parent.map(id => ApiOrganizationId(id.id)),
      organization.members.map(_.id),
      organization.owners.map(_.id),
      organization.contacts.map(convertContactsToApiContacts),
      organization.orgMeta.map(convertMetaInfoToApiMetaInfo),
      organization.name,
      convertOrganizationStatusToApiOrganizationStatus(organization.status)
    )
  }

  def convertAddressToApiAddress(address: Address): ApiAddress = {
    ApiAddress(
      address.line1,
      address.line2,
      address.city,
      address.stateProvince,
      address.country,
      address.postalCode match {
        case Address.PostalCode.UsPostalCode(_) =>
          ApiAddress.PostalCode.UsPostalCode(ApiUSPostalCode.defaultInstance)
        case Address.PostalCode.CaPostalCode(_) =>
          ApiAddress.PostalCode.CaPostalCode(
            ApiCAPostalCode.defaultInstance
          )
        case Address.PostalCode.Empty => ApiAddress.PostalCode.Empty
      }
    )
  }

  def convertOrganizationEstablishedToApiOrganization(
      organizationEstablished: OrganizationEstablished
  ): ApiOrganization = {
    ApiOrganization(
      organizationEstablished.orgId.map(org => ApiOrganizationId(org.id)),
      organizationEstablished.info.map(convertInfoToApiInfo),
      organizationEstablished.parent.flatMap(
        _.orgId.map(org => ApiOrganizationId(org.id))
      ),
      organizationEstablished.members.toList
        .flatMap(_.memberId.map(member => member.id)),
      organizationEstablished.owners.toList
        .flatMap(_.owners.map(owner => owner.id)),
      organizationEstablished.contacts.toList.flatMap(
        _.contacts.map(contact =>
          ApiContacts(
            contact.primaryContacts.map(ct => ApiMemberId(ct.id)),
            contact.billingContacts.map(ct => ApiMemberId(ct.id)),
            contact.distributionContacts.map(ct => ApiMemberId(ct.id))
          )
        )
      ),
      organizationEstablished.meta.map(convertMetaInfoToApiMetaInfo),
      organizationEstablished.info
        .map(_.name)
        .getOrElse("Name is not provided."),
      ApiOrganizationStatus.DRAFT
    )
  }
//  private def convertOrganizationStatusToApiOrganizationStatus(
//                                                                organizationStatus: OrganizationStatus
//                                                              ): ApiOrganizationStatus = {
//    organizationStatus match {
//      case OrganizationStatus.DRAFT => ApiOrganizationStatus.DRAFT
//      case OrganizationStatus.ACTIVE => ApiOrganizationStatus.ACTIVE
//      case OrganizationStatus.SUSPENDED => ApiOrganizationStatus.SUSPENDED
//      case OrganizationStatus.TERMINATED => ApiOrganizationStatus.TERMINATED
//      case OrganizationStatus.Unrecognized(unrecognizedValue) =>
//        ApiOrganizationStatus.Unrecognized(unrecognizedValue)
//    }
//  }
}
