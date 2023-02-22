package app.improving.projection

import app.improving.{EventId, MemberId, OrganizationId, Sku}
import app.improving.eventcontext.EventScheduled
import app.improving.membercontext.{MemberRegistered, MemberStatus, MemberStatusUpdated}
import app.improving.ordercontext.{LineItemCancelled, LineItemOrdered, OrderCreated, OrderStatus, OrderStatusUpdated}
import app.improving.organizationcontext.{OrganizationEstablished, OrganizationStatus, OrganizationStatusUpdated}
import app.improving.productcontext.{ProductActivated, ProductCreated, ProductDeleted, ProductInactivated, ProductStatus}
import app.improving.productcontext.infrastructure.util._
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

import java.time.{Instant, LocalTime, ZoneId, ZoneOffset}

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationsForMembersAttendingEventsView(context: ViewContext)
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
            organizationEstablished.orgId
              .getOrElse(OrganizationId.defaultInstance)
              .id,
            organizationEstablished.info.get.name,
            OrganizationStatus.ORGANIZATION_STATUS_DRAFT.name
          )
        )

    override def processOrganizationStatusUpdated(
        state: OrgsTableRow,
        organizationStatusUpdated: OrganizationStatusUpdated
    ): UpdateEffect[OrgsTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status = organizationStatusUpdated.newStatus.name)
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
          memberRegistered.memberId
            .getOrElse(MemberId.defaultInstance)
            .id,
          s"${memberRegistered.info.get.firstName} ${memberRegistered.info.get.lastName}"
        )
      )

    override def processMemberStatusUpdated(
        state: MembersTableRow,
        memberStatusUpdated: MemberStatusUpdated
    ): UpdateEffect[MembersTableRow] = if (state == emptyState)
      effects.ignore()
    else if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status =
          memberStatusUpdated.meta
            .map(_.memberStatus)
            .getOrElse(MemberStatus.MEMBER_STATUS_UNKNOWN)
            .name
        )
      )
  }

  object OrgMemberCorrViewTable extends AbstractOrgMemberCorrViewTable {

    override def emptyState: OrgMemberCorrTableRow =
      OrgMemberCorrTableRow.defaultInstance

    override def processMemberRegisteredCorrTable(
        state: OrgMemberCorrTableRow,
        memberRegistered: MemberRegistered
    ): UpdateEffect[OrgMemberCorrTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        OrgMemberCorrTableRow(
          memberRegistered.info.get.organizationMembership.head.id,
          memberRegistered.memberId
            .getOrElse(MemberId.defaultInstance)
            .id
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
          eventScheduled.eventId
            .getOrElse(EventId.defaultInstance)
            .id,
          eventScheduled.info.get.eventName,
          eventDate = eventScheduled.info.get.expectedStart.map(time =>
            Timestamp(
              Instant
                .ofEpochSecond(time.seconds, time.nanos)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate
                .toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.of("UTC"))
            )
          ),
          status = eventScheduled.meta.get.status.name
        )
      )
  }

  object TicketEventCorrViewTable extends AbstractTicketEventCorrViewTable {

    override def emptyState: TicketEventCorrTableRow =
      throw new UnsupportedOperationException(
        "Not implemented yet, replace with your empty view state"
      )

    def processProductCreated(
        state: TicketEventCorrTableRow,
        productCreated: ProductCreated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state != emptyState)
      effects.ignore()
    else
      effects.updateState(
        TicketEventCorrTableRow(
          productCreated.sku
            .getOrElse(Sku.defaultInstance)
            .id,
          extractEventIdFromProductInfo(productCreated.info.get)
            .getOrElse(EventId.defaultInstance)
            .id
        )
      )
    override def processProductActivated(
        state: TicketEventCorrTableRow,
        productActivated: ProductActivated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(ticketStatus = ProductStatus.PRODUCT_STATUS_ACTIVE.name)
      )

    override def processProductInactivated(
        state: TicketEventCorrTableRow,
        productInactivated: ProductInactivated
    ): UpdateEffect[TicketEventCorrTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(ticketStatus = ProductStatus.PRODUCT_STATUS_INACTIVE.name)
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
          lineItemOrdered.productId
            .getOrElse(Sku.defaultInstance)
            .id,
          lineItemOrdered.forMemberId
            .getOrElse(MemberId.defaultInstance)
            .id
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
        state.copy(status = OrderStatus.ORDER_STATUS_DRAFT.name)
      )

    override def processOrderStatusUpdated(
        state: OrderTableRow,
        orderStatusUpdated: OrderStatusUpdated
    ): UpdateEffect[OrderTableRow] = if (state == emptyState)
      effects.ignore()
    else
      effects.updateState(
        state.copy(status = orderStatusUpdated.newStatus.name)
      )
  }
}
