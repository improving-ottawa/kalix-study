package app.improving.gateway

import app.improving.membercontext.member.{ApiRegisterMember, MemberService}
import app.improving.ordercontext.order.{ApiCreateOrder, OrderAction}
import app.improving.eventcontext.event.{ApiScheduleEvent, EventService}
import app.improving.organizationcontext.organization.{
  ApiEstablishOrganization,
  OrganizationService
}
import app.improving.storecontext.store.{ApiCreateStore, StoreService}
import app.improving.productcontext.product.{ApiCreateProduct, ProductService}
import app.improving.tenantcontext.tenant.{ApiEstablishTenant, TenantService}
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.StatusRuntimeException
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success}

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class GatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractGatewayApiAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  log.info(
    config.getString(
      "app.improving.gateway.tenant.grpc-client-name"
    ) + " config.getString(\"app.improving.gateway.tenant.grpc-client-name\")"
  )
  val tenantService =
    creationContext.getGrpcClient(
      classOf[TenantService],
      config.getString("app.improving.gateway.tenant.grpc-client-name")
    )

  log.info(
    config.getString(
      "app.improving.gateway.organization.grpc-client-name"
    ) + " config.getString(\"app.improving.gateway.organization.grpc-client-name\")"
  )
  val organizationService = creationContext.getGrpcClient(
    classOf[OrganizationService],
    config.getString(
      "app.improving.gateway.organization.grpc-client-name"
    )
  )

  val eventService = creationContext.getGrpcClient(
    classOf[EventService],
    config.getString(
      "app.improving.gateway.event.grpc-client-name"
    )
  )

  val storeService = creationContext.getGrpcClient(
    classOf[StoreService],
    config.getString(
      "app.improving.gateway.store.grpc-client-name"
    )
  )

  val productService = creationContext.getGrpcClient(
    classOf[ProductService],
    config.getString(
      "app.improving.gateway.product.grpc-client-name"
    )
  )

  val memberService = creationContext.getGrpcClient(
    classOf[MemberService],
    config.getString(
      "app.improving.gateway.member.grpc-client-name"
    )
  )

  val orderAction = creationContext.getGrpcClient(
    classOf[OrderAction],
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
            ),
            createOrganization.establishOrganization.flatMap(_.meta)
          )
        )
        .map(id => OrganizationCreated(Some(id)))
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
                    establishOrganization.establishingMember,
                    establishOrganization.meta
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
            createStore.info.map(
              _.copy(
                storeId = storeId
              )
            ),
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
              Some(
                info.copy(
                  storeId = storeId
                )
              ),
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
            createProduct.establishProduct
              .flatMap(_.info.map(_.copy(sku = sku))),
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
                    establishProduct.info.map(_.copy(sku = sku)),
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
            createOrder.establishOrder
              .flatMap(_.info.map(_.copy(orderId = orderId))),
            createOrder.establishOrder.flatMap(_.creatingMember)
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
                  establishOrder.info.map(_.copy(orderId = orderId)),
                  establishOrder.creatingMember
                )
              )
          })
        )
        .map(OrdersCreated(_))
    )
  }
}
