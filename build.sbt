ThisBuild / organization := "app.improving"

ThisBuild / scalaVersion := "2.13.10"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val api = project.in(file("api"))
  .configure(Kalix.apiImpl("api"))

lazy val org = project.in(file("organization"))
  .configure(Kalix.serviceImpl("organization"))
  .dependsOn(api, api % "protobuf")

lazy val event = project.in(file("event"))
  .configure(Kalix.serviceImpl("event"))
  .dependsOn(api, api % "protobuf")

lazy val member = project.in(file("member"))
  .configure(Kalix.serviceImpl("member"))
  .dependsOn(api, api % "protobuf")

lazy val order = project.in(file("order"))
  .configure(Kalix.serviceImpl("order"))
  .dependsOn(api, api % "protobuf")

lazy val product = project.in(file("product"))
  .configure(Kalix.serviceImpl("product"))
  .dependsOn(api, api % "protobuf")

lazy val store = project.in(file("store"))
  .configure(Kalix.serviceImpl("store"))
  .dependsOn(api, api % "protobuf")

lazy val tenant = project.in(file("tenant"))
  .configure(Kalix.serviceImpl("tenant"))
  .settings(
    libraryDependencies ++= Seq(
      "app.improving" %% "api" % version.value % "protobuf"
    )
  )
  .dependsOn(api)

lazy val root = project.in(file(".")).settings(
  publish := {},
  publishLocal := {},
  publishTo := Some(Resolver.defaultLocal)
).aggregate(api, org, event, member, order, product, store, tenant)
