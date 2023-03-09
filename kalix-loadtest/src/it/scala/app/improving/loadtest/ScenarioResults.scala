package app.improving.loadtest

final case class OrganizationIds(
    orgIds: Seq[ApiOrganizationId] = Seq.empty
)

final case class ApiOrderIds(
    orderIds: Seq[ApiOrderId] = Seq.empty
)

final case class ApiOrganizationId(organizationId: String = "")
final case class ApiOrderId(orderId: String = "")
final case class ApiTenantId(tenantId: String = "")
final case class MemberIds(
    memberIds: Seq[ApiMemberId] = Seq.empty
)
final case class ApiMemberId(memberId: String = "")
final case class EventIds(
    eventIds: Seq[ApiEventId] = Seq.empty
)
final case class ApiEventId(eventId: String = "")
final case class StoreIds(
    storeIds: Seq[ApiStoreId] = Seq.empty
)
final case class ApiStoreId(storeId: String = "")
final case class Skus(
    skus: Seq[ApiSku] = Seq.empty
)
final case class ApiSku(sku: String = "")
final case class ScenarioResults(
    tenants: Seq[ApiTenantId],
    orgsForTenants: Map[String, OrganizationIds],
    membersForOrgs: Map[String, MemberIds],
    eventsForOrgs: Map[String, EventIds],
    storesForOrgs: Map[String, StoreIds],
    productsForStores: Map[String, Skus]
)
final case class OrdersForStores(
    ordersForStores: Map[String, ApiOrderInfo]
)

final case class OrderCreated(orderCreated: Option[ApiOrderId])
final case class ApiOrderInfo(
    lineItems: Seq[ApiLineItem] = Seq.empty,
    specialInstructions: String = ""
)
final case class ApiLineItem(
    product: Option[ApiSku] = None,
    quantity: Int = 0,
    lineTotal: Double = 0.0
)
