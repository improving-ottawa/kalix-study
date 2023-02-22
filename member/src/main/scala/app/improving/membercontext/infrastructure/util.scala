package app.improving.membercontext.infrastructure

import app.improving.{
  ApiContact,
  ApiEmailAddress,
  ApiMemberId,
  ApiMobileNumber,
  ApiOrganizationId,
  Contact,
  EmailAddress,
  MobileNumber,
  OrganizationId,
  TenantId
}
import app.improving.membercontext.member.{
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
  NotificationPreference,
  UpdateInfo
}
object util {

  def convertInfoToApiUpdateInfo(info: Info): ApiUpdateInfo = {
    ApiUpdateInfo(
      info.contact.map(convertContactToApiContact),
      Option(info.handle),
      Option(info.avatar),
      Option(info.firstName),
      Option(info.lastName),
      info.notificationPreference.map(convertNotificationPreference),
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
      case MemberStatus.MEMBER_STATUS_DRAFT =>
        ApiMemberStatus.API_MEMBER_STATUS_DRAFT
      case MemberStatus.MEMBER_STATUS_ACTIVE =>
        ApiMemberStatus.API_MEMBER_STATUS_ACTIVE
      case MemberStatus.MEMBER_STATUS_INACTIVE =>
        ApiMemberStatus.API_MEMBER_STATUS_INACTIVE
      case MemberStatus.MEMBER_STATUS_SUSPENDED =>
        ApiMemberStatus.API_MEMBER_STATUS_SUSPENDED
      case MemberStatus.MEMBER_STATUS_TERMINATED =>
        ApiMemberStatus.API_MEMBER_STATUS_TERMINATED
      case MemberStatus.MEMBER_STATUS_RELEASED =>
        ApiMemberStatus.API_MEMBER_STATUS_RELEASED
      case MemberStatus.MEMBER_STATUS_UNKNOWN =>
        ApiMemberStatus.API_MEMBER_STATUS_UNKNOWN
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

  def convertApiUpdateInfoToUpdateInfo(
      apiUpdateInfo: ApiUpdateInfo
  ): UpdateInfo = {
    UpdateInfo(
      apiUpdateInfo.contact.map(convertApiContactToContact),
      apiUpdateInfo.handle,
      apiUpdateInfo.avatar,
      apiUpdateInfo.firstName,
      apiUpdateInfo.lastName,
      apiUpdateInfo.notificationPreference.map(convertNotificationPreference),
      apiUpdateInfo.organizationMembership.map(org =>
        OrganizationId(org.organizationId)
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
      apiInfo.notificationPreference.map(convertNotificationPreference),
      apiInfo.organizationMembership.map(org =>
        OrganizationId(org.organizationId)
      ),
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
      case ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_EMAIL =>
        NotificationPreference.NOTIFICATION_PREFERENCE_EMAIL
      case ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_SMS =>
        NotificationPreference.NOTIFICATION_PREFERENCE_SMS
      case ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_APPLICATION =>
        NotificationPreference.NOTIFICATION_PREFERENCE_APPLICATION
      case ApiNotificationPreference.Unrecognized(unrecognizedValue) =>
        NotificationPreference.Unrecognized(unrecognizedValue)
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

  def convertNotificationPreference(
      notificationPreference: NotificationPreference
  ): ApiNotificationPreference = {
    notificationPreference match {
      case NotificationPreference.NOTIFICATION_PREFERENCE_EMAIL =>
        ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_EMAIL
      case NotificationPreference.NOTIFICATION_PREFERENCE_SMS =>
        ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_SMS
      case NotificationPreference.NOTIFICATION_PREFERENCE_APPLICATION =>
        ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_APPLICATION
      case NotificationPreference.Unrecognized(unrecognizedValue) =>
        ApiNotificationPreference.Unrecognized(unrecognizedValue)
    }
  }

  def convertMemberStatus(
      apiMemberStatus: ApiMemberStatus
  ): MemberStatus = {
    apiMemberStatus match {
      case ApiMemberStatus.API_MEMBER_STATUS_DRAFT =>
        MemberStatus.MEMBER_STATUS_DRAFT
      case ApiMemberStatus.API_MEMBER_STATUS_ACTIVE =>
        MemberStatus.MEMBER_STATUS_ACTIVE
      case ApiMemberStatus.API_MEMBER_STATUS_INACTIVE =>
        MemberStatus.MEMBER_STATUS_INACTIVE
      case ApiMemberStatus.API_MEMBER_STATUS_SUSPENDED =>
        MemberStatus.MEMBER_STATUS_SUSPENDED
      case ApiMemberStatus.API_MEMBER_STATUS_TERMINATED =>
        MemberStatus.MEMBER_STATUS_TERMINATED
      case ApiMemberStatus.API_MEMBER_STATUS_RELEASED =>
        MemberStatus.MEMBER_STATUS_RELEASED
      case ApiMemberStatus.API_MEMBER_STATUS_UNKNOWN =>
        MemberStatus.MEMBER_STATUS_UNKNOWN
      case ApiMemberStatus.Unrecognized(unrecognizedValue) =>
        MemberStatus.Unrecognized(unrecognizedValue)
    }
  }

  def convertMemberRegisteredToApiMemberData(
      memberRegistered: MemberRegistered
  ): ApiMemberData = {
    ApiMemberData(
      memberRegistered.memberId
        .map(member => member.id)
        .getOrElse("MemberId is not found."),
      memberRegistered.info.map(convertInfoToApiUpdateInfo),
      memberRegistered.meta.map(convertMetaInfoToApiMetaInfo)
    )
  }

  def convertApiUpdateInfoToInfo(apiUpdateInfo: ApiUpdateInfo): Info = {
    Info(
      apiUpdateInfo.contact.map(convertApiContactToContact),
      apiUpdateInfo.handle.getOrElse("handle is NOT FOUND."),
      apiUpdateInfo.avatar.getOrElse("avatar is NOT FOUND."),
      apiUpdateInfo.firstName.getOrElse("firstName is NOT FOUND."),
      apiUpdateInfo.lastName.getOrElse("lastName is NOT FOUND."),
      apiUpdateInfo.notificationPreference.map(convertNotificationPreference),
      apiUpdateInfo.organizationMembership.map(org =>
        OrganizationId(org.organizationId)
      ),
      apiUpdateInfo.tenantId.map(tenant => TenantId(tenant.tenantId))
    )
  }
}
