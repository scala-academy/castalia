package castalia.matcher

import akka.actor.{Props, ActorRef, ActorContext, ActorSystem}
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.TestProbe
import castalia.actors.ActorSpecBase
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.matcher.RequestMatcherActor.{FindMatchAndForward, AddMatcher}
import castalia.matcher.types._

class RequestMatcherActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("RequestMatcherActor"))

  trait TestRequestMatcherActorCreator extends RequestMatcherActorCreator {
    val probe = TestProbe("TestRequestMatcherActor")
    def createRequestMatcherActor(context: ActorContext, segments: Segments, handler: ActorRef): ActorRef = {
      probe.ref
    }
  }

  "RequestMatcherActor" must {
    "forwards requests to the registered matcher (single matcher)" in new TestRequestMatcherActorCreator {
      val origin = ActorRef.noSender
      val requestMatcherActor = system.actorOf(Props(new RequestMatcherActor with TestRequestMatcherActorCreator))
      val segments = List()
      val handler = ActorRef.noSender

      requestMatcherActor ! AddMatcher(segments, handler)
      requestMatcherActor ! FindMatchAndForward(HttpRequest(), origin)

      probe.expectMsgClass(classOf[RespondIfMatched]) // Probe not receiving anything???
    }
  }
}