package app.improving.ordercontext.order

import app.improving.ApiOrderId
import app.improving.eventcontext.event.{ApiGetEventById, EventService}
import app.improving.organizationcontext.organization.{
  ApiGetOrganizationById,
  OrganizationService
}
import app.improving.productcontext.product.{ApiGetProductInfo, ProductService}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}

////////////////////////////////////////////////////////////////////////////////////////
// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.
// Steps to check before the purchase:
//
// Get ProductIds from Order
// Get MemberId from Order
// Get Product with ProductId
// Get EventId from Product
// Get Event with EventId and get isPrivate flag.
// If yes -> get OrganizationId to get Organization to check memberId is in members ->
//           if yes -> allow else not allow
// If no -> allow the purchase
////////////////////////////////////////////////////////////////////////////////////////
class OrderActionImpl(creationContext: ActionCreationContext)
    extends AbstractOrderAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def purchaseTicket(
      order: ApiCreateOrder
  ): Action.Effect[ApiOrderId] = {

    log.info(s"in purchaseTicket - ${order} : ApiCreateOrder")

    val event =
      creationContext.getGrpcClient(classOf[EventService], "kalix-study-event")
    val product =
      creationContext.getGrpcClient(
        classOf[ProductService],
        "kalix-study-product"
      )
    val organization = creationContext.getGrpcClient(
      classOf[OrganizationService],
      "kalix-study-org"
    )
    val orderInfo = order.info.getOrElse(ApiOrderInfo())
    val productIds = orderInfo.lineItems.map(
      _.product.map(_.productId).getOrElse("ProductId is not found.")
    )

    val memberId = order.creatingMember
      .map(_.memberId)
      .getOrElse("MemberId is not found.")

    val productInfoResults = Future.sequence(productIds.map(sku => {
      val eventIdFut =
        (for {
          apiProductInfo <- product
            .getProductInfo(ApiGetProductInfo(sku))
        } yield apiProductInfo.info
          .flatMap(_.event)
          .map(_.eventId)
          .getOrElse("EventId is not found.")).transformWith {
          case Success(id) => Future.successful(id)
          case Failure(exception) =>
            Future.failed(
              new IllegalStateException(
                exception.getMessage + s" product.getProductInfo for ${sku} failed"
              )
            )
        }

      val tupleFut = (for {
        eventId <- eventIdFut
        event <- event
          .getEventById(ApiGetEventById(eventId))
      } yield {
        (
          event.info.map(_.isPrivate) == Some(false),
          event.info
            .flatMap(_.sponsoringOrg)
        )
      }).transformWith {
        case Success(result) => Future.successful(result)
        case Failure(exception) => {
          log.error(
            s"Error in OrderActionImpl - event.getEventById - Error: ${exception.getMessage}"
          )
          Future.failed(
            new IllegalStateException(
              exception.getMessage + s" and event.getEventById failed"
            )
          )
        }
      }

      // true -> event is not private (private is false)
      tupleFut.flatMap(tuple => {
        tuple match {
          case (true, _) => Future.successful(true)
          case (false, Some(orgId)) => {
            for {
              organization <- organization
                .getOrganization(
                  ApiGetOrganizationById(orgId.organizationId)
                )
            } yield {
              if (organization.memberIds.contains(memberId)) {
                true
              } else {
                false
              }
            }
          }
          case (false, None) => Future.successful(false)
        }
      })
    }))

    val orderValidFut = productInfoResults.map(seq =>
      if (seq.isEmpty) false else seq.forall(x => x)
    )
    val call = components.orderAPI.createOrder(order)

    effects.asyncEffect(
      orderValidFut.transform(
        {
          case true => effects.forward(call)
          case false =>
            effects.error(
              "The purchase is not allowed - The event is private and the buyer is not a member of the organizer. " +
                s"Please make sure you provide the correct record for the order - ${order}"
            )
        },
        error => {
          throw new IllegalStateException(error.getMessage)
        }
      ) recoverWith ({
        case exception: Exception => {
          log.error(
            s"Error in OrderActionImpl - orderValidFut.transform - Error: ${exception.getMessage}"
          )
          Future.successful(
            effects.error(
              s"The purchase is not allowed - Errors: ${exception.getMessage}"
            )
          )
        }
      })
    )
  }
}
