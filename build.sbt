lazy val `sbt-play-gulp` = (project in file("."))
  .aggregate(`play-gulp`)
  .dependsOn(`play-gulp`)
  .settings(
    name := "sbt-play-gulp",
    version := "0.0.1",
    sbtVersion in Global := "0.13.8",
    //scalaVersion := "2.10.4",
    //scalaVersion := "2.11.6",
    //crossScalaVersions := Seq("2.10.4", "2.11.6"),
    sbtPlugin := true,
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2"),
    organization := "com.github.mmizutani",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage := Some(url("https://github.com/mmizutani/sbt-play-gulp")),
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    javacOptions in Compile ++= Seq("-encoding", "utf8", "-g")
  )
  //.settings(bintrayPublishSettings("sbt-play-gulp"): _*)
  .settings(mavenPublishSettings)

lazy val `play-gulp` = project.in(file("play-gulp"))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-gulp",
    version := "0.0.1",
    libraryDependencies ++= Seq(),
    scalaVersion := "2.11.6",
    //scalaVersion in Global := "2.11.6",
    crossScalaVersions := Seq("2.10.4", "2.11.6"),
    organization := "com.github.mmizutani",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage := Some(url("https://github.com/mmizutani/sbt-play-gulp")),
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    javacOptions in Compile ++= Seq("-encoding", "utf8", "-g")
  )
  //.settings(bintrayPublishSettings("play-gulp"): _*)
  .settings(mavenPublishSettings)

def bintrayPublishSettings(projectName: String) = Seq(
  bintrayRepository in bintray := projectName,
  bintrayReleaseOnPublish in bintray := false // false = two stage publishing (publish & bintrayRelease)
)

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
