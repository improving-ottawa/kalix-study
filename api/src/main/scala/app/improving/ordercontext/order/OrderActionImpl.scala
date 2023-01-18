package app.improving.ordercontext.order

import app.improving.ApiOrganizationId
import app.improving.eventcontext.event.ApiGetEventById
import app.improving.organizationcontext.organization.ApiGetOrganizationById
import app.improving.productcontext.product.ApiGetProductInfo
import com.google.protobuf.empty.Empty
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
  ): Action.Effect[Empty] = {
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

      for {
        eventId <- eventIdFut
        event <- components.eventAPI
          .getEventById(ApiGetEventById(eventId))
          .execute()
        organization = event.info
          .flatMap(_.sponsoringOrg)
          .getOrElse(ApiOrganizationId("OrganizationId is not found."))
        organization <- components.organizationAPI
          .getOrganization(
            ApiGetOrganizationById(organization.organizationId)
          )
          .execute()
      } yield {
        if (event.info.map(_.isPrivate) == Some(false)) {
          true
        } else if (organization.memberIds.contains(memberId)) {
          true
        } else {
          false
        }
      }
    }))

    val orderValidFut = productInfoResults.map(seq => seq.forall(x => x))
    val call = components.orderAPI.createOrder(order)

    effects.asyncEffect(
      orderValidFut.map(valid =>
        valid match {
          case true => effects.forward(call)
          case false =>
            effects.error(
              "The purchase is not allowed - The event is private and the buyer is not a member of the organizer."
            )
        }
      )
    )
  }
}
