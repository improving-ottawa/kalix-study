package app.improving.mega

import app.improving.eventcontext.{AllEventsViewImpl, AllEventsViewProvider}
import app.improving.eventcontext.event.{EventAPI, EventAPIProvider}
import app.improving.membercontext.{
  AllMembersViewImpl,
  AllMembersViewProvider,
  MemberByMemberIdsQueryView,
  MemberByMemberIdsQueryViewProvider,
  MemberByMetaInfoViewImpl,
  MemberByMetaInfoViewProvider,
  MemberByOrderQueryView,
  MemberByOrderQueryViewProvider
}
import app.improving.membercontext.member.{
  MemberAPI,
  MemberAPIProvider,
  MemberActionServiceActionProvider,
  MemberActionServiceImpl
}
import app.improving.membercontext.membermap.{MemberMap, MemberMapProvider}
import app.improving.ordercontext.{
  AllOrdersViewImpl,
  AllOrdersViewProvider,
  OrderByProductQueryView,
  OrderByProductQueryViewProvider
}
import app.improving.ordercontext.order.{
  OrderAPI,
  OrderAPIProvider,
  OrderActionImpl,
  OrderActionProvider
}
import app.improving.organizationcontext.{
  AllOrganizationsViewImpl,
  AllOrganizationsViewProvider,
  OrganizationByMemberViewImpl,
  OrganizationByMemberViewProvider,
  OrganizationByOwnerViewImpl,
  OrganizationByOwnerViewProvider
}
import app.improving.organizationcontext.organization.{
  OrganizationAPI,
  OrganizationAPIProvider
}
import app.improving.productcontext.{
  AllProductsViewImpl,
  AllProductsViewProvider,
  TicketByEventTimeQueryView,
  TicketByEventTimeQueryViewProvider,
  TicketByEventViewImpl,
  TicketByEventViewProvider
}
import app.improving.productcontext.product.{ProductAPI, ProductAPIProvider}
import app.improving.storecontext.{AllStoresViewImpl, AllStoresViewProvider}
import app.improving.storecontext.store.{StoreAPI, StoreAPIProvider}
import app.improving.tenantcontext.{AllTenantsViewImpl, AllTenantsViewProvider}
import app.improving.tenantcontext.tenant.{TenantAPI, TenantAPIProvider}
import kalix.scalasdk.Kalix
import kalix.scalasdk.action.ActionCreationContext
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext
import kalix.scalasdk.replicatedentity.ReplicatedEntityContext
import kalix.scalasdk.view.ViewCreationContext

object ManualKalixFactory {

  def withComponents(
      createMegaActionImpl: ActionCreationContext => MegaActionImpl,
      createTenantAPI: EventSourcedEntityContext => TenantAPI,
      createAllTenantsViewImpl: ViewCreationContext => AllTenantsViewImpl,
      createOrganizationsApi: EventSourcedEntityContext => OrganizationAPI,
      createAllOrganizationsViewImpl: ViewCreationContext => AllOrganizationsViewImpl,
      createOrganizationsByMemberViewImpl: ViewCreationContext => OrganizationByMemberViewImpl,
      createOrganizationByOwnerViewImpl: ViewCreationContext => OrganizationByOwnerViewImpl,
      createMemberAPI: EventSourcedEntityContext => MemberAPI,
      createMemberMap: ReplicatedEntityContext => MemberMap,
      createAllMembersViewImpl: ViewCreationContext => AllMembersViewImpl,
      createMemberActionServiceImpl: ActionCreationContext => MemberActionServiceImpl,
      createMemberByMemberIdsQueryView: ViewCreationContext => MemberByMemberIdsQueryView,
      createMemberByMetaInfoViewImpl: ViewCreationContext => MemberByMetaInfoViewImpl,
      createMemberByOrderQueryView: ViewCreationContext => MemberByOrderQueryView,
      createEventAPI: EventSourcedEntityContext => EventAPI,
      createAllEventsViewImpl: ViewCreationContext => AllEventsViewImpl,
      createStoreAPI: EventSourcedEntityContext => StoreAPI,
      createAllStoresViewImpl: ViewCreationContext => AllStoresViewImpl,
      createProductAPI: EventSourcedEntityContext => ProductAPI,
      createAllProductsViewImpl: ViewCreationContext => AllProductsViewImpl,
      createTicketByEventTimeQueryView: ViewCreationContext => TicketByEventTimeQueryView,
      createTicketByEventViewImpl: ViewCreationContext => TicketByEventViewImpl,
      createOrderAPI: EventSourcedEntityContext => OrderAPI,
      createAllOrdersViewImpl: ViewCreationContext => AllOrdersViewImpl,
      createOrderActionImpl: ActionCreationContext => OrderActionImpl,
      createOrderByProductQueryView: ViewCreationContext => OrderByProductQueryView
  ): Kalix = {
    val kalix = Kalix()
    kalix
      .register(MegaActionProvider(createMegaActionImpl))
      .register(TenantAPIProvider(createTenantAPI))
      .register(AllTenantsViewProvider(createAllTenantsViewImpl))
      .register(OrganizationAPIProvider(createOrganizationsApi))
      .register(AllOrganizationsViewProvider(createAllOrganizationsViewImpl))
      .register(
        OrganizationByMemberViewProvider(createOrganizationsByMemberViewImpl)
      )
      .register(
        OrganizationByOwnerViewProvider(createOrganizationByOwnerViewImpl)
      )
      .register(
        MemberAPIProvider(createMemberAPI)
      )
      .register(
        MemberMapProvider(createMemberMap)
      )
      .register(
        AllMembersViewProvider(createAllMembersViewImpl)
      )
      .register(
        MemberActionServiceActionProvider(createMemberActionServiceImpl)
      )
      .register(
        MemberByMemberIdsQueryViewProvider(createMemberByMemberIdsQueryView)
      )
      .register(
        MemberByMetaInfoViewProvider(createMemberByMetaInfoViewImpl)
      )
      .register(
        MemberByOrderQueryViewProvider(createMemberByOrderQueryView)
      )
      .register(EventAPIProvider(createEventAPI))
      .register(AllEventsViewProvider(createAllEventsViewImpl))
      .register(StoreAPIProvider(createStoreAPI))
      .register(AllStoresViewProvider(createAllStoresViewImpl))
      .register(ProductAPIProvider(createProductAPI))
      .register(AllProductsViewProvider(createAllProductsViewImpl))
      .register(
        TicketByEventTimeQueryViewProvider(createTicketByEventTimeQueryView)
      )
      .register(TicketByEventViewProvider(createTicketByEventViewImpl))
      .register(OrderAPIProvider(createOrderAPI))
      .register(AllOrdersViewProvider(createAllOrdersViewImpl))
      .register(OrderActionProvider(createOrderActionImpl))
      .register(OrderByProductQueryViewProvider(createOrderByProductQueryView))
  }
}
