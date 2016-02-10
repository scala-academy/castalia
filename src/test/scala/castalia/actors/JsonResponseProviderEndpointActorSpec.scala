package castalia.actors

import akka.actor.Status.Failure
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{HttpMethods, HttpProtocols, HttpRequest}
import akka.testkit.TestProbe
import castalia.StubConfigParser._
import castalia.matcher.RequestMatch
import castalia.model.Messages.{EndpointCalled, EndpointMetricsInit}
import castalia.model.Model.StubResponse

class JsonResponseProviderEndpointActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("StubServerSystem"))

  "JsonResponseProviderEndpointActorTest" should {

    val metricsCollector = new TestProbe(_system)

    "execute successfully with an response" in {
      val httpRequest = new HttpRequest(method = HttpMethods.GET, uri = "somepath/1/with/2", protocol = HttpProtocols.`HTTP/1.1`)
      val jsonConfig = parseStubConfig("jsonprogrammedstub1.json")
      val jsonEndpoint = system.actorOf(Props(
        new JsonResponseProviderEndpointActor(jsonConfig.endpoint, jsonConfig.responseprovider.get, metricsCollector.ref)))

      jsonEndpoint ! new RequestMatch(httpRequest, List("1" -> "1", "2" -> "2"), Nil)

      expectMsg(StubResponse(200, """{"result":"1 with 2"}"""))

      metricsCollector.expectMsg(EndpointMetricsInit(jsonConfig.endpoint))
      metricsCollector.expectMsg(EndpointCalled(jsonConfig.endpoint))
    }

    "execute successfully with an exception" in {
      val httpRequest = new HttpRequest(method = HttpMethods.GET, uri = "somepath/3/with/4", protocol = HttpProtocols.`HTTP/1.1`)
      val jsonConfig = parseStubConfig("jsonprogrammedstub2.json")
      val jsonEndpoint = system.actorOf(Props(
        new JsonResponseProviderEndpointActor(jsonConfig.endpoint, jsonConfig.responseprovider.get, metricsCollector.ref)))

      jsonEndpoint ! new RequestMatch(httpRequest, List("1" -> "3", "2" -> "4"), Nil)
      expectMsgClass(classOf[Failure])
      //expectMsg(Failure(new Exception("some expected failure")))
    }
  }
}
