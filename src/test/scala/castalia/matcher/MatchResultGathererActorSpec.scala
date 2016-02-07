package castalia.matcher

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.{TestActor, TestProbe}
import castalia.actors.ActorSpecBase
import castalia.matcher.MatchResultGatherer.{MatchFound, MatchNotFound}
import castalia.model.Model.StubResponse

class MatchResultGathererActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("MatchResultGatherer"))

  "MatchResultGatherer" must {

    "forward the first match result to origin" in {
      val origin = TestProbe()
      val matchResultGathererActor = system.actorOf(MatchResultGatherer.props(3, origin.ref))
      val handler = TestProbe("StubHandlerProbe")
          handler.setAutoPilot(new TestActor.AutoPilot {
            def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
              sender ! "StubResponse"
              TestActor.NoAutoPilot
            }
          })

      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! MatchFound(handler.ref, RequestMatch(HttpRequest(), List(), Nil))

      origin.expectMsg("StubResponse")
    }
    "forward the first match and not the second result to origin" in {
      val handler1 = TestProbe("StubHandlerProbe1")
      handler1.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
          sender ! "FirstStubResponse"
          TestActor.NoAutoPilot
        }
      })
      val handler2 = TestProbe("StubHandlerProbe2")
      handler2.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
          sender ! "SecondStubResponse"
          TestActor.NoAutoPilot
        }
      })
      val origin = TestProbe()
      val matchResultGathererActor = system.actorOf(MatchResultGatherer.props(3, origin.ref))
      origin watch matchResultGathererActor

      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! MatchFound(handler1.ref, RequestMatch(HttpRequest(), List(), Nil))
      origin.expectMsg("FirstStubResponse")

      matchResultGathererActor ! MatchFound(handler2.ref, RequestMatch(HttpRequest(), List(), Nil))
      origin.expectTerminated(matchResultGathererActor)
    }
    "send a 404 if no match is received" in {
      val origin = TestProbe()
      val matchResultGathererActor = system.actorOf(MatchResultGatherer.props(3, origin.ref))
      origin watch matchResultGathererActor

      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! MatchNotFound

      origin.expectMsg(StubResponse(NotFound.intValue, NotFound.reason))
      origin.expectTerminated(matchResultGathererActor)
    }
  }
}
