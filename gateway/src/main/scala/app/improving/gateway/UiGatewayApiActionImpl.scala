package app.improving.gateway

import app.improving.{ApiMemberId, ApiOrderId, ApiProductId, ApiStoreId}
import app.improving.ordercontext.order.{
  ApiCreateOrder,
  ApiLineItem,
  ApiOrder,
  ApiOrderInfo,
  OrderAction
}
import app.improving.productcontext.product.{ApiGetProductInfo, ProductService}
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.Random
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

  override def handlePurchaseTicket(
      purchaseTicketRequest: PurchaseTicketRequest
  ): Action.Effect[ApiOrderId] = {

    log.info("in handlePurchaseTicket")
    val r = new Random()
    val storeId = purchaseTicketRequest.storeIds
      .map(stores =>
        stores.storeIds.toIndexedSeq(r.nextInt(stores.storeIds.size))
      )
      .getOrElse(ApiStoreId("StoreId is NOT FOUND."))
    val products =
      purchaseTicketRequest.productsForStores
        .get(storeId.storeId)
        .map(_.skus)
        .getOrElse(Seq.empty[ApiProductId])
    val product = Await.result(
      productService
        .getProductInfo(
          ApiGetProductInfo(
            products.toIndexedSeq(r.nextInt(products.size)).productId
          )
        ),
      10 seconds
    )
    val memberIds =
      purchaseTicketRequest.membersForOrgs.values.map(_.memberIds).flatten
    val memberId =
      memberIds.toIndexedSeq(r.nextInt(memberIds.size))
    val orderId = UUID.randomUUID().toString
    effects.asyncReply(
      orderAction.purchaseTicket(
        ApiCreateOrder(
          orderId,
          Some(
            ApiOrderInfo(
              orderId,
              Seq[ApiLineItem](
                ApiLineItem(
                  Some(ApiProductId(product.sku)),
                  1,
                  product.info.map(_.price).getOrElse(0.0)
                )
              ),
              r.nextString(15),
              1.0 * product.info.map(_.price).getOrElse(0.0)
            )
          ),
          Some(memberId),
          Some(storeId)
        )
      )
    )

  }
}
