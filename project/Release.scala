import sbt.UpperStateOps.UpperStateOpsImpl
import sbt.{Command, Project, SettingKey, State, TaskKey, settingKey}
import sbtrelease._
import sbtrelease.ReleasePlugin
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.ReleasePlugin.autoImport._
import autoversion._

object Release {

  type SettingKeyWithCommand = (SettingKey[Boolean], String)

  implicit class SettingKeyOps(booleanSettingKey: SettingKey[Boolean]) {
    def ~>(releaseStep: ReleaseStep): ReleaseStep =
      releaseStepCommandCond(booleanSettingKey)(releaseStep, identity[State] _)

    def ?>(command: String): SettingKeyWithCommand =
      booleanSettingKey -> command

    def !>(releaseStep: ReleaseStep): ReleaseStep =
      releaseStepCommandCond(booleanSettingKey)(identity[State] _, releaseStep)
  }

  implicit class SettingKeyWithCommandOps(
    settingKeyWithCommand: SettingKeyWithCommand) {
    def <>(onFalseCommand: String): ReleaseStep =
      settingKeyWithCommand match {
        case (settingKey, command) =>
          releaseStepCommandCond(settingKey)(releaseStepCommand(command), releaseStepCommand(onFalseCommand))
      }
  }

  private def releaseStepCommandCond(
    cond: SettingKey[Boolean]
  )(onTrue: ReleaseStep,
    onFalse: ReleaseStep
  ): ReleaseStep = { st: State =>
    if (st.extract.get(cond))
      onTrue(st)
    else
      onFalse(st)
  }

  val releaseLocal = settingKey[Boolean](
    "Whether to publish local. This setting is intended for tests/debugging, we don't expect client projects to use it"
  )
  val defaultTag = settingKey[String](
    "Default tag number. For a new project, when no 'current' tag can be inferred this default comes into effect"
  )

  lazy val setReleaseVersionByTag: ReleaseStep = { state: State =>
    val extracted = Project extract state

    val (suggestedBumpState, suggestedBump) =
      extracted.runTask(autoversion.Keys.suggestedBump, state)

    val (tagState, latestTag) =
      suggestedBumpState
        .extract
        .runTask(autoversion.Keys.latestTag, suggestedBumpState)

    val getReleaseVersion = AutoVersion.setReleaseVersion(suggestedBump)

    val previousVersion = latestTag
      .map(_.version.toString)
      .getOrElse(tagState.extract.get(defaultTag))

    val version = getReleaseVersion(previousVersion)

    tagState.log.info(s"Setting version to '$version' [latestTag: $previousVersion, suggestedBump: $suggestedBump].")

    Command.process(s"""set ThisBuild / version := "$version" """, tagState)
  }

  lazy val checkForUnreleasedCommits: ReleaseStep = { state: State =>
    val extracted = Project extract state
    val (unreleasedCommitsState, unreleased) = extracted.runTask(autoversion.Keys.unreleasedCommits, state)

    if (unreleased.isEmpty) {
      sys.error("No unreleased commits found since prior release, aborting release process")
    } else {
      unreleasedCommitsState
    }
  }

  val releaseSummary = TaskKey[String]("release-summary", "Calculates the commits that summarize the upcoming release")

}
