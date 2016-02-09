package castalia.matcher

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpProtocols, HttpRequest}
import akka.testkit.TestProbe
import castalia.actors.ActorSpecBase
import org.scalatest.BeforeAndAfterEach

/**
  * Created by Jean-Marc van Leerdam on 2016-01-09
  */
class RequestMatcherTest(_system: ActorSystem) extends ActorSpecBase(_system) with BeforeAndAfterEach {

  def this() = this(ActorSystem("RequestMatcherTest"))

  private var uriMatcher: RequestMatcher = _
  private var a1 = TestProbe().ref
  private var a2 = TestProbe().ref

  override def beforeEach() {
    val s1 = List("sample", "path", "with", "{partyId}", "id")
    val s2 = List("another", "path", "without", "id")

    val m1 = new Matcher(s1, a1)
    val m2 = new Matcher(s2, a2)

    val matchers = List(m1, m2)

    uriMatcher = new RequestMatcher(matchers)
  }

  "RequestMatcherTest" should {

    "not find a match for an incorrect Request" in {

      val r1 = uriMatcher.matchRequest(new HttpRequest(method = HttpMethods.GET, uri = "foo", protocol = HttpProtocols.`HTTP/1.1` ))
      r1.shouldBe(None)

    }

    "find a match for a correct Request" in {
      val r1 = uriMatcher.matchRequest(new HttpRequest(method = HttpMethods.GET, uri = "http://localhost:1234/sample/path/with/12/id?p1=foo&p2=bar", protocol = HttpProtocols.`HTTP/1.1` ))
      r1.get.handler.shouldBe(a1)

      val pathParms = r1.get.pathParams

      pathParms.length.shouldBe(1)
      pathParms.head._1.shouldBe("partyId")
      pathParms.head._2.shouldBe("12")
    }

    "find a match for another correct Request" in {
      val r1 = uriMatcher.matchRequest(new HttpRequest(method = HttpMethods.GET, uri = "http://localhost:1234/another/path/without/id?p1=foo&p2=bar", protocol = HttpProtocols.`HTTP/1.1` ))
      r1.get.handler.shouldBe(a2)
      val pathParms = r1.get.pathParams
      pathParms.isEmpty.shouldBe(true)
    }
  }
}
