package app.improving.gateway

import app.improving.eventcontext.AllEventsRequest
import app.improving.eventcontext.AllEventsResult
import app.improving.membercontext.AllMembersRequest
import app.improving.membercontext.AllMembersResult
import app.improving.ordercontext.AllOrdersRequest
import app.improving.ordercontext.AllOrdersresult
import app.improving.organizationcontext.AllOrganizationsRequest
import app.improving.organizationcontext.AllOrganizationsresult
import app.improving.productcontext.AllProductsRequest
import app.improving.productcontext.AllProductsResult
import app.improving.storecontext.AllStoresRequest
import app.improving.storecontext.AllStoresResult
import app.improving.tenantcontext.AllTenantResult
import app.improving.tenantcontext.GetAllTenantRequest
import kalix.scalasdk.action.Action
import kalix.scalasdk.testkit.ActionResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MegaGatewayApiActionImplSpec
    extends AnyWordSpec
    with Matchers {

  "MegaGatewayApiActionImpl" must {

    "have example test that can be removed" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
      pending
      // use the testkit to execute a command
      // and verify final updated state:
      // val result = service.someOperation(SomeRequest)
      // verify the reply
      // result.reply shouldBe expectedReply
    }

    "handle command HandleEstablishTenant" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleEstablishTenant(CreateTenant(...))
    }

    "handle command HandleEstablishTenants" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleEstablishTenants(CreateTenants(...))
    }

    "handle command HandleEstablishOrganization" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleEstablishOrganization(CreateOrganization(...))
    }

    "handle command HandleEstablishOrganizations" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleEstablishOrganizations(CreateOrganizations(...))
    }

    "handle command HandleScheduleEvent" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleScheduleEvent(CreateEvent(...))
    }

    "handle command HandleScheduleEvents" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleScheduleEvents(CreateEvents(...))
    }

    "handle command HandleCreateStore" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleCreateStore(CreateStore(...))
    }

    "handle command HandleCreateStores" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleCreateStores(CreateStores(...))
    }

    "handle command HandleCreateProduct" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleCreateProduct(CreateProduct(...))
    }

    "handle command HandleCreateProducts" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleCreateProducts(CreateProducts(...))
    }

    "handle command HandleRegisterMember" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleRegisterMember(RegisterMember(...))
    }

    "handle command HandleRegisterMembers" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleRegisterMembers(RegisterMembers(...))
    }

    "handle command HandleCreateOrder" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleCreateOrder(CreateOrder(...))
    }

    "handle command HandleCreateOrders" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleCreateOrders(CreateOrders(...))
    }

    "handle command HandleGetAllEvents" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleGetAllEvents(AllEventsRequest(...))
    }

    "handle command HandleGetAllOrganizations" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleGetAllOrganizations(AllOrganizationsRequest(...))
    }

    "handle command HandleGetAllTenants" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleGetAllTenants(GetAllTenantRequest(...))
    }

    "handle command HandleGetAllStores" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleGetAllStores(AllStoresRequest(...))
    }

    "handle command HandleGetAllProducts" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleGetAllProducts(AllProductsRequest(...))
    }

    "handle command HandleGetAllMembers" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleGetAllMembers(AllMembersRequest(...))
    }

    "handle command HandleGetAllOrders" in {
      val service = MegaGatewayApiActionImplTestKit(new MegaGatewayApiActionImpl(_))
          pending
      // val result = service.handleGetAllOrders(AllOrdersRequest(...))
    }

  }
}
