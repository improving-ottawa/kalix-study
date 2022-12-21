package app.improving.membercontext.infrastructure

import app.improving.{
  ApiContact,
  ApiEmailAddress,
  ApiMemberId,
  ApiMobileNumber,
  Contact,
  EmailAddress,
  MobileNumber,
  OrganizationId,
  TenantId
}
import app.improving.member.{
  ApiInfo,
  ApiMemberData,
  ApiMemberMap,
  ApiMemberStatus,
  ApiMetaInfo,
  ApiNotificationPreference,
  ApiUpdateInfo
}
import app.improving.membercontext.{
  Info,
  MemberMap,
  MemberRegistered,
  MemberStatus,
  MetaInfo,
  NotificationPreference
}
import app.improving.organization.ApiOrganizationId

object util {

  def convertInfoToApiUpdateInfo(info: Info): ApiUpdateInfo = {
    ApiUpdateInfo(
      info.handle,
      info.avatar,
      info.firstName,
      info.lastName,
      info.mobileNumber.map(mobile => ApiMobileNumber(mobile.value)),
      info.emailAddress.map(email => ApiEmailAddress(email.value)),
      convertNotificationPreference(info.notificationPreference),
      info.organizationMembership.map(org => ApiOrganizationId(org.id))
    )
  }

  def convertMetaInfoToApiMetaInfo(metaInfo: MetaInfo): ApiMetaInfo = {
    ApiMetaInfo(
      metaInfo.createdOn,
      metaInfo.createdBy.map(member => ApiMemberId(member.id)),
      metaInfo.lastModifiedOn,
      metaInfo.lastModifiedBy.map(member => ApiMemberId(member.id)),
      convertMemberStatus(metaInfo.memberStatus)
    )
  }

  def convertMemberStatus(
      memberStatus: MemberStatus
  ): ApiMemberStatus = {
    memberStatus match {
      case MemberStatus.ACTIVE     => ApiMemberStatus.ACTIVE
      case MemberStatus.INACTIVE   => ApiMemberStatus.INACTIVE
      case MemberStatus.SUSPENDED  => ApiMemberStatus.SUSPENDED
      case MemberStatus.TERMINATED => ApiMemberStatus.TERMINATED
      case MemberStatus.UNKNOWN    => ApiMemberStatus.UNKNOWN
      case MemberStatus.Unrecognized(unrecognizedValue) =>
        ApiMemberStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertApiMemberMapToMemberMap(
      apiMemberMap: ApiMemberMap
  ): MemberMap = {
    MemberMap(
      apiMemberMap.map.map { case (s, aif) => (s, convertApiInfoToInfo(aif)) }
    )
  }

  def convertApiUpdateInfoToInfo(apiUpdateInfo: ApiUpdateInfo): Info = {
    Info(
      Some(
        Contact(
          apiUpdateInfo.firstName,
          apiUpdateInfo.lastName,
          apiUpdateInfo.emailAddress.map(email => EmailAddress(email.value)),
          apiUpdateInfo.mobileNumber.map(mobile => MobileNumber(mobile.value)),
          apiUpdateInfo.handle
        )
      ),
      apiUpdateInfo.handle,
      apiUpdateInfo.avatar,
      apiUpdateInfo.firstName,
      apiUpdateInfo.lastName,
      apiUpdateInfo.mobileNumber.map(mobile => MobileNumber(mobile.value)),
      apiUpdateInfo.emailAddress.map(email => EmailAddress(email.value)),
      convertNotificationPreference(apiUpdateInfo.notificationPreference),
      apiUpdateInfo.organizationMembership.map(org =>
        OrganizationId(org.orgId)
      ),
      apiUpdateInfo.tenantId.map(tenant => TenantId(tenant.tenantId))
    )
  }

  def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.contact.map(convertApiContactToContact),
      apiInfo.handle,
      apiInfo.avatar,
      apiInfo.firstName,
      apiInfo.lastName,
      apiInfo.mobileNumber.map(mobile => MobileNumber(mobile.value)),
      apiInfo.emailAddress.map(email => EmailAddress(email.value)),
      convertNotificationPreference(apiInfo.notificationPreference),
      apiInfo.organizationMembership.map(org => OrganizationId(org.orgId)),
      apiInfo.tenant.map(tenant => TenantId(tenant.tenantId))
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

  def convertNotificationPreference(
      apiNotificationPreference: ApiNotificationPreference
  ): NotificationPreference = {
    apiNotificationPreference match {
      case ApiNotificationPreference.EMAIL => NotificationPreference.EMAIL
      case ApiNotificationPreference.SMS   => NotificationPreference.SMS
      case ApiNotificationPreference.APPLICATION =>
        NotificationPreference.APPLICATION
      case ApiNotificationPreference.Unrecognized(unrecognizedValue) =>
        NotificationPreference.Unrecognized(unrecognizedValue)
    }
  }

  def convertNotificationPreference(
      notificationPreference: NotificationPreference
  ): ApiNotificationPreference = {
    notificationPreference match {
      case NotificationPreference.EMAIL => ApiNotificationPreference.EMAIL
      case NotificationPreference.SMS   => ApiNotificationPreference.SMS
      case NotificationPreference.APPLICATION =>
        ApiNotificationPreference.APPLICATION
      case NotificationPreference.Unrecognized(unrecognizedValue) =>
        ApiNotificationPreference.Unrecognized(unrecognizedValue)
    }
  }

  def convertMemberStatus(
      apiMemberStatus: ApiMemberStatus
  ): MemberStatus = {
    apiMemberStatus match {
      case ApiMemberStatus.ACTIVE     => MemberStatus.ACTIVE
      case ApiMemberStatus.INACTIVE   => MemberStatus.INACTIVE
      case ApiMemberStatus.SUSPENDED  => MemberStatus.SUSPENDED
      case ApiMemberStatus.TERMINATED => MemberStatus.TERMINATED
      case ApiMemberStatus.UNKNOWN    => MemberStatus.UNKNOWN
      case ApiMemberStatus.Unrecognized(unrecognizedValue) =>
        MemberStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertMemberRegisteredToApiMemberData(
      memberRegistered: MemberRegistered
  ): ApiMemberData = {
    ApiMemberData(
      memberRegistered.memberId.map(member => ApiMemberId(member.id)),
      memberRegistered.info.map(convertInfoToApiUpdateInfo),
      memberRegistered.meta.map(convertMetaInfoToApiMetaInfo)
    )
  }
}
