package com.github.mmizutani.playgulp

import javax.inject._

import play.api._
import play.api.mvc._
import controllers.Assets
import java.io.File

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._

@Singleton
class Gulp @Inject() (env: play.api.Environment,
                      assets: Assets,
                      devAssets: DevAssets) extends Controller {
  def index = Action.async {
    request =>
      if (request.path.endsWith("/")) {
        at("index.html").apply(request)
      } else {
        Future(Redirect(request.path + "/"))
      }
  }

  def redirectRoot(base: String = "/ui/") = Action {
    request =>
      if (base.endsWith("/")) {
        Redirect(base)
      } else {
        Redirect(base + "/")
      }
  }

  def assetHandler(file: String): Action[AnyContent] = {
    assets.at("/public", file)
  }

  lazy val atHandler: String => Action[AnyContent] = if (env.mode == Mode.Prod) assetHandler(_: String) else devAssets.assetHandler(_: String)

  def at(file: String): Action[AnyContent] = atHandler(file)
}

@Singleton
class DevAssets @Inject() (env: play.api.Environment,
                           conf: play.api.Configuration) extends Controller {
  // paths to the grunt compile directory or else the application directory
  // in descending order of importance
  val runtimeDirs = conf.underlying.getStringList("gulp.devDirs")
  val basePaths: List[java.io.File] = runtimeDirs match {
    case List() => List(
      env.getFile("ui/.tmp/serve"),
      env.getFile("ui/src"),
      env.getFile("ui")
    )
    case dirs => dirs.map(env.getFile).toList
  }

  /**
   * Construct the temporary and real path under the application.
   *
   * The play application path is prepended to the full path, to make sure the
   * absolute path is in the correct SBT sub-project.
   */
  def assetHandler(fileName: String): Action[AnyContent] = Action {
    // generate a non-strict (lazy) list of the full paths
    val targetPaths = basePaths.view map {
      new File(_, fileName)
    }

    // take the files that exist and generate the response that they would return
    val responses = targetPaths filter {
      file =>
        file.exists()
    } map {
      file =>
        Ok.sendFile(file, inline = true).withHeaders(CACHE_CONTROL -> "no-store")
    }

    // return the first valid path, return NotFound if no valid path exists
    responses.headOption getOrElse NotFound
  }
}