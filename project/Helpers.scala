import Dependencies._
import com.typesafe.sbt.SbtNativePackager
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.{
  JavaAppPackaging,
  JavaServerAppPackaging
}
import com.typesafe.sbt.packager.docker.DockerPlugin
import kalix.sbt.KalixPlugin
import sbt.Keys.{libraryDependencies, _}
import sbt._
import sbtdynver.DynVerPlugin.autoImport.dynverSeparator
import sbtprotoc.ProtocPlugin.autoImport.PB
import scalapb.GeneratorOption.{
  FlatPackage,
  RetainSourceCodeInfo,
  SingleLineToProtoString
}
import sbtdynver.DynVerPlugin.autoImport._

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import scala.util.matching.Regex

object Compilation {

  def scala(project: Project): Project = {
    project.settings(
      ThisBuild / dynverSeparator := "-",
      run / fork := true,
      run / envVars += ("HOST", "0.0.0.0"),
      Compile / run := {
        // needed for the proxy to access the user function on all platforms
        sys.props += "kalix.user-function-interface" -> "0.0.0.0"
        (Compile / run).evaluated
      },
      Compile / scalacOptions ++= Seq(
        "-release:11",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-Xlog-reflective-calls",
        "-Xlint"
      ),
      Compile / javacOptions ++= Seq(
        "-Xlint:unchecked",
        "-Xlint:deprecation",
        "-parameters" // for Jackson
      )
    )
  }

}

object Testing {
  def scalaTest(project: Project): Project = {
    project.settings(
      Test / parallelExecution := false,
      Test / testOptions += Tests.Argument("-oDF"),
      Test / logBuffered := false,
      libraryDependencies ++= basicTestingDependencies
    )
  }
}

object Packaging {

  def docker(project: Project): Project = {
    project.settings(
      dockerBaseImage := "docker.io/library/adoptopenjdk:11-jre-hotspot",
      dockerUsername := sys.props.get("docker.username"),
      dockerRepository := sys.props.get("docker.registry"),
      dockerUpdateLatest := true,
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
  }

  def dockerConfiguration(project: Project): Project = {
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
  }
}

object KalixPb {
  lazy val copyProtobuf = taskKey[Unit]("copyProtobuf")

  lazy val serviceProtobufProjectDependencies =
    settingKey[Seq[File]]("serviceProtobufProjectDependencies")

  private val protobufKalixAnnotationRegex: Regex =
    """(?s)(\n?\s*)option\s*(\(\s*kalix.*?)\s*=.*?}\s*;\s*(\n)""".r
  private val protobufKalixImportRegex: Regex =
    """([\n^]\s*)import\s+"?kalix/annotations\.proto"?\s*;""".r
  private val protobufKalixSingleFieldRegex: Regex =
    """\s*\[\s*\(kalix\.field\)[^,\]]*?]""".r
  private val protobufKalixStartFieldRegex: Regex =
    """(\s*\[)\s*\(kalix\.field\)[^,\]]*?,\s*""".r
  private val protobufKalixEndFieldRegex: Regex =
    """\s*,\s*\(kalix\.field\)[^,\]]*?(])""".r
  private val protobufKalixMiddleFieldRegex: Regex =
    """(,)\s*\(kalix\.field\)[^,\]]*?,(\s*)""".r
  def copyKalixFreeProtobufFiles(
      srcDirectory: File,
      dstDirectory: File
  ): Unit = {
    if (!dstDirectory.exists()) dstDirectory.mkdirs()

    val (srcDirs, srcFiles) = srcDirectory.listFiles.partition(_.isDirectory)

    srcFiles.filter(_.getName.endsWith("_api.proto")).foreach { srcFile =>
      val destFile = new File(dstDirectory, srcFile.getName)
      if (
        !destFile.exists() || srcFile.lastModified() > destFile.lastModified()
      ) {
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
        Files.writeString(
          destFile.toPath,
          kalixFreeContent,
          StandardCharsets.UTF_8
        )
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

  def service(componentName: String)(project: Project): Project = {
    project
      .enablePlugins(KalixPlugin, JavaAppPackaging, DockerPlugin)
      .configure(Compilation.scala)
      .configure(Testing.scalaTest)
      .configure(Packaging.docker)
      .settings(
        name := componentName,
        run / fork := true,
        libraryDependencies ++= utilityDependencies ++ loggingDependencies,
        Compile / managedSourceDirectories ++= Seq(
          target.value / "scala-2.13" / "akka-grpc",
          target.value / "scala-2.13" / "src_managed"
        )
      )
  }

  def library(componentName: String)(project: Project): Project = {
    project
//      .enablePlugins(KalixPlugin)
      .configure(Compilation.scala)
      .configure(Testing.scalaTest)
      .settings(
        name := componentName,
        run / fork := true,
        libraryDependencies ++= loggingDependencies,
        Compile / managedSourceDirectories ++= Seq(
          target.value / "scala-2.13" / "akka-grpc",
          target.value / "scala-2.13" / "src_managed"
        )
      )
  }

  def dependsOn(dependency: Project, name: String)(
      project: Project
  ): Project = {
    project
      .settings(
        libraryDependencies ++= {
          Seq(
            "app.improving" %% name % version.value % "protobuf"
          )
        }
      )
      .dependsOn(dependency)
  }

  def componentBaseConfiguration(project: Project): Project =
    project
      .settings(
        Compile / packageDoc / publishArtifact := false,
        Compile / doc / sources := Seq.empty
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
        libraryDependencies += scalaPbCompilerPlugin,
        Compile / PB.recompile := sys.env
          .get("SCALAPB_RECOMPILE")
          .exists(_.toLowerCase.startsWith("t"))
      )

  def scalapbValidationConfiguration(project: Project): Project =
    project.settings(
      Compile / PB.targets ++= Seq(
        scalapb.gen() -> (Compile / sourceManaged).value / "scalapb",
        scalapb.validate.gen() -> (Compile / sourceManaged).value / "scalapb"
      ),
      libraryDependencies ++= scalaPbValidationDependencies
    )

  def nonKalixComponentBaseConfiguration(project: Project): Project =
    project
      .configure(componentBaseConfiguration)
      .configure(Testing.scalaTest)
      .configure(scalapbConfiguration)
      .configure(scalapbCodeGenConfiguration)
      .configure(scalapbValidationConfiguration)
      .settings(
        libraryDependencies ++= utilityDependencies ++ loggingDependencies ++ Seq(
          kalixScalaSdk
        )
      )

  def generalComponent(componentName: String)(project: Project): Project =
    project
      .configure(nonKalixComponentBaseConfiguration)
      .settings(
        name := s"$componentName"
      )

  def multiProjectProtobufConfiguration(project: Project): Project =
    project.settings(
      Compile / PB.protoSources += target.value / "protobuf_improvingapp",
      //        Compile / PB.includePaths ++= serviceProtobufProjectDependencies.value.map { dir =>
      //          dir / "src/main/protobuf"
      //        },
      Test / PB.protoSources += baseDirectory.value / "src/test/protobuf",
      (Compile / PB.generate) := (Compile / PB.generate)
        .dependsOn(KalixPb.copyProtobuf)
        .value,
      KalixPb.serviceProtobufProjectDependencies := Seq.empty,
      KalixPb.copyProtobuf := KalixPb.serviceProtobufProjectDependencies.value
        .map { depProjectBaseDir =>
          KalixPb.copyKalixFreeProtobufFiles(
            depProjectBaseDir / "src/main/protobuf",
            target.value / "protobuf_improvingapp"
          )
        }
    )
}
