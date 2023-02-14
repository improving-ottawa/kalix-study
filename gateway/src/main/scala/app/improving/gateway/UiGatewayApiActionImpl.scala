package app.improving.gateway

import app.improving.{ApiMemberId, ApiOrderIds, ApiStoreId}
import app.improving.ordercontext.order.{ApiCreateOrder, OrderAction}
import app.improving.productcontext.product.ProductService
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.concurrent.Future
import scala.language.postfixOps

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class UiGatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractUiGatewayApiAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val productService: ProductService = creationContext.getGrpcClient(
    classOf[ProductService],
    config.getString(
      "app.improving.gateway.product.grpc-client-name"
    )
  )

  val orderAction = creationContext.getGrpcClient(
    classOf[OrderAction],
    config.getString(
      "app.improving.gateway.order.grpc-client-name"
    )
  )

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
}
