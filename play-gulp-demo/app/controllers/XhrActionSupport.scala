package controllers

import controllers.filters.{AllowXhrOnlyFilter, CsrfFilter}

trait XhrActionSupport {

  object GetAction extends StackableAction with CsrfFilter with AllowXhrOnlyFilter

  object PostAction extends StackableAction with CsrfFilter with AllowXhrOnlyFilter

}
