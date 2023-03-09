package app.improving.membercontext

import app.improving.membercontext.member.MemberAPI
import app.improving.membercontext.member.MemberActionServiceImpl
import app.improving.membercontext.membermap.MemberMap
import kalix.scalasdk.Kalix
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

object Main {

  private val log = LoggerFactory.getLogger("app.improving.membercontext.Main")

  def createKalix(): Kalix = {
    // The KalixFactory automatically registers any generated Actions, Views or Entities,
    // and is kept up-to-date with any changes in your protobuf definitions.
    // If you prefer, you may remove this and manually register these components in a
    // `Kalix()` instance.
    KalixFactory
      .withComponents(
        new MemberAPI(_),
        new MemberMap(_),
        new AllMembersViewImpl(_),
        new MemberActionServiceImpl(_),
        new MemberByMemberIdsQueryView(_),
        new MemberByMetaInfoViewImpl(_),
        new MemberByOrderQueryView(_)
      )
      .register(
        AllMembersViewProvider(new AllMembersViewImpl(_))
          .withViewId("AllMembersViewV3")
      )
      .register(
        MemberByMetaInfoViewProvider(new MemberByMetaInfoViewImpl(_))
          .withViewId("MemberByMetaInfoViewV3")
      )
      .register(
        MemberByMemberIdsQueryViewProvider(new MemberByMemberIdsQueryView(_))
          .withViewId("MemberByMemberIdsQueryViewV3")
      )
  }

  def main(args: Array[String]): Unit = {
    log.info("starting the Kalix service")
    createKalix().start()
  }
}
