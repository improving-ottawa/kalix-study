package app.improving.projections

import com.google.`type`.Date
import com.google.protobuf.timestamp.Timestamp
import kalix.scalasdk.action.Action
import kalix.scalasdk.testkit.{ActionResult, KalixTestKit}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{Instant, ZoneId}
import java.time.temporal.ChronoField

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class QueryMembersAttendingEventsForAnOrganizationActionSpec
    extends AnyWordSpec
    with Matchers {

  "QueryMembersAttendingEventsForAnOrganization" must {


    "parse timestamp correctly" in {

      val localDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate



      val date = new Date(localDate.getYear, localDate.getMonthValue, localDate.getDayOfMonth)

      //date.toString shouldBe ""

      /*val ts = Timestamp(Instant.now().toEpochMilli)
      val date = com.google.`type`.Date.parseFrom(ts.toString.getBytes)
      date.toString shouldBe ""*/

      date.year shouldBe 2023
      date.month shouldBe 2
      date.day shouldBe 16
    }


    "handle command QueryMembersAttendingEventsForAnOrganization" in {}

  }
}
