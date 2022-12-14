package com.improving.organization

import akka.actor.TypedActor.context
import com.improving.OrganizationId
import com.improving.organizationcontext.{
  ByMemberRequest,
  OrganizationByMemberView,
  OrganizationByOwnerView,
  OrganizationEstablished
}
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

class OrganizationServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  private val memberViewClient =
    testKit.getGrpcClient(classOf[OrganizationByMemberView])
  private val ownerViewClient =
    testKit.getGrpcClient(classOf[OrganizationByOwnerView])

  "OrganizationService" must {

    "have example test that can be removed" in {
      memberViewClient
        .processOrganizationEstablished(
          OrganizationEstablished(
            Some(OrganizationId("1")),
            None,
            None,
            None,
            None,
            None,
            None
          )
        )
        .futureValue
        .status shouldBe ApiOrganizationStatus.DRAFT
    }

  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
