import Dependencies._
import com.typesafe.sbt.SbtNativePackager
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.{JavaAppPackaging, JavaServerAppPackaging}
import com.typesafe.sbt.packager.docker.DockerPlugin
import kalix.sbt.KalixPlugin
import sbt.Keys.{libraryDependencies, _}
import sbt._
import sbtdynver.DynVerPlugin.autoImport.dynverSeparator
import sbtprotoc.ProtocPlugin.autoImport.PB
import scalapb.GeneratorOption.{FlatPackage, RetainSourceCodeInfo, SingleLineToProtoString}
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

  def versioningConfiguration(project: Project): Project = {
      project.settings(
        ThisBuild / dynverSeparator := "-",
      )
  }
}

object Kalix {

  def service(componentName: String)(project: Project): Project = {
    project.enablePlugins(KalixPlugin, JavaAppPackaging, DockerPlugin)
      .configure(Compilation.scala)
      .configure(Testing.scalaTest)
      .configure(Packaging.docker)
      .settings(
        name := componentName,
        run / fork := true,
        libraryDependencies ++= utilityDependencies ++ loggingDependencies,
        Compile / managedSourceDirectories ++= Seq(
          file(componentName) / "target" / "scala-2.13" / "akka-grpc",
          file(componentName) / "target" / "scala-2.13" / " src_managed"
        ),
        Compile / unmanagedSourceDirectories ++= Seq(
          file(componentName) / "target" / "scala-2.13" / " kalix-unmanaged",
        )
      )
  }

  def library(componentName: String)(project: Project): Project = {
    project.enablePlugins(KalixPlugin)
      .configure(Compilation.scala)
      .configure(Testing.scalaTest)
      .settings(
        name := componentName,
        run / fork := true,
        libraryDependencies ++= loggingDependencies,
        Compile / managedSourceDirectories ++= Seq(
          file(componentName) / "target" / "scala-2.13" / "akka-grpc",
          file(componentName) / "target" / "scala-2.13" / " src_managed"
        ),
        Compile /unmanagedSourceDirectories ++= Seq(
          file(componentName) / "target" / "scala-2.13" / " kalix-unmanaged",
        )
      )
  }

  def dependsOn(dependency: Project, name: String)(project: Project): Project = {
    project.settings(
      libraryDependencies ++= {
        Seq(
          "app.improving" %% name % version.value % "protobuf"
        )
      }
    ).dependsOn(dependency)
  }
}
