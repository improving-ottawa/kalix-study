package app.improving.projections

import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

import scala.collection.immutable

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class QueryMembersAttendingEventsForAnOrganizationAction(
    creationContext: ActionCreationContext
) extends AbstractQueryMembersAttendingEventsForAnOrganizationAction {

  override def queryMembersAttendingEventsForAnOrganization(
      findMembersAtEventsOnDayForOrg: FindMembersAtEventsOnDayForOrg
  ): Action.Effect[MembersAtEventsOnDay] = {
    effects.asyncReply(
      components.membersAttendingEventsForAnOrganizationView
        .processFindMembersAtEventsOnDayForOrg(findMembersAtEventsOnDayForOrg)
        .execute()
        .map(result =>
          MembersAtEventsOnDay(
            result.memberInfos
              .map(memberInfo =>
                MemberEventsResult(
                  memberInfo.memberId
                    .map(memberId =>
                      immutable.Map(
                        memberId.id -> EventInfos(
                          result.memberEvents
                            .find(_.memberId == memberInfo.memberId)
                            .head
                            .eventInfos
                        )
                      )
                    )
                    .getOrElse(Map.empty),
                  memberInfo.memberName
                )
              )
          )
        )
    )

  }
}
