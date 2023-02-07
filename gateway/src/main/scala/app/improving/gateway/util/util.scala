package app.improving.gateway.util

import app.improving.{
  ApiAddress,
  ApiCAPostalCode,
  ApiContact,
  ApiEmailAddress,
  ApiMobileNumber,
  ApiUSPostalCode
}

import scala.util.Random

object util {
  def genEmailAddressForName(r: Random, name: String): String =
    s"${r.nextString(10)}@$name.com}"

  def genMobileNumber(r: Random): String =
    s"(${r.nextInt()}${r.nextInt()}${r.nextInt()})-${r.nextInt()}${r.nextInt()}${r
        .nextInt()}-${r.nextInt()}${r.nextInt()}${r.nextInt()}${r.nextInt()}"

  def genAddress(r: Random): ApiAddress = {
    ApiAddress(
      r.nextString(20),
      r.nextString(20),
      r.nextString(15),
      r.nextString(15),
      r.nextString(15),
      if (r.nextInt() % 2 == 0)
        ApiAddress.PostalCode.CaPostalCode(ApiCAPostalCode.defaultInstance)
      else ApiAddress.PostalCode.UsPostalCode(ApiUSPostalCode.defaultInstance)
    )
  }

  def genContact(r: Random): ApiContact = {
    val name = r.nextString(20)
    ApiContact(
      name,
      r.nextString(20),
      Some(ApiEmailAddress(genEmailAddressForName(r, name))),
      Some(ApiMobileNumber(genMobileNumber(r))),
      r.nextString(20)
    )
  }
}
