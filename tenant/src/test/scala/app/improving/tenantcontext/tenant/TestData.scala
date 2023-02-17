package app.improving.tenantcontext.tenant

import app.improving._

object TestData {

  val testTenantId = "test-tenant-id"
  val testTenantId2 = "test-tenant-id2"
  val testNewName = "new-name"
  val testMemberId: ApiMemberId = ApiMemberId("member-id")
  val apiContact: ApiContact = ApiContact(
    "firtname",
    "lastname",
    Some(ApiEmailAddress("email@email.com")),
    Some(ApiMobileNumber("999-999-999")),
    "username"
  )
  val newApiContact: ApiContact = ApiContact(
    "firtname1",
    "lastname1",
    Some(ApiEmailAddress("email1@email.com")),
    Some(ApiMobileNumber("888-888-888")),
    "username1"
  )
  val apiAddress: ApiAddress = ApiAddress(
    "line1",
    "line2",
    "city",
    "state",
    "canada",
    ApiAddress.PostalCode.CaPostalCode(
      ApiCAPostalCode.defaultInstance
    )
  )
  val apiInfo: ApiInfo = ApiInfo(
    "name",
    Some(apiContact),
    Some(apiAddress)
  )
}
