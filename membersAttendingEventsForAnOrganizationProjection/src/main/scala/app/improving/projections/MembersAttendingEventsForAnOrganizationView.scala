package app.improving.projections

import app.improving.eventcontext.EventCancelled
import app.improving.eventcontext.EventScheduled
import app.improving.membercontext.MemberRegistered
import app.improving.ordercontext.{LineItemCancelled, LineItemOrdered}
import app.improving.productcontext.ProductCreated
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

    override def processEventCancelledForCorrTable(
        state: EventOrgCorrTableRow,
        eventCancelled: EventCancelled
    ): UpdateEffect[EventOrgCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.deleteState()
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
          lineItemOrdered.forMemberId
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

}
