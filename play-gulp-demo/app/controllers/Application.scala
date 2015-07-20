package controllers

import play.api.mvc.{Action, Controller}

class Application extends Controller {
  def index = Action {
    Ok(views.html.main("Play Gulp Demo"))
  }
}