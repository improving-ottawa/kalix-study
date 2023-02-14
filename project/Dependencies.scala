import kalix.sbt.KalixPlugin.{KalixProtocolVersion, KalixSdkVersion}
import sbt._

object Dependencies {
  object Versions {
    val akka = "2.7.0"
    val akkaHttp = "10.4.0"
    val akkaKafka = "4.0.0"
    val alpakka = "5.0.0"
    val chimney = "0.6.2"
    val commonsCodec = "1.15"
    val jsoniterScala = "2.19.1"
    val logback = "1.4.5"
    val monocle = "3.1.0"
    val pureconfig = "0.17.2"
    val scapegoat = "2.1.0"
    val scalalogging = "3.9.5"
    val scalamock = "5.2.0"
    val scalapbCompiler = "0.11.12"
    val scalatest = "3.2.14"
    val scalacheck = "1.14.1"
    val scalaxml = "2.1.0"
    val sttp = "3.8.5"
    val testcontainers = "1.17.6"
    val cats = "2.9.0"
    val circe = "0.14.1"
  }

  import Versions._

  val scalaPbCompilerPlugin: ModuleID =
    "com.thesamet.scalapb" %% "compilerplugin" % scalapbCompiler

  // lazy val kalixScalaSdk: ModuleID = "io.kalix" %% "kalix-scala-sdk" % KalixSdkVersion

  val basicTestingDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % scalatest % Test,
    "org.scalamock" %% "scalamock" % scalamock % Test,
    "org.scalacheck" %% "scalacheck" % scalacheck % "test"
  )

  val loggingDependencies: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % logback,
    "com.typesafe.scala-logging" %% "scala-logging" % scalalogging
  )

  val utilityDependencies: Seq[ModuleID] = Seq(
    "org.scala-lang.modules" %% "scala-xml" % scalaxml,
    // "io.scalaland" %% "chimney" % chimney,
    "com.github.pureconfig" %% "pureconfig" % pureconfig,
    "org.scalacheck" %% "scalacheck" % scalacheck
    // "dev.optics" %% "monocle-core" % monocle,
    // "dev.optics" %% "monocle-macro" % monocle,
    // noinspection SbtDependencyVersionInspection
    // "commons-codec" % "commons-codec" % commonsCodec,
    // "org.typelevel" %% "cats-core" % cats
  )

  val httpDependencies: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.client3" %% "core" % sttp,
    "com.softwaremill.sttp.client3" %% "jsoniter" % sttp,
    "com.softwaremill.sttp.client3" %% "okhttp-backend" % sttp
  )

  val jsonDependencies: Seq[ModuleID] = Seq(
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsoniterScala,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterScala,
    "io.circe" %% "circe-core" % circe,
    "io.circe" %% "circe-generic" % circe,
    "io.circe" %% "circe-parser" % circe
  )

  val integrationTestDependencies: Seq[ModuleID] = Seq(
    "org.testcontainers" % "testcontainers" % testcontainers % Test,
    "com.typesafe.akka" %% "akka-stream" % akka % Test,
    "com.lightbend.akka" %% "akka-stream-alpakka-google-cloud-pub-sub-grpc" % alpakka % Test,
    "com.typesafe.akka" %% "akka-stream-kafka" % akkaKafka % Test,
    "com.typesafe.akka" %% "akka-http" % akkaHttp % Test,
    "com.typesafe.akka" %% "akka-http2-support" % akkaHttp % Test
  )

  val scalaPbDependencies: Seq[ModuleID] = Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "io.kalix" % "kalix-sdk-protocol" % KalixProtocolVersion % "protobuf-src",
    "com.google.protobuf" % "protobuf-java" % "3.21.9" % "protobuf"
  )

  val scalaPbValidationDependencies: Seq[ModuleID] = Seq(
    "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "com.thesamet.scalapb" %% "scalapb-validate-core" % scalapb.validate.compiler.BuildInfo.version % "protobuf",
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  )
}
