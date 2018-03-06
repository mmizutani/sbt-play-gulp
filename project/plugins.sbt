addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.3")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value