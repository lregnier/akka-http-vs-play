//import io.gatling.sbt.GatlingPlugin
import sbt._
import Keys._

object Build extends Build {

  val dependencies = {
    val akkaV       = "2.4.1"
    val akkaStreamV = "2.0-M2"
    val scalaTestV  = "2.2.5"
    val json4s    = "3.3.0"
    Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
      "com.typesafe.akka" %% "akka-actor"                           % akkaV,
      "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-experimental"               % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamV,

      "com.typesafe.slick" %% "slick" % "3.1.1",
      "com.h2database" % "h2" % "1.3.170",
      "com.novocode" % "junit-interface" % "0.10" % "test",
      "org.slf4j" % "slf4j-nop" % "1.6.4",

      "org.json4s"        %% "json4s-core"            % json4s,
      "org.json4s"        %% "json4s-jackson"         % json4s,
      "org.json4s"        %% "json4s-native"          % json4s,
      "de.heikoseeberger" %% "akka-http-json4s" % "1.4.1",

      "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "test",
      "io.gatling"            % "gatling-test-framework"    % "2.1.7" % "test",

      "com.typesafe.akka" %% "akka-stream-testkit-experimental"     % akkaStreamV % "test",
      "org.scalatest"     %% "scalatest"                            % scalaTestV % "test",
      "com.github.frossi85" %% "slick-migration-api-flyway" % "0.2.1",

      // [For monitoring]
      "io.kamon" %% "kamon-core" % "0.5.2",
      "io.kamon" %% "kamon-scala" % "0.5.2",
      "io.kamon" %% "kamon-system-metrics" % "0.5.2",
      "io.kamon" %% "kamon-jdbc" % "0.5.2",
      "io.kamon" %% "kamon-akka" % "0.5.2",
      "io.kamon" %% "kamon-akka-remote" % "0.5.2",

      // [For reporting monitored data]
      "io.kamon" %% "kamon-statsd" % "0.5.2" //Read http://kamon.io/backends/statsd/
      //Follow http://kamon.io/teamblog/2015/10/06/kamon-0.5.2-is-out/

      /*
  "io.kamon" %% "kamon-play-24" % "0.5.2", //for PLay 2.4 or "io.kamon" %% "kamon-play-23" % "0.5.2", for Play 2.3
       */
    )
  }

  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


  resolvers += "frossi85 bintray" at "http://dl.bintray.com/frossi85/maven"

  resolvers += Resolver.bintrayRepo("hseeberger", "maven")

  lazy val commonSettings = Seq(
    organization := "com.frossi85",
    version := "0.1.0",
    scalaVersion := "2.11.7"
  )

  lazy val root = (project in file(".")).
    //enablePlugins(GatlingPlugin).
    settings(commonSettings: _*).
    settings(
      name := "complete-akka-microservice",
      libraryDependencies ++= dependencies
    )
}