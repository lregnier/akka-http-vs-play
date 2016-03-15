
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

lazy val commonSettings = Seq(
  organization := "com.frossi85",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "Root Project"
  )
  .aggregate(core, apiAkkaHttp, play_example, stress_tests)

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "Core",
    libraryDependencies ++= Dependencies.sharedDependencies ++ Dependencies.akkaDependencies
  )

lazy val apiAkkaHttp = Project(
  "api-akka-http",
  file("api-akka-http"),
  settings = Seq(
    name := "API Akka-Http",
    libraryDependencies ++= Dependencies.sharedDependencies ++ Dependencies.akkaDependencies
  )
) dependsOn (core)


lazy val play_example = (project in file("play_example"))
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(routesGenerator := InjectedRoutesGenerator)
  .settings(
    name := "Play Example",
    libraryDependencies ++=
      Dependencies.sharedDependencies ++
      Dependencies.kamonPlayDependencies
  )
  .settings(
    routesGenerator := InjectedRoutesGenerator
  )
  .dependsOn(core)


lazy val stress_tests = (project in file("stress_tests"))
  .enablePlugins(GatlingPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "Stress tests",
    libraryDependencies ++= Dependencies.sharedDependencies ++ Dependencies.gatlingDependencies
  )
  .dependsOn(core)
  .dependsOn(apiAkkaHttp)


//javaOptions in Test := Seq("-Dkamon.auto-start=true")

scalacOptions in Test ++= Seq("-Yrangepos")
