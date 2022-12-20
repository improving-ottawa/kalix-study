package app.improving.organizationcontext.infrastructure

import app.improving.{Address, MemberId, OrganizationId}
import app.improving.organization.{
  ApiAddress,
  ApiCAPostalCode,
  ApiInfo,
  ApiMetaInfo,
  ApiOrganizationStatus,
  ApiParent,
  ApiUSPostalCode,
  ApiUpdateInfo
}
import app.improving.organizationcontext.{
  Info,
  MetaInfo,
  OrganizationStatus,
  Parent
}

object util {

  def convertUpdateInfoToInfo(
      updateInfo: ApiUpdateInfo
  ): Info = {
    Info(
      updateInfo.name,
      updateInfo.shortName,
      updateInfo.address.map(convertApiAdressToAddress(_)),
      updateInfo.isPrivate,
      updateInfo.url,
      updateInfo.logo
    )
  }

  def convertApiAdressToAddress(
      apiAddress: ApiAddress
  ): Address = {
    Address(
      apiAddress.line1,
      apiAddress.line2,
      apiAddress.city,
      apiAddress.stateProvince,
      apiAddress.country,
      apiAddress.postalCode match {
        case ApiAddress.PostalCode.Empty => Address.PostalCode.Empty
        case ApiAddress.PostalCode.UsPostalCode(_) =>
          Address.PostalCode.UsPostalCode(
            app.improving.USPostalCode.defaultInstance
          )
        case ApiAddress.PostalCode.CaPostalCode(_) =>
          Address.PostalCode.CaPostalCode(
            app.improving.CAPostalCode.defaultInstance
          )
      }
    )
  }

  def convertAddressToApiAdress(address: Address): ApiAddress = {
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
      }
    )
  }

  def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.name,
      apiInfo.shortName,
      apiInfo.address.map(convertApiAdressToAddress(_)),
      apiInfo.isPrivate,
      apiInfo.url,
      apiInfo.logo
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
      apiMetaInfo.children.map(child => OrganizationId(child.orgId))
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
}
