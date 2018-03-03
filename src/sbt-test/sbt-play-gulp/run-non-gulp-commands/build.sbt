lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    scalaVersion := "2.12.4"
  )