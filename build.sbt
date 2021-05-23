// might want to change
name := "databricks115"
// used as `groupId`. (might want to change)
organization := "databricks"
version := "1.0"
scalaVersion := "2.12.10"
// open source licenses that apply to the project
licenses := Seq("APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
description := "IPv4 and IPv6 Network address manipulation library for Scala."

/*
  ***fill with our own***

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("scalacenter", "library-example", "julien.richard-foy@epfl.ch"))

// publish to the sonatype repository
publishTo := sonatypePublishTo.value
 */

libraryDependencies += "com.google.guava" % "guava" % "30.1-jre"

/*
  maybe remove testing dependencies before publishing
 */
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.1"
libraryDependencies += "com.github.mrpowers" %% "spark-daria" % "0.38.2"
libraryDependencies += "com.github.mrpowers" %% "spark-fast-tests" % "0.21.3" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
// test suite settings
fork in Test := true
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M")
// Show runtime of tests
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
