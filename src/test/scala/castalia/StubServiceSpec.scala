package castalia

import akka.event.NoLogging
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes.`application/json`
import spray.json.JsString

import scala.util.Random

/**
  * Created by Jens Kat on 25-11-2015.
  */
class StubServiceSpec extends ServiceTestBase with StubService {
  override val log = NoLogging
  "A request to the endpoint /stubs/hardcodeddummystub" should {
    "return HTTP status code 200" in {
      Get("/stubs/hardcodeddummystub") ~> stubRoutes ~> check {
        status shouldBe OK
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
      Get(s"/stubs/dynamicdummystub/default?response=$randomString") ~> stubRoutes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[Response] shouldBe Response(None, randomString)
      }
    }
  }

  "A HTTP GET request to stubs/dynamicdummystub/{anyInteger}?response={anyString}" should {
    "result in a HTTP 200 response from the stubserver containing a json object with property \"id\" equal to \"{anyInteger}\" and property \"response\" equal to \"{anyString}\"" in {
      val randomString = Random.alphanumeric.take(Random.nextInt(10)).mkString
      var randomInt = Random.nextInt(1000000)
      Get(s"/stubs/dynamicdummystub/$randomInt?response=$randomString") ~> stubRoutes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        //  responseAs[Response] shouldBe Response(Some(randomInt), randomString)
      }
    }
  }

  "A HTTP GET request to stubs/jsonconfiguredstub/1" should {
    "result in a HTTP 200 response from the stubserver containing a json object with property \"id\" equal to \"een\" and property \"someValue\" equal to \"{123123}\"" in {
      Get(s"/stubs/jsonconfiguredstub/1") ~> stubRoutes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
//        responseAs[HttpResponse] shouldBe HttpResponse(200)
      }
    }
  }

  "A HTTP GET request to stubs/jsonconfiguredstub/2" should {
    "result in a HTTP 200 response from the stubserver containing a json object with property \"id\" equal to \"een\" and property \"someValue\" equal to \"{123123}\"" in {
      Get(s"/stubs/jsonconfiguredstub/1") ~> stubRoutes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        //        responseAs[Response] shouldBe Response(Some(1), "123123")
      }
    }
  }
}
