package castalia.matcher

import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.{TestActor, TestProbe}
import castalia.actors.ActorSpecBase
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.matcher.RequestMatcherActor.{AddMatcher, FindMatchAndForward}
import castalia.matcher.types._

class RequestMatcherActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("RequestMatcherActor"))

  trait TestCaseSetup {
    val probe = TestProbe("TestRequestMatcherActor")
    val requestMatcherActor = system.actorOf(Props(new RequestMatcherActor with TestRequestMatcherActorCreator))
    trait TestRequestMatcherActorCreator extends RequestMatcherActorCreator {
      def createRequestMatcherActor(context: ActorContext, segments: Segments, handler: ActorRef): ActorRef = {
        probe.ref
      }
    }
  }

  "RequestMatcherActor" must {
    "forwards requests to the registered matcher (single matcher)" in new TestCaseSetup {
      val origin = ActorRef.noSender
      val segments = List()
      val handler = ActorRef.noSender

      requestMatcherActor ! AddMatcher(segments, handler)
      requestMatcherActor ! FindMatchAndForward(HttpRequest(), origin)

      probe.expectMsgClass(classOf[RespondIfMatched]) // Probe not receiving anything???
    }
  }
}