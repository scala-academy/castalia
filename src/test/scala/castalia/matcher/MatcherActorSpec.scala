package castalia.matcher

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.TestProbe
import castalia.actors.ActorSpecBase
import castalia.matcher.MatcherActor.RespondIfMatched
import scala.concurrent.duration._

class MatcherActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("MatcherActor"))

  "MatcherActor" must {

    "forward a match to the match result gatherer" in {
      val gatherer = TestProbe()
      val handler = TestProbe()
      val segments = List("a", "{bparm}", "c")
      val matcherActor = system.actorOf(MatcherActor.props(segments, handler.ref))
      val uriParser = new UriParser()
      val parsedUri = uriParser.parse("/a/123/c")
      val httpRequest = HttpRequest()

      matcherActor ! RespondIfMatched(parsedUri, httpRequest, gatherer.ref)

      handler.expectMsgClass(2.seconds, classOf[RequestMatch])
      handler.reply("HandlerResponse")
      gatherer.expectMsg("HandlerResponse")
    }
  }
}
