package app.improving.mega

import app.improving.eventcontext.event.EventAPI
import kalix.scalasdk.Kalix
import org.slf4j.LoggerFactory
import app.improving.tenantcontext.AllTenantsViewImpl
import app.improving.organizationcontext.{
  AllOrganizationsViewImpl,
  OrganizationByMemberViewImpl,
  OrganizationByOwnerViewImpl
}
import app.improving.membercontext.{
  AllMembersViewImpl,
  MemberByMemberIdsQueryView,
  MemberByMetaInfoViewImpl,
  MemberByOrderQueryView
}
import app.improving.eventcontext.AllEventsViewImpl
import app.improving.membercontext.member.{MemberAPI, MemberActionServiceImpl}
import app.improving.membercontext.membermap.MemberMap
import app.improving.ordercontext.order.{OrderAPI, OrderActionImpl}
import app.improving.storecontext.AllStoresViewImpl
import app.improving.productcontext.{
  AllProductsViewImpl,
  TicketByEventTimeQueryView,
  TicketByEventViewImpl
}
import app.improving.ordercontext.{AllOrdersViewImpl, OrderByProductQueryView}
import app.improving.organizationcontext.organization.OrganizationAPI
import app.improving.productcontext.product.ProductAPI
import app.improving.storecontext.store.StoreAPI
import app.improving.tenantcontext.tenant.TenantAPI

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

object Main {

  private val log = LoggerFactory.getLogger("app.improving.mega.Main")

  def createKalix(): Kalix = {
    // The KalixFactory automatically registers any generated Actions, Views or Entities,
    // and is kept up-to-date with any changes in your protobuf definitions.
    // If you prefer, you may remove this and manually register these components in a
    // `Kalix()` instance.
    ManualKalixFactory.withComponents(
      new MegaActionImpl(_),
      new TenantAPI(_),
      new AllTenantsViewImpl(_),
      new OrganizationAPI(_),
      new AllOrganizationsViewImpl(_),
      new OrganizationByMemberViewImpl(_),
      new OrganizationByOwnerViewImpl(_),
      new MemberAPI(_),
      new MemberMap(_),
      new AllMembersViewImpl(_),
      new MemberActionServiceImpl(_),
      new MemberByMemberIdsQueryView(_),
      new MemberByMetaInfoViewImpl(_),
      new MemberByOrderQueryView(_),
      new EventAPI(_),
      new AllEventsViewImpl(_),
      new StoreAPI(_),
      new AllStoresViewImpl(_),
      new ProductAPI(_),
      new AllProductsViewImpl(_),
      new TicketByEventTimeQueryView(_),
      new TicketByEventViewImpl(_),
      new OrderAPI(_),
      new AllOrdersViewImpl(_),
      new OrderActionImpl(_),
      new OrderByProductQueryView(_)
    )
  }

  def main(args: Array[String]): Unit = {
    log.info("starting the Kalix service")
    createKalix().start()
  }
}
