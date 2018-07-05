name := "dh_restaurants"
sbtVersion := "1.1.6"
addCommandAlias("mgm", "migration_manager/run")

addCommandAlias("mg", "migrations/run")


lazy val slickV = "3.2.1"
lazy val forkliftV = "0.3.1"
lazy val AkkaV = "2.5.13"
lazy val AkkaHttpV = "10.1.3"

lazy val ItTest = config("it") extend Test

lazy val commonSettings = Seq(
  version := "1.0.5",
  scalaVersion := "2.12.3",
  scalacOptions += "-deprecation",
  scalacOptions += "-feature",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("public"),
    Resolver.bintrayRepo("naftoligug", "maven"),
    Resolver.sonatypeRepo("snapshots"),
    "bintray-sbt-plugin-releases" at "http://dl.bintray.com/content/sbt/sbt-plugin-releases",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  ))

lazy val loggingDependencies = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

lazy val slickDependencies = Seq(
  "com.typesafe.slick" %% "slick" % slickV
)

lazy val testDependencies = Seq(
  "com.whisk" %% "docker-testkit-scalatest" % "0.9.5" % "it,test",
  "com.whisk" %% "docker-testkit-impl-spotify" % "0.9.5" % "it,test",
  "org.scalatest" %% "scalatest" % "3.0.3" % "it,test",
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpV % "it,test"
)

lazy val dbDependencies = Seq(
  "com.typesafe.slick" %% "slick-hikaricp" % slickV,
  "org.postgresql" % "postgresql" % "42.2.2"
)

lazy val forkliftDependencies = Seq(
  "com.liyaos" %% "scala-forklift-slick" % forkliftV,
  "io.github.nafg" %% "slick-migration-api" % "0.4.1"
)

lazy val confDependencies = Seq(
  "com.typesafe" % "config" % "1.3.3"
)

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % AkkaHttpV,
  "com.typesafe.akka" %% "akka-stream" % AkkaV,
  "com.typesafe.akka" %% "akka-actor" % AkkaV,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpV)

lazy val appDependencies = dbDependencies ++ loggingDependencies ++ akkaDependencies ++ confDependencies ++ testDependencies

lazy val migrationsDependencies =
  dbDependencies ++ forkliftDependencies ++ loggingDependencies

lazy val migrationManagerDependencies = dbDependencies ++ forkliftDependencies

lazy val app = project.in(file("app"))
  .enablePlugins(JavaAppPackaging)
  .dependsOn(dbModels)
  .configs(ItTest)
  .settings(inConfig(ItTest)(Defaults.testSettings): _*)
  .settings(commonSettings: _*)
  .settings {
    libraryDependencies ++= appDependencies
  }
  .settings(mainClass := Some("com.victor.restaurant.HttpService"))

lazy val dbModels = project.in(file("db"))
  .settings(commonSettings: _*)
  .settings {
    libraryDependencies ++= dbDependencies
  }

lazy val migrationManager = project.in(file("migration_manager"))
  .settings(commonSettings: _*)
  .settings {
    libraryDependencies ++= migrationManagerDependencies
  }

lazy val migrations = project.in(file("migrations"))
  .dependsOn(migrationManager, dbModels)
  .settings(commonSettings: _*)
  .settings {
    libraryDependencies ++= migrationsDependencies
  }

lazy val dhRestaurants = project.in(file("."))
  .aggregate(app, migrations, migrationManager)
  .dependsOn(app)
  .settings(commonSettings: _*)
  .settings(mainClass in Compile := Some("com.victor.restaurant.HttpService"))
