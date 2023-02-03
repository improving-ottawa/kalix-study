package app.improving.gateway.util

import scala.util.Random

object util {
  def genEmailAddressForTenantName(r: Random, tenantName: String): String =
    s"${r.nextString(10)}@$tenantName.com}"

  def genMobileNumber(r: Random): String =
    s"(${r.nextInt()}${r.nextInt()}${r.nextInt()})-${r.nextInt()}${r.nextInt()}${r
        .nextInt()}-${r.nextInt()}${r.nextInt()}${r.nextInt()}${r.nextInt()}"
}
