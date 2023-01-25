package app.improving

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(50, Seconds), Span(1000, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  private val client = testKit.getGrpcClient(classOf[MemberService])

  "MemberService" must {

    "register member correctly" in {
      val command = ApiRegisterMember(
        testMemberId,
        Some(apiInfo),
        Some(ApiMemberId(testMemberId))
      )
      client.registerMember(command).futureValue

      val memberData =
        client.getMemberData(ApiGetMemberData(testMemberId)).futureValue

      memberData.meta.map(_.memberStatus) shouldBe Some(ApiMemberStatus.ACTIVE)
    }

  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
