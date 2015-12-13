package castalia

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes._
import spray.json._

/**
  * Created by Jens Kat on 25-11-2015.
  */
class StubServiceSpec extends ServiceTestBase with Protocol{
//  override val log = NoLogging
  val stubsByEndpoints = StubConfigParser.readAndParseStubConfigFiles(Array("castalia.json"))
  val service = new StubService(stubsByEndpoints)
  "A request to the endpoint /stubs/hardcodeddummystub" should {
    "return HTTP status code 200" in {
      Get("/stubs/hardcodeddummystub") ~> service.routes ~> check {
        status shouldBe OK
      }
    }
  }

  "A request to a non-existing endpoint" should {
    "result in HTTP status code 404 and handled by the rejectionhandler" in {
      Get("/stubs/nonexistingstub") ~> service.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "Oh man, what you are looking for is long gone."
      }
    }
  }

  "A HTTP GET request to stubs/jsonconfiguredstub/1" should {
    "result in a HTTP 200 response from the stubserver containing a json object with property \"id\" equal to \"een\" and property \"someValue\" equal to \"{123123}\"" in {
      Get(s"/stubs/jsonconfiguredstub/1") ~> service.routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[String].parseJson.convertTo[AnyJsonObject] shouldBe Some(Map("id" -> JsString("een"),
                                                                                "someValue" -> JsString("123123")))
      }
    }
  }

  "A HTTP GET request to stubs/jsonconfiguredstub/2" should {
    "result in a HTTP 200 response from the stubserver containing a json object with property \"id\" equal to \"twee\" and property \"someValue\" equal to \"{123123}\" and property someAdditionalValue\" equal to \"345345" in {
      Get(s"/stubs/jsonconfiguredstub/2") ~> service.routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
//        responseAs[String].parseJson.convertTo[AnyJsonObject] shouldBe Some(Map("id" -> "twee",
//                                                                                "someValue" -> "123123",
//                                                                                "someAdditionalValue" -> "345345"))
      }
    }
  }
}
