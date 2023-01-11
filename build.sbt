organization := "com.improving"

scalaVersion := "2.13.10"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val api = project.in(file("api"))
  .configure(Kalix.apiComponent("api"))

lazy val org = project.in(file("organization"))
  .configure(Kalix.kalixServiceComponent("organization"))
  .dependsOn(api)

lazy val root = project.in(file(".")).settings(
  publish := {},
  publishLocal := {},
  // pgpSigner / skip := true,
  publishTo := Some(Resolver.defaultLocal)
).aggregate(org)


/*
enablePlugins(KalixPlugin, JavaAppPackaging, DockerPlugin)
dockerBaseImage := "docker.io/library/adoptopenjdk:11-jre-hotspot"
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")
dockerUpdateLatest := true
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
ThisBuild / dynverSeparator := "-"
run / fork := true
run / envVars += ("HOST", "0.0.0.0")

Test / parallelExecution := false
Test / testOptions += Tests.Argument("-oDF")
Test / logBuffered := false

Compile / run := {
  // needed for the proxy to access the user function on all platforms
  sys.props += "kalix.user-function-interface" -> "0.0.0.0"
  (Compile / run).evaluated
}

Compile / scalacOptions ++= Seq(
  "-target:11",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint"
)

Compile / javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-parameters" // for Jackson
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.scalamock" %% "scalamock" % "5.2.0" % Test
)
*/
