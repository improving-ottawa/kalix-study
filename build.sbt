organization := "app.improving"

ThisBuild / scalaVersion := "2.13.10"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val api = project.in(file("api"))
  .configure(Kalix.apiImpl("api"))

lazy val org = project.in(file("organization"))
  .configure(Kalix.serviceImpl("organization"))
  .dependsOn(api)

lazy val root = project.in(file(".")).settings(
  publish := {},
  publishLocal := {},
  publishTo := Some(Resolver.defaultLocal)
).aggregate(org)
