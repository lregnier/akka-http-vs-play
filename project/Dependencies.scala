import sbt._

object Dependencies {

  val sharedDependencies = {
    val scalaTestV = "2.2.5"
    val json4s = "3.3.0"
    Seq(
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.h2database" % "h2" % "1.3.170",
      "com.novocode" % "junit-interface" % "0.10" % "test",
      "org.json4s" %% "json4s-core" % json4s,
      "org.json4s" %% "json4s-jackson" % json4s,
      "org.json4s" %% "json4s-native" % json4s,

      "com.google.inject" % "guice" % "4.0",

      "org.scalatest" %% "scalatest" % scalaTestV % "test",
      "com.github.frossi85" %% "slick-migration-api-flyway" % "0.2.1",

      // [For monitoring]
      "io.kamon" %% "kamon-core" % "0.5.2",
      "io.kamon" %% "kamon-system-metrics" % "0.5.2",
      "io.kamon" %% "kamon-scala" % "0.5.2" % Runtime,
      "io.kamon" %% "kamon-jdbc" % "0.5.2" % Runtime,
      "io.kamon" %% "kamon-akka" % "0.5.2" % Runtime,
      "io.kamon" %% "kamon-akka-remote" % "0.5.2" % Runtime,

      // [For reporting monitored data]
      "io.kamon" %% "kamon-statsd" % "0.5.2" //Read http://kamon.io/backends/statsd/
    )
  }

  val akkaDependencies = {
    val akkaV = "2.4.1"
    val akkaStreamV = "2.0-M2"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaV,
      "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV,


      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.2",
      "com.typesafe.akka" %% "akka-slf4j" % akkaV,

      "de.heikoseeberger" %% "akka-http-json4s" % "1.4.1",

      "com.typesafe.akka" %% "akka-stream-testkit-experimental" % akkaStreamV % "test"
    )
  }

  val kamonPlayDependencies = Seq(
    "io.kamon" %% "kamon-play-24" % "0.5.2"
  )

  val gatlingDependencies = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "test",
    "io.gatling"            % "gatling-test-framework"    % "2.1.7" % "test"
  )
}

