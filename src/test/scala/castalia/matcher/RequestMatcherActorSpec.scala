package castalia.matcher

import akka.actor.{Props, ActorRef, ActorContext, ActorSystem}
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.{TestActor, TestProbe}
import castalia.actors.ActorSpecBase
import castalia.matcher.MatchResultGatherer.MatchNotFound
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.matcher.RequestMatcherActor.{FindMatchAndForward, AddMatcher}
import castalia.matcher.types._
import castalia.model.Model.StubResponse
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._

class RequestMatcherActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("RequestMatcherActor"))

  trait TestRequestMatcherActorCreator extends RequestMatcherActorCreator {
    val probe = TestProbe()
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
//    "forwards requests to the registered matchers (multiple matchers)" in {
//      val origin = TestProbe()
//      val matchers = List(TestProbe(),TestProbe(),TestProbe(),TestProbe())
//      val requestMatcherActor = system.actorOf(Props(new RequestMatcherActor with TestRequestMatcherActorCreater))
//
//      matchers.foreach(matcher => requestMatcherActor ! AddMatcher(matcher.ref))
//      requestMatcherActor ! FindMatchAndForward(HttpRequest(), origin.ref)
//
//      matchers.foreach(_.expectMsgClass(classOf[RespondIfMatched]))
//    }
  }
}