package app.improving.common.infrastructure

import app.improving.{Address, ApiAddress, ApiCAPostalCode, ApiUSPostalCode}

object util {

  def convertApiAddressToAddress(
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

}
