
// =====================================================================================================================
// = Metadata
// =====================================================================================================================
name := "mapper"
version := "1.1.12"
organization := "de.salt.sce"
scalaVersion := "2.12.11"

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

val akkaVersion = "2.5.26"
val akkaHttpVersion = "10.1.12"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.3"
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "12.0"

// Akka CORS :: https://github.com/lomigmegard/akka-http-cors
libraryDependencies += "ch.megard" %% "akka-http-cors" % "1.0.0"

libraryDependencies += "commons-codec" % "commons-codec" % "1.13"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.7"
libraryDependencies += "de.heikoseeberger" %% "akka-http-json4s" % "1.28.0"

libraryDependencies += "com.google.code.gson" % "gson" % "2.8.6"

// Scalatest :: http://www.scalatest.org
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.0.0" % "test"
libraryDependencies += "org.mockito" % "mockito-junit-jupiter" % "2.23.0" % "test"
libraryDependencies += "org.assertj" % "assertj-core" % "3.11.1" % "test"

libraryDependencies += "commons-io" % "commons-io" % "2.5"
libraryDependencies += "commons-validator" % "commons-validator" % "1.6"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4"

libraryDependencies += "org.milyn" % "milyn-smooks-edi" % "1.7.1"
libraryDependencies += "org.milyn" % "milyn-smooks-csv" % "1.7.1"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "data-decoders.inf") => MergeStrategy.concat
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("overview.html") => MergeStrategy.concat
  case x => MergeStrategy.first
}