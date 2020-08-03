// =====================================================================================================================
// = Metadata
// =====================================================================================================================
name := "mapper"
version := "0.0.1"
organization := "de.salt.sce"
scalaVersion := "2.12.3"

lazy val root = project in file("")

// Remove feature warning
// Doc: http://stackoverflow.com/questions/27895790/sbt-0-12-4-there-were-x-feature-warnings-re-run-with-feature-for-details
scalacOptions in ThisBuild ++= Seq("-feature")

// =====================================================================================================================
// = Unit Test Settings
// =====================================================================================================================
parallelExecution in Test := false // Execute unit tests one by one
logBuffered in Test := false

// =====================================================================================================================
// = Dependencies
// =====================================================================================================================
// library for all microservices
libraryDependencies += "de.salt.sce" %% "sce-model-webservices" % "0.4.13"

// Akka HTTP :: https://doc.akka.io/docs/akka-http/current/scala/http/index.html
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.1.10" % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.26" % "test"


// Scalatest :: http://www.scalatest.org
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.0.0" % "test"
libraryDependencies += "org.mockito" % "mockito-junit-jupiter" % "2.23.0" % "test"
libraryDependencies += "org.assertj" % "assertj-core" % "3.11.1" % "test"
