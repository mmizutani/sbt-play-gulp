import akka.stream.Materializer
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  implicit lazy val materializer: Materializer = app.materializer

  "Router" should {

    "send 404 on a bad request" in {
      val result = route(app, FakeRequest(GET, "/boum")).get
      status(result) mustEqual NOT_FOUND
    }

  }

  "Application Controller" should {

    "render the yeoman frontend index page" in {
      val home = route(app, FakeRequest(GET, "/ui/")).get
      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Gulp React Browserify")
    }

    "render the old index page" in {
      val oldhome = route(app, FakeRequest(GET, "/oldhome")).get
      status(oldhome) mustBe OK
      contentType(oldhome) mustBe Some("text/html")
      contentAsString(oldhome) must include ("Play Framework")
    }

  }
}