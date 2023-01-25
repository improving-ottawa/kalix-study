package app.improving.gateway

import app.improving.{
  ApiAddress,
  ApiCAPostalCode,
  ApiContact,
  ApiEmailAddress,
  ApiMobileNumber,
  ApiTenantId
}
import app.improving.tenantcontext.tenant.{
  ApiEstablishTenant,
  ApiGetTenantById,
  ApiInfo,
  TenantService
}
import kalix.scalasdk.testkit.{ActionResult, MockRegistry}
import org.scalamock.scalatest.proxy.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class GatewayApiActionImplSpec
    extends AnyWordSpec
    with Matchers
    with MockFactory {

  trait Fixture {
    val tenantInfo: ApiInfo = ApiInfo(
      "tenantName",
      Some(
        ApiContact(
          firstName = "Some",
          lastName = "One",
          emailAddress = Some(ApiEmailAddress("someone@gmail.com")),
          phone = Some(ApiMobileNumber("1234567")),
          userName = "Someone"
        )
      ),
      Some(
        ApiAddress(
          line1 = "Line 1 St",
          line2 = "Line 2 Ave",
          city = "Sometown",
          stateProvince = "Ontario",
          country = "Canada",
          postalCode =
            ApiAddress.PostalCode.CaPostalCode(ApiCAPostalCode("A1B2C3"))
        )
      )
    )

    val tenantServiceStub: TenantService = stub[TenantService]
  }

  "GatewayAPIActionImpl" must {
    "handle command HandleEstablishTenant" in new Fixture {
      var tenantId: Option[ApiTenantId] = None

      (tenantServiceStub.establishTenant _)
        .expects(*)
        .onCall { establishTenant: ApiEstablishTenant =>
          tenantId = Some(ApiTenantId(establishTenant.tenantId))
          Future.successful(tenantId.get)
        }

      val mockRegistry: MockRegistry = MockRegistry.withMock(tenantServiceStub)
      val service: GatewayAPIActionImplTestKit =
        GatewayAPIActionImplTestKit(new GatewayApiActionImpl(_), mockRegistry)

      val command: EstablishTenant = EstablishTenant(Some(tenantInfo))
      val reply: ActionResult[ApiTenantId] =
        service.handleEstablishTenant(command)

      assert(reply.isReply)
      tenantId.map(_ shouldEqual reply.reply.tenantId)
    }

  }
}
