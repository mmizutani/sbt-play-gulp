logLevel := Level.Warn

// Let the demo play app depend on the sbt-play-gulp plugin
lazy val root = (project in file(".")).dependsOn(plugin)

// Obtain reference path to the sbt-play-plugin project
lazy val plugin = file("../").getCanonicalFile.toURI

// or alternately use the github snapshot version
//lazy val plugin = uri("git://github.com/mmizutani/sbt-play-gulp")

//resolvers ++= Seq(
//  Resolver.mavenLocal,
//  Resolver.sbtPluginRepo("snapshots"),
//  Resolver.sonatypeRepo("snapshots"),
//  Resolver.typesafeRepo("snapshots")
//)

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2")

// Use the sbt-play-gulp plugin to get support for npm/bower/gulp commands in the play console
//addSbtPlugin("com.mmizutani" % "sbt-play-gulp" % "0.0.1")
addSbtPlugin("com.typesafe.play" % "sbt-play-gulp" % "0.0.1")

// Not necessary for the sbt-play-gulp plugin to work but useful for development
addSbtPlugin("com.jamesward" % "play-auto-refresh" % "0.0.13")
