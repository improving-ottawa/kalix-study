package app.improving.organizationcontext

import app.improving.organizationcontext.organization.OrganizationAPI
import kalix.scalasdk.Kalix
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

object Main {

  private val log =
    LoggerFactory.getLogger("app.improving.organizationcontext.Main")

  def createKalix(): Kalix = {
    // The KalixFactory automatically registers any generated Actions, Views or Entities,
    // and is kept up-to-date with any changes in your protobuf definitions.
    // If you prefer, you may remove this and manually register these components in a
    // `Kalix()` instance.
    KalixFactory
      .withComponents(
        new OrganizationAPI(_),
        new AllOrganizationsViewImpl(_),
        new OrganizationByMemberViewImpl(_),
        new OrganizationByOwnerViewImpl(_)
      )
      .register(
        AllOrganizationsViewProvider(new AllOrganizationsViewImpl(_))
          .withViewId("AllOrganizationsViewV1")
      )
  }

  def main(args: Array[String]): Unit = {
    log.info("starting the Kalix service")
    createKalix().start()
  }
}
