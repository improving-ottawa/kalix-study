package app.improving.eventcontext.event

import TestData._
import app.improving.eventcontext.Main
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class EventServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  private val client = testKit.getGrpcClient(classOf[EventService])

  "EventService" must {

    "schedule event correctly" in {
      client.scheduleEvent(apiScheduleEvent).futureValue

      val scheduled =
        client
          .getEventById(ApiGetEventById(testEventId.eventId))
          .futureValue

      scheduled.status shouldBe ApiEventStatus.API_EVENT_STATUS_SCHEDULED

    }

  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
