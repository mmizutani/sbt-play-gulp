package controllers

import javax.inject._
import play.api._
import play.api.mvc.{Action, Controller}
import play.api.routing.{JavaScriptReverseRoute, JavaScriptReverseRouter, Router}
import com.github.mmizutani.playgulp.GulpAssets

@Singleton
class Application(env: Environment,
                  gulpAssets: GulpAssets,
                  router: => Option[Router] = None) extends Controller {

  // Router needs to be wrapped by Provider to avoid circular dependency when doing dependency injection
  @Inject
  def this(env: Environment, gulpAssets: GulpAssets, router: Provider[Router]) =
    this(env, gulpAssets, Some(router.get))

  def index = gulpAssets.redirectRoot("/ui/")

  def oldhome = Action {
    Ok(views.html.index("Play Framework"))
  }

  // Collects JavaScript routes using reflection
  val routeCache: Array[JavaScriptReverseRoute] = {
    val jsRoutesClass = classOf[controllers.routes.javascript]
    for {
      controller <- jsRoutesClass.getFields.map(_.get(null))
      method <- controller.getClass.getDeclaredMethods if method.getReturnType == classOf[JavaScriptReverseRoute]
    } yield method.invoke(controller).asInstanceOf[JavaScriptReverseRoute]
  }

  /**
    * Returns the JavaScript router that the client can use for "type-safe" routes.
    * @param varName The name of the global variable, defaults to `jsRoutes`
    */
  def jsRoutes(varName: String = "jsRoutes") = Action { implicit request =>
    Ok(JavaScriptReverseRouter(varName)(routeCache: _*)).as(JAVASCRIPT)
  }

  /**
    * Returns true if this app is running as a demo on the herokuapp.com domain.
    */
  def onHerokuDomain(fqdn: String): Boolean = {
    val regex = """.*?([^\.]+)\.(?:com)$""".r
    fqdn match {
      case regex(domain) if domain == "herokuapp" => true
      case _ => false
    }
  }

  /**
    * Returns a list of all the HTTP action routes for easier debugging.
    */
  def routes = Action { request =>
    if (env.mode == Mode.Dev || env.mode == Mode.Test || onHerokuDomain(request.domain))
      Ok(views.html.devRoutes(request.method, request.uri, Some(router.get)))
    else
      NotFound
  }

}
