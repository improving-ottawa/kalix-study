//package app.improving.gateway
//
//import app.improving.{
//  ApiAddress,
//  ApiCAPostalCode,
//  ApiContact,
//  ApiEmailAddress,
//  ApiMobileNumber,
//  ApiTenantId
//}
//import app.improving.tenantcontext.tenant.{
//  ApiEstablishTenant,
//  ApiInfo,
//  TenantService
//}
//import kalix.scalasdk.testkit.{ActionResult, MockRegistry}
//import org.scalamock.matchers.ArgCapture.CaptureAll
//import org.scalamock.scalatest.MockFactory
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//
//import scala.concurrent.Future
//
//// This class was initially generated based on the .proto definition by Kalix tooling.
////
//// As long as this file exists it will not be overwritten: you can maintain it yourself,
//// or delete it so it is regenerated as needed.
//
//class GatewayApiActionImplSpec
//    extends AnyWordSpec
//    with Matchers
//    with MockFactory {
//
//  trait Fixture {
//    val tenantInfo: ApiInfo = ApiInfo(
//      "tenantName",
//      Some(
//        ApiContact(
//          firstName = "Some",
//          lastName = "One",
//          emailAddress = Some(ApiEmailAddress("someone@gmail.com")),
//          phone = Some(ApiMobileNumber("1234567")),
//          userName = "Someone"
//        )
//      ),
//      Some(
//        ApiAddress(
//          line1 = "Line 1 St",
//          line2 = "Line 2 Ave",
//          city = "Sometown",
//          stateProvince = "Ontario",
//          country = "Canada",
//          postalCode =
//            ApiAddress.PostalCode.CaPostalCode(ApiCAPostalCode("A1B2C3"))
//        )
//      )
//    )
//
//    val tenantServiceStub: TenantService = mock[TenantService]
//    val mockRegistry: MockRegistry = MockRegistry.withMock(tenantServiceStub)
//    val service: GatewayApiActionImplTestKit =
//      GatewayApiActionImplTestKit(new GatewayApiActionImpl(_), mockRegistry)
//  }
//
//  "GatewayApiActionImpl" should {
//    "handle command EstablishTenant" in new Fixture {
//      val establishTenantCommand = CaptureAll[ApiEstablishTenant]()
//
//      tenantServiceStub.establishTenant _ expects capture(
//        establishTenantCommand
//      ) onCall { msg: ApiEstablishTenant =>
//        Future.successful(
//          ApiTenantId(msg.tenantId)
//        )
//      }
//
//      val command: EstablishTenant = EstablishTenant(Some(tenantInfo))
//      val reply: ActionResult[ApiTenantId] =
//        service.handleEstablishTenant(command)
//      println(reply.asyncResult.value)
//      assert(reply.isReply)
//      establishTenantCommand.value.tenantId shouldEqual reply.reply.tenantId
//    }
//  }
//}
