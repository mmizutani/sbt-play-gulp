package com.github.mmizutani.sbt.gulp

import sbt._
import sbt.Keys._
import play.sbt.{PlayWeb, PlayRunHook}
import play.sbt.PlayImport.PlayKeys._
import play.twirl.sbt.Import.TwirlKeys
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import com.typesafe.sbt.web.Import.Assets

import scala.sys.process.Process

object PlayGulpPlugin extends AutoPlugin {

  // This plugin requires the sbt Play plugin to be enabled
  override def requires = PlayWeb

  // If all requirements are met, this plugin is automatically enabled
  override def trigger = allRequirements

  // by defining autoImport, the settings are automatically imported into user's `*.sbt` when the plugin is enabled
  object autoImport {
    lazy val gulpDirectory =
      SettingKey[File]("gulp-directory", "gulp directory")
    lazy val gulpFile = SettingKey[String]("gulp-file", "gulpfile")
    lazy val gulpExcludes = SettingKey[Seq[String]]("gulp-excludes")
    lazy val gulpForce = SettingKey[Boolean](
      "gulp-force",
      "key to enable/disable gulp tasks with force option")
    lazy val gulp = InputKey[Unit]("gulp", "Task to run gulp")
    lazy val gulpBuild = TaskKey[Int]("gulp-dist", "Task to run dist gulp")
    lazy val gulpClean = TaskKey[Unit]("gulp-clean", "Task to run gulp clean")
  }
  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] =
    super.projectSettings ++ playGulpSettings ++ withTemplates

  // Main plugin settings which add gulp commands to sbt tasks
  lazy val playGulpSettings: Seq[Def.Setting[_]] = Seq(
    // sbt-play-gulp plugin needs play-gulp library of the same version
    libraryDependencies += "com.github.mmizutani" %% "play-gulp" % "0.2.0" exclude ("com.typesafe.play", "play"),
    // Path of the frontend project root
    gulpDirectory := (baseDirectory in Compile) {
      _ / "ui"
    }.value,
    gulpFile := "gulpfile.js",
    gulpForce := false,
    // Allow all the specified commands below to be run within sbt in addition to gulp
    commands ++= baseDirectory { base =>
      Seq(
        "git"
      ).map(cmd(_, base))
    }.value,
    commands ++= gulpDirectory { base =>
      Seq(
        "yo",
        "npm",
        "bower",
        "yarn"
      ).map(cmd(_, base))
    }.value,
    gulp := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val isForceEnabled = (gulpForce in Compile).value
      runGulp(base,
              gulpfileName,
              Def.spaceDelimited("<arg>").parsed.toList,
              isForceEnabled).exitValue()
    },
    gulpClean := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val isForceEnabled = (gulpForce in Compile).value
      val result = runGulp(base,
                           gulpfileName,
                           List("clean"),
                           isForceEnabled = isForceEnabled).exitValue()
      if (result != 0) throw new Exception("gulp failed")
    },
    gulpBuild := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val isForceEnabled = (gulpForce in Compile).value
      val result = runGulp(base,
                           gulpfileName,
                           List("build"),
                           isForceEnabled = isForceEnabled).exitValue()
      if (result == 0) result
      else throw new Exception("gulp failed")
    },
    // Execute `gulp build` before `sbt dist`
    dist := (dist dependsOn gulpBuild).value,
    // Execute `gulp build` before `sbt stage`
    stage := (stage dependsOn gulpBuild).value,
    // Execute `gulp clean` before `sbt clean`
    clean := (clean dependsOn gulpClean).value,
    // Ensures that static assets in the ui/dist directory are packaged into
    // target/scala-2.11/play-gulp_2.11-1.0.0-web-asset.jar/public when the play app is compiled
    unmanagedResourceDirectories in Assets += (gulpDirectory in Compile)(base =>
      base / "dist").value,
    // Add asset files in ui/src directory to the watch list for auto browser
    watchSources ++= (gulpDirectory map { path =>
      ((path / "src") ** "*.scala.*").get
    }).value,
    // Run gulp before sbt run
    playRunHooks += GulpWatch(gulpDirectory.value,
                              gulpFile.value,
                              gulpForce.value)
  )

  val withTemplates = Seq(
    sourceDirectories in TwirlKeys.compileTemplates in Compile ++= Seq(
      gulpDirectory.value / "src"),
    gulpExcludes := gulpDirectory(
      gulpDir =>
        Seq(
          gulpDir + "/src/app/",
          gulpDir + "/src/assets/",
          gulpDir + "/src/bower_components/"
      )).value,
    excludeFilter in unmanagedSources := ((excludeFilter in unmanagedSources).value || new FileFilter {
      def accept(pathname: File): Boolean = {
        (gulpExcludes.value.map(excludePath =>
          pathname.getAbsolutePath.startsWith(excludePath)) foldLeft true)(
          _ && _)
      }
    })
  )

  private def detectGulp(base: sbt.File): String = {
    val globallyInstalledGulp = "gulp"
    val npmInstalledGulp = base / "node_modules" / "gulp" / "bin" / "gulp.js"
    val gulpCandidates: List[sbt.File] = npmInstalledGulp :: Nil
    val maybeGulp = gulpCandidates
      .find(_.exists)
      .map(_.getAbsolutePath)

    maybeGulp
      .getOrElse(globallyInstalledGulp)
  }

  private def runGulp(base: sbt.File,
                      fileName: String,
                      args: List[String] = List.empty,
                      isForceEnabled: Boolean = false,
                      detach: Boolean = false): Process = {
    //println(s"Will run: gulp --gulpfile=$gulpFile $args in ${base.getPath}")

    val arguments = if (isForceEnabled) {
      args ++ List("--force")
    } else args

    if (isForceEnabled)
      println("'force' enabled")
    else
      println("'force' not enabled")

    val gulpExecutable = detectGulp(base)

    val process = if (System.getProperty("os.name").startsWith("Windows")) {
      Process(
        "cmd" :: "/c" :: "node" :: gulpExecutable :: "--gulpfile=" + fileName :: arguments,
        base)
    } else {
      Process(gulpExecutable :: "--gulpfile=" + fileName :: arguments, base)
    }
    println(s"Will run: ${process.toString} in ${base.getPath}")
    val startedProcess = process.run()
    // this will block but only if we don't want to detach (eG watch)
    val mustSucceedAndFailed = !detach && startedProcess.exitValue() != 0
    if (mustSucceedAndFailed)
      throw new java.io.IOException(
        s"$process in ${base.getPath} failed with exit code ${startedProcess.exitValue}")

    startedProcess
  }

  import scala.language.postfixOps

  private def cmd(name: String, base: File): Command = {
    if (!base.exists()) {
      base.mkdirs()
    }
    Command.args(name, "<" + name + "-command>") { (state, args) =>
      if (System.getProperty("os.name").startsWith("Windows")) {
        Process("cmd" :: "/c" :: "node" :: name :: args.toList, base) !<
      } else {
        Process(name :: args.toList, base) !<
      }
      state
    }
  }

  object GulpWatch {
    val Guard = new java.util.concurrent.Semaphore(1, true)

    def apply(base: File,
              fileName: String,
              isForceEnabled: Boolean): PlayRunHook = {

      object GulpSubProcessHook extends PlayRunHook {

        var watchProcess: Option[Process] = None

        override def beforeStarted(): Unit = {
          if (Guard.tryAcquire)
            watchProcess = Some(
              runGulp(base,
                      fileName,
                      "watch" :: Nil,
                      isForceEnabled,
                      detach = true))
          else
            ()
        }

        override def afterStopped(): Unit = {
          watchProcess.foreach { p =>
            Guard.release()
            p.destroy()
          }
          watchProcess = None
        }
      }

      GulpSubProcessHook
    }

  }

}
