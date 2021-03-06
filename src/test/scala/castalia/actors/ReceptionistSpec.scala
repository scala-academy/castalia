package castalia.actors

//
//import akka.actor.ActorSystem
import akka.actor.ActorDSL._
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.testkit.TestActor.{NoAutoPilot, AutoPilot}
import akka.testkit.TestProbe
import castalia.StubConfigParser._
import castalia._
import castalia.model.Messages.{EndpointMetricsInit, EndpointMetricsGet, Done, UpsertEndpoint}
import castalia.model.Model.{EndpointMetrics, StubResponse}

class ReceptionistSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("ReceptionistSpec"))

  val metricsCollectorProbe = new TestProbe(_system)
  val receptionist = actor("receptionist")(new Receptionist() {
    override def createMetricsCollector(): ActorRef = metricsCollectorProbe.ref
  })

  val stubConfig = parseStubConfigs(List("jsonconfiguredstub.json")).head

  override def beforeAll: Unit = {
    receptionist ! UpsertEndpoint(stubConfig)
    expectMsg(Done(stubConfig.endpoint))
  }

  "Receptionist actor" when {
    "receives UpsertEndpoint message" should {
      "process it and reply with Done" in {
        receptionist ! UpsertEndpoint(stubConfig)
        expectMsg(Done(stubConfig.endpoint))
      }
    }

    "receives a request to an existing endpoint " should {
      "forward the request to the endpoint and get a 200 response" in {
          val r = HttpRequest(HttpMethods.GET, "/doublepathparam/1/responsedata/id2" )
          receptionist ! r
          expectMsg(StubResponse(OK.intValue, "{\"id\":\"twee\",\"someValue\":\"123123\",\"someAdditionalValue\":\"345345\"}"))
          // todo: rewrite into converting the response to json, unmarshal it and inspect.
      }
    }

    "receives a request to get metrics for endpoints" should {
      "forward the request to the metrics collector" in {
        val metrics = Map("endpoint" -> Map("calls" -> 1))
        metricsCollectorProbe.setAutoPilot(new AutoPilot {
          override def run(sender: ActorRef, msg: Any): AutoPilot = msg match{
            case EndpointMetricsGet(None) => sender ! EndpointMetrics(metrics)
              NoAutoPilot
          }
        })

        receptionist ! EndpointMetricsGet(None)
        expectMsg(EndpointMetrics(metrics))
      }
    }


/*
    "receives a request to a non-existing endpoint" should {
      "return HTTP status code 404" in {
        val r = HttpRequest(HttpMethods.GET, "/nonexistingstub" )
        receptionist ! r
        expectMsg(StubResponse(NotFound.intValue, "From receptionist " + NotFound.reason))
      }
    }

    "receives a request to an existing endpoint " should {
      "forward the request to the endpoint and get a 404" in {
        val r = HttpRequest(HttpMethods.GET, "/doublepathparam/0/responsedata/notfound" )
        receptionist ! r
        expectMsg(StubResponse(NotFound.intValue, ""))
      }
    }
*/

  }



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
}
