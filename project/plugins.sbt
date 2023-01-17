addSbtPlugin("io.kalix" % "sbt-kalix" % "1.1.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.1")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.0")

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
