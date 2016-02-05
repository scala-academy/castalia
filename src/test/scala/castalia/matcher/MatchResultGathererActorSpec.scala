package castalia.matcher

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.TestProbe
import castalia.actors.ActorSpecBase
import castalia.matcher.MatchResultGatherer.MatchNotFound
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.model.Model.StubResponse
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._
import scala.concurrent.duration._

class MatchResultGathererActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("MatchResultGatherer"))

  "MatchResultGatherer" must {

    "forward the first match result to origin" in {
      val origin = TestProbe()
      val matchResultGathererActor = system.actorOf(MatchResultGatherer.props(3, origin.ref))

      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! "match"

      origin.expectMsg("match")
    }
    "forward the first match and not the second result to origin" in {
      val origin = TestProbe()
      val matchResultGathererActor = system.actorOf(MatchResultGatherer.props(3, origin.ref))

      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! "match"
      origin.expectMsg("match")

      matchResultGathererActor ! "match2"
      origin.expectNoMsg(100.millis)
    }
    "send a 404 if no match is received" in {
      val origin = TestProbe()
      val matchResultGathererActor = system.actorOf(MatchResultGatherer.props(3, origin.ref))

      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! MatchNotFound
      matchResultGathererActor ! MatchNotFound

      origin.expectMsg(StubResponse(NotFound.intValue, NotFound.reason))
    }
  }
}
