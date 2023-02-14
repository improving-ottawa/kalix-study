package app.improving.membercontext.member

import app.improving.ApiMemberId
import app.improving.membercontext.{
  MemberByMemberIdsQuery,
  MemberByMemberIdsRequest
}
import app.improving.ordercontext.{
  OrderByProductQuery,
  OrderByProductQueryClient,
  OrderByProductRequest
}
import app.improving.productcontext.{
  TicketByEventTimeQuery,
  TicketByEventTimeRequest
}
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import scala.concurrent.Future

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberActionServiceImpl(creationContext: ActionCreationContext)
    extends AbstractMemberActionServiceAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val memberService: MemberService =
    creationContext.getGrpcClient(
      classOf[MemberService],
      config.getString(
        "app.improving.member.member.grpc-client-name"
      )
    )

  val memberByMemberIdsView = creationContext.getGrpcClient(
    classOf[MemberByMemberIdsQuery],
    config.getString(
      "app.improving.member.member.grpc-client-name"
    )
  )

  val orderByProductView = creationContext.getGrpcClient(
    classOf[OrderByProductQuery],
    config.getString(
      "app.improving.member.order.grpc-client-name"
    )
  )

  val ticketByEventTimeView = creationContext.getGrpcClient(
    classOf[TicketByEventTimeQuery],
    config.getString(
      "app.improving.member.product.grpc-client-name"
    )
  )

  override def registerMemberList(
      apiRegisterMemberList: ApiRegisterMemberList
  ): Action.Effect[ApiMemberIds] = {

    log.info("MemberActionServiceImpl in registerMemberList")

    val memberIdOpt = apiRegisterMemberList.registeringMember.map(member =>
      ApiMemberId(member.memberId)
    )
    val apiMemberMap =
      apiRegisterMemberList.memberList
        .getOrElse(ApiMemberMap.defaultInstance)
        .map

    val result = Future
      .sequence(
        apiMemberMap
          .map { case (memberId, info) =>
            ApiRegisterMember(
              memberId,
              Some(info),
              memberIdOpt
            )
          }
          .map(register => memberService.registerMember(register))
      )
      .map(memberIds => ApiMemberIds(memberIds.toSeq))

    effects.asyncReply(result)
  }

  override def findMembersByEventTime(
      memberByEventTimeRequest: MembersByEventTimeRequest
  ): Action.Effect[MembersByEventTimeResponse] = {

    log.info("in MemberActionServiceImpl findMembersByEventTime")

    val givenTimeOpt = memberByEventTimeRequest.givenTime

    val products = ticketByEventTimeView.findProductsByEventTime(
      TicketByEventTimeRequest(givenTimeOpt)
    )

    val productIds = products.map(_.products.map(_.sku))

    effects.asyncReply(
      for {
        productIds <- productIds
        memberIds <- orderByProductView
          .findOrdersByProducts(
            OrderByProductRequest(productIds)
          )
          .map(response =>
            response.orders
              .map(_.meta.flatMap(_.memberId.map(_.memberId)))
              .flatten
          )
        members <- memberByMemberIdsView.findMembersByMemberIds(
          MemberByMemberIdsRequest(memberIds)
        )
      } yield {
        MembersByEventTimeResponse(members.members)
      }
    )
  }
}
