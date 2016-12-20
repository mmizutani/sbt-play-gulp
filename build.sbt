lazy val root = (project in file("."))
  .aggregate(`sbt-play-gulp`, `play-gulp`)

lazy val `sbt-play-gulp`: Project = (project in file("."))
  .settings(
    name := "sbt-play-gulp",
    scalaVersion := "2.10.6",
    sbtPlugin := true,
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.10"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.3.0"),
    commonSettings
  )

lazy val `play-gulp`: Project = project.in(file("play-gulp"))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-gulp",
    scalaVersion := "2.11.8",
    commonSettings
  )

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
  javacOptions in Compile ++= Seq("-encoding", "utf8", "-g")
) ++ mavenPublishSettings

lazy val mavenPublishSettings = Seq(
  organization := "com.github.mmizutani",
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/mmizutani/sbt-play-gulp")),
  publishMavenStyle := true,
  publishTo <<= version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
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
