logLevel := Level.Warn

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.3")

// For developers only
//lazy val root = (project in file(".")).dependsOn(plugin)
//lazy val plugin = file("../").getCanonicalFile.toURI
//lazy val plugin = uri("git://github.com/mmizutani/sbt-play-gulp")
//resolvers ++= Seq(
//  Resolver.mavenLocal
//  Resolver.sonatypeRepo("snapshots"),
//  Resolver.sonatypeRepo("releases"),
//  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns)
//)

// Import the plugin of this repository
addSbtPlugin("com.github.mmizutani" % "sbt-play-gulp" % "0.1.1")
