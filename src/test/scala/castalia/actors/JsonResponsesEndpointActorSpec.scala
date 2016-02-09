package castalia.actors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{HttpMethods, HttpProtocols, HttpRequest}
import akka.testkit.TestProbe
import castalia.StubConfigParser._
import castalia.matcher.RequestMatch
import castalia.model.Model.StubResponse

import scala.concurrent.duration._

class JsonResponsesEndpointActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("StubServerSystem"))

  "JsonResponsesEndpointActorTest" should {

    val metricsCollector = new TestProbe(_system)

    "receive" in {
        val httpRequest = new HttpRequest(method = HttpMethods.GET, uri = "somepath/2", protocol = HttpProtocols.`HTTP/1.1` )
        val jsonConfig = parseStubConfig("jsonsimplestub.json")
        val jsonEndpoint = system.actorOf(Props(new JsonResponsesEndpointActor(jsonConfig.endpoint, jsonConfig.responses.get, metricsCollector.ref)))
        //RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params, handler: String)

      within(1000.millis, 1200.millis) {
        jsonEndpoint ! new RequestMatch(httpRequest, List(("1", "1")), Nil, jsonEndpoint)

        expectMsg(StubResponse(200, """{"id":"een","someValue":"123123"}"""))
      }

      within(100.millis, 200.millis) {
        jsonEndpoint ! new RequestMatch(httpRequest, List(("1", "2")), Nil, jsonEndpoint)

        expectMsg(StubResponse(200, """{"id":"twee","someValue":"2222"}"""))
      }
      within(50.millis) {
        jsonEndpoint ! new RequestMatch(httpRequest, List(("1", "3")), Nil, jsonEndpoint)

        expectMsg(StubResponse(200, """{"id":"drie","someValue":"123123"}"""))
      }
    }

    "use default delay from endpoint definition" in {
      val httpRequest = new HttpRequest(method = HttpMethods.GET, uri = "somestub/1", protocol = HttpProtocols.`HTTP/1.1` )
      val jsonConfig = parseStubConfig("sharedproperties.json")
      val endpoint = system.actorOf(Props(new JsonResponsesEndpointActor(jsonConfig.endpoint, jsonConfig.responses.get, metricsCollector.ref)))

      within(100.millis, 200.millis) {
        endpoint ! new RequestMatch(httpRequest, List(("parm", "1")), Nil, endpoint)

        expectMsg(StubResponse(200, """{"id":"een","someValue":"123123"}"""))
      }
    }

    "use response specific delay from endpoint definition" in {
      val httpRequest = new HttpRequest(method = HttpMethods.GET, uri = "somestub/0", protocol = HttpProtocols.`HTTP/1.1` )
      val jsonConfig = parseStubConfig("sharedproperties.json")
      val endpoint = system.actorOf(Props(new JsonResponsesEndpointActor(jsonConfig.endpoint, jsonConfig.responses.get, metricsCollector.ref)))

      within(2000.millis, 2100.millis) {
        endpoint ! new RequestMatch(httpRequest, List(("parm", "0")), Nil, endpoint)

        expectMsg(StubResponse(404, ""))
      }
    }

  }
}
