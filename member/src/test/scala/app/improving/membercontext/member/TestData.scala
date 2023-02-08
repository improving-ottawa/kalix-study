package app.improving.membercontext.member

import app.improving._
import app.improving.membercontext.{Info, NotificationPreference}

object TestData {
  val testMemberId = "test-member-id"
  val testMemberId2 = "test-member-id2"
  val testOrganizationId = "test-organization-id"
  val testTenantId = "test-tenant-id"
  val testTenantId1 = "test-tenant-id1"
  val apiContact = ApiContact(
    "member-first-name",
    "member-last-name",
    Some(ApiEmailAddress("member@memberapi.com")),
    Some(ApiMobileNumber("987-878-0987")),
    "user-name"
  )
  val apiInfo = ApiInfo(
    Some(apiContact),
    "handle",
    "avartar",
    "member-name",
    "short-name",
    Some(ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_SMS),
    Seq[ApiOrganizationId](ApiOrganizationId(testMemberId)),
    Some(ApiTenantId(testTenantId))
  )
  val apiUpdateInfo = ApiUpdateInfo(
    Some(apiContact),
    Some("new-handle"),
    Some("new-avatar"),
    Some("new-firstname"),
    Some("new-lastname"),
    Some(ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_EMAIL),
    Seq[ApiOrganizationId](ApiOrganizationId(testOrganizationId)),
    Some(ApiTenantId(testTenantId1))
  )
  val apiPartialUpdateInfo = ApiUpdateInfo(
    Some(apiContact),
    Some("new-handle"),
    Some("new-avatar"),
    Some(""),
    Some("new-lastname"),
    Some(ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_EMAIL),
    Seq.empty,
    None
  )
  val contact = Contact(
    "member-first-name",
    "member-last-name",
    Some(EmailAddress("member@memberapi.com")),
    Some(MobileNumber("987-878-0987")),
    "user-name"
  )
  val info = Info(
    Some(contact),
    "handle",
    "avartar",
    "member-name",
    "short-name",
    Some(NotificationPreference.NOTIFICATION_PREFERENCE_SMS),
    Seq[OrganizationId](OrganizationId(testMemberId)),
    Some(TenantId(testTenantId))
  )
}
