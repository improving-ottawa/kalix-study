package app.improving.membercontext

import app.improving.eventcontext._
import app.improving.eventcontext.event.ApiEvent
import app.improving.membercontext.member.ApiMemberData
import app.improving.ordercontext.{OrderCanceled, OrderCreated, OrderInfoUpdated, OrderStatusUpdated}
import app.improving.ordercontext.order.ApiOrder
import app.improving.productcontext._

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberByEventQueryView(context: ViewContext) extends AbstractMemberByEventQueryView {

  object MemberByEventMemberViewTable extends AbstractMemberByEventMemberViewTable {

    override def emptyState: ApiMemberData =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processRegisterMember(
        state: ApiMemberData,
        memberRegistered: MemberRegistered): UpdateEffect[ApiMemberData] =
      throw new UnsupportedOperationException("Update handler for 'ProcessRegisterMember' not implemented yet")

    override def processUpdateMemberStatus(
        state: ApiMemberData,
        memberStatusUpdated: MemberStatusUpdated): UpdateEffect[ApiMemberData] =
      throw new UnsupportedOperationException("Update handler for 'ProcessUpdateMemberStatus' not implemented yet")

    override def processUpdateMemberInfo(
        state: ApiMemberData,
        memberInfoUpdated: MemberInfoUpdated): UpdateEffect[ApiMemberData] =
      throw new UnsupportedOperationException("Update handler for 'ProcessUpdateMemberInfo' not implemented yet")

  }

  object MemberByEventOrderViewTable extends AbstractMemberByEventOrderViewTable {

    override def emptyState: ApiOrder =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processOrderCreated(
        state: ApiOrder,
        orderCreated: OrderCreated): UpdateEffect[ApiOrder] =
      throw new UnsupportedOperationException("Update handler for 'ProcessOrderCreated' not implemented yet")

    override def processOrderStatusUpdated(
        state: ApiOrder,
        orderStatusUpdated: OrderStatusUpdated): UpdateEffect[ApiOrder] =
      throw new UnsupportedOperationException("Update handler for 'ProcessOrderStatusUpdated' not implemented yet")

    override def processOrderInfoUpdated(
        state: ApiOrder,
        orderInfoUpdated: OrderInfoUpdated): UpdateEffect[ApiOrder] =
      throw new UnsupportedOperationException("Update handler for 'ProcessOrderInfoUpdated' not implemented yet")

    override def processOrderCanceled(
        state: ApiOrder,
        orderCanceled: OrderCanceled): UpdateEffect[ApiOrder] =
      throw new UnsupportedOperationException("Update handler for 'ProcessOrderCanceled' not implemented yet")

  }

  object MemberByEventProductViewTable extends AbstractMemberByEventProductViewTable {

    override def emptyState: ApiProduct =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processProductCreated(
        state: ApiProduct,
        productCreated: ProductCreated): UpdateEffect[ApiProduct] =
      throw new UnsupportedOperationException("Update handler for 'ProcessProductCreated' not implemented yet")

    override def processProductInfoUpdated(
        state: ApiProduct,
        productInfoUpdated: ProductInfoUpdated): UpdateEffect[ApiProduct] =
      throw new UnsupportedOperationException("Update handler for 'ProcessProductInfoUpdated' not implemented yet")

    override def processProductDeleted(
        state: ApiProduct,
        productDeleted: ProductDeleted): UpdateEffect[ApiProduct] =
      throw new UnsupportedOperationException("Update handler for 'ProcessProductDeleted' not implemented yet")

    override def processProductActivated(
        state: ApiProduct,
        productActivated: ProductActivated): UpdateEffect[ApiProduct] =
      throw new UnsupportedOperationException("Update handler for 'ProcessProductActivated' not implemented yet")

    override def processProductInactivated(
        state: ApiProduct,
        productInactivated: ProductInactivated): UpdateEffect[ApiProduct] =
      throw new UnsupportedOperationException("Update handler for 'ProcessProductInactivated' not implemented yet")

  }

  object MemberByEventEventViewTable extends AbstractMemberByEventEventViewTable {

    override def emptyState: ApiEvent =
      throw new UnsupportedOperationException("Not implemented yet, replace with your empty view state")

    override def processEventScheduled(
        state: ApiEvent,
        eventScheduled: EventScheduled): UpdateEffect[ApiEvent] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventScheduled' not implemented yet")

    override def processEventRescheduled(
        state: ApiEvent,
        eventRescheduled: EventRescheduled): UpdateEffect[ApiEvent] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventRescheduled' not implemented yet")

    override def processEventStarted(
        state: ApiEvent,
        eventStarted: EventStarted): UpdateEffect[ApiEvent] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventStarted' not implemented yet")

    override def processEventEnded(
        state: ApiEvent,
        eventEnded: EventEnded): UpdateEffect[ApiEvent] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventEnded' not implemented yet")

    override def processEventDelayed(
        state: ApiEvent,
        eventDelayed: EventDelayed): UpdateEffect[ApiEvent] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventDelayed' not implemented yet")

    override def processEventCancelled(
        state: ApiEvent,
        eventCancelled: EventCancelled): UpdateEffect[ApiEvent] =
      throw new UnsupportedOperationException("Update handler for 'ProcessEventCancelled' not implemented yet")

  }

}
