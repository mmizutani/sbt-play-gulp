lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(plugin)

// Obtain reference path to the sbt-play-plugin project
lazy val plugin = file("../").getCanonicalFile.toURI

name := """play-gulp-demo"""

//sbtVersion := "0.13.8"

//scalaVersion := "2.10.5"
//scalaVersion in ThisBuild := "2.11.7"
//crossScalaVersions := Seq("2.11.7")

//scalaVersion in ThisBuild := "2.10.4"
//sbtVersion in Global := "0.13.8"

// Required by specs2 to get scalaz-stream
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.2" % "test",
  "junit" % "junit" % "4.12" % "test"
)

routesGenerator := InjectedRoutesGenerator
