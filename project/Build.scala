import sbt._
import Keys._

object Build extends Build {

  val dependencies = {
    val akkaV       = "2.4.1"
    val akkaStreamV = "2.0-M2"
    val scalaTestV  = "2.2.5"
    Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
      "com.typesafe.akka" %% "akka-actor"                           % akkaV,
      "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-experimental"               % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaStreamV,
      //"com.hunorkovacs" %% "koauth" % "1.1.0",
      "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamV,

      "com.typesafe.slick" %% "slick" % "3.1.1",
      "com.h2database" % "h2" % "1.3.170",
      "com.novocode" % "junit-interface" % "0.10" % "test",
      "org.slf4j" % "slf4j-nop" % "1.6.4",

      "com.typesafe.akka" %% "akka-stream-testkit-experimental"     % akkaStreamV % "test",
      "org.scalatest"     %% "scalatest"                            % scalaTestV % "test"
    )
  }

  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

  lazy val commonSettings = Seq(
    organization := "com.frossi85",
    version := "0.1.0",
    scalaVersion := "2.11.7"
  )

  lazy val root = (project in file(".")).
    settings(commonSettings: _*).
    settings(
      name := "hello",
      libraryDependencies ++= dependencies
    )
}
