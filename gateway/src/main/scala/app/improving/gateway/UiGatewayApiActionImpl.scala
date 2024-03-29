package app.improving.gateway

import akka.actor.ActorSystem
import app.improving.eventcontext.{
  AllEventsRequest,
  AllEventsResult,
  AllEventsView
}
import app.improving.membercontext.{
  AllMembersRequest,
  AllMembersResult,
  AllMembersView
}
import app.improving.membercontext.member.{
  MemberActionService,
  MembersByEventTimeRequest,
  MembersByEventTimeResponse
}
import app.improving.ordercontext.{
  AllOrdersRequest,
  AllOrdersResult,
  AllOrdersView
}
import app.improving.{ApiMemberId, ApiOrderIds, ApiStoreId}
import app.improving.ordercontext.order.{ApiCreateOrder, OrderAction}
import app.improving.organizationcontext.{
  AllOrganizationsRequest,
  AllOrganizationsResult,
  AllOrganizationsView
}
import app.improving.productcontext.{
  AllProductsRequest,
  AllProductsResult,
  AllProductsView
}
import app.improving.productcontext.product.ProductService
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

class UiGatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractUiGatewayApiAction {

  private implicit val system: ActorSystem = ActorSystem("GatewayApiActionImpl")

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val productService: ProductService = creationContext.getGrpcClient(
    classOf[ProductService],
    config.getString(
      "app.improving.gateway.product.grpc-client-name"
    )
  )

  val orderAction: OrderAction = creationContext.getGrpcClient(
    classOf[OrderAction],
    config.getString(
      "app.improving.gateway.order.grpc-client-name"
    )
  )

  val memberAction: MemberActionService = creationContext.getGrpcClient(
    classOf[MemberActionService],
    config.getString(
      "app.improving.gateway.member.grpc-client-name"
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

  override def handleGetMembersByEventTime(
      membersByEventTimeRequest: MembersByEventTimeRequest
  ): Action.Effect[MembersByEventTimeResponse] = {

    log.info("in handleGetMembersByEventTime")

    effects.asyncReply(
      memberAction.findMembersByEventTime(
        MembersByEventTimeRequest(membersByEventTimeRequest.givenTime)
      )
    )
  }

  override def handlePurchaseTickets(
      purchaseTicketRequest: PurchaseTicketsRequest
  ): Action.Effect[ApiOrderIds] = {

    log.info(s"in handlePurchaseTicket $purchaseTicketRequest")
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
}
