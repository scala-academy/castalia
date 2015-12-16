package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.EventFilter
import castalia.model.ResponseConfig
import com.typesafe.config.ConfigFactory
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

/**
  * Created by Jens Kat on 25-11-2015.
  */
class StubServiceSpec extends ServiceTestBase with Protocol with SprayJsonSupport {

  val stubsByEndpoints = StubConfigParser.readAndParseStubConfigFiles(List("jsonconfiguredstub.json"))
  val service = new StubService(stubsByEndpoints)

  "A request to a non-existing endpoint" should {
    "result in HTTP status code 404 and handled by the rejectionhandler" in {
      Get("/stubs/nonexistingstub") ~> service.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "Oh man, what you are looking for is long gone."
      }
    }
  }

  "A HTTP GET request to stubs/jsonconfiguredstub/0" should {
    "result in a HTTP 404 response from the stubserver" in {
      Get(s"/stubs/jsonconfiguredstub/0") ~> service.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe empty
      }
    }
  }

  "A HTTP GET request to stubs/jsonconfiguredstub/1" should {
    "result in a HTTP 200 response from the stubserver containing a json " +
      "object with property \"id\" equal to \"een\" and property \"someValue\" " +
      "equal to \"{123123}\"" in {
      Get(s"/stubs/jsonconfiguredstub/1") ~> service.routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[String].parseJson.convertTo[AnyJsonObject] shouldBe Some(Map("id" -> JsString("een"),
                                                                                "someValue" -> JsString("123123")))
      }
    }
  }

  "A HTTP GET request to stubs/jsonconfiguredstub/2" should {
    "result in a HTTP 200 response from the stubserver containing a json object" +
      " with property \"id\" equal to \"twee\" and property \"someValue\" equal to " +
      "\"{123123}\" and property someAdditionalValue\" equal to \"345345" in {
      Get(s"/stubs/jsonconfiguredstub/2") ~> service.routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
      }
    }
  }

  "A HTTP GET request to a non-described endpoint in stubs/jsonconfiguredstub/?" should {
    "result in a HTTP 501 response from the stubserver" in {
      Get("/stubs/jsonconfiguredstub/idontexist") ~> service.routes ~> check {
        status shouldBe NotImplemented
        responseAs[String] shouldBe "Unknown response"
      }
    }
  }

  "An empty list of static responses and map of dynamic responses" should {
    implicit val system = ActorSystem("StubServiceSpecSystem", ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]"""))


    "result in a log message at info of \"No stubConfigs given\"" in {
      val stubService = new StubService(Map.empty)

      EventFilter.info(message = "No StubConfigs given", occurrences = 1) intercept {
        Get("/stubs/static") ~> stubService.routes ~> check {
          handled shouldBe true
          status shouldBe NotFound
        }
      }

    }
  }

  "A HTTP POST request to a endpoint described in /responses" should {
    "result in a HTTP 200 response from the stubserver" in {
      Post("/stubs/jsonconfiguredstub/responses", ResponseConfig("1", None, 200, None)) ~> service.routes ~> check {
        status shouldBe OK
      }
    }
  }
}
