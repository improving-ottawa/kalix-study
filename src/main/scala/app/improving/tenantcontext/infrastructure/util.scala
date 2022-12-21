package app.improving.tenantcontext.infrastructure

import app.improving.{ApiContact, Contact, EmailAddress, MobileNumber}
import app.improving.common.infrastructure.util._
import app.improving.tenant.ApiInfo
import app.improving.tenantcontext.Info

object util {

  def convertApiInfoToInfo(apiInfo: ApiInfo): Info = {
    Info(
      apiInfo.name,
      apiInfo.primaryContact.map(convertApiContactToContact),
      apiInfo.address.map(convertApiAddressToAddress)
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
}
