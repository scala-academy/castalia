package castalia.actors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.Uri
import castalia.StubConfigParser._
import castalia.matcher.RequestMatch
import castalia.model.Model.StubResponse
import scala.concurrent.duration._

class JsonEndpointActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("StubServerSystem"))

  "JsonEndpointActorTest" should {

    "receive" in {
        val uriString = "somepath/2"
        val uri: Uri = uriString
        val jsonConfig = parseStubConfig("jsonsimplestub.json")
        val jsonEndpoint = system.actorOf(Props(new JsonEndpointActor(jsonConfig)))
        //RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params, handler: String)

      within(1000.millis, 1200.millis) {
        jsonEndpoint ! new RequestMatch(uriString, uri.path, List(("1", "1")), Nil, jsonEndpoint)

        expectMsg(StubResponse(200, """{"id":"een","someValue":"123123"}"""))
      }

      within(100.millis, 200.millis) {
        jsonEndpoint ! new RequestMatch(uriString, uri.path, List(("1", "2")), Nil, jsonEndpoint)

        expectMsg(StubResponse(200, """{"id":"twee","someValue":"2222"}"""))
      }
      within(50.millis) {
        jsonEndpoint ! new RequestMatch(uriString, uri.path, List(("1", "3")), Nil, jsonEndpoint)

        expectMsg(StubResponse(200, """{"id":"drie","someValue":"123123"}"""))
      }
    }

    "use default delay from endpoint definition" in {
      val requestString = "somestub/1"
      val jsonConfig = parseStubConfig("sharedproperties.json")
      val endpoint = system.actorOf(Props(new JsonEndpointActor(jsonConfig)))

      within(100.millis, 200.millis) {
        endpoint ! new RequestMatch(requestString, Uri(requestString).path, List(("parm", "1")), Nil, endpoint)

        expectMsg(StubResponse(200, """{"id":"een","someValue":"123123"}"""))
      }
    }

    "use response specific delay from endpoint definition" in {
      val requestString = "somestub/0"
      val jsonConfig = parseStubConfig("sharedproperties.json")
      val endpoint = system.actorOf(Props(new JsonEndpointActor(jsonConfig)))

      within(2000.millis, 2100.millis) {
        endpoint ! new RequestMatch(requestString, Uri(requestString).path, List(("parm", "0")), Nil, endpoint)

        expectMsg(StubResponse(404, ""))
      }
    }

  }
}
