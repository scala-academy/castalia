package castalia.actors

import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.model.Uri
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import castalia.matcher.RequestMatch
import castalia.model.Messages.Done
import castalia.model.Model.StubResponse
import org.scalatest._
import castalia.StubConfigParser._

import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

/**
  * Created by Jean-Marc van Leerdam on 2016-01-16
  */
class JsonEndpointActorTest(_system: ActorSystem) extends TestKit(_system)
with ImplicitSender
with WordSpecLike
with Matchers
with BeforeAndAfterAll {

  def this() = this(ActorSystem("StubServerSystem"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private implicit val timeout = Timeout(2, TimeUnit.SECONDS)

  "JsonEndpointActorTest" should {

    "receive" in {
      val uriString = "somepath/2"
      val uri: Uri = uriString
      val jsonConfig = parseStubConfig("jsonsimplestub.json")
      val jsonEndpoint = system.actorOf(Props(new JsonEndpointActor(jsonConfig)))
      //RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params, handler: String)

      val t1 = System.currentTimeMillis()
      jsonEndpoint ! new RequestMatch( uriString, uri.path, List(("$1", "1")), Nil, "bar" )

      expectMsg(StubResponse(200, """{"id":"een","someValue":"123123"}"""))
      assert(System.currentTimeMillis() > 1000 + t1)
      assert(System.currentTimeMillis() < 1100 + t1)

      val t2 = System.currentTimeMillis()
      jsonEndpoint ! new RequestMatch( uriString, uri.path, List(("$1", "2")), Nil, "bar" )

      expectMsg(StubResponse(200, """{"id":"twee","someValue":"2222"}"""))
      assert(System.currentTimeMillis() > 100 + t2)
      assert(System.currentTimeMillis() < 200 + t2)

    }

  }
}
