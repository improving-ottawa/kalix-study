package app.improving.tenantcontext.tenant

import app.improving._

object TestData {

  val testTenantId = "test-tenant-id"
  val testTenantId2 = "test-tenant-id2"
  val newName = "new-name"
  val apiContact = ApiContact(
    "firtname",
    "lastname",
    Some(ApiEmailAddress("email@email.com")),
    Some(ApiMobileNumber("999-999-999")),
    "username"
  )
  val newApiContact = ApiContact(
    "firtname1",
    "lastname1",
    Some(ApiEmailAddress("email1@email.com")),
    Some(ApiMobileNumber("888-888-888")),
    "username1"
  )
  val apiAddress = ApiAddress(
    "line1",
    "line2",
    "city",
    "state",
    "canada",
    ApiAddress.PostalCode.CaPostalCode(
      ApiCAPostalCode.defaultInstance
    )
  )
  val apiInfo = ApiInfo(
    "name",
    Some(apiContact),
    Some(apiAddress)
  )
}
