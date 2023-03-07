package kalix.study

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration.DurationInt
import scala.util.Random

class TestGatewayQueryAllSimulation extends Simulation {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val httpProtocol = http
    .baseUrl(
      s"https://${config.getString("app.improving.akka.grpc.gateway-client-url")}"
    ) // Here is the root for all relative URLs
    .acceptHeader(
      "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,application/json"
    ) // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
    )

  val availableUrls = Array(
    Map("url" -> "/product/get-all-products"),
    Map("url" -> "/tenant/get-all-tenants"),
    Map("url" -> "/order/get-all-orders"),
    Map("url" -> "/organization/get-all-organizations"),
    Map("url" -> "/store/get-all-stores"),
    Map("url" -> "/member/get-all-members"),
    Map("url" -> "/event/get-all-events")
  )

  def pickARandomUrl() = {
    availableUrls(Random.nextInt(availableUrls.size))
  }

  val feeder = Iterator.continually(pickARandomUrl())

  val scn =
    scenario(
      "Gateway Start Scenario"
    ).feed(feeder)
      .exec(
        http("query-all")
          .get("#{url}")
          .asJson
          .check(status.is(200))
          .check(bodyString.exists)
          .check(bodyString.saveAs("BODY"))
      )
      .exec(session => {
        val response = session("BODY").as[String]
        println(s"Response body: \n$response")
        session
      })
      .pause(500 millis)

  setUp(scn.inject(rampUsers(1).during(10 seconds)).protocols(httpProtocol))
}
