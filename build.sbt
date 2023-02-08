ThisBuild / organization := "app.improving"

ThisBuild / scalaVersion := "2.13.10"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val common: Project = project
  .disablePlugins(KalixPlugin)
  .in(file("common"))
  .configure(Kalix.library("common"))

lazy val org = project
  .in(file("organization"))
  .configure(Kalix.service("organization"))
  .configure(Kalix.dependsOn(common, "common"))

lazy val event = project
  .in(file("event"))
  .configure(Kalix.service("event"))
  .configure(Kalix.dependsOn(common, "common"))

lazy val member = project
  .in(file("member"))
  .configure(Kalix.service("member"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(event, "event"))
  .configure(Kalix.dependsOn(product, "product"))
  .configure(Kalix.dependsOn(order, "order"))

lazy val order = project
  .in(file("order"))
  .configure(Kalix.service("order"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(product, "product"))
  .configure(Kalix.dependsOn(event, "event"))
  .configure(Kalix.dependsOn(org, "organization"))

lazy val product = project
  .in(file("product"))
  .configure(Kalix.service("product"))
  .configure(Kalix.dependsOn(common, "common"))

lazy val store = project
  .in(file("store"))
  .configure(Kalix.service("store"))
  .configure(Kalix.dependsOn(common, "common"))

lazy val tenant = project
  .in(file("tenant"))
  .configure(Kalix.service("tenant"))
  .configure(Kalix.dependsOn(common, "common"))

lazy val projection = project
  .in(file("projection"))
  .configure(
    Kalix.service("projection")
  )
  .configure(Kalix.dependsOn(org, "organization"))
  .configure(Kalix.dependsOn(member, "member"))

lazy val gateway = project
  .in(file("gateway"))
  .configure(Kalix.service("gateway"))
  .configure(Kalix.dependsOn(tenant, "tenant"))
  .configure(Kalix.dependsOn(org, "organization"))
  .configure(Kalix.dependsOn(order, "order"))
  .configure(Kalix.dependsOn(store, "store"))
  .configure(Kalix.dependsOn(event, "event"))
  .configure(Kalix.dependsOn(product, "product"))
  .configure(Kalix.dependsOn(member, "member"))
  .configure(
    Kalix.dependsOn(
      projection,
      "projection"
    )
  )
lazy val root = project
  .in(file("."))
  .settings(
    publish := {},
    publishLocal := {},
    publishTo := Some(Resolver.defaultLocal)
  )
  .aggregate(
    projection
  )
