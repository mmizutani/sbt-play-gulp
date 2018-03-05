package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.routing.{
  JavaScriptReverseRoute,
  JavaScriptReverseRouter,
  Router
}
import play.twirl.api.Html
import com.github.mmizutani.playgulp.GulpAssets

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ApplicationController @Inject()(cc: ControllerComponents,
                                      env: Environment,
                                      gulpAssets: GulpAssets,
                                      router: Provider[Router])
    extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def oldhome = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  // Collects JavaScript routes using reflection
  val routeCache: Array[JavaScriptReverseRoute] = {
    val jsRoutesClass = classOf[_root_.controllers.routes.javascript]
    for {
      controller <- jsRoutesClass.getFields.map(_.get(null))
      method <- controller.getClass.getDeclaredMethods
      if method.getReturnType == classOf[JavaScriptReverseRoute]
    } yield method.invoke(controller).asInstanceOf[JavaScriptReverseRoute]
  }

  /**
    * Returns the JavaScript router that the client can use for "type-safe" routes.
    * @param varName The name of the global variable, defaults to `jsRoutes`
    */
  def jsRoutes(varName: String = "jsRoutes") = Action { implicit request =>
    Ok(JavaScriptReverseRouter(varName)(routeCache: _*)).as(JAVASCRIPT)
  }

  val demo = true

  /**
    * Returns a list of all the HTTP action routes for easier debugging
    */
  def routes = Action { request =>
    if (env.mode == Mode.Prod && !demo)
      NotFound
    else
      Ok(views.html.devRoutes(request.method, request.uri, Some(router.get)))
  }
}
