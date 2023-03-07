package app.improving.membercontext.member

import app.improving.{ApiMemberId, ApiSku}
import app.improving.membercontext.{
  MemberByMemberIdsQuery,
  MemberByMemberIdsRequest
}
import app.improving.ordercontext.{OrderByProductQuery, OrderByProductRequest}
import app.improving.productcontext.{
  TicketByEventTimeQuery,
  TicketByEventTimeRequest
}
import com.google.protobuf.timestamp.Timestamp
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

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

  private val memberByMemberIdsView = creationContext.getGrpcClient(
    classOf[MemberByMemberIdsQuery],
    config.getString(
      "app.improving.member.member.grpc-client-name"
    )
  )

  private val orderByProductView: OrderByProductQuery = creationContext.getGrpcClient(
    classOf[OrderByProductQuery],
    config.getString(
      "app.improving.member.order.grpc-client-name"
    )
  )

  private val ticketByEventTimeView = creationContext.getGrpcClient(
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

    log.info(
      s"in MemberActionServiceImpl findMembersByEventTime given_time - ${memberByEventTimeRequest.givenTime}"
    )

    val givenTimeOpt =
      Try(java.time.Instant.parse(memberByEventTimeRequest.givenTime))
        .map(instant => Timestamp.of(instant.getEpochSecond, instant.getNano))
        .toOption

    log.info(
      s"in MemberActionServiceImpl findMembersByEventTime givenTimeOpt - $givenTimeOpt"
    )

    val products = ticketByEventTimeView.findProductsByEventTime(
      TicketByEventTimeRequest(givenTimeOpt)
    )

    val productIds = products.map(_.products.map(_.sku))

    productIds.onComplete {
      case Failure(exception) =>
        log.info(
          s"in MemberActionServiceImpl findMembersByEventTime productIds exception - $exception"
        )
      case Success(value) =>
        log.info(
          s"in MemberActionServiceImpl findMembersByEventTime productIds value - $value"
        )
    }

    effects.asyncReply(
      for {
        pids <- productIds
        memberIds <- Future
          .sequence(
            pids
              .map { productId =>
                log.info(
                  s"in MemberActionServiceImpl findMembersByEventTime productId $productId"
                )
                val result: Future[Seq[String]] = orderByProductView
                  .findOrdersByProducts(
                    OrderByProductRequest(
                      productId.getOrElse(ApiSku.defaultInstance).sku
                    )
                  )
                  .map(response =>
                    response.orders
                      .flatMap(_.meta.flatMap(_.memberId.map(_.memberId)))
                  )

                result
              }
          )
        members <- memberByMemberIdsView.findMembersByMemberIds(
          MemberByMemberIdsRequest(memberIds.flatten)
        )
      } yield {
        MembersByEventTimeResponse(members.members)
      }
    )
  }
}
