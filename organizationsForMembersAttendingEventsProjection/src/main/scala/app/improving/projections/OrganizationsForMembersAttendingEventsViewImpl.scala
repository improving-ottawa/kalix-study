package app.improving.projections

import app.improving.eventcontext.EventCancelled
import app.improving.eventcontext.EventScheduled
import app.improving.membercontext.MemberRegistered
import app.improving.ordercontext.OrderCreated
import app.improving.organizationcontext.MembersAddedToOrganization
import app.improving.organizationcontext.MembersRemovedFromOrganization
import app.improving.organizationcontext.OrganizationEstablished
import app.improving.productcontext.ProductCreated
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationsForMembersAttendingEventsViewImpl(context: ViewContext) extends AbstractOrganizationsForMembersAttendingEventsView {

  object OrganizationsViewTable extends AbstractOrganizationsViewTable {

    override def emptyState: OrgsTableRow =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processOrganizationEstablished(
        state: OrgsTableRow,
        organizationEstablished: OrganizationEstablished): UpdateEffect[OrgsTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessOrganizationEstablished' not implemented yet")

  }

  object MembersViewTable extends AbstractMembersViewTable {

    override def emptyState: MembersTableRow =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processMemberRegistered(
        state: MembersTableRow,
        memberRegistered: MemberRegistered): UpdateEffect[MembersTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessMemberRegistered' not implemented yet")

  }

  object OrgMemberCorrViewTable extends AbstractOrgMemberCorrViewTable {

    override def emptyState: OrgMemberCorrTableRow =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processMembersAddedToOrganization(
        state: OrgMemberCorrTableRow,
        membersAddedToOrganization: MembersAddedToOrganization): UpdateEffect[OrgMemberCorrTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessMembersAddedToOrganization' not implemented yet")

    override def processMembersRemovedFromOrganization(
        state: OrgMemberCorrTableRow,
        membersRemovedFromOrganization: MembersRemovedFromOrganization): UpdateEffect[OrgMemberCorrTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessMembersRemovedFromOrganization' not implemented yet")

  }

  object EventsViewTable extends AbstractEventsViewTable {

    override def emptyState: EventsTableRow =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processEventScheduled(
        state: EventsTableRow,
        eventScheduled: EventScheduled): UpdateEffect[EventsTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventScheduled' not implemented yet")

    override def processEventCancelled(
        state: EventsTableRow,
        eventCancelled: EventCancelled): UpdateEffect[EventsTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventCancelled' not implemented yet")

  }

  object TicketEventCorrViewTable extends AbstractTicketEventCorrViewTable {

    override def emptyState: TicketEventCorrTableRow =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processProductCreatedForCorrTable(
        state: TicketEventCorrTableRow,
        productCreated: ProductCreated): UpdateEffect[TicketEventCorrTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessProductCreatedForCorrTable' not implemented yet")

  }

  object TicketMemberCorrViewTable extends AbstractTicketMemberCorrViewTable {

    override def emptyState: TicketMemberCorrTableRow =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processOrderCreatedForCorrTable(
        state: TicketMemberCorrTableRow,
        orderCreated: OrderCreated): UpdateEffect[TicketMemberCorrTableRow] =
      throw new UnsupportedOperationException("Update handler for 'ProcessOrderCreatedForCorrTable' not implemented yet")

  }

}
