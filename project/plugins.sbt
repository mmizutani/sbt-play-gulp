//resolvers ++= Seq(
//  Resolver.typesafeRepo("snapshots"),
//  Resolver.mavenLocal,
//  Resolver.sbtPluginRepo("releases"),
//  Resolver.sonatypeRepo("releases"),
//  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
//  "Typesafe repository" at "https://dl.bintray.com/typesafe/maven-releases/"
//)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.3.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")