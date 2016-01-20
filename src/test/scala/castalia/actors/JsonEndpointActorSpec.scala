package castalia.actors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.Uri
import castalia.StubConfigParser._
import castalia.matcher.RequestMatch
import castalia.model.Model.StubResponse

class JsonEndpointActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("StubServerSystem"))

  "JsonEndpointActorTest" should {

    "receive" in {
      val uriString = "somepath/2"
      val uri: Uri = uriString
      val jsonConfig = parseStubConfig("jsonsimplestub.json")
      val jsonEndpoint = system.actorOf(Props(new JsonEndpointActor(jsonConfig)))
      //RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params, handler: String)

      val t1 = System.currentTimeMillis()
      jsonEndpoint ! new RequestMatch( uriString, uri.path, List(("$1", "1")), Nil, jsonEndpoint )

      expectMsg(StubResponse(200, """{"id":"een","someValue":"123123"}"""))
      assert(System.currentTimeMillis() > 1000 + t1)
      assert(System.currentTimeMillis() < 1100 + t1)

      val t2 = System.currentTimeMillis()
      jsonEndpoint ! new RequestMatch( uriString, uri.path, List(("$1", "2")), Nil, jsonEndpoint )

      expectMsg(StubResponse(200, """{"id":"twee","someValue":"2222"}"""))
      assert(System.currentTimeMillis() > 100 + t2)
      assert(System.currentTimeMillis() < 200 + t2)

      val t3 = System.currentTimeMillis()
      jsonEndpoint ! new RequestMatch( uriString, uri.path, List(("$1", "3")), Nil, jsonEndpoint )

      expectMsg(StubResponse(200, """{"id":"drie","someValue":"123123"}"""))
      assert(System.currentTimeMillis() < 50 + t3)

    }

  }
}
