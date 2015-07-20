lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := "play-gulp-demo"

// Required by specs2 to get scalaz-stream
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.2" % "test",
  "junit" % "junit" % "4.12" % "test"
)

routesGenerator := InjectedRoutesGenerator
