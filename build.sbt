lazy val root = (project in file("."))
  .aggregate(`sbt-play-gulp`, `play-gulp`)
  .enablePlugins(GitVersioning)
  .settings(
    inThisBuild(
      Seq(
        crossSbtVersions := Seq("0.13.16", "1.1.1"),
        scalafmtOnCompile := true
      )),
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-unchecked",
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard",
      "-encoding",
      "utf8"
    )
  )

lazy val `sbt-play-gulp` = (project in file("sbt-play-gulp"))
  .settings(
    name := "sbt-play-gulp",
    sbtPlugin := true,
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.0"),
    commonSettings
  )

lazy val `play-gulp` = (project in file("play-gulp"))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-gulp",
    commonSettings
  )

lazy val commonSettings = Seq(
  description := "An SBT plugin to use Gulp for static assets compilation in Play Framework projects",
  organization := "com.github.mmizutani",
  licenses += ("Apache-2.0", url(
    "https://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/mmizutani/sbt-play-gulp"))
) ++ mavenPublishSettings ++ scriptedScalatestSettings

lazy val bintrayPublishSettings = Seq(
  publishMavenStyle := false,
  bintrayOrganization := None,
  bintrayRepository := "sbt-plugins",
  bintrayPackage := "sbt-play-gulp"
)

lazy val mavenPublishSettings = Seq(
  pomIncludeRepository := { _ =>
    false
  },
  scmInfo := Some(
    ScmInfo(url("https://github.com/mmizutani/sbt-play-gulp"),
            "scm:git@github.com:mmizutani/sbt-play-gulp.git")),
  publishMavenStyle := true,
  publishTo := Some(
    "releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"),
  publishArtifact in Test := false,
  developers := List(
    Developer(
      id = "minorumizutani",
      name = "Minoru Mizutani",
      email = "minoru.mizutani@gmail.com",
      url = url("https://github.com/mmizutani")
    )
  )
)

lazy val scriptedScalatestSettings = Seq(
  scriptedBufferLog := false,
  scriptedLaunchOpts := {
    scriptedLaunchOpts.value ++ Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + (version in ThisBuild).value)
  }
)
