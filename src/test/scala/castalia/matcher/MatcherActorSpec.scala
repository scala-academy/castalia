package castalia.matcher

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.{TestActor, TestActorRef, TestProbe}
import castalia.actors.ActorSpecBase
import castalia.matcher.MatchResultGatherer.MatchNotFound
import castalia.matcher.MatcherActor.RespondIfMatched

class MatcherActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("MatcherActor"))

  val uriParser = new UriParser()

  "MatcherActor" must {
    "forward the result after a match to the match result gatherer" in {
      val gatherer = TestProbe("GathererProbe")
      val segments = List("a", "{bparm}", "c")
      val parsedUri = uriParser.parse("/a/123/c")
      val httpRequest = HttpRequest()
      val handler = TestProbe("HandlerProbe")
      handler.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
          sender ! "HandlerResponse"
          TestActor.NoAutoPilot
        }
      })
      val matcherActor = system.actorOf(MatcherActor.props(segments, handler.ref))

      matcherActor ! RespondIfMatched(parsedUri, httpRequest, gatherer.ref)

      handler.expectMsgClass(classOf[RequestMatch])
      gatherer.expectMsg("HandlerResponse")
    }
  }
  "forward a match with the right parameters to the stub handler" in {
    val s1 = List("sample", "path", "with", "{partyId}", "id")
    val s2 = List("sample", "path", "with", "{partyId}", "id", "aswell")
    val s3 = List("sample", "path", "without", "id")
    val probe = TestProbe("probe1")
    probe.setAutoPilot(new TestActor.AutoPilot {
      def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
        sender ! "StubResponse"
        TestActor.KeepRunning
      }
    })
    val gatherer = TestProbe("GathererProbe")
    val matcherActor1 = TestActorRef(new MatcherActor(s1, probe.ref))
    val matcherActor2 = TestActorRef(new MatcherActor(s2, probe.ref))
    val matcherActor3 = TestActorRef(new MatcherActor(s3, probe.ref))

    matcherActor1 ! RespondIfMatched(uriParser.parse("/sample/path/with/12/id?p1=foo&p2=bar"), HttpRequest(), gatherer.ref)
    probe.expectMsgPF() { case RequestMatch(_, List(("partyId", "12")), List(("p1", "foo"), ("p2", "bar"))) => () }
    gatherer.expectMsg("StubResponse")

    matcherActor1 ! RespondIfMatched(uriParser.parse("/sample/path/with/12/id/aswell?p1=foo&p2=bar"), HttpRequest(), gatherer.ref)
    gatherer.expectMsg(MatchNotFound)

    matcherActor2 ! RespondIfMatched(uriParser.parse("/sample/path/with/12/id?p1=foo&p2=bar"), HttpRequest(), gatherer.ref)
    gatherer.expectMsg(MatchNotFound)

    matcherActor3 ! RespondIfMatched(uriParser.parse("/sample/path/without/id"), HttpRequest(), gatherer.ref)
    probe.expectMsgPF() { case RequestMatch(_, List(), List()) => () }
    gatherer.expectMsg("StubResponse")
  }
}
