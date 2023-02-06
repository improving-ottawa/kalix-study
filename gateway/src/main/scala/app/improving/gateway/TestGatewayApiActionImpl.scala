package app.improving.gateway

import app.improving.{
  ApiAddress,
  ApiContact,
  ApiEmailAddress,
  ApiEventId,
  ApiGeoLocation,
  ApiMemberId,
  ApiMobileNumber,
  ApiOrganizationId,
  ApiProductId,
  ApiStoreId,
  ApiTenantId
}
import app.improving.membercontext.member.{
  ApiInfo,
  ApiMemberStatus,
  ApiRegisterMember,
  ApiUpdateMemberStatus,
  MemberService
}
import app.improving.membercontext.member.{
  ApiInfo,
  ApiMemberStatus,
  ApiRegisterMember,
  ApiUpdateMemberStatus,
  MemberService
}
import app.improving.eventcontext.event.{
  ApiEventInfo,
  ApiReservationId,
  ApiScheduleEvent,
  EventService
}
import app.improving.organizationcontext.organization.{
  ApiContacts,
  ApiEstablishOrganization,
  ApiMetaInfo,
  ApiOrganizationStatus,
  ApiParent,
  OrganizationService
}
import app.improving.storecontext.store.{
  ApiCreateStore,
  ApiStoreInfo,
  StoreService
}
import app.improving.productcontext.product.{
  ApiCreateProduct,
  ApiProductInfo,
  ProductService
}
import app.improving.tenantcontext.tenant.{
  ApiActivateTenant,
  ApiEstablishTenant,
  TenantService
}
import app.improving.gateway.util.util.{
  genAddress,
  genContact,
  genEmailAddressForName,
  genMobileNumber
}
import app.improving.organizationcontext.organization
import com.google.protobuf.timestamp.Timestamp

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Random, Success}
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory
import org.scalacheck.Gen

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TestGatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractTestGatewayApiAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val tenantService =
    creationContext.getGrpcClient(
      classOf[TenantService],
      config.getString("app.improving.gateway.tenant.grpc-client-name")
    )

  val organizationService = creationContext.getGrpcClient(
    classOf[OrganizationService],
    config.getString(
      "app.improving.gateway.organization.grpc-client-name"
    )
  )

  val eventService = creationContext.getGrpcClient(
    classOf[EventService],
    config.getString(
      "app.improving.gateway.event.grpc-client-name"
    )
  )

  val storeService = creationContext.getGrpcClient(
    classOf[StoreService],
    config.getString(
      "app.improving.gateway.store.grpc-client-name"
    )
  )

  val productService = creationContext.getGrpcClient(
    classOf[ProductService],
    config.getString(
      "app.improving.gateway.product.grpc-client-name"
    )
  )

  val memberService = creationContext.getGrpcClient(
    classOf[MemberService],
    config.getString(
      "app.improving.gateway.member.grpc-client-name"
    )
  )

  override def handleStartScenario(
      startScenario: StartScenario
  ): Action.Effect[ScenarioResults] = {

    val r = new scala.util.Random()

    val scenarioInfo: ScenarioInfo = startScenario.scenarioInfo match {
      case Some(info) => info
      case None =>
        effects.error("no info has been provided for the scenario!")
        ScenarioInfo.defaultInstance
    }

    val totalNumOfMembers = startScenario.scenarioInfo
      .map(_.numMembersPerOrg)
      .getOrElse(0) * startScenario.scenarioInfo
      .map(
        _.numOrgsPerTenant
      )
      .getOrElse(0) * startScenario.scenarioInfo.map(_.numTenants).getOrElse(0)

    val memberIds = Future
      .sequence(
        genApiRegisterMembers(totalNumOfMembers).map(registerMember => {
          memberService
            .registerMember(registerMember)
            .map(memberId => {
              memberService.updateMemberStatus(
                ApiUpdateMemberStatus(
                  memberId.memberId,
                  Some(ApiMemberId(memberId.memberId)),
                  ApiMemberStatus.ACTIVE
                )
              )
              memberId
            })
        })
      )
      .value match {
      case Some(Success(memberIds: Seq[ApiMemberId])) => memberIds
      case _                                          => Seq.empty[ApiMemberId]
    }

    val tenantIds: Seq[ApiTenantId] = Future
      .sequence(
        genApiEstablishTenants(scenarioInfo.numTenants)
          .map(establishTenant => {
            tenantService
              .establishTenant(establishTenant)
              .map(tenantId => {
                val i = r.nextInt(totalNumOfMembers - 1)
                tenantService.activateTenant(
                  ApiActivateTenant(
                    tenantId.tenantId,
                    memberIds.toArray.apply(i).memberId
                  )
                )
                tenantId
              })
          })
      )
      .value match {
      case Some(Success(tenantIds: Seq[ApiTenantId])) => tenantIds
      case _                                          => Seq.empty[ApiTenantId]
    }

    val establishedOrgs: Seq[(ApiOrganizationId, ApiEstablishOrganization)] =
      genApiEstablishOrgs(
        scenarioInfo.numOrgsPerTenant,
        scenarioInfo.maxOrgsDepth,
        scenarioInfo.maxOrgsWidth,
        tenantIds,
        memberIds
      )
        .map(msg =>
          (
            organizationService.establishOrganization(msg).value match {
              case Some(Success(orgId: ApiOrganizationId)) => (orgId, msg)
              case error =>
                throw new IllegalStateException(
                  s"TestGatewayApi: handleStartScenario - orgsByTenant - error: ${error}"
                )
            }
          )
        )

    val orgsByTenant: Map[String, OrganizationIds] =
      establishedOrgs
        .map {
          case (_, establishedOrg) => {
            establishedOrg.info
              .flatMap(_.tenant)
              .map(_.tenantId)
              .getOrElse("TenantId is NOT FOUND.") ->
              ApiOrganizationId(establishedOrg.orgId)
          }
        }
        .groupBy(_._1)
        .map(tup => tup._1 -> OrganizationIds(tup._2.map(_._2).distinct))

    val membersByOrg: Map[String, MemberIds] =
      establishedOrgs
        .map {
          case (orgId, establishedOrg) => {
            orgId -> establishedOrg.members
          }
        }
        .groupBy(_._1)
        .map(tup =>
          tup._1.organizationId -> MemberIds(
            tup._2.map(_._2).flatten
          )
        )

    val eventsByOrg: Map[String, EventIds] = (Future
      .sequence(
        genApiScheduleEvents(
          startScenario.scenarioInfo.map(_.numEventsPerOrg).getOrElse(0),
          membersByOrg
        ).map(apiScheduleEvent => {
          eventService
            .scheduleEvent(apiScheduleEvent)
            .map(eventId => {
              (
                apiScheduleEvent.info
                  .flatMap(_.sponsoringOrg)
                  .getOrElse(ApiOrganizationId("OrganizationId is NOT FOUND.")),
                eventId
              )
            })
        })
      )
      .value match {
      case Some(Success(seq)) => seq
      case error =>
        throw new IllegalStateException(
          s"TestGatewayApi: handleStartScenario - eventsByOrg - error: ${error}"
        )
    }).groupBy(_._1).map { case (orgId, seq) =>
      orgId.organizationId -> EventIds(seq.map(_._2))
    }

    val storesByOrg: Map[String, StoreIds] = (Future
      .sequence(
        genApiCreateStores(
          startScenario.scenarioInfo.map(_.numStores).getOrElse(0),
          membersByOrg
        ).map(apiCreateStore => {
          storeService
            .createStore(apiCreateStore)
            .map(apiStoreId => {
              (
                apiCreateStore.info
                  .flatMap(_.sponsoringOrg)
                  .getOrElse(
                    ApiOrganizationId("ApiOrganizationId is NOT FOUND.")
                  ),
                apiStoreId
              )
            })
        })
      )
      .value match {
      case Some(Success(seq)) => seq
      case error =>
        throw new IllegalStateException(
          s"TestGatewayApi: handleStartScenario - storeIds - error: ${error}"
        )
    }).groupBy(_._1).map { case (orgId, seq) =>
      orgId.organizationId -> StoreIds(seq.map(_._2).toSeq)
    }

    val numOfEvents =
      startScenario.scenarioInfo.map(_.numEventsPerOrg).getOrElse(0)
    val numOfOrgs =
      startScenario.scenarioInfo.map(_.numOrgsPerTenant).getOrElse(0)
    val numOfTenants =
      startScenario.scenarioInfo.map(_.numTenants).getOrElse(0)

    val total = numOfEvents * numOfOrgs * numOfTenants

    val productsByStore: Map[String, Skus] = (Future
      .sequence(
        genApiCreateProduct(
          total,
          eventsByOrg,
          storesByOrg
        ).map(apiCreateProduct => {
          productService
            .createProduct(apiCreateProduct)
            .map(productId => {
              apiCreateProduct.info
                .flatMap(_.store)
                .getOrElse(ApiStoreId("ApiStoreId is NOT FOUND.")) -> productId
            })
        })
      )
      .value match {
      case Some(Success(seq)) => seq
      case error =>
        throw new IllegalStateException(
          s"TestGatewayApi: handleStartScenario - storeIds - error: ${error}"
        )
    }).groupBy(_._1).map { case (orgId, seq) =>
      orgId.storeId -> Skus(seq.map(_._2))
    }

    effects.reply(
      ScenarioResults(
        tenantIds,
        orgsByTenant,
        membersByOrg,
        eventsByOrg,
        ???,
        Some(
          storesByOrg.values.foldLeft(StoreIds(Seq.empty))(
            (accum, storeIds) => {
              StoreIds(accum.storeIds ++ storeIds.storeIds)
            }
          )
        ),
        productsByStore
      )
    )
  }

  private def genApiCreateProduct(
      numOfProducts: Int,
      eventsByOrg: Map[String, EventIds],
      storesByOrg: Map[String, StoreIds]
  ): Seq[ApiCreateProduct] = {
    val r = new Random()
    val sku = UUID.randomUUID().toString
    val keys = eventsByOrg.keys
    val orgId = eventsByOrg.keys.toArray.apply(r.nextInt(keys.size - 1))
    (1 to numOfProducts).map(_ => {
      ApiCreateProduct(
        sku,
        Some(
          ApiProductInfo(
            sku,
            r.nextString(15),
            r.nextString(15),
            r.nextString(15),
            r.nextString(15),
            r.nextString(15),
            eventsByOrg(orgId).eventIds.headOption,
            Seq[String](r.nextInt(15)),
            r.nextDouble(),
            r.nextDouble(),
            storesByOrg(orgId).storeIds.headOption
          )
        )
      )
    })
  }

  // val storeIds: Set[ApiStoreId]
  private def genApiCreateStores(
      numOfStores: Integer,
      membersByOrg: Map[String, Set[ApiMemberId]]
  ): Set[ApiCreateStore] = {
    val r = new Random()

    (1 to numOfStores)
      .map(_ => {
        val keys = membersByOrg.keys
        val orgId = keys.toArray.apply(r.nextInt(keys.size - 1))
        ApiCreateStore(
          UUID.randomUUID().toString,
          Some(
            ApiStoreInfo(
              r.nextString(15),
              r.nextString(15),
              r.nextString(15),
              Seq.empty[ApiProductId],
              None,
              None,
              None,
              Some(
                ApiOrganizationId(orgId)
              )
            )
          ),
          membersByOrg.get(orgId).flatMap(_.headOption)
        )
      })
      .toSet
  }

  private def genApiScheduleEvents(
      numOfEvents: Integer,
      membersByOrg: Map[String, Set[ApiMemberId]]
  ): Seq[ApiScheduleEvent] = {
    val r = new Random()

    (1 to numOfEvents).map(_ => {
      val organizationId =
        membersByOrg.keys.toArray.apply(r.nextInt(membersByOrg.size - 1))
      val schedulingMember = membersByOrg(organizationId).headOption
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(
        now.getEpochSecond,
        now.getNano
      )
      ApiScheduleEvent(
        UUID.randomUUID().toString,
        Some(
          ApiEventInfo(
            r.nextString(15),
            r.nextString(15),
            r.nextString(15),
            Some(ApiOrganizationId(organizationId)),
            Some(
              ApiGeoLocation(r.nextDouble(), r.nextDouble(), r.nextDouble())
            ),
            Some(ApiReservationId(r.nextString(15))),
            Some(timestamp),
            Some(
              Timestamp.of(
                now.getEpochSecond + r.nextLong(100000),
                now.getNano + r.nextInt()
              )
            ),
            false
          )
        ),
        schedulingMember
      )
    })
  }

  private def genApiEstablishTenants(
      numTenants: Integer
  ): Seq[ApiEstablishTenant] = {
    val r = new scala.util.Random()
    val tenantName =
      r.nextString(10)

    (1 to numTenants).map(_ =>
      ApiEstablishTenant(
        UUID.randomUUID().toString,
        Some(
          app.improving.tenantcontext.tenant.ApiInfo(
            tenantName,
            Some(
              ApiContact(
                r.nextString(10),
                r.nextString(10),
                Some(
                  ApiEmailAddress(genEmailAddressForName(r, tenantName))
                ),
                Some(ApiMobileNumber(genMobileNumber(r))),
                r.nextString(10)
              )
            ),
            Some(genAddress(r))
          )
        )
      )
    )
  }

  private def genApiRegisterMembers(
      numOfMembers: Integer
  ): Seq[ApiRegisterMember] = {
    val r = new scala.util.Random()

    (1 to numOfMembers).map(_ => {
      val name = r.nextString(10)
      ApiRegisterMember(
        UUID.randomUUID().toString,
        Some(
          ApiInfo(
            Some(
              ApiContact(
                name,
                r.nextString(10),
                Some(
                  ApiEmailAddress(
                    genEmailAddressForName(r, name)
                  )
                ),
                Some(
                  ApiMobileNumber(
                    genMobileNumber(r)
                  )
                ),
                r.nextString(10)
              )
            )
          )
        )
      )
    })
  }

  private def genApiEstablishOrgs(
      numOrgs: Integer,
      maxOrgDepth: Integer,
      maxOrgWidth: Integer,
      tenantIds: Set[ApiTenantId],
      memberIds: Set[ApiMemberId]
  ): Seq[ApiEstablishOrganization] = {
    val r = new scala.util.Random()

    val parents = (1 to numOrgs).map(_ => {
      val i = r.nextInt() % memberIds.size
      val ii = r.nextInt() % memberIds.size
      val members =
        (if (i < ii) memberIds.slice(i, ii) else memberIds.slice(ii, i)).toSeq
      val owners =
        Seq(memberIds.toArray.apply(r.nextInt() % memberIds.size - 1))
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      ApiEstablishOrganization(
        UUID.randomUUID().toString,
        Some(
          organization.ApiInfo(
            r.nextString(10),
            r.nextString(5),
            Some(genAddress(r)),
            r.nextBoolean(),
            r.nextString(15),
            r.nextString(15),
            Some(tenantIds.toArray.apply(r.nextInt(tenantIds.size - 1)))
          )
        ),
        None,
        members,
        owners,
        Seq(
          ApiContacts(
            owners,
            owners,
            members
          )
        ),
        owners.headOption,
        Some(
          ApiMetaInfo(
            Some(timestamp),
            owners.headOption,
            Some(timestamp),
            owners.headOption,
            ApiOrganizationStatus.DRAFT,
            Seq.empty[ApiOrganizationId]
          )
        )
      )
    })
    val subOrgs = (1 to maxOrgWidth).map(_ => {
      val i = r.nextInt() % memberIds.size
      val ii = r.nextInt() % memberIds.size
      val members =
        (if (i < ii) memberIds.slice(i, ii) else memberIds.slice(ii, i)).toSeq
      val owners = Seq(memberIds.toArray.apply(r.nextInt() % memberIds.size))
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      ApiEstablishOrganization(
        UUID.randomUUID().toString,
        Some(
          organization.ApiInfo(
            r.nextString(10),
            r.nextString(5),
            Some(genAddress(r)),
            r.nextBoolean(),
            r.nextString(15),
            r.nextString(15),
            Some(tenantIds.toArray.apply(r.nextInt(tenantIds.size - 1)))
          )
        ),
        Some(
          ApiParent(parents.toArray.apply(r.nextInt(parents.length - 1)).orgId)
        ),
        members,
        owners,
        Seq(
          ApiContacts(
            owners,
            owners,
            members
          )
        ),
        owners.headOption,
        Some(
          ApiMetaInfo(
            Some(timestamp),
            owners.headOption,
            Some(timestamp),
            owners.headOption,
            ApiOrganizationStatus.DRAFT,
            Seq.empty[ApiOrganizationId]
          )
        )
      )
    })
    val subsubOrgs = (1 to maxOrgDepth).map(_ => {
      val i = r.nextInt() % memberIds.size
      val ii = r.nextInt() % memberIds.size
      val members =
        (if (i < ii) memberIds.slice(i, ii) else memberIds.slice(ii, i)).toSeq
      val owners = Seq(memberIds.toArray.apply(r.nextInt() % memberIds.size))
      val now = java.time.Instant.now()
      val timestamp = Timestamp.of(now.getEpochSecond, now.getNano)
      ApiEstablishOrganization(
        UUID.randomUUID().toString,
        Some(
          organization.ApiInfo(
            r.nextString(10),
            r.nextString(5),
            Some(genAddress(r)),
            r.nextBoolean(),
            r.nextString(15),
            r.nextString(15),
            Some(tenantIds.toArray.apply(r.nextInt(tenantIds.size - 1)))
          )
        ),
        Some(
          ApiParent(subOrgs.toArray.apply(r.nextInt(parents.length - 1)).orgId)
        ),
        members,
        owners,
        Seq(
          ApiContacts(
            owners,
            owners,
            members
          )
        ),
        owners.headOption,
        Some(
          ApiMetaInfo(
            Some(timestamp),
            owners.headOption,
            Some(timestamp),
            owners.headOption,
            ApiOrganizationStatus.DRAFT,
            Seq.empty[ApiOrganizationId]
          )
        )
      )
    })

    parents ++ subOrgs ++ subsubOrgs
  }

  //  private def genApiRegisterMemberLists(
  //      numMembersPerOrg: Integer,
  //      orgIds: Set[ApiOrganizationId],
  //      registeringMember: ApiMemberId
  //  ): Seq[ApiRegisterMemberList] = orgIds.toSeq.map { orgId =>
  //    ApiRegisterMemberList(
  //      "",
  //      Some(
  //        ApiMemberMap(
  //          genApiMemberInfos(numMembersPerOrg, orgId)
  //            .map(info => UUID.randomUUID().toString -> info)
  //            .toMap
  //        )
  //      ),
  //      Some(registeringMember)
  //    )
  //  }
  //
  //  private def genApiMemberInfos(
  //      numMembers: Integer,
  //      orgId: ApiOrganizationId
  //  ): Seq[ApiInfo] = {}
}
