import Dependencies._
import KalixPb._
import Release._
import aether.AetherPlugin
import com.typesafe.sbt.SbtNativePackager
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import kalix.sbt.KalixPlugin
import sbt.Keys._
import sbt._
import sbtdynver.DynVerPlugin.autoImport.dynverSeparator
import sbtprotoc.ProtocPlugin.autoImport.PB
import scalapb.GeneratorOption.{FlatPackage, RetainSourceCodeInfo, SingleLineToProtoString}
import wartremover.WartRemover.autoImport._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._
// import sbtdynver.DynVerPlugin.autoImport._
import sbtrelease.ReleasePlugin
// import com.sksamuel.scapegoat.sbt.ScapegoatSbtPlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.ReleasePlugin.autoImport._

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import scala.util.matching.Regex

//noinspection ScalaFileName
object KalixPb {
  lazy val copyProtobuf = taskKey[Unit]("copyProtobuf")

  lazy val serviceProtobufProjectDependencies = settingKey[Seq[File]]("serviceProtobufProjectDependencies")

  private val protobufKalixAnnotationRegex: Regex = """(?s)(\n?\s*)option\s*(\(\s*kalix.*?)\s*=.*?}\s*;\s*(\n)""".r
  private val protobufKalixImportRegex: Regex = """([\n^]\s*)import\s+"?kalix/annotations\.proto"?\s*;""".r
  private val protobufKalixSingleFieldRegex: Regex = """\s*\[\s*\(kalix\.field\)[^,\]]*?]""".r
  private val protobufKalixStartFieldRegex: Regex = """(\s*\[)\s*\(kalix\.field\)[^,\]]*?,\s*""".r
  private val protobufKalixEndFieldRegex: Regex = """\s*,\s*\(kalix\.field\)[^,\]]*?(])""".r
  private val protobufKalixMiddleFieldRegex: Regex = """(,)\s*\(kalix\.field\)[^,\]]*?,(\s*)""".r

  def copyKalixFreeProtobufFiles(srcDirectory: File, dstDirectory: File): Unit = {

    if (!dstDirectory.exists()) dstDirectory.mkdirs()

    val (srcDirs, srcFiles) = srcDirectory.listFiles.partition(_.isDirectory)

    srcFiles.filter(_.getName.endsWith("_api.proto")).foreach { srcFile =>
      val destFile = new File(dstDirectory, srcFile.getName)
      if (!destFile.exists() || srcFile.lastModified() > destFile.lastModified()) {
        val content = Files.readString(srcFile.toPath, StandardCharsets.UTF_8)
        val kalixFreeContent =
          Seq(
            protobufKalixAnnotationRegex -> "$1// Kalix annotation $2 removed$3",
            protobufKalixImportRegex -> "$1// Kalix import removed",
            protobufKalixSingleFieldRegex -> "",
            protobufKalixStartFieldRegex -> "$1",
            protobufKalixEndFieldRegex -> "$1",
            protobufKalixMiddleFieldRegex -> "$1$2"
          )
            .foldRight(content) { case ((regex, replacement), input) =>
              regex.replaceAllIn(input, replacement)
            }
        Files.writeString(destFile.toPath, kalixFreeContent, StandardCharsets.UTF_8)
        destFile.setLastModified(srcFile.lastModified())
      }
    }

    srcDirs.foreach { srcDir =>
      val dstDir = new File(dstDirectory, srcDir.getName)
      copyKalixFreeProtobufFiles(srcDir, dstDir)
    }
  }
}

object Kalix {
  def codeQualityConfiguration(project: Project): Project =
    project
      .settings(
        Compile / compile / wartremoverErrors ++= Warts.allBut(
          Wart.Nothing, // Some generic types in Kalix evaluate to Nothing, e.g. Action.effects[Nothing]
          Wart.Equals
        ),
        Test / test / wartremoverErrors := Seq.empty,

        // wartremoverWarnings ++= Warts.allBut()
        wartremoverExcluded ++= Seq(
          baseDirectory.value / "target" / "scala-2.13" / "akka-grpc",
          sourceManaged.value
        ),
        Global / excludeLintKeys += buildInfoKeys,
        Global / excludeLintKeys += buildInfoPackage,
        Global / excludeLintKeys += defaultTag,
        // ThisBuild / scapegoatVersion := Versions.scapegoat,
        // scapegoatIgnoredFiles := Seq(
        //  ".*/target/scala-2.13/akka-grpc/.*",
        //  ".*/target/scala-2.13/src_managed/.*"
        //),
        scalacOptions ++= Seq(
          "-Wconf:src=target/scala-2\\.13/akka-grpc/.*:s",
          "-Wconf:src=target/scala-2\\.13/src_managed/.*:s",
          "-Wconf:cat=w-flag-dead-code&src=src/test/scala/com/bmo/epbspov/EbpsPovIntegrationSpecSuite.scala:s",
          "-Wconf:cat=deprecation&origin=com\\.bmo\\.epbspov\\.utils\\.Logging.*:i",
          "-Wconf:cat=deprecation&origin=com\\.bmo\\.epbspov\\.utils\\.ExceptionLogging.*:i"
        )
      )

  def apiTestingConfiguration(project: Project): Project =
    project
      .settings(
        libraryDependencies ++= basicTestingDependencies
      )

  def multiProjectProtobufConfiguration(project: Project): Project =
    project.settings(
      Compile / PB.protoSources += target.value / "protobuf_epbs",
      //        Compile / PB.includePaths ++= serviceProtobufProjectDependencies.value.map { dir =>
      //          dir / "src/main/protobuf"
      //        },
      Test / PB.protoSources += baseDirectory.value / "src/test/protobuf",
      (Compile / PB.generate) := (Compile / PB.generate).dependsOn(copyProtobuf).value,
      serviceProtobufProjectDependencies := Seq.empty,
      copyProtobuf := serviceProtobufProjectDependencies
        .value
        .map { depProjectBaseDir =>
          copyKalixFreeProtobufFiles(
            depProjectBaseDir / "src/main/protobuf",
            target.value / "protobuf_epbs"
          )
        }
    )

  def scalapbConfiguration(project: Project): Project =
    project
      .settings(
        libraryDependencies ++= scalaPbDependencies
      )

  def scalapbCodeGenConfiguration(project: Project): Project =
    project
      .settings(
        Compile / PB.targets := Seq(
          scalapb.gen(
            FlatPackage,
            SingleLineToProtoString,
            RetainSourceCodeInfo
          ) -> (Compile / sourceManaged).value / "scalapb"
        ),
//        Compile / packageSrc / mappings ++= {
//          val generatedSources = ((Compile / managedSources).value ** "*") filter {
//            _.isFile
//          }
//          generatedSources.get pair relativeTo((Compile / sourceManaged).value)
//        },
        libraryDependencies += scalaPbCompilerPlugin,
        Compile / PB.recompile := sys.env.get("SCALAPB_RECOMPILE").exists(_.toLowerCase.startsWith("t"))
      )

  def scalapbValidationConfiguration(project: Project): Project =
    project.settings(
      Compile / PB.targets ++= Seq(
        scalapb.gen() -> (Compile / sourceManaged).value / "scalapb",
        scalapb.validate.gen() -> (Compile / sourceManaged).value / "scalapb"
      ),
      libraryDependencies ++= scalaPbValidationDependencies
    )

  def buildInfoConfiguration(project: Project): Project =
    project
      .enablePlugins(BuildInfoPlugin)
      .settings(
        buildInfoKeys := Seq[BuildInfoKey](
          name,
          version,
          scalaVersion,
          sbtVersion
        ),
        buildInfoPackage := "hello"
        // buildInfoOptions ++= Seq(BuildInfoOption.ToMap, BuildInfoOption.BuildTime, BuildInfoOption.ConstantValue)
      )

  def dockerConfiguration(project: Project): Project =
    project.settings(
      dockerBaseImage := "docker.io/library/eclipse-temurin:17",
      dockerUsername := sys.props.get("docker.username"),
      dockerRepository := sys.props.get("docker.registry"),
      dockerUpdateLatest := true,
      // dockerAlias := dockerAlias.value.withTag(Some("latest"))
      dockerExposedPorts ++= Seq(8080),
      dockerBuildCommand := {
        if (sys.props("os.arch") != "amd64") {
          // use buildx with platform to build supported amd64 images on other CPU architectures
          // this may require that you have first run 'docker buildx create' to set docker buildx up
          dockerExecCommand.value ++ Seq(
            "buildx",
            "build",
            "--platform=linux/amd64",
            "--load"
          ) ++ dockerBuildOptions.value :+ "."
        } else dockerBuildCommand.value
      }
    )

  def releaseConfiguration(
    extraPreReleaseTasks: Seq[sbt.TaskKey[_]] = Seq.empty,
    extraPreReleaseCommands: Seq[String] = Seq.empty
  )(project: Project
  ): Project =
    project.settings(
      releaseLocal := sys.env.contains("RELEASE_LOCAL"),
      // publish artifacts task is set as a no-op since publishing is triggered inherently as part of docker:publish
      releasePublishArtifactsAction := {
        Def.task(())
      },
      releaseProcess :=
        Seq[ReleaseStep](
          checkForUnreleasedCommits,
          setReleaseVersionByTag,
          checkSnapshotDependencies,
          inquireVersions,
          runClean,
          releaseStepCommand("test")
        ) ++
          extraPreReleaseCommands.map(releaseStepCommand).map(ReleaseStep.func2ReleasePart) ++
          extraPreReleaseTasks.map(releaseStepTask(_)).map(ReleaseStep.func2ReleasePart) ++
          Seq(releaseLocal !> tagRelease, releaseLocal !> pushChanges),
      releaseCrossBuild := false, // Whether the release should be cross built
      releaseUseGlobalVersion := true, //  Whether to use a global version
      releaseIgnoreUntrackedFiles := false // Whether to ignore untracked files
    )

  def versioningConfiguration(project: Project): Project =
    project.settings(
      ThisBuild / dynverSeparator := "-",
      /*
      defaultBump := Some(Minor),
      releaseSummary := unreleasedCommits.value.mkString("\n"),
      releaseTagComment := Seq(releaseTagComment.value, releaseSummary.value).mkString("\n\n"),
      majorRegexes := Seq("(BREAKING[- ]CHANGES?:).*".r, "^(.*!:).*".r),
      minorRegexes := Seq("^(feat.*:).*".r),
      bugfixRegexes := Seq("^(fix.*:|docs:).*".r),
      defaultTag := "0.0.0",
      releaseVersionBump := Version.Bump.Bugfix

       */
    )

  def adapterComponent(project: Project): Project =
    project.settings(
      libraryDependencies ++= httpDependencies,
      libraryDependencies ++= jsonDependencies
    )

//  def kalixApiComponent(componentName: String)(project: Project): Project =
//    project
//      .enablePlugins(KalixPlugin)
//      .configure(codeQualityConfiguration)
//      .configure(serviceTestingConfiguration)
//      .configure(scalapbValidationConfiguration)
//      .configure(versioningConfiguration)
//      .configure(adapterComponent)
//      .settings(
//        name := s"$componentName-api",
//        run / fork := true,
//        Compile / run := {
//          // needed for the proxy to access the user function on all platforms
//          sys.props += "kalix.user-function-interface" -> "0.0.0.0"
//          (Compile / run).evaluated
//        },
//        libraryDependencies ++= generalDependencies
//      )

  def componentBaseConfiguration(project: Project): Project =
    project
      .configure(codeQualityConfiguration)
      .settings(
        Compile / packageDoc / publishArtifact := false,
        Compile / doc / sources := Seq.empty
      )

  def nonKalixComponentBaseConfiguration(project: Project): Project =
    project
      .configure(componentBaseConfiguration)
      .configure(apiTestingConfiguration)
      .configure(scalapbConfiguration)
      .configure(scalapbCodeGenConfiguration)
      .configure(scalapbValidationConfiguration)
      .settings(
        libraryDependencies ++= utilityDependencies ++ loggingDependencies
      )

  def kalixServiceComponent(
    componentName: String,
    extraPreReleaseTasks: Seq[sbt.TaskKey[_]] = Seq.empty,
    extraPreReleaseCommands: Seq[String] = Seq.empty
  )(project: Project
  ): Project =
    project
      .enablePlugins(KalixPlugin)
      .enablePlugins(JavaServerAppPackaging)
      .enablePlugins(SbtNativePackager)
      .enablePlugins(ReleasePlugin)
      .enablePlugins(AetherPlugin)
      .configure(componentBaseConfiguration)
      .configure(scalapbValidationConfiguration)
      .configure(versioningConfiguration)
//      .configure(releaseConfiguration(extraPreReleaseTasks, extraPreReleaseCommands))
      .configure(dockerConfiguration)
      .configure(adapterComponent)
      .configure(multiProjectProtobufConfiguration)
      .settings(
        name := componentName,
        run / fork := true,
        libraryDependencies ++= utilityDependencies ++ loggingDependencies,
        releaseProcess := Seq.empty
      )

  def apiComponent(componentName: String)(project: Project): Project =
    project
      .configure(nonKalixComponentBaseConfiguration)
      .settings(
        name := s"$componentName-api"
      )

  def generalComponent(componentName: String)(project: Project): Project =
    project
      .configure(nonKalixComponentBaseConfiguration)
      .settings(
        name := s"$componentName"
      )

}
