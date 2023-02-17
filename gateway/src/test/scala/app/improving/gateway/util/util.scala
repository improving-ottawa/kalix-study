package app.improving.gateway.util

import app.improving.{ApiOrderId, OrderId}
import app.improving.gateway.{EndScenario, ScenarioResults}

object util {
  def endFromResults(
      scenarioResults: ScenarioResults,
      orders: Seq[OrderId]
  ): EndScenario =
    EndScenario(
      scenarioResults.tenants,
      scenarioResults.orgsForTenants.values.toSeq.flatMap(_.orgIds),
      scenarioResults.membersForOrgs.values.toSeq.flatMap(_.memberIds),
      scenarioResults.eventsForOrgs.values.toSeq.flatMap(_.eventIds),
      scenarioResults.storesForOrgs.values.toSeq.flatMap(_.storeIds),
      scenarioResults.productsForStores.values.toSeq.flatMap(_.skus),
      orders.map(id => ApiOrderId(id.id))
    )
}
