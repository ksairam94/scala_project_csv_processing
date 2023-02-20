ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
 .settings(
   name := "Scala_Test"
 )
libraryDependencies ++= {
 val akkaV = "2.6.3"
 val scalaLoggingV = "3.9.2"
 Seq(
   "com.typesafe.akka" %% "akka-stream" % akkaV,
   "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
   "com.typesafe.akka" %% "akka-stream-testkit" % akkaV,
   "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV)
}
