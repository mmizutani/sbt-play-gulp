import PlayGulpPlugin._
import PlayGulpKeys._

lazy val `play-gulp-angular`: Project = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator

PlayGulpPlugin.playGulpSettings ++ PlayGulpPlugin.withTemplates


//unmanagedResourceDirectories in Assets <+= (gulpDirectory in Compile)(base => base / "build")
//sourceDirectories in TwirlKeys.compileTemplates in Compile ++= Seq(gulpDirectory.value / "app")