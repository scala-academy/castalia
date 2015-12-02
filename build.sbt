name          := """Castalia Stub Server"""
organization  := "castalia"
version       := "0.0.1"
scalaVersion  := "2.11.7"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamV      = "2.0-M1"
  val scalaTestV       = "3.0.0-M12"
  val scalaMockV       = "3.2.2"
  Seq(
    "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "org.scalatest"     %% "scalatest"                            % scalaTestV       % "test,it",
    "org.scalamock"     %% "scalamock-scalatest-support"          % scalaMockV       % "test,it",
    "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamV      % "test,it",
    "com.twitter"       %% "finagle-http"                         % "6.30.0"         % "it"
  )
}

// Use IntegrationTest
// (http://www.scala-sbt.org/release/docs/Testing.html#Integration+Tests)
lazy val root = project.in(file("."))
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
