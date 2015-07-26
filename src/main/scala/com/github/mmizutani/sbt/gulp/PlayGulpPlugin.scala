package com.github.mmizutani.sbt.gulp

import play.twirl.sbt.Import.TwirlKeys
import sbt._
import sbt.Keys._
import play.sbt.{Play, PlayRunHook}
import play.sbt.PlayImport.PlayKeys._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._ // for stage and dist TaskKeys
import com.typesafe.sbt.web.Import.Assets

/**
 * AutoPlugin References
 * http://mukis.de/pages/sbt-autoplugins-tutorial/
 * http://eed3si9n.com/ja/sbt-preview-auto-plugins
 * https://github.com/playframework/playframework/blob/master/framework%2Fsrc%2Fsbt-plugin%2Fsrc%2Fmain%2Fscala%2Fplay%2Fsbt%2FPlay.scala
 * http://www.scala-sbt.org/0.13/docs/Plugins.html
 * http://hakobe932.hatenablog.com/entry/2014/04/02/220457
 * https://www.playframework.com/documentation/2.4.x/SBTCookbook#Hook-actions-around-play-run
 * http://www.scala-sbt.org/0.13/docs/Community-Plugins.html
 */

/**
 * AutoPlugin Examples
 * https://github.com/sbt/sbt-web/src/main/scala/com/typesafe/sbt/web/SbtWeb.scala
 * https://github.com/heroku/sbt-heroku
 * https://github.com/mohiva/play-silhouette
 * https://github.com/jamesward/play-auto-refresh
 * https://github.com/playframework/play-slick/blob/master/build.sbt
 * https://github.com/VoxNova/sbt-plugin-seed
 * https://github.com/vmunier/sbt-play-scalajs
 */

/**
 * Defines all settings/tasks that get automatically imported when the plugin is enabled
 */
object Import {
  object PlayGulpKeys {
    lazy val gulpDirectory = SettingKey[File]("gulp-directory", "gulp directory")
    lazy val gulpFile = SettingKey[String]("gulp-file", "gulpfile")
    lazy val gulpExcludes = SettingKey[Seq[String]]("gulp-excludes")
    lazy val forceGulp = SettingKey[Boolean]("force-gulp", "key to enable/disable gulp tasks with force option")
    lazy val gulp = InputKey[Unit]("gulp", "Task to run gulp")
    lazy val gulpBuild = TaskKey[Int]("gulp-dist", "Task to run dist gulp")
    lazy val gulpClean = TaskKey[Unit]("gulp-clean", "Task to run gulp clean")
  }
}

object PlayGulpPlugin extends AutoPlugin {

  val autoImport = Import

  /** This plugin requires the sbt Play plugin to be enabled */
  //override def requires = plugins.JvmPlugin
  override def requires = Play

  /** If all requirements are met, this plugin will automatically get enabled */
  override def trigger = allRequirements

  import autoImport._
  import PlayGulpKeys._

  override lazy val projectSettings: Seq[Setting[_]] = playGulpSettings ++ withTemplates

  /**
   * Main plugin settings which add gulp commands to sbt tasks
   */
  lazy val playGulpSettings: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq("com.github.mmizutani" %% "play-gulp" % "0.0.3" intransitive()),

    // Where does the UI live?
    gulpDirectory <<= (baseDirectory in Compile) {
      _ / "ui"
    },

    gulpFile := "gulpfile.js",

    forceGulp := true,

    // Allow all the specified commands below to be run within sbt in addition to gulp
    commands <++= baseDirectory {
      base =>
        Seq(
          "npm",
          "bower",
          "yo"
        ).map(cmd(_, base))
    },

    gulp := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val isForceEnabled = (forceGulp in Compile).value
      runGulp(base, gulpfileName, Def.spaceDelimited("<arg>").parsed.toList, isForceEnabled).exitValue()
    },

    gulpClean := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val isForceEnabled = (forceGulp in Compile).value
      val result = runGulp(base, gulpfileName, List("clean"), isForceEnabled = isForceEnabled).exitValue()
      if (result != 0) throw new Exception("gulp failed")
    },

    gulpBuild := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val isForceEnabled = (forceGulp in Compile).value
      val result = runGulp(base, gulpfileName, List("build"), isForceEnabled = isForceEnabled).exitValue()
      if (result == 0) {
        result
      } else throw new Exception("gulp failed")
    },

    // Execute `gulp build` before `sbt dist`
    dist <<= dist dependsOn gulpBuild,

    // Execute `gulp build` before `sbt stage`
    stage <<= stage dependsOn gulpBuild,

    // Execute `gulp clean` before `sbt clean`
    clean <<= clean dependsOn gulpClean,

    // Add the views to the dist
    unmanagedResourceDirectories in Assets <+= (gulpDirectory in Compile)(base => base / "dist"),

    // Add asset files in ui/src directory to the watch list for auto browser reloading
    watchSources <++= gulpDirectory map { path => ((path / "src") ** "*").get},
  
    // Run gulp before sbt run
    playRunHooks <+= (gulpDirectory, gulpFile, forceGulp).map {
      (base, fileName, isForceEnabled) => GulpWatch(base, fileName, isForceEnabled)
    }
  )

  val withTemplates = Seq(
    sourceDirectories in TwirlKeys.compileTemplates in Compile ++= Seq(gulpDirectory.value / "src"),
    gulpExcludes <<= gulpDirectory(yd => Seq(
      yd + "/src/app/",
      yd + "/src/assets/",
      yd + "/src/bower_components/"
    )),
    excludeFilter in unmanagedSources <<=
      (excludeFilter in unmanagedSources, gulpExcludes) {
        (currentFilter: FileFilter, ye) =>
          currentFilter || new FileFilter {
            def accept(pathname: File): Boolean = {
              (true /: ye.map(s => pathname.getAbsolutePath.startsWith(s)))(_ && _)
            }
          }
      }
  )

  private def runGulp(base: sbt.File, fileName: String,
                       args: List[String] = List.empty,
                       isForceEnabled: Boolean = true): Process = {
    //println(s"Will run: gulp --gulpfile=$gulpFile $args in ${base.getPath}")

    val arguments = if (isForceEnabled) {
      args ++ List("--force")
    } else args

    if (isForceEnabled)
      println("'force' enabled")
    else
      println("'force' not enabled")

    if (System.getProperty("os.name").startsWith("Windows")) {
      val process: ProcessBuilder = Process("cmd" :: "/c" :: "gulp" :: "--gulpfile=" + fileName :: arguments, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      process.run()
    } else {
      val process: ProcessBuilder = Process("gulp" :: "--gulpfile=" + fileName :: arguments, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      process.run()
    }
  }

  import scala.language.postfixOps

  private def cmd(name: String, base: File): Command = {
    if (!base.exists()) {
      base.mkdirs()
    }
    Command.args(name, "<" + name + "-command>") {
      (state, args) =>
        if (System.getProperty("os.name").startsWith("Windows")) {
          Process("cmd" :: "/c" :: name :: args.toList, base) !<
        } else {
          Process(name :: args.toList, base) !<
        }
        state
    }
  }

  object GulpWatch {

    def apply(base: File, fileName: String, isForceEnabled: Boolean): PlayRunHook = {

      object GulpSubProcessHook extends PlayRunHook {

        var process: Option[Process] = None

        override def beforeStarted(): Unit = {
          process = Some(runGulp(base, fileName, "watch" :: Nil, isForceEnabled))
        }

        override def afterStopped(): Unit = {
          process.foreach(_.destroy())
          process = None
        }
      }

      GulpSubProcessHook
    }

  }

}

