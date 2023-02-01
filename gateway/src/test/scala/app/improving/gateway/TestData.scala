package app.improving.gateway

import app.improving._
import app.improving.membercontext.member.{
  ApiNotificationPreference,
  ApiInfo => MemberApiInfo
}
import app.improving.eventcontext.event.{ApiEventInfo, ApiReservationId}
import app.improving.organizationcontext.organization.{
  ApiOrganizationStatus,
  ApiParent,
  ApiInfo => OrgInfo,
  ApiMetaInfo => OrgMetaInfo,
  _
}
import app.improving.storecontext.store.ApiStoreInfo
import app.improving.productcontext.product.{ApiProductInfo, ApiProductMetaInfo}
import app.improving.tenantcontext.tenant.{ApiInfo => TenantInfo, _}
import app.improving.ordercontext.order._
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object TestData {
  trait Fixture {
    val tenantInfo: TenantInfo = TenantInfo(
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

    val testOrgId = "test-organization-id"
    val parentIdTest = "parent-id-test"
    val newParentId = "new-parent-id"
    val testTenantId = "test-tenant-id"
    val establishingMemberId = "establishing-member-id"
    val now = Instant.now()
    val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)

    val establishOrganization = EstablishOrganization(
      Some(
        OrgInfo(
          "name-test",
          "shortname-test",
          Some(
            ApiAddress(
              "line1",
              "line2",
              "city",
              "state",
              "canada",
              ApiAddress.PostalCode.CaPostalCode(
                ApiCAPostalCode.defaultInstance
              )
            )
          ),
          true,
          "www.test.com",
          "N/A",
          Some(ApiTenantId(testTenantId))
        )
      ),
      Some(ApiParent(parentIdTest)),
      Seq[ApiMemberId](
        ApiMemberId("test-member-id"),
        ApiMemberId("member2"),
        ApiMemberId("member3")
      ),
      Seq[ApiMemberId](
        ApiMemberId("member10"),
        ApiMemberId("member11"),
        ApiMemberId("member12")
      ),
      Seq.empty,
      Some(ApiMemberId(establishingMemberId)),
      Some(
        OrgMetaInfo(
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          ApiOrganizationStatus.DRAFT,
          Seq.empty[ApiOrganizationId]
        )
      )
    )

    val apiEstablishOrganization = ApiEstablishOrganization(
      testOrgId,
      Some(
        OrgInfo(
          "name-test",
          "shortname-test",
          Some(
            ApiAddress(
              "line1",
              "line2",
              "city",
              "state",
              "canada",
              ApiAddress.PostalCode.CaPostalCode(
                ApiCAPostalCode.defaultInstance
              )
            )
          ),
          true,
          "www.test.com",
          "N/A",
          Some(ApiTenantId(testTenantId))
        )
      ),
      Some(ApiParent(parentIdTest)),
      Seq[ApiMemberId](
        ApiMemberId("test-member-id"),
        ApiMemberId("member2"),
        ApiMemberId("member3")
      ),
      Seq[ApiMemberId](
        ApiMemberId("member10"),
        ApiMemberId("member11"),
        ApiMemberId("member12")
      ),
      Seq.empty,
      Some(ApiMemberId(establishingMemberId)),
      Some(
        OrgMetaInfo(
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          Some(timestamp),
          Some(ApiMemberId(establishingMemberId)),
          ApiOrganizationStatus.DRAFT,
          Seq.empty[ApiOrganizationId]
        )
      )
    )

    val start = Timestamp.of(now.getEpochSecond, now.getNano)
    val end = Timestamp.of(now.getEpochSecond + 1000000L, now.getNano)
    val testMember = "test-member-id"
    val apiEventInfo = ApiEventInfo(
      "try-out-event",
      "School footbal try out",
      "www.nowhere.com",
      Some(ApiOrganizationId("test-organization-id")),
      Some(ApiGeoLocation(0.12, 0.438, 4.322)),
      Some(ApiReservationId("reserve-1")),
      Some(start),
      Some(end),
      false
    )
    val apiEventInfoPrivate = ApiEventInfo(
      "another-try-out-event",
      "School footbal try out",
      "www.nowhere.com",
      Some(ApiOrganizationId("test-organization-id")),
      Some(ApiGeoLocation(0.12, 0.438, 4.322)),
      Some(ApiReservationId("reserve-1")),
      Some(start),
      Some(end),
      true
    )
    val scheduleEvent = CreateEvent(
      Some(apiEventInfo),
      Some(ApiMemberId(testMember))
    )
    val scheduleEventPrivate = CreateEvent(
      Some(apiEventInfoPrivate),
      Some(ApiMemberId(testMember))
    )
    val testStoreId = "test-store-id"
    val testName = "test-name"
    val testDescription = "test-description"
    val testProductId1 = "test-product-id1"
    val testProductId2 = "test-product-id2"
    val testEventId = "test-event-id"
    val testEvent = ApiEventId(testEventId)
    val testVenueId = "test-venue-id"
    val testVenue = ApiVenueId(testVenueId)
    val testLocationId = "test-location-id"
    val testLocaltion = ApiLocationId(testLocationId)
    val testProducts = Seq[ApiProductId](
      ApiProductId(testProductId1),
      ApiProductId(testProductId2)
    )
    val testOrg = ApiOrganizationId(testOrgId)
    val testMember1 = "test-member1"
    val testMember2 = "test-member2"
    val testMember3 = "test-member3"
    val apiStoreInfo = ApiStoreInfo(
      testStoreId,
      testName,
      testDescription,
      testProducts,
      Some(testEvent),
      Some(testVenue),
      Some(testLocaltion),
      Some(testOrg)
    )

    val testSku = "test-product-id"
    val testSku2 = "test-product-id2"
    val testSku3 = "test-product-id3"
    val testRow = "test-row"
    val testSeat = "test-seat"
    val testSection = "test-section"
    val testEventId2 = "test-event-id2"
    val testEventId3 = "test-event-id3"
    val testImages = Seq[String]("test-image")
    val testPrice = 10.0
    val testCost = 3.0
    val testMemberId = "test-member-id"
    val testMemberId1 = "test-member-id1"
    val testTimestamp = Timestamp.of(now.getEpochSecond, now.getNano)
    val apiProductInfo = ApiProductInfo(
      testSku,
      testName,
      testDescription,
      testSection,
      testRow,
      testSeat,
      Some(ApiEventId(testEventId)),
      testImages,
      testPrice,
      testCost,
      Some(ApiStoreId(testStoreId))
    )
    val apiProductMetaInfo = ApiProductMetaInfo(
      Some(ApiMemberId(testMemberId)),
      Some(testTimestamp),
      Some(ApiMemberId(testMemberId1)),
      Some(testTimestamp)
    )
    val establishProduct = EstablishProduct(
      Some(apiProductInfo),
      Some(apiProductMetaInfo)
    )

    val testMemberId2 = "test-member-id2"
    val testOrganizationId = "test-organization-id"
    val testTenantId1 = "test-tenant-id1"
    val apiContact = ApiContact(
      "member-first-name",
      "member-last-name",
      Some(ApiEmailAddress("member@memberapi.com")),
      Some(ApiMobileNumber("987-878-0987")),
      "user-name"
    )
    val memberApiInfo = MemberApiInfo(
      Some(apiContact),
      "handle",
      "avartar",
      "member-name",
      "short-name",
      Some(ApiMobileNumber("987-878-0987")),
      Some(ApiEmailAddress("member@memberapi.com")),
      ApiNotificationPreference.SMS,
      Seq[ApiOrganizationId](ApiOrganizationId(testMemberId)),
      Some(ApiTenantId(testTenantId))
    )

    val testOrderId = "test-order-id"
    val testOrderId2 = "test-order-id2"
    val testOrderId3 = "test-order-id3"
    val testProductId = "test-product-id"
    val testProductId3 = "test-product-id3"
    val testQuantity = 10
    val testLineTotal = 20
    val testQuantity2 = 13
    val testLineTotal2 = 26
    val testLineItem1 = ApiLineItem(
      Some(ApiProductId(testProductId)),
      testQuantity,
      testLineTotal
    )
    val testLineItem2 = ApiLineItem(
      Some(ApiProductId(testProductId)),
      testQuantity2,
      testLineTotal2
    )
    val testLineItem3 = ApiLineItem(
      Some(ApiProductId(testProductId2)),
      testQuantity2,
      testLineTotal2
    )
    val testLineItem4 = ApiLineItem(
      Some(ApiProductId(testProductId3)),
      testQuantity2,
      testLineTotal2
    )
    val testSpecialInstruction = "test-special-instruction"
    val testOrderTotal = 100.0
    val testLineItems = Seq[ApiLineItem](testLineItem1, testLineItem2)
    val testLineItemsPrivateEvent = Seq[ApiLineItem](testLineItem3)
    val testLineItemsPrivateFailedEvent =
      Seq[ApiLineItem](testLineItem3, testLineItem4)
    val testOrderInfo = ApiOrderInfo(
      testOrderId,
      testLineItems,
      testSpecialInstruction,
      testOrderTotal
    )
    val testOrderInfoPrivateEvent = ApiOrderInfo(
      testOrderId,
      testLineItemsPrivateEvent,
      testSpecialInstruction,
      testOrderTotal
    )
    val testOrderInfoPrivateFailedEvent = ApiOrderInfo(
      testOrderId3,
      testLineItemsPrivateFailedEvent,
      testSpecialInstruction,
      testOrderTotal
    )
  }
}
