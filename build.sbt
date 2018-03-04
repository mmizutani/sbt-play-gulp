lazy val root = (project in file("."))
  .aggregate(`sbt-play-gulp`, `play-gulp`)

lazy val `sbt-play-gulp` = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(
    name := "sbt-play-gulp",
    sbtPlugin := true,
    crossSbtVersions := Seq("0.13.16", "1.1.1"),
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11"),
    commonSettings,
    scriptedScalatestSettings
  )

lazy val `play-gulp` = (project in file("play-gulp"))
  .enablePlugins(PlayScala)
  .enablePlugins(GitVersioning)
  .settings(
    name := "play-gulp",
    commonSettings
  )

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
  javacOptions in Compile ++= Seq("-encoding", "utf8", "-g")
) ++ bintrayPublishSettings

lazy val bintrayPublishSettings = Seq(
  description := "An SBT plugin to use Gulp for static assets compilation in Play Framework projects",
  organization := "com.github.mmizutani",
  licenses += ("Apache-2.0", url(
    "https://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/mmizutani/sbt-play-gulp")),
  publishMavenStyle := false,
  bintrayOrganization := None,
  bintrayRepository := "sbt-plugins",
  bintrayPackage := "sbt-play-gulp",
  bintrayReleaseOnPublish := false
)

lazy val scriptedScalatestSettings = Seq(
  scriptedBufferLog := false,
  scriptedLaunchOpts += s"-Dplugin.version=${version.value}"
)

scalafmtOnCompile in ThisBuild := true
