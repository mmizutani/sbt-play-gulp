lazy val `play-gulp-sample`: Project = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator

fork in run := true

excludeDependencies ++= Seq(
  SbtExclusionRule("com.typesafe.akka", "akka_actor_2.10"),
  SbtExclusionRule("com.typesafe.play", "twirl_api_2.10")
)