
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
    name := "Root Project"
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
    libraryDependencies ++= Dependencies.sharedDependencies ++ Dependencies.akkaDependencies
  )
  /*.setName("common")
  .setDescription("Common utilities")
  .setInitialCommand("_")
  .configureModule*/

lazy val akka_http_example = (project in file("akka_http_example"))
  .settings(commonSettings: _*)
  .settings(
    name := "Akka Http Example",
    libraryDependencies ++= Dependencies.sharedDependencies ++ Dependencies.akkaDependencies
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
  .settings(routesGenerator := InjectedRoutesGenerator)
  .settings(
    name := "Play Example",
    libraryDependencies ++=
      Dependencies.sharedDependencies ++
      Dependencies.kamonPlayDependencies,
    libraryDependencies += specs2 % Test
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
    libraryDependencies ++= Dependencies.sharedDependencies ++ Dependencies.gatlingDependencies
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




//javaOptions in Test := Seq("-Dkamon.auto-start=true")

scalacOptions in Test ++= Seq("-Yrangepos")
