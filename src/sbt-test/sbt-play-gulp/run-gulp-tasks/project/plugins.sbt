addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")

sys.props.get("plugin.version") match {
  case Some(pluginVersion) => addSbtPlugin("com.github.mmizutani" % "sbt-play-gulp" % pluginVersion)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}