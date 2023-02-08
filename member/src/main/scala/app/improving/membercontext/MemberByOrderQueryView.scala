package app.improving.membercontext

import app.improving.ApiMemberId
import app.improving.membercontext.infrastructure.util.{
  convertInfoToApiUpdateInfo,
  convertMemberRegisteredToApiMemberData,
  convertMetaInfoToApiMetaInfo
}
import app.improving.membercontext.member.ApiMemberData
import app.improving.ordercontext.OrderCanceled
import app.improving.ordercontext.OrderCreated
import app.improving.ordercontext.OrderInfoUpdated
import app.improving.ordercontext.OrderStatusUpdated
import app.improving.ordercontext.infrastructure.util.{
  convertOrderCancelledToApiOrder,
  convertOrderCreatedToApiOrder,
  convertOrderInfoUpdatedToApiOrder,
  convertOrderStatusToApiOrderStatus
}
import app.improving.ordercontext.order.ApiOrder
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
        orderCreated: OrderCreated
    ): UpdateEffect[ApiOrder] = {
      if (state != emptyState) effects.ignore()
      else
        effects.updateState(
          convertOrderCreatedToApiOrder(orderCreated)
        )
    }

    override def processOrderStatusUpdated(
        state: ApiOrder,
        orderStatusUpdated: OrderStatusUpdated
    ): UpdateEffect[ApiOrder] = {
      val updatedStatus = convertOrderStatusToApiOrderStatus(
        orderStatusUpdated.newStatus
      )
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      val updatedMetaOpt = state.meta.map(
        _.copy(
          lastModifiedBy = orderStatusUpdated.updatingMember.map(member =>
            ApiMemberId(member.id)
          ),
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
        orderInfoUpdated: OrderInfoUpdated
    ): UpdateEffect[ApiOrder] = {
      effects.updateState(convertOrderInfoUpdatedToApiOrder(orderInfoUpdated))
    }

    override def processOrderCanceled(
        state: ApiOrder,
        orderCanceled: OrderCanceled
    ): UpdateEffect[ApiOrder] =
      effects.updateState(convertOrderCancelledToApiOrder(orderCanceled))

  }

}
