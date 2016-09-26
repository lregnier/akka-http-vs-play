import sbt._

object Dependencies {

  val commonDependencies = {
    val scalaTestVersion = "2.2.5"
    Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  }

  val akkaDependencies = {
    val akkaVersion = "2.4.1"
    Seq(
      "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
    )
  }

  val akkaStreamDependencies = {
    val akkaStreamVersion = "2.0-M2"
    val json4sVersion = "3.3.0"
    Seq(
      "com.typesafe.akka" %% "akka-http-core-experimental"    % akkaStreamVersion,
      "com.typesafe.akka" %% "akka-http-experimental"         % akkaStreamVersion,
      "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamVersion % "test",

      "org.json4s" %% "json4s-jackson" % json4sVersion,
      "de.heikoseeberger" %% "akka-http-json4s" % "1.4.1"
    )
  }

  val playDependencies = Seq(
    "org.scalatestplus" %% "play" % "1.4.0" % "test"
  )

  val gatlingDependencies = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "test",
    "io.gatling"            % "gatling-test-framework"    % "2.1.7" % "test"
  )
}

