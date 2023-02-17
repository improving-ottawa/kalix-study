package app.improving.ordercontext

import app.improving.ordercontext.order.{
  OrderAPI,
  OrderActionImpl,
  OrderEventsServiceAction
}
import kalix.scalasdk.Kalix
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

object Main {

  private val log = LoggerFactory.getLogger("app.improving.ordercontext.Main")

  def createKalix(): Kalix = {
    // The KalixFactory automatically registers any generated Actions, Views or Entities,
    // and is kept up-to-date with any changes in your protobuf definitions.
    // If you prefer, you may remove this and manually register these components in a
    // `Kalix()` instance.
    KalixFactory
      .withComponents(
        new OrderAPI(_),
        new AllOrdersViewImpl(_),
        new OrderActionImpl(_),
        new OrderByProductQueryView(_),
        new OrderEventsServiceAction(_)
      )
//      .register(
//        OrderByProductQueryViewProvider(new OrderByProductQueryView(_))
//          .withViewId("OrderByProductQueryViewV3")
//      )
  }

  def main(args: Array[String]): Unit = {
    log.info("starting the Kalix service")
    createKalix().start()
  }
}
