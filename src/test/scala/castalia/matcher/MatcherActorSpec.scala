package castalia.matcher

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.TestProbe
import castalia.Main
import castalia.actors.ActorSpecBase
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.metrics.MetricsCollectorActor
import castalia.metrics.MetricsCollectorActor._
import castalia.model.Messages._
import castalia.model.Model.EndpointMetrics
import org.scalamock.scalatest.MockFactory
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
      //      handler.expectMsg(new RequestMatch(httpRequest, List(("bparm","123")), List(), handler.ref), 2 seconds)
      gatherer.expectMsg("HandlerResponse")
    }
  }
}
