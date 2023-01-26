package app.improving.ordercontext.order

import app.improving.ApiOrderId
import app.improving.eventcontext.event.ApiGetEventById
import app.improving.organizationcontext.organization.ApiGetOrganizationById
import app.improving.productcontext.product.ApiGetProductInfo
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

import scala.concurrent.Future

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

  override def purchaseTicket(
      order: ApiCreateOrder
  ): Action.Effect[ApiOrderId] = {
    println(s"----------- in purchaseTicket")
    val orderInfo = order.info.getOrElse(ApiOrderInfo())
    val productIds = orderInfo.lineItems.map(
      _.product.map(_.productId).getOrElse("ProductId is not found.")
    )
    val memberId = order.creatingMember
      .map(_.memberId)
      .getOrElse("MemberId is not found.")

    val productInfoResults = Future.sequence(productIds.map(sku => {
      val eventIdFut =
        for {
          apiProductInfo <- components.productAPI
            .getProductInfo(ApiGetProductInfo(sku))
            .execute()
        } yield apiProductInfo.info
          .flatMap(_.event)
          .map(_.eventId)
          .getOrElse("EventId is not found.")

      val tupleFut = (for {
        eventId <- eventIdFut
        event <- components.eventAPI
          .getEventById(ApiGetEventById(eventId))
          .execute()
      } yield {
        println(event + " -------------")
        println(eventId + " ------------- eventId")
        (
          event.info.map(_.isPrivate) == Some(false),
          event.info
            .flatMap(_.sponsoringOrg)
        )
      })

      // true -> event is not private (private is false)
      tupleFut.flatMap(tuple => {
        println(tuple + " =================tuple")
        tuple match {
          case (true, _) => Future.successful(true)
          case (false, Some(orgId)) => {
            for {
              organization <- components.organizationAPI
                .getOrganization(
                  ApiGetOrganizationById(orgId.organizationId)
                )
                .execute()
            } yield {
              println(
                organization.memberIds + " organization.memberIds ------------"
              )
              println(memberId + " memberId 0000000000000")
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

    val orderValidFut = productInfoResults.map(seq => seq.forall(x => x))
    val call = components.orderAPI.createOrder(order)

    println(s"----------- ${orderValidFut}")
    effects.asyncEffect(
      orderValidFut.map(valid =>
        valid match {
          case true => {
            println("in true branch")
            effects.forward(call)
          }
          case false =>
            effects.error(
              "The purchase is not allowed - The event is private and the buyer is not a member of the organizer."
            )
        }
      )
    )
  }
}
