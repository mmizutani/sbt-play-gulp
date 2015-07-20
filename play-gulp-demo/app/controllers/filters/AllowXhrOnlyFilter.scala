package controllers.filters

import controllers.{RequestWithAttributes, StackableFilter}
import play.api.mvc.{Result, Results}

import scala.concurrent.Future

/**
 * XMLHttpRequest のみを受け付けるフィルター
 */
trait AllowXhrOnlyFilter extends StackableFilter with Results {

  private def headerName: String = "X-Requested-With"

  def resultIfNotXhrRequest[A](request: RequestWithAttributes[A]): Future[Result] = {
    Future.successful(BadRequest("Not XMLHttpRequest !!"))
  }

  abstract override def filter[A](request: RequestWithAttributes[A])(f: (RequestWithAttributes[A]) => Future[Result]): Future[Result] = {
    if (isAjaxRequest(request)) {
      super.filter(request)(f)
    } else {
      resultIfNotXhrRequest(request)
    }
  }

  def isAjaxRequest(request: RequestWithAttributes[_]): Boolean = {
    request.headers.get(headerName).isDefined
  }

}
