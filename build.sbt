//import bintray.Keys._

sbtPlugin := true

name := """sbt-play-gulp"""

version in ThisBuild := "0.0.1"

//scalaVersion in Global := "2.10.4"
//sbtVersion in ThisBuild := "0.13.8"

lazy val `sbt-play-gulp` = (project in file("."))
  .enablePlugins(PlaySbtPlugin, PlayReleaseBase)
  .aggregate(`play-gulp`)
  .dependsOn(`play-gulp`)
  .settings(mavenPublishSettings: _*)

lazy val `play-gulp` = project.in(file("play-gulp"))

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2")

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/mmizutani/sbt-play-gulp"))

scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8")
javacOptions in Compile ++= Seq("-encoding", "utf8", "-g")

playBuildRepoName in ThisBuild := "sbt-play-gulp"

playBuildExtraTests := {
  (scripted in `sbt-play-gulp`).toTask("").value
}

// playBuildExtraPublish := {
//   (publishSigned in `sbt-gulp`).value
// }

scriptedSettings
scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }
scriptedBufferLog := false

//lazy val bintrayPublishSettings = Seq(
//  repository in bintray := "sbt-play-gulp",
//  bintrayOrganization in bintray := None
//)

lazy val sbtPluginPublishSettings = Seq(
  publishMavenStyle := false,
  publishTo := {
    if (isSnapshot.value) Some(Classpaths.sbtPluginSnapshots)
    else Some(Classpaths.sbtPluginReleases)
  }
)

lazy val mavenPublishSettings = Seq(
  publishMavenStyle := true,
  publishTo <<= version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra :=
    <scm>
      <url>git@github.com:mmizutani/play-gulp.git</url>
      <connection>scm:git:git@github.com:mmizutani/sbt-play-gulp.git</connection>
    </scm>
    <developers>
      <developer>
        <id>minorumizutani</id>
        <name>Minoru Mizutani</name>
        <url>https://github.com/mmizutani</url>
      </developer>
    </developers>
)
