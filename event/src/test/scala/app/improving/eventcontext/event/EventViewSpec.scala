package app.improving.eventcontext.event

import TestData._
import akka.actor.ActorSystem
import app.improving.eventcontext.{AllEventsRequest, AllEventsView, Main}
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventViewSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit val sys = ActorSystem("OrderActionImpl")
  implicit val ec = sys.dispatcher

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  private val client = testKit.getGrpcClient(classOf[AllEventsView])

  private val event = testKit.getGrpcClient(classOf[EventService])

  "EventViewSpec" must {

    "get all events correctly" in {

      event.scheduleEvent(apiScheduleEvent).futureValue

      val result = client
        .getAllEvents(AllEventsRequest())
        .futureValue

      result.events.size > 0 shouldBe true

      true shouldBe true
    }
  }
}
