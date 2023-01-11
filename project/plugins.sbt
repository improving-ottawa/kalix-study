// addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.0")

// Should be in-sync with https://github.com/lightbend/kalix-jvm-sdk/blob/main/project/Dependencies.scala

addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.1")

addSbtPlugin("io.kalix" % "sbt-kalix" % "1.1.4")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.1")

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.11")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.0")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")
addSbtPlugin("no.arktekk.sbt" % "aether-deploy" % "0.27.0")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
addSbtPlugin("me.pdalpra" % "sbt-autoversion" % "2.0.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.0.7")
// addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.1.1")

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.12"
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-validate-codegen" % "0.3.3"

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
