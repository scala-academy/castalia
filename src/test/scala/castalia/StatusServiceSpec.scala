package castalia

import akka.event.NoLogging
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._

/**
  * Created by Jens Kat on 27-11-2015.
  */
class StatusServiceSpec extends ServiceTestBase {
//  override val log = NoLogging
  val service = new StatusService()
  "Return no body when asking /" in {
    Get("/") ~> service.routes ~> check {
      handled shouldBe false
    }
  }

  "A request to the endpoint /status" when {
    "I do a HTTP GET" should {
      "return a non-empty http body" in {
        Get("/status") ~> service.routes ~> check {
          status shouldBe OK
          contentType shouldBe `application/json`
          responseAs[String].length should be > 0
        }
      }
    }
  }
}
