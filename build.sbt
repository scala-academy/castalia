name          := """Castalia Stub Server"""
organization  := "castalia"
version       := "0.0.1"
scalaVersion  := "2.11.7"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamV      = "2.0.2"
  val scalaTestV       = "3.0.0-M15"

  Seq(
    "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "com.typesafe.akka" %%  "akka-contrib"                        % "2.4.1",
    "org.jliszka"       %% "probability-monad" % "1.0.1",
    "org.scalatest"     %% "scalatest"                            % scalaTestV       % "test,it",
    "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamV      % "test,it",
    "com.miguno.akka"   %% "akka-mock-scheduler"                  % "0.4.0"          % "test",
    "com.twitter"       %% "finagle-http"                         % "6.31.0"         % "it",
    "io.gatling.highcharts" % "gatling-charts-highcharts"         % "2.1.7" % "test",
    "io.gatling"            % "gatling-test-framework"            % "2.1.7" % "test",
    "org.scalaj"        % "scalaj-http_2.11"                      % "2.2.1"

  )
}

// Use IntegrationTest
// (http://www.scala-sbt.org/release/docs/Testing.html#Integration+Tests)
lazy val root = project.in(file("."))
  .enablePlugins(GatlingPlugin)
  .configs(IntegrationTest)
  .settings( Defaults.itSettings : _* )

initialCommands := """|import akka.actor._
                      |import akka.pattern._
                      |import akka.util._
                      |import scala.concurrent._
                      |import scala.concurrent.duration._""".stripMargin

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

fork in run := true

mainClass in (Compile, run) := Some("castalia.Main")