addSbtPlugin("io.kalix" % "sbt-kalix" % "1.1.4")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.11")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.12"
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-validate-codegen" % "0.3.3"
