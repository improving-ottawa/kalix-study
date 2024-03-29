package app.improving.gateway

import akka.actor.ActorSystem
import app.improving.eventcontext.{
  AllEventsRequest,
  AllEventsResult,
  AllEventsView
}
import app.improving.{
  ApiEventId,
  ApiMemberId,
  ApiOrderId,
  ApiOrderIds,
  ApiOrganizationId,
  ApiSku,
  ApiStoreId,
  ApiTenantId,
  OrganizationId
}
import app.improving.eventcontext.event.{
  ApiEvent,
  ApiGetEventById,
  ApiReleaseEvent,
  ApiScheduleEvent,
  EventService
}
import app.improving.membercontext.{
  AllMembersRequest,
  AllMembersResult,
  AllMembersView
}
import app.improving.membercontext.member.{
  ApiRegisterMember,
  ApiReleaseMember,
  MemberService
}
import app.improving.ordercontext.{
  AllOrdersRequest,
  AllOrdersResult,
  AllOrdersView,
  OrderByProductQuery,
  OrderByProductRequest,
  OrderByProductResponse
}
import app.improving.ordercontext.order.{
  ApiCreateOrder,
  ApiReleaseOrder,
  OrderAction,
  OrderService
}
import app.improving.organizationcontext._
import app.improving.organizationcontext.organization.{
  ApiEstablishOrganization,
  ApiReleaseOrganization,
  OrganizationService
}
import app.improving.productcontext.product.{
  ApiCreateProduct,
  ApiGetProductInfo,
  ApiProductInfoResult,
  ApiReleaseProduct,
  ProductService
}
import app.improving.storecontext.store.{
  ApiCreateStore,
  ApiReleaseStore,
}
import app.improving.productcontext.{
  AllProductsRequest,
  AllProductsResult,
  AllProductsView
}
import app.improving.storecontext.store.StoreService
import app.improving.tenantcontext.tenant.{ApiEstablishTenant, TenantService}
import app.improving.storecontext.{
  AllStoresRequest,
  AllStoresResult,
  AllStoresView
}
import app.improving.tenantcontext.{
  AllTenantResult,
  AllTenantsView,
  GetAllTenantRequest
}
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.concurrent.Future

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class CreationGatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractCreationGatewayApiAction {

  private implicit val system: ActorSystem = ActorSystem("GatewayApiActionImpl")

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  log.info(
    config.getString(
      "app.improving.gateway.tenant.grpc-client-name"
    ) + " config.getString(\"app.improving.gateway.tenant.grpc-client-name\")"
  )
  val tenantService: TenantService =
    creationContext.getGrpcClient(
      classOf[TenantService],
      config.getString("app.improving.gateway.tenant.grpc-client-name")
    )

  log.info(
    config.getString(
      "app.improving.gateway.organization.grpc-client-name"
    ) + " config.getString(\"app.improving.gateway.organization.grpc-client-name\")"
  )
  val organizationService: OrganizationService = creationContext.getGrpcClient(
    classOf[OrganizationService],
    config.getString(
      "app.improving.gateway.organization.grpc-client-name"
    )
  )

  val eventService: EventService = creationContext.getGrpcClient(
    classOf[EventService],
    config.getString(
      "app.improving.gateway.event.grpc-client-name"
    )
  )

  val storeService: StoreService = creationContext.getGrpcClient(
    classOf[StoreService],
    config.getString(
      "app.improving.gateway.store.grpc-client-name"
    )
  )

  val productService: ProductService = creationContext.getGrpcClient(
    classOf[ProductService],
    config.getString(
      "app.improving.gateway.product.grpc-client-name"
    )
  )

  val memberService: MemberService = creationContext.getGrpcClient(
    classOf[MemberService],
    config.getString(
      "app.improving.gateway.member.grpc-client-name"
    )
  )

  val orderService: OrderService = creationContext.getGrpcClient(
    classOf[OrderService],
    config.getString(
      "app.improving.gateway.order.grpc-client-name"
    )
  )

  val orderAction: OrderAction = creationContext.getGrpcClient(
    classOf[OrderAction],
    config.getString(
      "app.improving.gateway.order.grpc-client-name"
    )
  )

  private val allEventsView = creationContext.getGrpcClient(
    classOf[AllEventsView],
    config.getString(
      "app.improving.gateway.event.grpc-client-name"
    )
  )
  private val allOrganizationsView = creationContext.getGrpcClient(
    classOf[AllOrganizationsView],
    config.getString(
      "app.improving.gateway.organization.grpc-client-name"
    )
  )
  private val allTenantsView = creationContext.getGrpcClient(
    classOf[AllTenantsView],
    config.getString(
      "app.improving.gateway.tenant.grpc-client-name"
    )
  )

  private val allStoresView = creationContext.getGrpcClient(
    classOf[AllStoresView],
    config.getString(
      "app.improving.gateway.store.grpc-client-name"
    )
  )

  private val allProductsView = creationContext.getGrpcClient(
    classOf[AllProductsView],
    config.getString(
      "app.improving.gateway.product.grpc-client-name"
    )
  )

  private val allMembersView = creationContext.getGrpcClient(
    classOf[AllMembersView],
    config.getString(
      "app.improving.gateway.member.grpc-client-name"
    )
  )

  private val allOrdersView = creationContext.getGrpcClient(
    classOf[AllOrdersView],
    config.getString(
      "app.improving.gateway.order.grpc-client-name"
    )
  )

  private val orderByProductQuery = creationContext.getGrpcClient(
    classOf[OrderByProductQuery],
    config.getString(
      "app.improving.gateway.order.grpc-client-name"
    )
  )

  override def handleEstablishTenant(
      establishTenant: CreateTenant
  ): Action.Effect[TenantCreated] = {

    log.info("in handleEstablishTenant")

    effects.asyncReply(
      tenantService
        .establishTenant(
          ApiEstablishTenant(
            UUID.randomUUID().toString,
            establishTenant.tenantInfo
          )
        )
        .map(id => TenantCreated(Some(id)))
    )
  }

  override def handleEstablishTenants(
      createTenants: CreateTenants
  ): Action.Effect[TenantsCreated] = {

    log.info("in handleEstablishTenants")

    effects.asyncReply(
      Future
        .sequence(
          createTenants.tenantInfo.map(info =>
            tenantService.establishTenant(
              ApiEstablishTenant(
                UUID.randomUUID().toString,
                Some(info)
              )
            )
          )
        )
        .map(TenantsCreated(_))
    )
  }

  override def handleEstablishOrganization(
      createOrganization: CreateOrganization
  ): Action.Effect[OrganizationCreated] = {

    log.info("in handleEstablishOrganization")

    effects.asyncReply(
      organizationService
        .establishOrganization(
          ApiEstablishOrganization(
            UUID.randomUUID().toString,
            createOrganization.establishOrganization.flatMap(_.info),
            createOrganization.establishOrganization.flatMap(_.parent),
            createOrganization.establishOrganization
              .map(_.members)
              .toSeq
              .flatten,
            createOrganization.establishOrganization
              .map(_.owners)
              .toSeq
              .flatten,
            createOrganization.establishOrganization
              .map(_.contacts)
              .toSeq
              .flatten,
            createOrganization.establishOrganization.flatMap(
              _.establishingMember
            )
          )
        )
        .map(id => OrganizationCreated(Some(OrganizationId(id.organizationId))))
    )
  }

  override def handleEstablishOrganizations(
      createOrganizations: CreateOrganizations
  ): Action.Effect[OrganizationsCreated] = {
    log.info("in handleEstablishOrganizations")

    effects.asyncReply(
      Future
        .sequence(
          createOrganizations.establishOrganizations
            .map(establishOrganization => {
              organizationService
                .establishOrganization(
                  ApiEstablishOrganization(
                    UUID.randomUUID().toString,
                    establishOrganization.info,
                    establishOrganization.parent,
                    establishOrganization.members,
                    establishOrganization.owners,
                    establishOrganization.contacts,
                    establishOrganization.establishingMember
                  )
                )
            })
        )
        .map(OrganizationsCreated(_))
    )
  }

  override def handleScheduleEvent(
      createEvent: CreateEvent
  ): Action.Effect[EventCreated] = {

    log.info("in handleScheduleEvent")

    effects.asyncReply(
      eventService
        .scheduleEvent(
          ApiScheduleEvent(
            UUID.randomUUID().toString,
            createEvent.info,
            createEvent.schedulingMember
          )
        )
        .map(id => EventCreated(Some(id)))
    )
  }

  override def handleScheduleEvents(
      createEvents: CreateEvents
  ): Action.Effect[EventsCreated] = {

    log.info("in handleScheduleEvents")

    effects.asyncReply(
      Future
        .sequence(
          createEvents.infos
            .map(info => {
              eventService.scheduleEvent(
                ApiScheduleEvent(
                  UUID.randomUUID().toString,
                  Some(info),
                  createEvents.schedulingMember
                )
              )
            })
        )
        .map(EventsCreated(_))
    )
  }

  override def handleCreateStore(
      createStore: CreateStore
  ): Action.Effect[StoreCreated] = {

    log.info("in handleCreateStore")

    val storeId = UUID.randomUUID().toString
    effects.asyncReply(
      storeService
        .createStore(
          ApiCreateStore(
            storeId,
            createStore.info,
            createStore.creatingMember
          )
        )
        .map(id => StoreCreated(Some(id)))
    )
  }

  override def handleCreateStores(
      createStores: CreateStores
  ): Action.Effect[StoresCreated] = {
    log.info("in handleCreateStores")

    val storeId = UUID.randomUUID().toString
    effects.asyncReply(
      Future
        .sequence(createStores.infos.map(info => {
          storeService.createStore(
            ApiCreateStore(
              storeId,
              Some(info),
              createStores.creatingMember
            )
          )
        }))
        .map(StoresCreated(_))
    )
  }

  override def handleCreateProduct(
      createProduct: CreateProduct
  ): Action.Effect[ProductCreated] = {

    log.info("in handleCreateProduct")

    val sku = UUID.randomUUID().toString
    effects.asyncReply(
      productService
        .createProduct(
          ApiCreateProduct(
            sku,
            createProduct.establishProduct.flatMap(_.info),
            createProduct.establishProduct.flatMap(_.meta)
          )
        )
        .map(id => ProductCreated(Some(id)))
    )
  }

  override def handleCreateProducts(
      createProducts: CreateProducts
  ): Action.Effect[ProductsCreated] = {
    log.info("in handleCreateProducts")

    val sku = UUID.randomUUID().toString
    effects.asyncReply(
      Future
        .sequence(
          createProducts.establishProducts
            .map(establishProduct => {
              productService
                .createProduct(
                  ApiCreateProduct(
                    sku,
                    establishProduct.info,
                    establishProduct.meta
                  )
                )
            })
        )
        .map(ProductsCreated(_))
    )
  }

  override def handleRegisterMember(
      registerMember: RegisterMember
  ): Action.Effect[MemberRegistered] = {

    log.info("in handleRegisterMember")

    val memberId = UUID.randomUUID().toString
    effects.asyncReply(
      memberService
        .registerMember(
          ApiRegisterMember(
            memberId,
            registerMember.establishMember.flatMap(_.info),
            registerMember.establishMember.flatMap(_.registeringMember)
          )
        )
        .map(id => MemberRegistered(Some(id)))
    )
  }

  override def handleRegisterMembers(
      registerMembers: RegisterMembers
  ): Action.Effect[MembersRegistered] = {

    log.info("in handleRegisterMembers")

    val memberId = UUID.randomUUID().toString

    effects.asyncReply(
      Future
        .sequence(
          registerMembers.establishMembers.map(establishMember => {
            memberService
              .registerMember(
                ApiRegisterMember(
                  memberId,
                  establishMember.info,
                  establishMember.registeringMember
                )
              )
          })
        )
        .map(MembersRegistered(_))
    )
  }

  override def handleCreateOrder(
      createOrder: CreateOrder
  ): Action.Effect[OrderCreated] = {

    log.info("in handleCreateOrder")

    val orderId = UUID.randomUUID().toString

    effects.asyncReply(
      orderAction
        .purchaseTicket(
          ApiCreateOrder(
            orderId,
            createOrder.establishOrder.flatMap(_.info),
            createOrder.establishOrder.flatMap(_.creatingMember),
            createOrder.establishOrder.flatMap(_.storeId)
          )
        )
        .map(id => OrderCreated(Some(id)))
    )
  }

  override def handleCreateOrders(
      createOrders: CreateOrders
  ): Action.Effect[OrdersCreated] = {

    log.info("in handleCreateOrders")

    val orderId = UUID.randomUUID().toString
    effects.asyncReply(
      Future
        .sequence(
          createOrders.establishOrders.map(establishOrder => {
            orderAction
              .purchaseTicket(
                ApiCreateOrder(
                  orderId,
                  establishOrder.info,
                  establishOrder.creatingMember,
                  establishOrder.storeId
                )
              )
          })
        )
        .map(OrdersCreated(_))
    )
  }

  override def handleReleaseTenants(
      releaseTenants: ReleaseTenants
  ): Action.Effect[TenantsReleased] = {
    log.info("in releaseTenants")
    effects.asyncReply(
      Future
        .sequence(
          releaseTenants.releaseTenants.map { releaseTenant =>
            tenantService
              .releaseTenant(
                releaseTenant
              )
          }
        )
        .map(_ =>
          TenantsReleased(
            releaseTenants.releaseTenants.map(id => ApiTenantId(id.tenantId))
          )
        )
    )
  }

  override def handleReleaseOrganizations(
      releaseOrganizations: ReleaseOrganizations
  ): Action.Effect[OrganizationsReleased] = {

    log.info("in releaseTenants")
    effects.asyncReply(
      Future
        .sequence(
          releaseOrganizations.organizationIds.map { releaseOrg =>
            organizationService
              .releaseOrganization(
                ApiReleaseOrganization(releaseOrg.organizationId)
              )
          }
        )
        .map(_ =>
          OrganizationsReleased(
            releaseOrganizations.organizationIds
              .map(id => ApiOrganizationId(id.organizationId))
          )
        )
    )
  }

  override def handleReleaseEvents(
      releaseEvents: ReleaseEvents
  ): Action.Effect[EventsReleased] = {

    log.info("in releaseTenants")
    effects.asyncReply(
      Future
        .sequence(
          releaseEvents.eventIds.map { releaseEvent =>
            eventService
              .releaseEvent(
                ApiReleaseEvent(releaseEvent.eventId)
              )
          }
        )
        .map(_ =>
          EventsReleased(
            releaseEvents.eventIds.map(id => ApiEventId(id.eventId))
          )
        )
    )
  }

  override def handleReleaseStores(
      releaseStores: ReleaseStores
  ): Action.Effect[StoresReleased] = {

    log.info("in releaseTenants")
    effects.asyncReply(
      Future
        .sequence(
          releaseStores.storeIds.map { releaseStore =>
            storeService
              .releaseStore(
                ApiReleaseStore(releaseStore.storeId)
              )
          }
        )
        .map(_ =>
          StoresReleased(
            releaseStores.storeIds.map(id => ApiStoreId(id.storeId))
          )
        )
    )
  }

  override def handleReleaseProducts(
      releaseProducts: ReleaseProducts
  ): Action.Effect[ProductsReleased] = {
    log.info("in releaseTenants")
    effects.asyncReply(
      Future
        .sequence(
          releaseProducts.skus.map { releaseProduct =>
            productService
              .releaseProduct(
                ApiReleaseProduct(releaseProduct.sku)
              )
          }
        )
        .map(_ =>
          ProductsReleased(
            releaseProducts.skus.map(id => ApiSku(id.sku))
          )
        )
    )
  }

  override def handleReleaseMembers(
      releaseMembers: ReleaseMembers
  ): Action.Effect[MembersReleased] = {
    log.info("in releaseTenants")
    effects.asyncReply(
      Future
        .sequence(
          releaseMembers.members.map { releaseMember =>
            memberService
              .releaseMember(
                ApiReleaseMember(releaseMember.memberId)
              )
          }
        )
        .map(_ =>
          MembersReleased(
            releaseMembers.members.map(id => ApiMemberId(id.memberId))
          )
        )
    )
  }

  override def handleReleaseOrders(
      releaseOrders: ReleaseOrders
  ): Action.Effect[OrdersReleased] = {
    log.info("in releaseTenants")
    effects.asyncReply(
      Future
        .sequence(
          releaseOrders.orders.map { releaseOrder =>
            orderService.releaseOrder(
              ApiReleaseOrder(releaseOrder.orderId)
            )
          }
        )
        .map(_ =>
          OrdersReleased(
            releaseOrders.orders.map(id => ApiOrderId(id.orderId))
          )
        )
    )
  }

  override def handleGetAllEvents(
      allEventsRequest: AllEventsRequest
  ): Action.Effect[AllEventsResult] = {
    effects.asyncReply(allEventsView.getAllEvents(allEventsRequest))
  }

  override def handleGetAllOrganizations(
      allOrganizationsRequest: AllOrganizationsRequest
  ): Action.Effect[AllOrganizationsResult] = {

    log.info("in handleGetAllOrganizations")

    effects.asyncReply(
      allOrganizationsView
        .getAllOrganizations(AllOrganizationsRequest())
    )
  }

  override def handleGetAllTenants(
      getAllTenantRequest: GetAllTenantRequest
  ): Action.Effect[AllTenantResult] = {
    effects.asyncReply(allTenantsView.getAllTenants(GetAllTenantRequest()))
  }

  override def handleGetAllStores(
      allStoresRequest: AllStoresRequest
  ): Action.Effect[AllStoresResult] = {

    log.info("in handleRegisterMembers")

    effects.asyncReply(allStoresView.getAllStores(AllStoresRequest()))
  }

  override def handleGetAllProducts(
      allProductsRequest: AllProductsRequest
  ): Action.Effect[AllProductsResult] = {

    log.info("in handleRegisterMembers")

    effects.asyncReply(allProductsView.getAllSkus(AllProductsRequest()))
  }

  override def handleGetAllMembers(
      allMembersRequest: AllMembersRequest
  ): Action.Effect[AllMembersResult] = {

    log.info("in handleGetAllMembers")

    effects.asyncReply(
      allMembersView
        .getAllMembers(AllMembersRequest())
        .runFold(AllMembersResult.defaultInstance)((accum, elem) => {
          AllMembersResult(accum.members :+ elem)
        })
    )
  }

  override def handleGetAllOrders(
      allOrdersRequest: AllOrdersRequest
  ): Action.Effect[AllOrdersResult] = {

    log.info("in handleGetAllOrders")

    effects.asyncReply(allOrdersView.getAllOrders(AllOrdersRequest()))
  }

  override def handlePurchaseTickets(
      purchaseTicketRequest: PurchaseTicketsRequest
  ): Action.Effect[ApiOrderIds] = {

    log.info("in handlePurchaseTicket")
    effects
      .asyncReply(
        Future
          .sequence(purchaseTicketRequest.ordersForStoresForMembers.map {
            case (memberId, ordersForStores) =>
              Future
                .sequence(ordersForStores.ordersForStores.map {
                  case (storeId, orderInfo) =>
                    orderAction.purchaseTicket(
                      ApiCreateOrder(
                        UUID.randomUUID().toString,
                        Some(orderInfo),
                        Some(ApiMemberId(memberId)),
                        Some(ApiStoreId(storeId))
                      )
                    )
                }.toSeq)
          }.toSeq)
          .map(reqs => ApiOrderIds(reqs.flatten))
      )
  }

  override def handleGetProductInfoById(
      getProductInfoById: GetProductInfoById
  ): Action.Effect[ApiProductInfoResult] = {

    log.info("in handleGetProductInfoById")

    effects.asyncReply(
      productService
        .getProductInfo(
          ApiGetProductInfo(
            getProductInfoById.sku
          )
        )
    )
  }

  override def handleGetEventById(
      getEventById: GetEventById
  ): Action.Effect[ApiEvent] = {

    log.info("in handleGetEventById")

    effects.asyncReply(
      eventService.getEventById(
        ApiGetEventById(
          getEventById.eventId
        )
      )
    )
  }

  override def handleGetOrdersByProductId(
      getOrdersByProductId: GetOrdersByProductId
  ): Action.Effect[OrderByProductResponse] = {

    log.info("in handleGetOrdersByProductId")

    effects.asyncReply(
      orderByProductQuery.findOrdersByProducts(
        OrderByProductRequest(
          getOrdersByProductId.sku
        )
      )
    )
  }
}
