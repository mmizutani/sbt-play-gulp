package controllers

import javax.inject.Inject
import play.api.mvc.{ControllerComponents, AbstractController}

class ApplicationController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {
  def index = Action {
    Ok("OK!")
  }
}