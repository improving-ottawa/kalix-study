package app.improving.organizationcontext.infrastructure

import app.improving.common.infrastructure.util.convertApiAddressToAddress
import app.improving.{MemberId, OrganizationId}
import app.improving.organizationcontext.{
  Info,
  MetaInfo,
  OrganizationStatus,
  Parent
}
import app.improving.organizationcontext.organization.{
  ApiInfo,
  ApiMetaInfo,
  ApiOrganizationStatus,
  ApiParent,
  ApiUpdateInfo
}

object util {

  def convertUpdateInfoToInfo(
      updateInfo: ApiUpdateInfo
  ): Info = {
    Info(
      updateInfo.name,
      updateInfo.shortName,
      updateInfo.address.map(convertApiAddressToAddress(_)),
      updateInfo.isPrivate,
      updateInfo.url,
      updateInfo.logo
    )
  }

  def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.name,
      apiInfo.shortName,
      apiInfo.address.map(convertApiAddressToAddress(_)),
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
