package app.improving.projections

import app.improving.eventcontext.{EventCancelled, EventDelayed, EventEnded, EventMetaInfo, EventRescheduled, EventScheduled, EventStarted, EventStatus}
import app.improving.membercontext.{MemberRegistered, MemberStatus, MemberStatusUpdated}
import app.improving.ordercontext.{LineItemCancelled, LineItemOrdered, OrderCreated, OrderStatus, OrderStatusUpdated}
import app.improving.productcontext.{ProductActivated, ProductCreated, ProductDeleted, ProductInactivated, ProductMetaInfo, ProductStatus}
import com.google.`type`.Date
import com.google.protobuf.duration.Duration
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

import java.time.{Instant, ZoneId}
import java.time.temporal.ChronoField

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
          s"${memberRegistered.info.get.firstName} ${memberRegistered.info.get.lastName}",
          memberRegistered.meta.map(_.memberStatus).getOrElse(MemberStatus.UNKNOWN)
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

    override def processEventCancelledForCorrTable(state: EventOrgCorrTableRow, eventScheduled: EventScheduled): UpdateEffect[EventOrgCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.deleteState()
  }


  object EventsViewTable extends AbstractEventsViewTable {

    override def emptyState: EventsTableRow = EventsTableRow.defaultInstance

    private def getDateFromTimestamp(ts: Timestamp): Date = {
      val localDate = Instant.ofEpochSecond(ts.seconds, ts.nanos).atZone(ZoneId.systemDefault()).toLocalDate
      Date(localDate.getYear, localDate.getMonthValue, localDate.getDayOfMonth)
    }

    private def getStatusOrUnknown(meta: Option[EventMetaInfo]): EventStatus = {
      meta.map(_.status).getOrElse(EventStatus.UNKNOWN)
    }

    override def processEventScheduledForEventsTable(
        state: EventsTableRow,
        eventScheduled: EventScheduled
    ): UpdateEffect[EventsTableRow] = if (state != emptyState)
      effects.ignore()
    else {

      val date: Option[Date] = eventScheduled.info.flatMap(_.expectedStart.map(getDateFromTimestamp))

      effects.updateState(
        EventsTableRow(
          eventScheduled.eventId,
          eventScheduled.info.get.eventName,
          eventDate = date,
          status = getStatusOrUnknown(eventScheduled.meta),
          eventOrgId = eventScheduled.info.flatMap(_.sponsoringOrg)
        )
      )
    }

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
    ): UpdateEffect[EventsTableRow] = {
      if (state == emptyState)
        effects.ignore()
      else {
        val newDate: Option[Date] = Some(getDateFromTimestamp(Timestamp(Instant.now.getEpochSecond)))

        effects.updateState(
          state.copy(eventDate = newDate,
            status = getStatusOrUnknown(eventStarted.meta)),
          )
      }
    }

    override def processEventRescheduled(
        state: EventsTableRow,
        eventRescheduled: EventRescheduled
    ): UpdateEffect[EventsTableRow] = {
      if (state == emptyState)
        effects.ignore()
      else {
        val newDate = eventRescheduled.info.flatMap(_.expectedStart.map(getDateFromTimestamp))
        effects.updateState(
          state.copy(eventDate = newDate,
            status = getStatusOrUnknown(eventRescheduled.meta)
          )
        )
      }
    }



    override def processEventDelayed(
        state: EventsTableRow,
        eventDelayed: EventDelayed
    ): UpdateEffect[EventsTableRow] = {
      if (state == emptyState)
        effects.ignore()
      else {
        //TODO: I don't think it's actually possible to calculate if there's a new date from the information we have; we're only storing a date so no way to tell if the duration pushes it over.
        //probably we should do the calculation in the command handler and have the event handler just copy over the new expectedStart etc in EventApi, and then here we can likewise just pick up on the date.
        //val duration: Duration = eventDelayed.expectedDuration
        //eventDelayed.expectedDuration.
        //val newDate = eventDelayed..info.flatMap(_.expectedStart.map(getDateFromTimestamp))
        effects.updateState(
          state.copy(status =
            getStatusOrUnknown(eventDelayed.meta)
          )
        )
      }
    }

    override def processEventEnded(
        state: EventsTableRow,
        eventEnded: EventEnded
    ): UpdateEffect[EventsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status = getStatusOrUnknown(eventEnded.meta)
        )
      )
  }

  object TicketEventCorrViewTable extends AbstractTicketEventCorrViewTable {

    override def emptyState: TicketEventCorrTableRow =
      TicketEventCorrTableRow.defaultInstance

    //TODO: Product doesn't have status in metaInfo, which means most of the events won't have it & we have to replicate the logic, which seems dangerous+unneccessary; might want to put it in meta instead.

    override def processProductCreated(
        state: TicketEventCorrTableRow,
        productCreated: ProductCreated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        TicketEventCorrTableRow(
          productCreated.sku,
          productCreated.info.get.event,
          ProductStatus.ACTIVE //Active is the default status, and also what gets set for ProductCreated in ProductAPI
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
