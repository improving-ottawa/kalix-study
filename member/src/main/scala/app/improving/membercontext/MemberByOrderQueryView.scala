package app.improving.membercontext

import app.improving.ApiMemberId
import app.improving.membercontext.infrastructure.util.{
  convertInfoToApiUpdateInfo,
  convertMemberRegisteredToApiMemberData,
  convertMetaInfoToApiMetaInfo
}
import app.improving.membercontext.member.ApiMemberData
import app.improving.ordercontext.infrastructure.util.{
  convertOrderCancelledToApiOrder,
  convertOrderCreatedToApiOrder,
  convertOrderInfoUpdatedToApiOrder,
  convertOrderStatusToApiOrderStatus
}
import app.improving.ordercontext.order._
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.view.View.UpdateEffect
import kalix.scalasdk.view.ViewContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberByOrderQueryView(context: ViewContext)
    extends AbstractMemberByOrderQueryView {

  object MemberByOrderMemberViewTable
      extends AbstractMemberByOrderMemberViewTable {

    override def emptyState: ApiMemberData = ApiMemberData.defaultInstance

    override def processRegisterMember(
        state: ApiMemberData,
        memberRegistered: MemberRegistered
    ): UpdateEffect[ApiMemberData] = {
      if (state != emptyState) effects.ignore()
      else
        effects.updateState(
          convertMemberRegisteredToApiMemberData(memberRegistered)
        )
    }

    override def processUpdateMemberStatus(
        state: ApiMemberData,
        memberStatusUpdated: MemberStatusUpdated
    ): UpdateEffect[ApiMemberData] = {
      val updatedMetaOpt =
        memberStatusUpdated.meta.map(convertMetaInfoToApiMetaInfo)
      effects.updateState(
        state.copy(meta = updatedMetaOpt)
      )
    }

    override def processUpdateMemberInfo(
        state: ApiMemberData,
        memberInfoUpdated: MemberInfoUpdated
    ): UpdateEffect[ApiMemberData] = {
      val updatedInfoOpt =
        memberInfoUpdated.info.map(convertInfoToApiUpdateInfo)
      effects.updateState(
        state.copy(info = updatedInfoOpt)
      )
    }

  }

  object MemberByOrderOrderViewTable
      extends AbstractMemberByOrderOrderViewTable {

    override def emptyState: ApiOrder = ApiOrder.defaultInstance

    override def processOrderCreated(
        state: ApiOrder,
        orderCreated: ApiOrderCreated
    ): UpdateEffect[ApiOrder] = {
      if (state != emptyState) effects.ignore()
      else
        effects.updateState(
          ApiOrder(
            orderCreated.orderId
              .map(_.orderId)
              .getOrElse("OrderId is NOT FOUND."),
            orderCreated.info,
            orderCreated.meta,
            ApiOrderStatus.API_ORDER_STATUS_DRAFT
          )
        )
    }

    override def processOrderStatusUpdated(
        state: ApiOrder,
        orderStatusUpdated: ApiOrderStatusUpdated
    ): UpdateEffect[ApiOrder] = {
      val updatedStatus = orderStatusUpdated.newStatus
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val updatedMetaOpt = state.meta.map(
        _.copy(
          lastModifiedBy = orderStatusUpdated.updatingMember,
          lastModifiedOn = Some(timestamp),
          status = updatedStatus
        )
      )
      effects.updateState(
        state.copy(meta = updatedMetaOpt, status = updatedStatus)
      )
    }

    override def processOrderInfoUpdated(
        state: ApiOrder,
        orderInfoUpdated: ApiOrderInfoUpdated
    ): UpdateEffect[ApiOrder] = {
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val updatedMetaOpt = orderInfoUpdated.meta.map(
        _.copy(
          lastModifiedBy = orderInfoUpdated.updatingMember,
          lastModifiedOn = Some(timestamp)
        )
      )
      effects.updateState(
        state.copy(
          info = orderInfoUpdated.info,
          meta = updatedMetaOpt
        )
      )
    }

    override def processOrderCanceled(
        state: ApiOrder,
        orderCanceled: ApiOrderCanceled
    ): UpdateEffect[ApiOrder] = {
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val updatedMetaOpt = state.meta.map(
        _.copy(
          lastModifiedBy = orderCanceled.cancellingMember,
          lastModifiedOn = Some(timestamp),
          status = ApiOrderStatus.API_ORDER_STATUS_CANCELLED
        )
      )
      effects.updateState(
        state.copy(
          meta = updatedMetaOpt,
          status = ApiOrderStatus.API_ORDER_STATUS_CANCELLED
        )
      )
    }
  }

}
