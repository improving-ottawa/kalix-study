package app.improving.projection

import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext


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
                  Map(
                    memberInfo.attendingMemberId -> EventInfos(
                      result.memberEvents
                        .find(
                          _.attendingMemberId == memberInfo.attendingMemberId
                        )
                        .head
                        .eventInfos
                    )
                  ),
                  memberInfo.attendingMemberName
                )
              )
          )
        )
    )

  }
}
