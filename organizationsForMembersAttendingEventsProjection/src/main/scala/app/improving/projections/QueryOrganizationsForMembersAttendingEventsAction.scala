package app.improving.projections

import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class QueryOrganizationsForMembersAttendingEventsAction(
    creationContext: ActionCreationContext
) extends AbstractQueryOrganizationsForMembersAttendingEventsAction {

  override def queryOrganizationsForMembersAttendingEvents(
      findOrgsByMembersForEvents: FindOrgsByMembersForEvents
  ): Action.Effect[OrgsByMembersForEvents] = {
    effects.asyncReply(
      components.organizationsForMembersAttendingEventsView
        .processFindOrgsByMembersForEvents(findOrgsByMembersForEvents)
        .execute()
        .map(reply =>
          OrgsByMembersForEvents(
            reply.orgInfos
              .map(orgInfo =>
                orgInfo.attendingMemberOrg
                  .map(orgId =>
                    orgId.id -> OrgInfoMembers(
                      orgInfo.attendingMemberOrgName,
                      reply.orgMembers
                        .filter(_.attendingMemberOrg.contains(orgId))
                        .flatMap(_.memberInfos)
                    )
                  )
              )
              .filter(_.isDefined)
              .map(_.get)
              .toMap,
            reply.orgMembers
              .flatMap(_.memberInfos)
              .map(memberInfo =>
                memberInfo.attendingMember
                  .map(memberId =>
                    memberId.id -> MemberInfoEvents(
                      memberInfo.attendingMemberName,
                      reply.memberEvents
                        .filter(_.attendingMember.contains(memberId))
                        .flatMap(_.eventInfos)
                    )
                  )
              )
              .filter(_.isDefined)
              .map(_.get)
              .toMap
          )
        )
    )
  }
}
