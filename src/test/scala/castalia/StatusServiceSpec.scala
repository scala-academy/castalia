package castalia

import akka.event.NoLogging
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._

/**
  * Created by Jens Kat on 27-11-2015.
  */
class StatusServiceSpec extends ServiceTestBase {
  val service = new StatusService()

  "A request to the endpoint /" when {
    "I do an HTTP GET" should {
      "Be unhandled" in {
        Get("/") ~> service.routes ~> check {
          handled shouldBe false
        }
      }
    }
  }

  "A request to the endpoint /status" when {
    "I do an HTTP GET" should {
      "return an OK status and a non-empty http body" in {
        Get("/status") ~> service.routes ~> check {
          status shouldBe OK
          contentType shouldBe `application/json`
          responseAs[String].length should be > 0
        }
      }
    }
  }
}
