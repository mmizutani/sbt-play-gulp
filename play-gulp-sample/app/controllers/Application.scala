package controllers

import javax.inject._
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

@Singleton
class Application @Inject() extends Controller {
	
  def index = Action {
    Ok(views.html.index("SBT Play Gulp Plugin Demo"))
  }

  def templateDemo = Action {
    Ok(views.html.demo("Scala template in Angular")
      (Html("<div>This is a play scala template in angular views folder which is compiled and used inplace!</div>"))
    )
  }

  def serveTemplate = Action {
    Ok(views.html.templ("Compiled from a scala template!"))
  }

}
