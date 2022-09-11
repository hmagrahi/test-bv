
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val AkkaVersion = "2.6.19"
val Slf4jConvertorVersion = "1.7.36"
lazy val root = (project in file("."))
  .settings(
    name := "bv-test",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.0",
      "org.slf4j" % "jcl-over-slf4j" % Slf4jConvertorVersion,
      "org.slf4j" % "jul-to-slf4j" % Slf4jConvertorVersion,
      "org.slf4j" % "log4j-over-slf4j" % Slf4jConvertorVersion,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.13" % "test"
    )
  )
