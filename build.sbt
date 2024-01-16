// =====================================================================================================================
// = Metadata
// =====================================================================================================================
name := "mapper"
version := "2024.1.0"
organization := "de.salt.sce"
scalaVersion := "2.12.11"

lazy val root = (project in file(""))
  .enablePlugins(BuildInfoPlugin).settings(
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
  buildInfoPackage := "de.salt.sce.mapper")

// Remove feature warning
// Doc: http://stackoverflow.com/questions/27895790/sbt-0-12-4-there-were-x-feature-warnings-re-run-with-feature-for-details
ThisBuild / scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

// =====================================================================================================================
// = Unit Test Settings
// =====================================================================================================================
Test / parallelExecution := false // Execute unit tests one by one
Test / logBuffered       := false

// =====================================================================================================================
// = Dependencies
// =====================================================================================================================

val akkaVersion = "2.6.21"
val akkaHttpVersion = "10.2.10"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.20.0"
libraryDependencies += "org.slf4j" % "slf4j-ext" % "1.7.36" // sic: org.apache.logging 2.20.0 depends on 1.7.36
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.20.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.20.0"

// Akka CORS :: https://github.com/lomigmegard/akka-http-cors
libraryDependencies += "ch.megard" %% "akka-http-cors" % "1.2.0"

libraryDependencies += "commons-codec" % "commons-codec" % "1.15"
libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.6"
libraryDependencies += "de.heikoseeberger" %% "akka-http-json4s" % "1.39.2"

libraryDependencies += "com.google.code.gson" % "gson" % "2.10.1"

// Scalatest :: http://www.scalatest.org
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.9.3" % Test
libraryDependencies += "org.assertj" % "assertj-core" % "3.24.2" % Test

libraryDependencies += "commons-io" % "commons-io" % "2.13.0" // used in ConfigResponseWriter

libraryDependencies += "org.milyn" % "milyn-smooks-edi" % "1.7.1"
libraryDependencies += "org.milyn" % "milyn-smooks-csv" % "1.7.1"
libraryDependencies += "org.milyn" % "milyn-smooks-fixed-length" % "1.7.1"
//libraryDependencies ++= Seq(
//  "org.milyn" % "milyn-smooks-all" % "1.7.0",
//  "javax.jms" % "jms" % "1.1"
//) 


assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "data-decoders.inf") => MergeStrategy.concat
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("overview.html") => MergeStrategy.concat
  case x => MergeStrategy.first
}
