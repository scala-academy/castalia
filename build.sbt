name          := "castalia"
description   := "Castalia Stub Server"
organization  := "com.github.scala-academy"
version       := "0.0.1-SNAPSHOT"
scalaVersion  := "2.11.7"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")


// http://jdistlib.sourceforge.net/javadoc/
lazy val jDistLibVersion = "0.4.1"
lazy val jDistLib = "jdistlib" % "jdistlib" % jDistLibVersion from
  s"""http://downloads.sourceforge.net/project/jdistlib/jdistlib-$jDistLibVersion-bin.jar"""


libraryDependencies ++= {
  val akkaStreamV      = "2.0.3"
  val scalaTestV       = "3.0.0-M15"

  Seq(
    "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "com.typesafe.akka" %% "akka-contrib"                         % "2.4.1",
    "org.jliszka"       %% "probability-monad" % "1.0.1",
    jDistLib,
    "org.scalatest"     %% "scalatest"                            % scalaTestV       % "test,it",
    "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamV      % "test",
    "com.typesafe.akka" %% "akka-testkit"                         % "2.4.1"          % "test",
    "com.miguno.akka"   %% "akka-mock-scheduler"                  % "0.4.0"          % "test",
    "com.twitter"       %% "finagle-http"                         % "6.31.0"         % "it"
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
