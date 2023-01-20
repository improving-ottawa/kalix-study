package app.improving.membercontext.membermap

import kalix.scalasdk.testkit.KalixTestKit
import app.improving.membercontext.NotificationPreference
import app.improving.{
  Contact,
  EmailAddress,
  Main,
  MobileNumber,
  OrganizationId,
  TenantId
}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec

class MemberMapIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()
  import testKit.executionContext

  private val memberMapService =
    testKit.getGrpcClient(classOf[MemberMapService])

  "MemberMapService" must {

    val testMemberId = "test-member-id"
    val testTenantId = "test-tenant-id"

    val contact = Contact(
      "member-first-name",
      "member-last-name",
      Some(EmailAddress("member@memberapi.com")),
      Some(MobileNumber("987-878-0987")),
      "user-name"
    )
    val value1 = Value(
      Some(contact),
      "handle",
      "avartar",
      "member-name",
      "short-name",
      Some(MobileNumber("987-878-0987")),
      Some(EmailAddress("member@memberapi.com")),
      NotificationPreference.SMS,
      Seq[OrganizationId](OrganizationId(testMemberId)),
      Some(TenantId(testTenantId))
    )
    val value2 = Value(
      Some(contact),
      "handle2",
      "avartar2",
      "member-name2",
      "short-name2",
      Some(MobileNumber("987-878-0982")),
      Some(EmailAddress("membe2r@memberapi.com")),
      NotificationPreference.SMS,
      Seq[OrganizationId](OrganizationId(testMemberId)),
      Some(TenantId(testTenantId))
    )

    "set and get value from map should work correctly" in {
      val Member1Key = Key("member1")
      val Member2Key = Key("member2")

      val memberId = "test-member-id"

      val updateResult =
        for {
          _ <- memberMapService.set(
            SetValue(
              memberId,
              key = Some(Member1Key),
              value = Some(value1)
            )
          )
          _ <- memberMapService.set(
            SetValue(
              memberId,
              key = Some(Member2Key),
              value = Some(value2)
            )
          )
        } yield ()
      updateResult.futureValue

      val state =
        memberMapService.getAll(GetAllValues(memberId)).futureValue

      state.values.map(_.value.get.firstName) shouldBe Seq(
        "member-name",
        "member-name2"
      )

      state.values shouldEqual Seq[CurrentValue](
        CurrentValue(Some(Member1Key), Some(value1)),
        CurrentValue(Some(Member2Key), Some(value2))
      )
    }
  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
