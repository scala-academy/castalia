package castalia.actors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{HttpProtocols, HttpMethods, HttpRequest, Uri}
import castalia.StubConfigParser._
import castalia.matcher.RequestMatch
import castalia.model.Model.StubResponse
import scala.concurrent.duration._

class JsonEndpointActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("StubServerSystem"))

  "JsonResponsesEndpointActorTest" should {

    "receive" in {
        val httpRequest = new HttpRequest(method = HttpMethods.GET, uri = "somepath/2", protocol = HttpProtocols.`HTTP/1.1` )
        val jsonConfig = parseStubConfig("jsonsimplestub.json")
        val jsonEndpoint = system.actorOf(Props(new JsonResponsesEndpointActor(jsonConfig)))
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

  }

  "JsonResponseProviderEndpointActorTest" should {

    "receive" in {
      val httpRequest = new HttpRequest(method = HttpMethods.GET, uri = "somepath/1/with/2", protocol = HttpProtocols.`HTTP/1.1` )
      val jsonConfig = parseStubConfig("jsonprogrammedstub.json")
      val jsonEndpoint = system.actorOf(Props(new JsonResponseProviderEndpointActor(jsonConfig)))
      //RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params, handler: String)

      within(0.millis, 200.millis) {
        jsonEndpoint ! new RequestMatch(httpRequest, List("1"->"1", "2"->"2"), Nil, jsonEndpoint)

        expectMsg(StubResponse(200, """{"result":"1 with 2"}"""))
      }
    }

  }
}
