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
import castalia.model.CastaliaConfig._
import castalia.model.Messages.{UpsertResponse, Done, UpsertEndpoint}
import castalia.model.Model.{ResponseConfig, EndpointResponseConfig, StubResponse}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfter
import spray.json.JsString

import scala.concurrent.{ExecutionContext, Future}

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

    "receives UpsertResponse message" should {
      "forward it to a correct endpoint actor and eventually reply with Done" in {
        //TODO: how to test the forwarding? will have to provide endpointMatcher to receptionist somehow
        val responseConfig = EndpointResponseConfig("doublepathparam/$1/responsedata/$2",
          ResponseConfig( ids = Some(Map("1" -> "1","2" -> "id1")),
                          delay = None,
                          httpStatusCode = 200,
                          response = Some(Map("id" -> JsString("newId")))))
        receptionist ! UpsertResponse(responseConfig)
        expectMsg(Done(responseConfig.endpoint))
      }
    }

    "receives a request to an existing endpoint " should {
        "forward the request to the endpoint and get a configured 200 response" in {
    "receives a request to an existing endpoint " should {
        "forward the request to the endpoint and get a 200 response" in {
          val r = HttpRequest(HttpMethods.GET, "/doublepathparam/1/responsedata/id2" )
          receptionist ! r
          expectMsg(StubResponse(OK.intValue, "{\"id\":\"twee\",\"someValue\":\"123123\",\"someAdditionalValue\":\"345345\"}"))
          // todo: rewrite into converting the response to json, unmarshal it and inspect.
        }

        "forward the request to the endpoint and get a configured 404 response" in {
          val r = HttpRequest(HttpMethods.GET, "/doublepathparam/0/responsedata/notfound" )
          receptionist ! r
          expectMsg(StubResponse(NotFound.intValue, ""))
        }

        "forward the request to the endpoint and get a configured 503 response" in {
          val r = HttpRequest(HttpMethods.GET, "/doublepathparam/0/responsedata/internalerror" )
          receptionist ! r
          expectMsg(StubResponse(ServiceUnavailable.intValue, ""))
        }
      }

    "receives a request to get metrics for endpoints" should {
      "forward the request to the metrics collector" in {
        val metrics = Map("endpoint" -> Map("calls" -> 1))
        metricsCollectorProbe.setAutoPilot(new AutoPilot {
          override def run(sender: ActorRef, msg: Any): AutoPilot = msg match{
            case EndpointMetricsGet => sender ! EndpointMetrics(metrics)
              NoAutoPilot
          }
        })

        receptionist ! EndpointMetricsGet
        expectMsg(EndpointMetrics(metrics))
      }
    }



    "receives a request to a non-existing endpoint" should {
      "return HTTP status code 404" in {
        val r = HttpRequest(HttpMethods.GET, "/nonexistingstub" )
        receptionist ! r
        expectMsg(StubResponse(NotFound.intValue, NotFound.reason))
      }
    }

  }



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
