
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

lazy val commonSettings = Seq(
  organization := "com.whiteprompt",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "Root Project"
  )
  .aggregate(core, apiAkkaHttp, apiPlay, test)

lazy val core = Project(
  "core",
  file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "Core",
    libraryDependencies ++= Dependencies.commonDependencies ++ Dependencies.akkaDependencies ++
      Dependencies.kamonDependencies
  )

lazy val apiAkkaHttp = Project(
  "api-akka-http",
  file("api-akka-http"))
  .settings(commonSettings: _*)
  .settings(
    name := "API Akka-Http",
    libraryDependencies ++= Dependencies.commonDependencies ++ Dependencies.akkaDependencies ++
      Dependencies.akkaStreamDependencies ++ Dependencies.kamonDependencies ++ Dependencies.loggingDependencies ++
        Dependencies.kamonDependencies
  )
  .dependsOn(core)


lazy val apiPlay = Project(
  "api-play",
  file("api-play"))
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(routesGenerator := InjectedRoutesGenerator)
  .settings(
    name := "API Play",
    libraryDependencies ++= Dependencies.commonDependencies ++ Dependencies.playDependencies ++
      Dependencies.akkaDependencies ++ Dependencies.kamonDependencies ++ Dependencies.loggingDependencies
  )
  .settings(
    routesGenerator := InjectedRoutesGenerator
  )
  .dependsOn(core)

lazy val test = Project(
  "test",
  file("test"))
  .enablePlugins(GatlingPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "Stress tests",
    libraryDependencies ++= Dependencies.commonDependencies ++ Dependencies.gatlingDependencies
  )
  .dependsOn(core)
  .dependsOn(apiAkkaHttp)

scalacOptions in Test ++= Seq("-Yrangepos")
