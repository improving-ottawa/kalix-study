package app.improving.projections

import app.improving.eventcontext.EventCancelled
import app.improving.eventcontext.EventScheduled
import app.improving.membercontext.MemberRegistered
import app.improving.ordercontext.OrderCreated
import app.improving.productcontext.ProductCreated
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MembersAttendingEventsForAnOrganizationViewImpl(context: ViewContext)
    extends AbstractMembersAttendingEventsForAnOrganizationView {

  object MembersViewTable extends AbstractMembersViewTable {

    override def emptyState: MembersTableRow = MembersTableRow.defaultInstance
    override def processMemberRegistered(
        state: MembersTableRow,
        memberRegistered: MemberRegistered
    ): UpdateEffect[MembersTableRow] = state.
  }

  object EventOrgCorrViewTable extends AbstractEventOrgCorrViewTable {

    override def emptyState: EventOrgCorrTableRow =
      throw new UnsupportedOperationException(
        "Not implemented yet, replace with your empty view state"
      )

    override def processEventScheduledForCorrTable(
        state: EventOrgCorrTableRow,
        eventScheduled: EventScheduled
    ): UpdateEffect[EventOrgCorrTableRow] =
      throw new UnsupportedOperationException(
        "Update handler for 'ProcessEventScheduledForCorrTable' not implemented yet"
      )

    override def processEventCancelledForCorrTable(
        state: EventOrgCorrTableRow,
        eventCancelled: EventCancelled
    ): UpdateEffect[EventOrgCorrTableRow] =
      throw new UnsupportedOperationException(
        "Update handler for 'ProcessEventCancelledForCorrTable' not implemented yet"
      )

  }

  object EventsViewTable extends AbstractEventsViewTable {

    override def emptyState: EventsTableRow =
      throw new UnsupportedOperationException(
        "Not implemented yet, replace with your empty view state"
      )

    override def processEventScheduledForEventsTable(
        state: EventsTableRow,
        eventScheduled: EventScheduled
    ): UpdateEffect[EventsTableRow] =
      throw new UnsupportedOperationException(
        "Update handler for 'ProcessEventScheduledForEventsTable' not implemented yet"
      )

    override def processEventCancelledForEventsTable(
        state: EventsTableRow,
        eventCancelled: EventCancelled
    ): UpdateEffect[EventsTableRow] =
      throw new UnsupportedOperationException(
        "Update handler for 'ProcessEventCancelledForEventsTable' not implemented yet"
      )

  }

  object TicketEventCorrViewTable extends AbstractTicketEventCorrViewTable {

    override def emptyState: TicketEventCorrTableRow =
      throw new UnsupportedOperationException(
        "Not implemented yet, replace with your empty view state"
      )

    override def processProductCreated(
        state: TicketEventCorrTableRow,
        productCreated: ProductCreated
    ): UpdateEffect[TicketEventCorrTableRow] =
      throw new UnsupportedOperationException(
        "Update handler for 'ProcessProductCreated' not implemented yet"
      )

  }

  object TicketMemberCorrViewTable extends AbstractTicketMemberCorrViewTable {

    override def emptyState: TicketMemberCorrTableRow =
      throw new UnsupportedOperationException(
        "Not implemented yet, replace with your empty view state"
      )

    override def processOrderCreated(
        state: TicketMemberCorrTableRow,
        orderCreated: OrderCreated
    ): UpdateEffect[TicketMemberCorrTableRow] =
      throw new UnsupportedOperationException(
        "Update handler for 'ProcessOrderCreated' not implemented yet"
      )

  }

}
