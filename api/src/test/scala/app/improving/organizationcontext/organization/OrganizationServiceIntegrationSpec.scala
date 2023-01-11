package app.improving.organizationcontext.organization

import app.improving._
import TestData._
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant

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

  private val client =
    testKit.getGrpcClient(classOf[OrganizationService])

  private val memberViewClient =
    testKit.getGrpcClient(classOf[OrganizationByMemberView])
  private val ownerViewClient =
    testKit.getGrpcClient(classOf[OrganizationByOwnerView])

  "OrganizationService" must {

    "process established organization for view" in {
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

    "establish organization correctly" in {
      client.establishOrganization(apiEstablishOrganization).futureValue

      val organization =
        client.getOrganization(ApiGetOrganizationById(testOrgId)).futureValue

      organization.status shouldBe ApiOrganizationStatus.DRAFT

      organization.oid shouldBe Some(
        ApiOrganizationId(apiEstablishOrganization.orgId)
      )

      client.updateOrganizationStatus(
        ApiOrganizationStatusUpdated(testOrgId, ApiOrganizationStatus.ACTIVE)
      )

      val activeOrganization =
        client.getOrganization(ApiGetOrganizationById(testOrgId)).futureValue

      activeOrganization.status shouldBe ApiOrganizationStatus.ACTIVE

      val apiAddMembersToOrganization = ApiAddMembersToOrganization(
        testOrgId,
        Seq[ApiMemberId](
          ApiMemberId("member20"),
          ApiMemberId("member22"),
          ApiMemberId("member24")
        ),
        Some(ApiMemberId("member100"))
      )

      client.addMembersToOrganization(apiAddMembersToOrganization).futureValue

      val addedMemberOrganization =
        client.getOrganization(ApiGetOrganizationById(testOrgId)).futureValue

      addedMemberOrganization.memberIds should contain("member20")
      addedMemberOrganization.memberIds should have size 6
    }

  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
