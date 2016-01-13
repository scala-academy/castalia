//package castalia
//
//import akka.actor.ActorSystem
//import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
//import akka.http.scaladsl.model.ContentTypes.`application/json`
//import akka.http.scaladsl.model.StatusCodes._
//import akka.testkit.EventFilter
//import castalia.StubConfigParser._
//import castalia.model.CastaliaConfig._
//import castalia.model.ResponseConfig
//import com.typesafe.config.ConfigFactory
//import spray.json._
//
///**
//  * Created by Jens Kat on 25-11-2015.
//  */
//// TODO reuse these tests for Receptionist
//class StubServiceSpec extends ServiceTestBase with SprayJsonSupport {
//
//  val responseByEndpoint = parseStubConfigs(List("jsonconfiguredstub.json"))
//  val service = new StubService(responseByEndpoint)
//
//  "A request to a non-existing endpoint" should {
//    "result in HTTP status code 404" in {
//      Get("/stubs/nonexistingstub") ~> service.routes ~> check {
//        status shouldBe NotFound
//        responseAs[String] shouldBe "Not Found"
//      }
//    }
//  }
//
//  "A HTTP GET request to '/stubs/doublepathparam/0/responsedata/notfound' " should {
//    "result in a HTTP 404 response from the stubserver" in {
//      Get(s"/stubs/doublepathparam/0/responsedata/notfound") ~> service.routes ~> check {
//        status shouldBe NotFound
//        responseAs[String] shouldBe empty
//      }
//    }
//  }
//
//
//  "A HTTP GET request to '/stubs/doublepathparam/0/responsedata/internalerror' " should {
//    "result in a HTTP 503 response from the stubserver and related response" in {
//      Get(s"/stubs/doublepathparam/0/responsedata/internalerror") ~> service.routes ~> check {
//        status shouldBe ServiceUnavailable
//        responseAs[String] shouldBe empty
//      }
//    }
//  }
//
//  "A HTTP GET request to '/stubs/doublepathparam/1/responsedata/id1' " should {
//    "result in a HTTP 200 response from the stubserver and related response" in {
//      Get(s"/stubs/doublepathparam/1/responsedata/id1") ~> service.routes ~> check {
//        status shouldBe OK
//        contentType shouldBe `application/json`
//        responseAs[String].parseJson.convertTo[AnyJsonObject] shouldBe
//          Some(Map("id" -> JsString("een"), "someValue" -> JsString("123123")))
//      }
//    }
//  }
//
//  "An empty list of static responses and map of dynamic responses" should {
//    implicit val system = ActorSystem("StubServiceSpecSystem", ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]"""))
//
//    "result in a log message at info of 'No stubConfigs given' " in {
//      val stubService = new StubService(List.empty)
//
//      EventFilter.info(message = "No StubConfigs given", occurrences = 1) intercept {
//        Get("/stubs/static") ~> stubService.routes ~> check {
//          handled shouldBe true
//          status shouldBe NotFound
//        }
//      }
//
//    }
//  }
//
//  "Duplicated endpoints configured" should {
//    val duplicatedStubConfigs = parseStubConfigs(parse("multiple-same-endpoints-config.json").stubs)
//    "result in an IllegalArgumentException" in {
//      intercept[IllegalArgumentException] {
//        new StubService(duplicatedStubConfigs)
//      }
//    }
//  }
//
//  "A HTTP POST request to a endpoint described in /responses" should {
//    "result in a HTTP 200 response from the stubserver" in {
//      Post("/stubs/jsonconfiguredstub/more/responses",
//        ResponseConfig(None, None, 200, Some(Map("someValue" -> JsString("123123"))))) ~> service.routes ~> check {
//        status shouldBe OK
//        responseAs[String] shouldBe empty
//      }
//    }
//  }
//}
