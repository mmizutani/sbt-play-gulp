package controllers

import javax.inject.Inject
import play.api.mvc.{ControllerComponents, AbstractController}
import com.github.mmizutani.playgulp.GulpAssets

class ApplicationController @Inject() (cc: ControllerComponents, gulpAssets: GulpAssets) extends AbstractController(cc) {
  def oldhome = Action {
    Ok("OK!")
  }

  def index() = gulpAssets.redirectRoot("/ui/")
}