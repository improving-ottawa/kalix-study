package app.improving.gateway.util

import scala.util.Random

object util {
  def genEmailAddressForName(r: Random, name: String): String =
    s"${r.nextString(10)}@$name.com}"

  def genMobileNumber(r: Random): String =
    s"(${r.nextInt()}${r.nextInt()}${r.nextInt()})-${r.nextInt()}${r.nextInt()}${r
        .nextInt()}-${r.nextInt()}${r.nextInt()}${r.nextInt()}${r.nextInt()}"
}
