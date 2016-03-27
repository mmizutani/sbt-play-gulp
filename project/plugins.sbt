resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sbtPluginRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
