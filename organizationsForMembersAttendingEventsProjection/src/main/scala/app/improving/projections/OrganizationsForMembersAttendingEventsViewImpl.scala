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

class OrganizationsForMembersAttendingEventsViewImpl(context: ViewContext)
    extends AbstractOrganizationsForMembersAttendingEventsView {

  object OrganizationsViewTable extends AbstractOrganizationsViewTable {

    override def emptyState: OrgsTableRow = OrgsTableRow.defaultInstance
    override def processOrganizationEstablished(
        state: OrgsTableRow,
        organizationEstablished: OrganizationEstablished
    ): UpdateEffect[OrgsTableRow] =
      if (state != emptyState)
        effects.ignore()
      else
        effects.updateState(
          OrgsTableRow(
            organizationEstablished.orgId,
            organizationEstablished.info.get.name
          )
        )
  }

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

  object OrgMemberCorrViewTable extends AbstractOrgMemberCorrViewTable {

    override def emptyState: OrgMemberCorrTableRow =
      OrgMemberCorrTableRow.defaultInstance

    override def processMemberRegisteredOrganization(
        state: OrgMemberCorrTableRow,
        memberRegistered: MemberRegistered
    ): UpdateEffect[OrgMemberCorrTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        OrgMemberCorrTableRow(
          Some(memberRegistered.info.get.organizationMembership.head),
          memberRegistered.memberId
        )
      )
  }

  object EventsViewTable extends AbstractEventsViewTable {

    override def emptyState: EventsTableRow = EventsTableRow.defaultInstance

    override def processEventScheduled(
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

    override def processEventCancelled(
        state: EventsTableRow,
        eventCancelled: EventCancelled
    ): UpdateEffect[EventsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.deleteState()

  }

  object TicketEventCorrViewTable extends AbstractTicketEventCorrViewTable {

    override def emptyState: TicketEventCorrTableRow =
      throw new UnsupportedOperationException(
        "Not implemented yet, replace with your empty view state"
      )

    override def processProductCreatedForCorrTable(
        state: TicketEventCorrTableRow,
        productCreated: ProductCreated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state != emptyState)
      effects.ignore() // already created
    else
      effects.updateState(
        TicketEventCorrTableRow(
          productCreated.sku,
          productCreated.info.get.event
        )
      )

  }

}
