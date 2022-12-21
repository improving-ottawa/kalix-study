package app.improving

import app.improving.eventcontext.event.EventAPI
import app.improving.membercontext.MemberByMetaInfoViewImpl
import app.improving.membercontext.member.MemberAPI
import app.improving.membercontext.membermap.MemberMap
import app.improving.organizationcontext.OrganizationByMemberViewImpl
import app.improving.organizationcontext.OrganizationByOwnerViewImpl
import app.improving.organizationcontext.organization.OrganizationAPI
import app.improving.tenantcontext.tenant.TenantAPI
import kalix.scalasdk.Kalix
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

object Main {

  private val log = LoggerFactory.getLogger("app.improving.Main")

  def createKalix(): Kalix = {
    // The KalixFactory automatically registers any generated Actions, Views or Entities,
    // and is kept up-to-date with any changes in your protobuf definitions.
    // If you prefer, you may remove this and manually register these components in a
    // `Kalix()` instance.
    KalixFactory.withComponents(
      new EventAPI(_),
      new MemberAPI(_),
      new MemberMap(_),
      new OrganizationAPI(_),
      new TenantAPI(_),
      new MemberByMetaInfoViewImpl(_),
      new OrganizationByMemberViewImpl(_),
      new OrganizationByOwnerViewImpl(_)
    )
  }

  def main(args: Array[String]): Unit = {
    log.info("starting the Kalix service")
    createKalix().start()
  }
}
