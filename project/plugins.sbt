//resolvers ++= Seq(
//  Resolver.typesafeRepo("snapshots"),
//  Resolver.mavenLocal,
//  Resolver.sbtPluginRepo("releases"),
//  Resolver.sonatypeRepo("releases"),
//  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
//  "Typesafe repository" at "https://dl.bintray.com/typesafe/maven-releases/"
//)

//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.3")
//addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.1")
//addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.5")
//addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.3")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value