
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



    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,


    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.h2database" % "h2" % "1.3.170",
    "com.novocode" % "junit-interface" % "0.10" % "test",
    //"org.slf4j" % "slf4j-nop" % "1.6.4",

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
    "io.kamon" %% "kamon-system-metrics" % "0.5.2",
    "io.kamon" %% "kamon-scala" % "0.5.2" % Runtime,
    "io.kamon" %% "kamon-jdbc" % "0.5.2" % Runtime,
    "io.kamon" %% "kamon-akka" % "0.5.2" % Runtime,
    "io.kamon" %% "kamon-akka-remote" % "0.5.2" % Runtime,

    // [For reporting monitored data]
    "io.kamon" %% "kamon-statsd" % "0.5.2" //Read http://kamon.io/backends/statsd/
    //Follow http://kamon.io/teamblog/2015/10/06/kamon-0.5.2-is-out/

    /*
"io.kamon" %% "kamon-play-24" % "0.5.2", //for PLay 2.4 or "io.kamon" %% "kamon-play-23" % "0.5.2", for Play 2.3
     */
  )
}

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "frossi85 bintray" at "http://dl.bintray.com/frossi85/maven"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

lazy val commonSettings = Seq(
  organization := "com.frossi85",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)



lazy val root = (project in file("."))
  //enablePlugins(GatlingPlugin).
  .settings(commonSettings: _*)
  .settings(
    name := "Root Project",
    libraryDependencies ++= dependencies
  )
  /*.setName("SBT template")
  .setDescription("Backup DSL")
  .setInitialCommand("_")
  .configureRoot*/
  .aggregate(core, akka_http_example, play_example, stress_tests)

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "Core",
    libraryDependencies ++= dependencies
  )
  /*.setName("common")
  .setDescription("Common utilities")
  .setInitialCommand("_")
  .configureModule*/

lazy val akka_http_example = (project in file("akka_http_example"))
  .settings(commonSettings: _*)
  .settings(
    name := "Akka Http Example",
    libraryDependencies ++= dependencies
  )
  /*.setName("first")
  .setDescription("First project")
  .setInitialCommand("first._")
  .configureModule
  .configureIntegrationTests
  .configureFunctionalTests
  .configureUnitTests*/
  .dependsOn(core)

lazy val play_example = (project in file("play_example"))
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(
    name := "Play Example",
    libraryDependencies ++= dependencies
  )
  .settings(
    // Play provides two styles of routers, one expects its actions to be injected, the
    // other, legacy style, accesses its actions statically.
    routesGenerator := InjectedRoutesGenerator
  )
  /*.setName("second")
  .setDescription("Second project")
  .setInitialCommand("second._")
  .configureModule
  .configureIntegrationTests
  .configureFunctionalTests
  .configureUnitTests*/
  .dependsOn(core)



lazy val stress_tests = (project in file("stress_tests"))
  .enablePlugins(GatlingPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "Stress tests",
    libraryDependencies ++= dependencies
  )
  /*.setName("first")
  .setDescription("First project")
  .setInitialCommand("first._")
  .configureModule
  .configureIntegrationTests
  .configureFunctionalTests
  .configureUnitTests*/
  .dependsOn(core)
  .dependsOn(akka_http_example)


routesGenerator := InjectedRoutesGenerator

//javaOptions in Test := Seq("-Dkamon.auto-start=true")