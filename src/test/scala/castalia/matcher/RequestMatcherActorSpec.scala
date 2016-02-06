package castalia.matcher

import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.TestProbe
import castalia.actors.ActorSpecBase
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.matcher.RequestMatcherActor.{AddMatcher, FindMatchAndForward}
import castalia.matcher.types._
import scala.concurrent.duration._

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
    "forward requests to the registered matcher (single matcher)" in new TestCaseSetup {
      val origin = ActorRef.noSender
      val segments = List()
      val handler = ActorRef.noSender

      requestMatcherActor ! AddMatcher(segments, handler)
      requestMatcherActor ! FindMatchAndForward(HttpRequest(), origin)

      probe.expectMsgClass(classOf[RespondIfMatched])
    }
    "forward requests to the registered matcher (multiple matchers)" in new TestCaseSetup {
      val origin = ActorRef.noSender
      val segments1 = List("1")
      val segments2 = List("2")
      val handler = ActorRef.noSender

      requestMatcherActor ! AddMatcher(segments1, handler)
      requestMatcherActor ! AddMatcher(segments2, handler)
      requestMatcherActor ! FindMatchAndForward(HttpRequest(), origin)

      probe.expectMsgClass(classOf[RespondIfMatched])
      probe.expectMsgClass(classOf[RespondIfMatched])
      probe.expectNoMsg(50.millis)
    }
    "forward remove existing matchers for an endpoint if it is updated" in new TestCaseSetup {
      val origin = ActorRef.noSender
      val segments1 = List("1")
      val segments2 = List("2")
      val segments3 = List("1")
      val handler = ActorRef.noSender
      val probeWatcher = TestProbe()
      probeWatcher watch probe.ref

      requestMatcherActor ! AddMatcher(segments1, handler)
      requestMatcherActor ! AddMatcher(segments2, handler)
      requestMatcherActor ! AddMatcher(segments3, handler)

      probeWatcher.expectTerminated(probe.ref)
    }
  }
}