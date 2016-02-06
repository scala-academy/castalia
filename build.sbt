name          := """Castalia"""
organization  := "castalia"
version       := "0.0.1"
scalaVersion  := "2.11.7"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

lazy val castalia = project

lazy val gatling = project

lazy val root = (project in file(".")).
  aggregate(castalia)


fork in run := true

