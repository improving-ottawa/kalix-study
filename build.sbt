organization := "app.improving"

ThisBuild / scalaVersion := "2.13.10"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val api = project.in(file("api"))
  .configure(Kalix.apiImpl("api"))

lazy val org = project.in(file("organization"))
  .configure(Kalix.serviceImpl("organization"))
  .dependsOn(api)

lazy val event = project.in(file("event"))
  .configure(Kalix.serviceImpl("event"))
  .dependsOn(api)

lazy val member = project.in(file("member"))
  .configure(Kalix.serviceImpl("member"))
  .dependsOn(api)

lazy val order = project.in(file("order"))
  .configure(Kalix.serviceImpl("order"))
  .dependsOn(api)

lazy val product = project.in(file("product"))
  .configure(Kalix.serviceImpl("product"))
  .dependsOn(api)

lazy val store = project.in(file("store"))
  .configure(Kalix.serviceImpl("store"))
  .dependsOn(api)

lazy val tenant = project.in(file("tenant"))
  .configure(Kalix.serviceImpl("tenant"))
  .dependsOn(api)

lazy val root = project.in(file(".")).settings(
  publish := {},
  publishLocal := {},
  publishTo := Some(Resolver.defaultLocal)
).aggregate(api, org, event, member, tenant)
