package app.improving.gateway

import app.improving.{ApiContact, ApiEmailAddress, ApiEventId, ApiGeoLocation, ApiLocationId, ApiMemberId, ApiMobileNumber, ApiOrganizationId, ApiProductId, ApiStoreId, ApiTenantId, ApiVenueId}
import app.improving.membercontext.member.{ApiInfo, ApiMemberIds, ApiMemberMap, ApiMemberStatus, ApiNotificationPreference, ApiRegisterMember, ApiRegisterMemberList, ApiUpdateInfo, ApiUpdateMemberInfo, ApiUpdateMemberStatus, MemberActionService, MemberService}
import app.improving.eventcontext.event.{ApiEventInfo, ApiGetEventById, ApiScheduleEvent, EventService}
import app.improving.organizationcontext.organization.{ApiContacts, ApiEstablishOrganization, ApiMetaInfo, ApiOrganizationStatus, ApiOrganizationStatusUpdated, ApiParent, OrganizationService}
import app.improving.storecontext.store.{ApiCreateStore, ApiStoreInfo, ApiStoreUpdateInfo, ApiUpdateStore, StoreService}
import app.improving.productcontext.product.{ApiCreateProduct, ApiProductInfo, ProductService}
import app.improving.tenantcontext.tenant.{ApiActivateTenant, ApiEstablishTenant, TenantService}
import app.improving.gateway.util.util.{genAddress, genEmailAddressForName, genMobileNumber}
import app.improving.ordercontext.order.OrderService
import app.improving.eventcontext.event.{ApiEventInfo, ApiGetEventById, ApiScheduleEvent, EventService}
import app.improving.organizationcontext.organization
import com.google.protobuf.empty.Empty
import com.google.protobuf.timestamp.Timestamp

import java.util.UUID
import scala.concurrent.{Await, Future}
import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.concurrent.duration.DurationInt

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

  val orderService: OrderService = creationContext.getGrpcClient(
    classOf[OrderService],
    config.getString(
      "app.improving.gateway.order.grpc-client-name"
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

    log.info(s"in handleStartScenario scenarioInfo - $scenarioInfo")

    val initialMemberId: ApiMemberId =
      Await.result(
        memberService
          .registerMember(genApiRegisterInitialMember())
          .map(memberId => {
            log.info(
              s"handleStartScenario - initialMemberId - registerMember - $memberId"
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

    log.info(s"in handleStartScenario initialMemberId - $initialMemberId")

    log.info(
      s"in handleStartScenario numOfTenants - ${startScenario.scenarioInfo.map(_.numTenants).getOrElse(0)}"
    )

    val tenantIds: Seq[ApiTenantId] = Await.result(
      Future
        .sequence(
          genApiEstablishTenants(scenarioInfo.numTenants)
            .map(establishTenant => {
              log.info(
                s"in handleStartScenario tenantIds - establishTenant $establishTenant"
              )
              tenantService
                .establishTenant(establishTenant)
                .map { tenantId =>
                  log.info(
                    s"in handleStartScenario tenantIds - establishTenant - tenantId $tenantId"
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

    log.info(s"in handleStartScenario tenantIds - $tenantIds")

    val owners: Map[ApiTenantId, Set[ApiMemberId]] =
      Await
        .result(
          Future.sequence(tenantIds.map { tenant: ApiTenantId =>
            memberService
              .registerMember(
                genApiRegisterInitialMember(forTenant = Some(tenant))
              )
              .map(memberId => {
                log.info(
                  s"handleStartScenario - initialMemberId - registerMember - $memberId"
                )
                memberService.updateMemberStatus(
                  ApiUpdateMemberStatus(
                    memberId.memberId,
                    Some(ApiMemberId(memberId.memberId)),
                    ApiMemberStatus.API_MEMBER_STATUS_ACTIVE
                  )
                )
                (tenant, memberId)
              })
          }),
          10 seconds
        )
        .groupBy(_._1)
        .map(tuple => tuple._1 -> tuple._2.map(_._2).toSet)

    var topOrgs: Map[ApiTenantId, ApiOrganizationId] = Map.empty

    val establishedOrgs: Map[ApiOrganizationId, ApiEstablishOrganization] =
      genApiEstablishOrgs(
        scenarioInfo.maxOrgsDepth,
        scenarioInfo.maxOrgsWidth,
        owners
      )
        .map(msg => {
          val orgId = Await.result(
            organizationService.establishOrganization(msg),
            10 seconds
          )
          msg.info.foreach { info =>
            info.tenant.foreach { tenant =>
              topOrgs = topOrgs ++ Map(tenant -> orgId)
            }
          }
          orgId -> msg
        })
        .toMap

    log.info(
      s"in handleStartScenario establishedOrgs total orgs - ${establishedOrgs.size}"
    )

    Await.result(
      Future
        .sequence(
          establishedOrgs.keys
            .map(orgId => {
              organizationService
                .updateOrganizationStatus(
                  ApiOrganizationStatusUpdated(
                    Some(orgId),
                    ApiOrganizationStatus.API_ORGANIZATION_STATUS_ACTIVE
                  )
                )
            })
        ),
      10 seconds
    )

    Await.result(
      Future
        .sequence(
          owners
            .flatMap(ownersForTenant =>
              ownersForTenant._2
                .map(owner =>
                  memberService
                    .updateMemberInfo(
                      ApiUpdateMemberInfo(
                        owner.memberId,
                        Some(
                          ApiUpdateInfo(
                            None,
                            Some(""),
                            Some(""),
                            Some(""),
                            Some(""),
                            Some(ApiNotificationPreference.API_NOTIFICATION_PREFERENCE_EMAIL),
                            Seq(topOrgs(ownersForTenant._1)),
                            Some(ownersForTenant._1)
                          )
                        )
                      )
                    )
                )
            )
        ),
      10 seconds
    )

    log.info(
      s"in handleStartScenario establishedOrgs - updateOrganizationStatus"
    )

    val orgsByTenant: Map[ApiTenantId, Seq[ApiOrganizationId]] =
      establishedOrgs.toSeq
        .map {
          case (
                orgId: ApiOrganizationId,
                establishedOrg: ApiEstablishOrganization
              ) =>
            (
              establishedOrg.info
                .flatMap(_.tenant)
                .getOrElse(ApiTenantId.defaultInstance),
              orgId
            )
        }
        .groupBy(_._1)
        .map(tup => tup._1 -> tup._2.map(_._2).distinct)

    log.info(s"in handleStartScenario orgsByTenant - $orgsByTenant")

    val memberIds: Seq[ApiMemberId] = Await.result(
      Future
        .sequence(
          genApiRegisterMemberLists(
            scenarioInfo.numMembersPerOrg,
            orgsByTenant.flatMap { case (tenantId, orgIds) =>
              orgIds.map(orgId =>
                (
                  orgId,
                  initialMemberId,
                  tenantId,
                  establishedOrgs
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

    log.info(s"in handleStartScenario memberIds - $memberIds")

    val membersByOrg: Map[ApiOrganizationId, Seq[ApiMemberId]] =
      establishedOrgs
        .map { case (orgId, establishedOrg) =>
          orgId -> establishedOrg.members
        }
        .groupBy(_._1)
        .map(tup => tup._1 -> tup._2.flatMap(_._2).toSeq)

    log.info(s"in handleStartScenario membersByOrg - $membersByOrg")

    val eventsByOrg: Map[ApiOrganizationId, Set[ApiEventId]] = Await
      .result(
        Future
          .sequence(
            genApiScheduleEvents(
              startScenario.scenarioInfo
                .map(_.numEventsPerOrg)
                .getOrElse(0): Int,
              membersByOrg
            ).map(apiScheduleEvent => {
              eventService
                .scheduleEvent(apiScheduleEvent)
                .map(eventId => {
                  (
                    apiScheduleEvent.info
                      .flatMap(_.sponsoringOrg)
                      .getOrElse(
                        ApiOrganizationId.defaultInstance
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
      .map {
        case (
              orgId: ApiOrganizationId,
              seq: Seq[(ApiOrganizationId, ApiEventId)]
            ) =>
          orgId -> seq.map(_._2).toSet
      }

    log.info(s"in handleStartScenario eventsByOrg - $eventsByOrg")

    val storesByOrg: Map[ApiOrganizationId, Set[ApiStoreId]] = Await
      .result(
        Future
          .sequence(
            genApiCreateStores(
              owners.flatMap(owner =>
                owner._2
                  .map(memberId => orgsByTenant(owner._1).head -> memberId)
                  .groupBy(_._1)
                  .map(tup => tup._1 -> tup._2.map(_._2))
              ),
              eventsByOrg
            ).map(apiCreateStore => {
              storeService
                .createStore(apiCreateStore)
                .map(apiStoreId => {
                  apiCreateStore.info
                    .flatMap(_.sponsoringOrg)
                    .getOrElse(
                      ApiOrganizationId.defaultInstance
                    ) ->
                    apiStoreId
                })
            })
          ),
        10 seconds
      )
      .groupBy(_._1)
      .map(orgWithStores =>
        orgWithStores._1 -> orgWithStores._2.map(_._2).toSet
      )

    log.info(s"in handleStartScenario storesByOrg - $storesByOrg")

    val productsByStore =
      genApiCreateProduct(
        scenarioInfo.numTicketsPerEvent,
        eventsByOrg,
        storesByOrg,
        orgsByTenant,
        orgsByTenant
          .map(tenantOrgs => tenantOrgs._2.map(org => org -> tenantOrgs._1))
          .toSeq
          .flatten
          .toMap
      ) match {
        case productsMap =>
          val apiCreateProducts = productsMap.values.flatten
          log.info(
            s"in handleStartScenario genApiCreateProduct - apiCreateProducts $productsMap"
          )

          val result = Await
            .result(
              Future
                .sequence(
                  apiCreateProducts.map { apiCreateProduct =>
                    log.info(
                      s"in handleStartScenario genApiCreateProduct - apiCreateProduct $apiCreateProduct"
                    )
                    productService
                      .createProduct(apiCreateProduct)
                      .map { productId =>
                        apiCreateProduct.info
                          .flatMap(_.store)
                          .getOrElse(
                            ApiStoreId.defaultInstance
                          ) -> productId
                      }
                  }
                ),
              10 seconds
            )
            .groupBy(_._1)
            .map { case (storeId, set) =>
              log.info(
                s"in handleStartScenario - apiCreateProduct storeId $storeId set $set"
              )
              storeId -> Skus(set.map(_._2).toSeq)
            }

          log.info(
            s"in handleStartScenario genApiCreateProduct - result $result"
          )

          val eventIds = eventsByOrg.values.toSeq.flatten
          Await.result(
            for {
              event <- eventService.getEventById(
                ApiGetEventById(
                  Some(eventIds.toArray.apply(r.nextInt(eventIds.size)))
                )
              )
              temp <- Future
                .sequence(result.map { case (storeId, products) =>
                  storeService.updateStore(
                    ApiUpdateStore(
                      Some(storeId),
                      Some(
                        ApiStoreUpdateInfo(
                          products = products.skus,
                          event = event.eventId,
                          venue = Some(ApiVenueId("test-venue-id")),
                          location = Some(ApiLocationId("test-location-id"))
                        )
                      )
                    )
                  )
                })
            } yield temp,
            10 seconds
          )

          result
      }

    log.info(s"in handleStartScenario productsByStore - $productsByStore")

    effects.reply(
      ScenarioResults(
        tenantIds,
        orgsByTenant.map(tenantWithOrgs =>
          tenantWithOrgs._1.toString -> OrganizationIds(tenantWithOrgs._2)
        ),
        membersByOrg.map(orgWithMembers =>
          orgWithMembers._1.toString -> MemberIds(orgWithMembers._2)
        ),
        eventsByOrg.map(orgWithEvents =>
          orgWithEvents._1.toString -> EventIds(orgWithEvents._2.toSeq)
        ),
        Some(
          storesByOrg.values.foldLeft(StoreIds(Seq.empty))(
            (accum, storeIds) => {
              StoreIds(accum.storeIds ++ storeIds)
            }
          )
        ),
        productsByStore
      )
    )
  }

  override def handleEndScenario(
      endScenario: EndScenario
  ): Action.Effect[Empty] = {
    endScenario.orders.map(
      _.orderIds.map(id => orderService.releaseOrder(ApiReleaseOrder(Some(id))))
    )
    endScenario.products.map(
      _.skus.map(id =>
        productService.releaseProduct(ApiReleaseProduct(Some(id)))
      )
    )
    endScenario.stores.map(
      _.storeIds.map(id => storeService.releaseStore(ApiReleaseStore(Some(id))))
    )
    endScenario.events.map(
      _.eventIds.map(id => eventService.releaseEvent(ApiReleaseEvent(Some(id))))
    )
    endScenario.members.map(
      _.memberIds.map(id =>
        memberService.releaseMember(ApiReleaseMember(Some(id)))
      )
    )

    effects.reply(Empty.defaultInstance)
  }

  private def genApiCreateProduct(
      numOfProductsPerEvent: Int,
      eventsByOrg: Map[ApiOrganizationId, Set[ApiEventId]],
      storesByOrg: Map[ApiOrganizationId, Set[ApiStoreId]],
      orgsByTenant: Map[ApiTenantId, Seq[ApiOrganizationId]],
      tenantsByOrg: Map[ApiOrganizationId, ApiTenantId]
  ): Map[ApiStoreId, Set[ApiCreateProduct]] = {

    log.info(
      s"in handleStartScenario genApiCreateProduct - eventsByOrg $eventsByOrg storesByOrg $storesByOrg numOfProductsPerEvent $numOfProductsPerEvent"
    )

    val r = new Random()

    eventsByOrg
      .flatMap { case (orgId: ApiOrganizationId, eventIds: Set[ApiEventId]) =>
        eventIds.map { eventId: ApiEventId =>
          log.info(
            s"in handleStartScenario genApiCreateProduct orgsForTenant ${orgsByTenant(tenantsByOrg(orgId))
                .dropWhile(!storesByOrg.contains(_))}"
          )
          val storeId: ApiStoreId =
            if (storesByOrg.contains(orgId))
              storesByOrg(orgId).head
            else {
              storesByOrg(
                orgsByTenant(tenantsByOrg(orgId))
                  .dropWhile(
                    !storesByOrg.contains(_)
                  )
                  .head
              ).head
            }

          val numProducts = r.between(1, numOfProductsPerEvent + 1)

          log.info(
            s"in handleStartScenario genApiCreateProduct - $numProducts products for org $orgId in store $storeId "
          )
          (1 to numProducts)
            .map { _ =>
              val sku = UUID.randomUUID().toString
              storeId -> ApiCreateProduct(
                sku,
                Some(
                  ApiProductInfo(
                    sku,
                    r.nextString(15),
                    r.nextString(15),
                    r.nextString(15),
                    r.nextString(15),
                    r.nextString(15),
                    Some(eventId),
                    Seq[String](r.nextString(15)),
                    r.nextDouble(),
                    r.nextDouble(),
                    Some(
                      storeId
                    )
                  )
                )
              )
            }
        }.toSeq
      }
      .toSeq
      .flatten
      .groupBy(_._1)
      .map(tuple => tuple._1 -> tuple._2.map(_._2).toSet)
  }

  private def genApiCreateStores(
      ownersByOrg: Map[ApiOrganizationId, Set[ApiMemberId]],
      eventsByOrg: Map[ApiOrganizationId, Set[ApiEventId]]
  ): Seq[ApiCreateStore] = {
    val r = new Random()
    log.info(
      s"in handleStartScenario genApiCreateStores - $eventsByOrg eventsByOrg $ownersByOrg ownersByOrg "
    )

    ownersByOrg.keys.map { orgId =>
      ApiCreateStore(
        Some(ApiStoreId(UUID.randomUUID().toString)),
        Some(
          ApiStoreInfo(
            Some(ApiStoreId(r.nextString(15))),
            r.nextString(15),
            r.nextString(15),
            Seq.empty[ApiProductId],
            None,
            None,
            None,
            Some(
              orgId
            )
          )
        ),
        Some(ownersByOrg(orgId).head)
      )
    }.toSeq
  }

  private def genApiScheduleEvents(
      numOfEvents: Integer,
      membersByOrg: Map[ApiOrganizationId, Seq[ApiMemberId]]
  ): Seq[ApiScheduleEvent] = {
    val r = new Random()

    membersByOrg.keys
      .map { organizationId =>
        (1 to r.between(1, numOfEvents + 1)).map { _ =>
          val schedulingMember =
            membersByOrg.get(organizationId).flatMap(_.headOption)

          val now = java.time.Instant.now()
          val timestamp = Timestamp.of(
            now.getEpochSecond,
            now.getNano
          )
          ApiScheduleEvent(
            Some(ApiEventId(UUID.randomUUID().toString)),
            Some(
              ApiEventInfo(
                r.nextString(15),
                r.nextString(15),
                r.nextString(15),
                Some(organizationId),
                Some(
                  ApiGeoLocation(r.nextDouble(), r.nextDouble(), r.nextDouble())
                ),
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
        }
      }
      .toSeq
      .flatten
  }

  private def genApiRegisterInitialMember(
      forTenant: Option[ApiTenantId] = None
  ) = {
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
          ),
          r.nextString(10),
          r.nextString(10),
          r.nextString(10),
          r.nextString(10),
          Some(ApiNotificationPreference.values(
            r.nextInt(ApiNotificationPreference.values.length)
          )),
          Seq.empty,
          forTenant
        )
      )
    )
  }

  private def genApiEstablishTenants(
      numTenants: Integer
  ): Seq[ApiEstablishTenant] = {
    val r = new scala.util.Random()

    log.info(
      s"handleStartScenario genApiEstablishTenants numTenants $numTenants"
    )
    (1 to numTenants).map(i => {
      log.info(
        s"handleStartScenario genApiEstablishTenants numTenants $i"
      )

      val tenantName = r.nextString(10)

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

  private def genApiRegisterMemberLists(
      numOfMembersPerOrg: Integer,
      orgsWithNameRegisteringMemberAndTenant: Seq[
        (ApiOrganizationId, ApiMemberId, ApiTenantId, String)
      ]
  ): Seq[ApiRegisterMemberList] = {

    log.info(
      s"handleStartScenario genApiRegisterMemberLists numOfMembersPerOrg $numOfMembersPerOrg"
    )

    val r = new scala.util.Random()

    orgsWithNameRegisteringMemberAndTenant.map(
      orgWithNameRegisteringMemberAndTenant =>
        ApiRegisterMemberList(
          Some(
            ApiMemberMap(
              (1 to r.between(1, numOfMembersPerOrg + 1)).map { _ =>
                UUID.randomUUID().toString -> genApiInfoForMember(
                  r,
                  orgWithNameRegisteringMemberAndTenant
                )
              }.toMap
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
      Some(
        ApiNotificationPreference.values(
          r.nextInt(ApiNotificationPreference.values.length)
        )
      ),
      Seq(orgWithMemberTenantAndName._1),
      Some(orgWithMemberTenantAndName._3)
    )
  }

  private def genApiEstablishOrgs(
      maxOrgDepth: Integer,
      maxOrgWidth: Integer,
      ownerIds: Map[ApiTenantId, Set[ApiMemberId]]
  ): Seq[ApiEstablishOrganization] = {

    log.info(
      s"handleStartScenario genApiEstablishOrgs maxOrgDepth $maxOrgDepth maxOrgWidth $maxOrgWidth"
    )

    val r = new scala.util.Random()
    var establishOrgs: Seq[ApiEstablishOrganization] = Seq()

    ownerIds.keys.map { tenant =>

      val depth = r.between(2, maxOrgDepth + 1)

      val owners = ownerIds(tenant).toSeq

      log.info(
        s"handleStartScenario genApiEstablishOrgs parents owners $owners"
      )

      val topParent = UUID.randomUUID().toString
      establishOrgs = establishOrgs ++ Seq(
        ApiEstablishOrganization(
          Some(ApiOrganizationId(topParent)),
          Some(
            organization.ApiInfo(
              r.nextString(10),
              Some(r.nextString(5)),
              Some(genAddress(r)),
              Some(r.nextBoolean()),
              Some(r.nextString(15)),
              Some(r.nextString(15)),
              Some(tenant)
            )
          ),
          None,
          Seq.empty,
          owners,
          Seq(
            ApiContacts(
              owners,
              Seq.empty,
              Seq.empty
            )
          ),
          owners.headOption
        )
      )
      var parentsForPrevDepth: Seq[String] = Seq.empty
      var nextParents: Seq[String] = Seq.empty
      (1 to depth).map { d =>
        log.info(
          s"handleStartScenario genApiEstablishOrgs tenant $tenant with depth $depth"
        )
        if (parentsForPrevDepth.isEmpty) parentsForPrevDepth = Seq(topParent)
        parentsForPrevDepth.foreach { parent =>
          val width = r.between(2, maxOrgWidth + 1)
          log
            .info(
              s"handleStartScenario genApiEstablishOrgs tenant $tenant with width $width at depth $d"
            )
          (1 to width)
            .foreach { w =>

              log.info(
                s"handleStartScenario genApiEstablishOrgs tenant $tenant at width $w, depth $d"
              )
              val id = UUID.randomUUID().toString
              establishOrgs = establishOrgs ++ Seq(
                ApiEstablishOrganization(
                  Some(ApiOrganizationId(id)),
                  Some(
                    organization.ApiInfo(
                      r.nextString(10),
                      Some(r.nextString(5)),
                      Some(genAddress(r)),
                      Some(r.nextBoolean()),
                      Some(r.nextString(15)),
                      Some(r.nextString(15)),
                      Some(tenant)
                    )
                  ),
                  Some(
                    ApiParent(Some(ApiOrganizationId(parent)))
                  ),
                  Seq.empty,
                  owners,
                  Seq(
                    ApiContacts(
                      owners,
                      Seq.empty,
                      Seq.empty
                    )
                  ),
                  owners.headOption
                )
              )
              nextParents = nextParents ++ Seq(id)
            }
        }
        parentsForPrevDepth = nextParents
      }
    }
    establishOrgs
  }

}
