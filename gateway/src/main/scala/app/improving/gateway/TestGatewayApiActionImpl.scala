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
  ApiTenantId,
  OrganizationId
}
import app.improving.membercontext.member.{
  ApiInfo,
  ApiMemberIds,
  ApiMemberMap,
  ApiMemberStatus,
  ApiNotificationPreference,
  ApiRegisterMember,
  ApiRegisterMemberList,
  ApiUpdateMemberStatus,
  MemberActionService,
  MemberService
}
import app.improving.eventcontext.event.{
  ApiEventInfo,
  ApiGetEventById,
  ApiReservationId,
  ApiScheduleEvent,
  EventService
}
import app.improving.organizationcontext.organization.{
  ApiContacts,
  ApiEstablishOrganization,
  ApiMetaInfo,
  ApiOrganizationStatus,
  ApiOrganizationStatusUpdated,
  ApiParent,
  OrganizationService
}
import app.improving.storecontext.store.{
  ApiCreateStore,
  ApiStoreInfo,
  ApiUpdateStore,
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
import scala.concurrent.{Await, Future}
import scala.util.{Random, Success}
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory
import scala.language.postfixOps

import scala.concurrent.duration.{DurationInt, FiniteDuration}

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TestGatewayApiActionImpl(creationContext: ActionCreationContext)
    extends AbstractTestGatewayApiAction {

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val config: Config = ConfigFactory.load()

  val tenantService: TenantService =
    creationContext.getGrpcClient(
      classOf[TenantService],
      config.getString("app.improving.gateway.tenant.grpc-client-name")
    )

  val organizationService: OrganizationService = creationContext.getGrpcClient(
    classOf[OrganizationService],
    config.getString(
      "app.improving.gateway.organization.grpc-client-name"
    )
  )

  val eventService: EventService = creationContext.getGrpcClient(
    classOf[EventService],
    config.getString(
      "app.improving.gateway.event.grpc-client-name"
    )
  )

  val storeService: StoreService = creationContext.getGrpcClient(
    classOf[StoreService],
    config.getString(
      "app.improving.gateway.store.grpc-client-name"
    )
  )

  val productService: ProductService = creationContext.getGrpcClient(
    classOf[ProductService],
    config.getString(
      "app.improving.gateway.product.grpc-client-name"
    )
  )

  val memberService: MemberService = creationContext.getGrpcClient(
    classOf[MemberService],
    config.getString(
      "app.improving.gateway.member.grpc-client-name"
    )
  )

  private val memberAction = creationContext.getGrpcClient(
    classOf[MemberActionService],
    config.getString(
      "app.improving.gateway.member.grpc-client-name"
    )
  )

  override def handleStartScenario(
      startScenario: StartScenario
  ): Action.Effect[ScenarioResults] = {

    log.info("in handleStartScenario")

    val r = new scala.util.Random()

    val scenarioInfo: ScenarioInfo = startScenario.scenarioInfo match {
      case Some(info) => info
      case None =>
        effects.error("no info has been provided for the scenario!")
        ScenarioInfo.defaultInstance
    }

    log.info(s"in handleStartScenario scenarioInfo - ${scenarioInfo}")

    val initialMemberId: ApiMemberId =
      Await.result(
        memberService
          .registerMember(genApiRegisterInitialMember())
          .map(memberId => {
            log.info(
              s"handleStartScenario - initialMemberId - registerMember - ${memberId}"
            )
            memberService.updateMemberStatus(
              ApiUpdateMemberStatus(
                memberId.memberId,
                Some(ApiMemberId(memberId.memberId)),
                ApiMemberStatus.API_MEMBER_STATUS_ACTIVE
              )
            )
            memberId
          }),
        10 seconds
      )

    log.info(s"in handleStartScenario initialMemberId - ${initialMemberId}")

    val totalNumOfMembers = startScenario.scenarioInfo
      .map(_.numMembersPerOrg)
      .getOrElse(0) * startScenario.scenarioInfo
      .map(
        _.numOrgsPerTenant
      )
      .getOrElse(0) * startScenario.scenarioInfo.map(_.numTenants).getOrElse(0)

    log.info(s"in handleStartScenario totalNumOfMembers - ${totalNumOfMembers}")

    val tenantIds: Seq[ApiTenantId] = Await.result(
      Future
        .sequence(
          genApiEstablishTenants(scenarioInfo.numTenants)
            .map(establishTenant => {
              log.info(
                s"in handleStartScenario tenantIds - establishTenant ${establishTenant}"
              )
              tenantService
                .establishTenant(establishTenant)
                .map { tenantId =>
                  log.info(
                    s"in handleStartScenario tenantIds - establishTenant - tenantId ${tenantId}"
                  )
                  tenantService.activateTenant(
                    ApiActivateTenant(
                      tenantId.tenantId,
                      initialMemberId.memberId
                    )
                  )
                  tenantId
                }
            })
        ),
      10 seconds
    )

    log.info(s"in handleStartScenario tenantIds - ${tenantIds}")

    val establishedOrgs: Seq[(ApiOrganizationId, ApiEstablishOrganization)] =
      genApiEstablishOrgs(
        scenarioInfo.numOrgsPerTenant,
        scenarioInfo.maxOrgsDepth,
        scenarioInfo.maxOrgsWidth,
        tenantIds.toSet,
        Set(initialMemberId)
      )
        .map(msg => {
          val orgId = Await.result(
            organizationService.establishOrganization(msg),
            10 seconds
          )
          (orgId, msg)
        })

    log.info(
      s"in handleStartScenario establishedOrgs - ${establishedOrgs} - total orgs - ${establishedOrgs.size}"
    )

    Await.result(
      Future
        .sequence(
          establishedOrgs
            .map(_._1)
            .map(orgId => {
              organizationService
                .updateOrganizationStatus(
                  ApiOrganizationStatusUpdated(
                    orgId.organizationId,
                    ApiOrganizationStatus.API_ORGANIZATION_STATUS_ACTIVE
                  )
                )
            })
        ),
      10 seconds
    )

    log.info(
      s"in handleStartScenario establishedOrgs - updateOrganizationStatus"
    )

    val orgsByTenant: Map[String, OrganizationIds] =
      establishedOrgs
        .map { case (_, establishedOrg) =>
          establishedOrg.info
            .flatMap(_.tenant)
            .map(_.tenantId)
            .getOrElse("TenantId is NOT FOUND.") ->
            ApiOrganizationId(establishedOrg.orgId)
        }
        .groupBy(_._1)
        .map(tup => tup._1 -> OrganizationIds(tup._2.map(_._2).distinct))

    log.info(s"in handleStartScenario orgsByTenant - ${orgsByTenant}")

    val memberIds = Await.result(
      Future
        .sequence(
          genApiRegisterMemberLists(
            totalNumOfMembers,
            orgsByTenant.flatMap { case (tenantId, orgIds) =>
              orgIds.orgIds.map(orgId =>
                (
                  orgId,
                  initialMemberId,
                  ApiTenantId(tenantId),
                  establishedOrgs.toMap
                    .find(_._1 == orgId)
                    .flatMap(_._2.info.map(_.name))
                    .getOrElse(r.nextString(10))
                )
              )
            }.toSeq
          ).map { registerMemberList =>
            memberAction
              .registerMemberList(registerMemberList)
              .map(memberIds =>
                memberIds.memberIds.map(memberId => {
                  memberService.updateMemberStatus(
                    ApiUpdateMemberStatus(
                      memberId.memberId,
                      Some(ApiMemberId(memberId.memberId)),
                      ApiMemberStatus.API_MEMBER_STATUS_ACTIVE
                    )
                  )
                  memberId
                })
              )
          }
        )
        .map(_.flatten),
      10 seconds
    )

    log.info(s"in handleStartScenario memberIds - ${memberIds}")

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
            tup._2.flatMap(_._2)
          )
        )

    log.info(s"in handleStartScenario membersByOrg - ${membersByOrg}")

    val eventsByOrg: Map[String, EventIds] = Await
      .result(
        Future
          .sequence(
            genApiScheduleEvents(
              startScenario.scenarioInfo
                .map(_.numEventsPerOrg)
                .getOrElse(0) * establishedOrgs.size: Int,
              membersByOrg
            ).map(apiScheduleEvent => {
              eventService
                .scheduleEvent(apiScheduleEvent)
                .map(eventId => {
                  (
                    apiScheduleEvent.info
                      .flatMap(_.sponsoringOrg)
                      .getOrElse(
                        ApiOrganizationId("OrganizationId is NOT FOUND.")
                      ),
                    eventId
                  )
                })
            })
          ),
        10 seconds
      )
      .groupBy { orgAndEvents: (ApiOrganizationId, ApiEventId) =>
        orgAndEvents._1
      }
      .map { case (orgId, seq) =>
        orgId.organizationId -> EventIds(seq.map(_._2))
      }

    log.info(s"in handleStartScenario eventsByOrg - ${eventsByOrg}")

    val storesByOrg: Map[String, StoreIds] = Await
      .result(
        Future
          .sequence(
            genApiCreateStores(
              startScenario.scenarioInfo.map(_.numStores).getOrElse(0): Int,
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
          ),
        10 seconds
      )
      .groupBy(_._1)
      .map { case (orgId, seq) =>
        orgId.organizationId -> StoreIds(seq.map(_._2).toSeq)
      }

    log.info(s"in handleStartScenario storesByOrg - ${storesByOrg}")

    val numOfEvents =
      startScenario.scenarioInfo.map(_.numEventsPerOrg).getOrElse(0)
    val numOfOrgs =
      startScenario.scenarioInfo.map(_.numOrgsPerTenant).getOrElse(0)
    val numOfTenants =
      startScenario.scenarioInfo.map(_.numTenants).getOrElse(0)

    val total = numOfEvents * numOfOrgs * numOfTenants

    val productsByStore: Map[String, Skus] =
      genApiCreateProduct(
        total,
        eventsByOrg,
        storesByOrg
      ) match {
        case (eventsByOrg, storesByOrg, apiCreateProducts) => {

          log.info(
            s"in handleStartScenario genApiCreateProduct - apiCreateProducts ${apiCreateProducts}"
          )

          val result = Await
            .result(
              Future
                .sequence(
                  apiCreateProducts.map(apiCreateProduct => {
                    log.info(
                      s"in handleStartScenario genApiCreateProduct - apiCreateProduct ${apiCreateProduct}"
                    )
                    productService
                      .createProduct(apiCreateProduct)
                      .map(productId => {
                        apiCreateProduct.info
                          .flatMap(_.store)
                          .getOrElse(
                            ApiStoreId("ApiStoreId is NOT FOUND.")
                          ) -> productId
                      })
                  })
                ),
              10 seconds
            )
            .groupBy(_._1)
            .map { case (orgId, seq) =>
              orgId.storeId -> Skus(seq.map(_._2))
            }

          log.info(
            s"in handleStartScenario genApiCreateProduct - result ${result}"
          )

          val eventIds = eventsByOrg.values.map(_.eventIds).flatten

          Await.result(
            for {
              event <- eventService.getEventById(
                ApiGetEventById(
                  eventIds.toArray.apply(r.nextInt(eventIds.size)).eventId
                )
              )
              temp <- (Future
                .sequence(result.map { case (storeId, products) =>
                  storeService.updateStore(
                    ApiUpdateStore(
                      storeId,
                      Some(
                        ApiStoreInfo(
                          storeId = storeId,
                          products = products.skus,
                          event = Some(ApiEventId(event.eventId)),
                          venue = None,
                          location = None
                        )
                      )
                    )
                  )
                }))
            } yield temp,
            10 seconds
          )
          result
        }
        case error => {
          log.info(
            s"in handleStartScenario genApiCreateProduct - error ${error}"
          )
          throw new IllegalStateException(
            s"in handleStartScenario genApiCreateProduct - error ${error}"
          )
        }
      }

    log.info(s"in handleStartScenario productsByStore - ${productsByStore}")

    effects.reply(
      ScenarioResults(
        tenantIds,
        orgsByTenant,
        membersByOrg,
        eventsByOrg,
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
  ): (Map[String, EventIds], Map[String, StoreIds], Seq[ApiCreateProduct]) = {

    log.info(
      s"in handleStartScenario genApiCreateProduct - eventsByOrg ${eventsByOrg} storesByOrg ${storesByOrg}"
    )

    val r = new Random()
    val sku = UUID.randomUUID().toString
    val eventKeys = eventsByOrg.keySet
    val storeKeys = storesByOrg.keySet
    val orgId =
      eventKeys
        .intersect(storeKeys)
        .toArray
        .apply(r.nextInt(storeKeys.size + eventKeys.size))
    (
      eventsByOrg,
      storesByOrg,
      (1 to numOfProducts).map(i => {

        log.info(
          s"in handleStartScenario genApiCreateProduct - i ${i} eventsByOrg ${eventsByOrg} storesByOrg ${storesByOrg} "
        )

        log.info(
          s"in handleStartScenario genApiCreateProduct - orgId ${orgId} eventsByOrg(orgId) ${eventsByOrg
              .get(orgId)} storesByOrg(orgId) ${storesByOrg.get(orgId)} "
        )
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
              Seq[String](r.nextString(15)),
              r.nextDouble(),
              r.nextDouble(),
              storesByOrg(orgId).storeIds.headOption
            )
          )
        )
      })
    )
  }

  // val storeIds: Set[ApiStoreId]
  private def genApiCreateStores(
      numOfStores: Integer,
      membersByOrg: Map[String, MemberIds]
  ): Set[ApiCreateStore] = {
    val r = new Random()

    (1 to numOfStores)
      .map(_ => {
        val keys = membersByOrg.keys
        val orgId = keys.toArray.apply(r.nextInt(keys.size))
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
          membersByOrg.get(orgId).flatMap(_.memberIds.headOption)
        )
      })
      .toSet
  }

  private def genApiScheduleEvents(
      numOfEvents: Integer,
      membersByOrg: Map[String, MemberIds]
  ): Seq[ApiScheduleEvent] = {
    val r = new Random()

    (1 to numOfEvents).map(_ => {
      val organizationId =
        membersByOrg.keys.toArray.apply(r.nextInt(membersByOrg.size))
      val schedulingMember = membersByOrg(organizationId).memberIds.headOption
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
            )
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

    log.info(
      s"handleStartScenario genApiEstablishTenants numTenants ${numTenants}"
    )
    (1 to numTenants).map(i => {
      log.info(
        s"handleStartScenario genApiEstablishTenants numTenants ${i}"
      )
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
    })
  }

  private def genApiRegisterInitialMember() = {
    val r = new scala.util.Random()

    log.info(
      s"handleStartScenario genApiRegisterInitialMember"
    )
    ApiRegisterMember(
      UUID.randomUUID().toString,
      Some(
        ApiInfo(
          Some(
            ApiContact(
              r.nextString(10),
              r.nextString(10),
              None,
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
  }

  private def genApiRegisterMemberLists(
      numOfMembersPerOrg: Integer,
      orgsWithNameRegisteringMemberAndTenant: Seq[
        (ApiOrganizationId, ApiMemberId, ApiTenantId, String)
      ]
  ): Seq[ApiRegisterMemberList] = {

    log.info(
      s"handleStartScenario genApiRegisterMemberLists numOfMembersPerOrg ${numOfMembersPerOrg}"
    )

    val r = new scala.util.Random()

    orgsWithNameRegisteringMemberAndTenant.map(
      orgWithNameRegisteringMemberAndTenant =>
        ApiRegisterMemberList(
          Some(
            ApiMemberMap(
              (1 to numOfMembersPerOrg)
                .map(_ => {
                  UUID.randomUUID().toString -> genApiInfoForMember(
                    r,
                    orgWithNameRegisteringMemberAndTenant
                  )
                })
                .toMap
            )
          ),
          Some(orgWithNameRegisteringMemberAndTenant._2)
        )
    )
  }

  private def genApiInfoForMember(
      r: Random,
      orgWithMemberTenantAndName: (
          ApiOrganizationId,
          ApiMemberId,
          ApiTenantId,
          String
      )
  ) = {
    log.info(
      s"handleStartScenario genApiInfoForMember"
    )
    ApiInfo(
      Some(
        ApiContact(
          r.nextString(10),
          r.nextString(10),
          Some(
            ApiEmailAddress(
              genEmailAddressForName(r, orgWithMemberTenantAndName._4)
            )
          ),
          Some(
            ApiMobileNumber(
              genMobileNumber(r)
            )
          ),
          r.nextString(10)
        )
      ),
      r.nextString(10),
      r.nextString(10),
      r.nextString(10),
      r.nextString(10),
      ApiNotificationPreference.values(
        r.nextInt(ApiNotificationPreference.values.length)
      ),
      Seq(orgWithMemberTenantAndName._1),
      Some(orgWithMemberTenantAndName._3)
    )
  }

  private def genApiEstablishOrgs(
      numOrgs: Integer,
      maxOrgDepth: Integer,
      maxOrgWidth: Integer,
      tenantIds: Set[ApiTenantId],
      memberIds: Set[ApiMemberId]
  ): Seq[ApiEstablishOrganization] = {

    log.info(
      s"handleStartScenario genApiEstablishOrgs"
    )

    val r = new scala.util.Random()

    val parents = (1 to numOrgs).map(n => {
      log.info(
        s"handleStartScenario genApiEstablishOrgs parents numOrgs ${n}"
      )
      val i = r.nextInt() % memberIds.size
      val ii = r.nextInt() % memberIds.size
      log.info(
        s"handleStartScenario genApiEstablishOrgs parents i ${i} ii ${ii}"
      )
      val members =
        (if (i == ii) memberIds.slice(i, ii + 1)
         else if (i < ii) memberIds.slice(i, ii)
         else memberIds.slice(ii, i)).toSeq
      val owners =
        Seq(memberIds.toArray.apply(r.nextInt() % memberIds.size))

      log.info(
        s"handleStartScenario genApiEstablishOrgs parents members ${members} owners ${owners}"
      )

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
            Some(tenantIds.toArray.apply(r.nextInt(tenantIds.size)))
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
            ApiOrganizationStatus.API_ORGANIZATION_STATUS_DRAFT,
            Seq.empty[ApiOrganizationId]
          )
        )
      )
    })
    val subOrgs = (1 to maxOrgWidth).map(n => {
      log.info(
        s"handleStartScenario genApiEstablishOrgs subOrgs maxOrgWidth ${n}"
      )
      val i = r.nextInt() % memberIds.size
      val ii = r.nextInt() % memberIds.size

      log.info(
        s"handleStartScenario genApiEstablishOrgs subOrgs i ${i} ii ${ii}"
      )

      val members =
        (if (i == ii) memberIds.slice(i, ii + 1)
         else if (i < ii) memberIds.slice(i, ii)
         else memberIds.slice(ii, i)).toSeq
      val owners = Seq(memberIds.toArray.apply(r.nextInt() % memberIds.size))

      log.info(
        s"handleStartScenario genApiEstablishOrgs parents subOrgs members ${members} owners ${owners}"
      )

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
            Some(tenantIds.toArray.apply(r.nextInt(tenantIds.size)))
          )
        ),
        Some(
          ApiParent(parents.toArray.apply(r.nextInt(parents.length)).orgId)
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
            ApiOrganizationStatus.API_ORGANIZATION_STATUS_DRAFT,
            Seq.empty[ApiOrganizationId]
          )
        )
      )
    })
    val subsubOrgs = (1 to maxOrgDepth).map(n => {
      log.info(
        s"handleStartScenario genApiEstablishOrgs subsubOrgs maxOrgDepth ${n}"
      )
      val i = r.nextInt() % memberIds.size
      val ii = r.nextInt() % memberIds.size
      val members =
        (if (i == ii) memberIds.slice(i, ii + 1)
         else if (i < ii) memberIds.slice(i, ii)
         else memberIds.slice(ii, i)).toSeq
      val owners = Seq(memberIds.toArray.apply(r.nextInt() % memberIds.size))

      log.info(
        s"handleStartScenario genApiEstablishOrgs parents subsubOrgs ${members} owners ${owners}"
      )

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
            Some(tenantIds.toArray.apply(r.nextInt(tenantIds.size)))
          )
        ),
        Some(
          ApiParent(subOrgs.toArray.apply(r.nextInt(parents.length)).orgId)
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
            ApiOrganizationStatus.API_ORGANIZATION_STATUS_DRAFT,
            Seq.empty[ApiOrganizationId]
          )
        )
      )
    })

    parents ++ subOrgs ++ subsubOrgs
  }
}
