package castalia

import akka.event.NoLogging
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes.`application/json`

import scala.util.Random

/**
  * Created by Jens Kat on 25-11-2015.
  */
class StubServiceSpec extends ServiceTestBase with StubService {
  override val log = NoLogging
  "A request to the endpoint /stubs/hardcodeddummystub" when {
    "I do a HTTP GET" should {
      "return HTTP status code 200" in {
        Get("/stubs/hardcodeddummystub") ~> stubRoutes ~> check {
          status shouldBe OK
        }
      }
    }
  }

  "A request to a non-existing endpoint" should {
    "result in HTTP status code 404 and handled by the rejectionhandler" in {
      Get("/stubs/nonexistingstub") ~> stubRoutes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "Oh man, what you are looking for is long gone."
      }
    }
  }

  "A HTTP GET request to stubs/dynamicdummystub/default?response={anyString}" should {
    "result in a HTTP 200 response from the stubserver containing a json object with property \"response\" equal to \"{anyString}\"" in {
      val randomString = Random.alphanumeric.take(Random.nextInt(10)).mkString
      Get(s"/stubs/dynamicdummystub/default?response=$randomString") ~> routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[Response] shouldBe Response(randomString)
      }
    }
  }

}
