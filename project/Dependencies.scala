import sbt._

object Dependencies {

  val commonDependencies = {
    val scalaTestVersion = "2.2.5"
    Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  }

  val akkaDependencies = {
    val akkaVersion = "2.4.14"
    Seq(
      "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
    )
  }

  val akkaHttpDependencies = {
    val akkaHttpVersion = "10.0.0"
    val json4sVersion = "3.3.0"
    Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",

      "org.json4s" %% "json4s-jackson" % json4sVersion,
      "de.heikoseeberger" %% "akka-http-json4s" % "1.11.0"
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

