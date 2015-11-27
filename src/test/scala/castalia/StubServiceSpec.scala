package castalia

import akka.event.NoLogging
import akka.http.scaladsl.model.StatusCodes._

/**
  * Created by Jens Kat on 25-11-2015.
  */
class StubServiceSpec extends ServiceTestBase with StubService {
  override val log = NoLogging
  "A request to the endpoint /stubs/hardcodeddummystub" when {
    "I do a HTTP GET" should {
      "return HTTP status code 200" in {
        Get("/stubs/hardcodeddummystub") ~> routes ~> check {
          status shouldBe OK
        }
      }
    }
  }

  "A request to a non-existing endpoint" should {
    "result in HTTP status code 404 and handled by the rejectionhandler" in {
      Get("/stubs/nonexistingstub") ~> routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "Oh man, what you are looking for is long gone."
      }
    }
  }
}
