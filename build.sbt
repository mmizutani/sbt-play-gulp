lazy val root = (project in file("."))
  .aggregate(`sbt-play-gulp`, `play-gulp`)
  .enablePlugins(GitVersioning)
  .settings(
    inThisBuild(
      Seq(
        version := "0.2.0-SNAPSHOT",
        crossSbtVersions := Seq("0.13.16", "1.1.1"),
        scalafmtOnCompile := true
      )),
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8")
  )

lazy val `sbt-play-gulp` = (project in file("sbt-play-gulp"))
  .settings(
    name := "sbt-play-gulp",
    sbtPlugin := true,
//    crossSbtVersions := Seq("0.13.16", "1.1.1"),
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11"),
    commonSettings
  )

lazy val `play-gulp` = (project in file("play-gulp"))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-gulp",
    commonSettings
  )

lazy val commonSettings = bintrayPublishSettings ++ scriptedScalatestSettings

lazy val bintrayPublishSettings = Seq(
  description := "An SBT plugin to use Gulp for static assets compilation in Play Framework projects",
  organization := "com.github.mmizutani",
  licenses += ("Apache-2.0", url(
    "https://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/mmizutani/sbt-play-gulp")),
  publishMavenStyle := true,
  bintrayOrganization := None,
  bintrayRepository := "maven",
  bintrayPackage := "sbt-play-gulp",
  bintrayReleaseOnPublish := false
)

lazy val scriptedScalatestSettings = Seq(
  scriptedBufferLog := false,
  scriptedLaunchOpts := {
    scriptedLaunchOpts.value ++ Seq(
      "-Xmx1024M",
      "-XX:MaxPermSize=256M",
      "-Dplugin.version=" + (version in ThisBuild).value)
  }
)
