//package app.improving.membercontext
//
//import app.improving.ApiMemberId
//import app.improving.eventcontext.EventCancelled
//import app.improving.eventcontext.EventDelayed
//import app.improving.eventcontext.EventEnded
//import app.improving.eventcontext.EventRescheduled
//import app.improving.eventcontext.EventScheduled
//import app.improving.eventcontext.EventStarted
//import app.improving.eventcontext.event.{ApiEvent, ApiEventStatus}
//import app.improving.eventcontext.infrastructure.util._
//import app.improving.membercontext.infrastructure.util._
//import app.improving.membercontext.member.ApiMemberData
//import app.improving.ordercontext.OrderCanceled
//import app.improving.ordercontext.OrderCreated
//import app.improving.ordercontext.OrderInfoUpdated
//import app.improving.ordercontext.OrderStatusUpdated
//import app.improving.ordercontext.infrastructure.util._
//import app.improving.ordercontext.order._
//import app.improving.productcontext.ProductActivated
//import app.improving.productcontext.ProductCreated
//import app.improving.productcontext.ProductDeleted
//import app.improving.productcontext.ProductInactivated
//import app.improving.productcontext.ProductInfoUpdated
//import app.improving.productcontext.infrastructure.util._
//import app.improving.productcontext.product.{ApiProduct, ApiProductStatus}
//import com.google.protobuf.timestamp.Timestamp
//import kalix.scalasdk.view.View.UpdateEffect
//import kalix.scalasdk.view.ViewContext
//
//// This class was initially generated based on the .proto definition by Kalix tooling.
////
//// As long as this file exists it will not be overwritten: you can maintain it yourself,
//// or delete it so it is regenerated as needed.
//
//class MemberByEventQueryView(context: ViewContext)
//    extends AbstractMemberByEventQueryView {
//
//  object MemberByEventMemberViewTable
//      extends AbstractMemberByEventMemberViewTable {
//
//    override def emptyState: ApiMemberData = ApiMemberData.defaultInstance
//
//    override def processRegisterMember(
//        state: ApiMemberData,
//        memberRegistered: MemberRegistered
//    ): UpdateEffect[ApiMemberData] = {
//      if (state != emptyState) effects.ignore()
//      else
//        effects.updateState(
//          convertMemberRegisteredToApiMemberData(memberRegistered)
//        )
//    }
//
//    override def processUpdateMemberStatus(
//        state: ApiMemberData,
//        memberStatusUpdated: MemberStatusUpdated
//    ): UpdateEffect[ApiMemberData] = {
//      val updatedMetaOpt =
//        memberStatusUpdated.meta.map(convertMetaInfoToApiMetaInfo)
//      effects.updateState(
//        state.copy(meta = updatedMetaOpt)
//      )
//    }
//
//    override def processUpdateMemberInfo(
//        state: ApiMemberData,
//        memberInfoUpdated: MemberInfoUpdated
//    ): UpdateEffect[ApiMemberData] = {
//      val updatedInfoOpt =
//        memberInfoUpdated.info.map(convertInfoToApiUpdateInfo)
//      effects.updateState(
//        state.copy(info = updatedInfoOpt)
//      )
//    }
//  }
//
//  object MemberByEventOrderViewTable
//      extends AbstractMemberByEventOrderViewTable {
//
//    override def emptyState: ApiOrder = ApiOrder.defaultInstance
//
//    override def processOrderCreated(
//        state: ApiOrder,
//        orderCreated: OrderCreated
//    ): UpdateEffect[ApiOrder] = {
//      if (state != emptyState) effects.ignore()
//      else
//        effects.updateState(
//          convertOrderCreatedToApiOrder(orderCreated)
//        )
//    }
//
//    override def processOrderStatusUpdated(
//        state: ApiOrder,
//        orderStatusUpdated: OrderStatusUpdated
//    ): UpdateEffect[ApiOrder] = {
//      val updatedStatus = convertOrderStatusToApiOrderStatus(
//        orderStatusUpdated.newStatus
//      )
//      val now = java.time.Instant.now()
//      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
//      val updatedMetaOpt = state.meta.map(
//        _.copy(
//          lastModifiedBy = orderStatusUpdated.updatingMember.map(member =>
//            ApiMemberId(member.id)
//          ),
//          lastModifiedOn = Some(timestamp),
//          status = updatedStatus
//        )
//      )
//      effects.updateState(
//        state.copy(meta = updatedMetaOpt, status = updatedStatus)
//      )
//    }
//
//    override def processOrderInfoUpdated(
//        state: ApiOrder,
//        orderInfoUpdated: OrderInfoUpdated
//    ): UpdateEffect[ApiOrder] = {
//      effects.updateState(convertOrderInfoUpdatedToApiOrder(orderInfoUpdated))
//    }
//
//    override def processOrderCanceled(
//        state: ApiOrder,
//        orderCanceled: OrderCanceled
//    ): UpdateEffect[ApiOrder] =
//      effects.updateState(convertOrderCancelledToApiOrder(orderCanceled))
//
//  }
//
//  object MemberByEventProductViewTable
//      extends AbstractMemberByEventProductViewTable {
//
//    override def emptyState: ApiProduct = ApiProduct.defaultInstance
//
//    override def processProductCreated(
//        state: ApiProduct,
//        productCreated: ProductCreated
//    ): UpdateEffect[ApiProduct] = {
//      if (state != emptyState) effects.ignore()
//      else
//        effects.updateState(
//          convertProductCreatedToApiProduct(productCreated)
//        )
//    }
//
//    override def processProductInfoUpdated(
//        state: ApiProduct,
//        productInfoUpdated: ProductInfoUpdated
//    ): UpdateEffect[ApiProduct] = effects.updateState(
//      state.copy(
//        info = productInfoUpdated.info.map(convertProductInfoToApiProductInfo),
//        meta = productInfoUpdated.meta.map(
//          convertProductMetaInfoToApiProductMetaInfo
//        )
//      )
//    )
//
//    override def processProductDeleted(
//        state: ApiProduct,
//        productDeleted: ProductDeleted
//    ): UpdateEffect[ApiProduct] = effects.deleteState()
//
//    override def processProductActivated(
//        state: ApiProduct,
//        productActivated: ProductActivated
//    ): UpdateEffect[ApiProduct] = {
//      val now = java.time.Instant.now()
//      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
//      val metaOpt = state.meta.map(
//        _.copy(
//          lastModifiedBy = productActivated.activatingMember.map(member =>
//            ApiMemberId(member.id)
//          ),
//          lastModifiedOn = Some(timestamp)
//        )
//      )
//      effects.updateState(
//        state.copy(
//          meta = metaOpt,
//          status = ApiProductStatus.ACTIVE
//        )
//      )
//    }
//
//    override def processProductInactivated(
//        state: ApiProduct,
//        productInactivated: ProductInactivated
//    ): UpdateEffect[ApiProduct] = {
//      val now = java.time.Instant.now()
//      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
//      val metaOpt = state.meta.map(
//        _.copy(
//          lastModifiedBy = productInactivated.inactivatingMember.map(member =>
//            ApiMemberId(member.id)
//          ),
//          lastModifiedOn = Some(timestamp)
//        )
//      )
//      effects.updateState(
//        state.copy(
//          meta = metaOpt,
//          status = ApiProductStatus.INACTIVE
//        )
//      )
//    }
//  }
//
//  object MemberByEventEventViewTable
//      extends AbstractMemberByEventEventViewTable {
//
//    override def emptyState: ApiEvent = ApiEvent.defaultInstance
//
//    override def processEventScheduled(
//        state: ApiEvent,
//        eventScheduled: EventScheduled
//    ): UpdateEffect[ApiEvent] = {
//      if (state != emptyState) effects.ignore()
//      else
//        effects.updateState(
//          convertEventScheduledToApiEvent(eventScheduled)
//        )
//    }
//
//    override def processEventRescheduled(
//        state: ApiEvent,
//        eventRescheduled: EventRescheduled
//    ): UpdateEffect[ApiEvent] =
//      effects.updateState(
//        convertEventReScheduledToApiEvent(eventRescheduled)
//      )
//
//    override def processEventStarted(
//        state: ApiEvent,
//        eventStarted: EventStarted
//    ): UpdateEffect[ApiEvent] =
//      effects.updateState(
//        state.copy(
//          info = eventStarted.info.map(convertEventInfoToApiEventInfo),
//          meta = eventStarted.meta.map(convertEventMetaInfoToApiEventMetaInfo),
//          status = ApiEventStatus.INPROGRESS
//        )
//      )
//
//    override def processEventEnded(
//        state: ApiEvent,
//        eventEnded: EventEnded
//    ): UpdateEffect[ApiEvent] =
//      effects.updateState(
//        state.copy(
//          meta = eventEnded.meta.map(convertEventMetaInfoToApiEventMetaInfo),
//          status = ApiEventStatus.PAST
//        )
//      )
//
//    override def processEventDelayed(
//        state: ApiEvent,
//        eventDelayed: EventDelayed
//    ): UpdateEffect[ApiEvent] = {
//      val infoOpt = state.info.map(info =>
//        info.copy(
//          expectedStart =
//            for {
//              timestamp <- info.expectedStart
//              duration <- eventDelayed.expectedDuration
//            } yield (
//              Timestamp.of(
//                timestamp.seconds + duration.seconds,
//                timestamp.nanos + duration.nanos
//              )
//            ),
//          expectedEnd =
//            for {
//              timestamp <- info.expectedEnd
//              duration <- eventDelayed.expectedDuration
//            } yield (
//              Timestamp.of(
//                timestamp.seconds + duration.seconds,
//                timestamp.nanos + duration.nanos
//              )
//            )
//        )
//      )
//      effects.updateState(
//        state.copy(
//          info = infoOpt,
//          meta = eventDelayed.meta.map(convertEventMetaInfoToApiEventMetaInfo),
//          status = ApiEventStatus.DELAYED
//        )
//      )
//    }
//
//    override def processEventCancelled(
//        state: ApiEvent,
//        eventCancelled: EventCancelled
//    ): UpdateEffect[ApiEvent] = {
//      val now = java.time.Instant.now()
//      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
//      val metaOpt = state.meta.map(meta =>
//        meta.copy(
//          status = ApiEventStatus.CANCELLED,
//          lastModifiedOn = Some(timestamp),
//          lastModifiedBy = eventCancelled.cancellingMember.map(member =>
//            ApiMemberId(member.id)
//          )
//        )
//      )
//      effects.updateState(
//        state.copy(meta = metaOpt, status = ApiEventStatus.CANCELLED)
//      )
//    }
//
//  }
//
//}