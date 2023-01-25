ThisBuild / organization := "app.improving"

ThisBuild / scalaVersion := "2.13.10"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val testkit = project
  .in(file("kalix-test/testkit"))
  .configure(Kalix.generalComponent("improving-app-testkit"))
  .settings(
    libraryDependencies := libraryDependencies.value.map { dep =>
      dep.withConfigurations(dep.configurations.filterNot(_ == "test"))
    }
  )

lazy val common: Project = project
  .in(file("common"))
  .configure(Kalix.service("common"))

lazy val org = project
  .in(file("organization"))
  .configure(Kalix.service("organization"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val event = project
  .in(file("event"))
  .configure(Kalix.service("event"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val member = project
  .in(file("member"))
  .configure(Kalix.service("member"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val order = project
  .in(file("order"))
  .configure(Kalix.service("order"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(product, "product"))
  .configure(Kalix.dependsOn(member, "member"))
  .configure(Kalix.dependsOn(event, "event"))
  .configure(Kalix.dependsOn(org, "organization"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val product = project
  .in(file("product"))
  .configure(Kalix.service("product"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val store = project
  .in(file("store"))
  .configure(Kalix.service("store"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val tenant = project
  .in(file("tenant"))
  .configure(Kalix.service("tenant"))
  .configure(Kalix.dependsOn(common, "common"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val gateway = project
  .in(file("gateway"))
  .configure(Kalix.service("gateway"))
  .configure(Kalix.dependsOn(tenant, "tenant"))
  .configure(Kalix.dependsOn(testkit, "improving-app-testkit"))

lazy val integration_tests: Project = project
  .in(file("kalix-study/it"))
  .enablePlugins(AkkaGrpcPlugin)
  .configure(Kalix.componentBaseConfiguration)
  .configure(Kalix.multiProjectProtobufConfiguration)
  .settings(
    name := "improving-app-integration-tests",
    libraryDependencies ++= Dependencies.loggingDependencies ++
      Dependencies.basicTestingDependencies ++
      Dependencies.integrationTestDependencies ++
      Dependencies.httpDependencies ++
      Dependencies.jsonDependencies ++
      Dependencies.scalaPbValidationDependencies ++
      Seq(
        Dependencies.kalixScalaSdk
      ),
    // Test / envVars ++= Map(
    //  "EPBS_VERSION" -> sys.env.getOrElse("EPBS_VERSION", version.value)
    // ),
    Test / fork := true,
    Compile / run := {
      // needed for the proxy to access the user function on all platforms
      sys.props += "kalix.user-function-interface" -> "0.0.0.0"
      (Compile / run).evaluated
    },
    Test / test := {
      if (!sys.env.contains("NO_PUBLISH"))
        (Test / test)
          .dependsOn(
            gateway / Docker / publishLocal,
            tenant / Docker / publishLocal,
            event / Docker / publishLocal,
            member / Docker / publishLocal,
            order / Docker / publishLocal,
            org / Docker / publishLocal,
            product / Docker / publishLocal,
            store / Docker / publishLocal
          )
      else Test / test
    }.value,
    KalixPb.serviceProtobufProjectDependencies := Seq(
      (gateway / baseDirectory).value,
      (tenant / baseDirectory).value,
      (event / baseDirectory).value,
      (member / baseDirectory).value,
      (order / baseDirectory).value,
      (org / baseDirectory).value,
      (product / baseDirectory).value,
      (store / baseDirectory).value
    ),
    akkaGrpcGeneratedSources := Seq(AkkaGrpc.Client),
    publish / skip := true,
    publishLocal / skip := true
  )
  .dependsOn(
    gateway,
    tenant,
    event,
    member,
    order,
    org,
    product,
    store,
    testkit % Test
  )

lazy val root = project
  .in(file("."))
  .settings(
    publish := {},
    publishLocal := {},
    publishTo := Some(Resolver.defaultLocal)
  )
  .aggregate(
    gateway,
    tenant,
    store,
    product,
    org,
    order,
    member,
    event
  )
