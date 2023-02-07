package app.improving.projections

import app.improving.eventcontext.{
  EventCancelled,
  EventDelayed,
  EventEnded,
  EventRescheduled,
  EventScheduled,
  EventStarted,
  EventStatus
}
import app.improving.membercontext.{
  MemberRegistered,
  MemberStatus,
  MemberStatusUpdated
}
import app.improving.ordercontext.{
  LineItemCancelled,
  LineItemOrdered,
  OrderCreated,
  OrderStatus,
  OrderStatusUpdated
}
import app.improving.productcontext.{
  ProductActivated,
  ProductCreated,
  ProductDeleted,
  ProductInactivated,
  ProductStatus
}
import com.google.`type`.Date
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MembersAttendingEventsForAnOrganizationView(context: ViewContext)
    extends AbstractMembersAttendingEventsForAnOrganizationView {
  object MembersViewTable extends AbstractMembersViewTable {

    override def emptyState: MembersTableRow = MembersTableRow.defaultInstance

    override def processMemberRegistered(
        state: MembersTableRow,
        memberRegistered: MemberRegistered
    ): UpdateEffect[MembersTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        MembersTableRow(
          memberRegistered.memberId,
          s"${memberRegistered.info.get.firstName} ${memberRegistered.info.get.lastName}"
        )
      )

    override def processMemberStatusUpdated(
        state: MembersTableRow,
        memberStatusUpdated: MemberStatusUpdated
    ): UpdateEffect[MembersTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status =
          memberStatusUpdated.meta
            .map(_.memberStatus)
            .getOrElse(MemberStatus.UNKNOWN)
        )
      )
  }

  object EventOrgCorrViewTable extends AbstractEventOrgCorrViewTable {

    override def emptyState: EventOrgCorrTableRow =
      EventOrgCorrTableRow.defaultInstance

    override def processEventScheduledForCorrTable(
        state: EventOrgCorrTableRow,
        eventScheduled: EventScheduled
    ): UpdateEffect[EventOrgCorrTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        EventOrgCorrTableRow(
          eventScheduled.eventId,
          eventScheduled.info.get.sponsoringOrg
        )
      )
  }

  object EventsViewTable extends AbstractEventsViewTable {

    override def emptyState: EventsTableRow = EventsTableRow.defaultInstance

    override def processEventScheduledForEventsTable(
        state: EventsTableRow,
        eventScheduled: EventScheduled
    ): UpdateEffect[EventsTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        EventsTableRow(
          eventScheduled.eventId,
          eventScheduled.info.get.eventName
        )
      )

    override def processEventCancelledForEventsTable(
        state: EventsTableRow,
        eventCancelled: EventCancelled
    ): UpdateEffect[EventsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.deleteState()

    override def processEventStartedForEventsTable(
        state: EventsTableRow,
        eventStarted: EventStarted
    ): UpdateEffect[EventsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        EventsTableRow(
          eventStarted.eventId,
          eventStarted.info.map(_.eventName).getOrElse(""),
          eventStarted.info.map(info =>
            Date.parseFrom(
              info.expectedStart
                .map(_.toString)
                .getOrElse(Timestamp.defaultInstance.toString)
                .getBytes()
            )
          )
        )
      )

    override def processEventRescheduled(
        state: EventsTableRow,
        eventRescheduled: EventRescheduled
    ): UpdateEffect[EventsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status =
          eventRescheduled.meta
            .map(_.status)
            .getOrElse(EventStatus.UNKNOWN)
        )
      )

    override def processEventDelayed(
        state: EventsTableRow,
        eventDelayed: EventDelayed
    ): UpdateEffect[EventsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status =
          eventDelayed.meta
            .map(_.status)
            .getOrElse(EventStatus.UNKNOWN)
        )
      )

    override def processEventEnded(
        state: EventsTableRow,
        eventEnded: EventEnded
    ): UpdateEffect[EventsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status =
          eventEnded.meta
            .map(_.status)
            .getOrElse(EventStatus.UNKNOWN)
        )
      )
  }

  object TicketEventCorrViewTable extends AbstractTicketEventCorrViewTable {

    override def emptyState: TicketEventCorrTableRow =
      TicketEventCorrTableRow.defaultInstance

    override def processProductCreated(
        state: TicketEventCorrTableRow,
        productCreated: ProductCreated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        TicketEventCorrTableRow(
          productCreated.sku,
          productCreated.info.get.event
        )
      )

    override def processProductActivated(
        state: TicketEventCorrTableRow,
        productActivated: ProductActivated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(ticketStatus = ProductStatus.ACTIVE)
      )

    override def processProductInactivated(
        state: TicketEventCorrTableRow,
        productInactivated: ProductInactivated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(ticketStatus = ProductStatus.INACTIVE)
      )

    override def processProductDeleted(
        state: TicketEventCorrTableRow,
        productDeleted: ProductDeleted
    ): UpdateEffect[TicketEventCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.deleteState()
  }

  object TicketMemberCorrViewTable extends AbstractTicketMemberCorrViewTable {

    override def emptyState: TicketMemberCorrTableRow =
      TicketMemberCorrTableRow.defaultInstance

    override def processLineItemCreated(
        state: TicketMemberCorrTableRow,
        lineItemOrdered: LineItemOrdered
    ): UpdateEffect[TicketMemberCorrTableRow] = if (state != emptyState)
      effects.ignore() // already created
    else
      effects.updateState(
        TicketMemberCorrTableRow(
          lineItemOrdered.productId,
          lineItemOrdered.forMemberId,
          lineItemOrdered.orderId
        )
      )

    override def processLineItemCancelled(
        state: TicketMemberCorrTableRow,
        lineItemCancelled: LineItemCancelled
    ): UpdateEffect[TicketMemberCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.deleteState()
  }

  object OrdersViewTable extends AbstractOrdersViewTable {
    override def emptyState: OrderTableRow = OrderTableRow.defaultInstance

    override def processOrderCreated(
        state: OrderTableRow,
        orderCreated: OrderCreated
    ): UpdateEffect[OrderTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        OrderTableRow(orderCreated.orderId, OrderStatus.ORDER_STATUS_DRAFT)
      )

    override def processOrderStatusUpdated(
        state: OrderTableRow,
        orderStatusUpdated: OrderStatusUpdated
    ): UpdateEffect[OrderTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status = orderStatusUpdated.newStatus)
      )

  }
}
