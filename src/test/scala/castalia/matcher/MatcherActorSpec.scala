package castalia.matcher

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri.Path
import akka.testkit.{TestActorRef, TestProbe}
import castalia.actors.ActorSpecBase
import castalia.matcher.MatchResultGatherer.{MatchFound, MatchNotFound}
import castalia.matcher.MatcherActor.TryMatch

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
      val matcherActor = system.actorOf(MatcherActor.props(segments, handler.ref))

      matcherActor ! TryMatch(parsedUri, httpRequest, gatherer.ref)

      gatherer.expectMsgClass(classOf[MatchFound])
    }

    "forward a match with the right parameters to the stub handler" in {
      val s1 = List("sample", "path", "with", "{partyId}", "id")
      val s2 = List("sample", "path", "with", "{partyId}", "id", "aswell")
      val s3 = List("sample", "path", "without", "id")
      val handler = TestProbe("HandlerProbe")
      val gatherer = TestProbe("GathererProbe")
      val matcherActor1 = TestActorRef(new MatcherActor(s1, handler.ref))
      val matcherActor2 = TestActorRef(new MatcherActor(s2, handler.ref))
      val matcherActor3 = TestActorRef(new MatcherActor(s3, handler.ref))

      matcherActor1 ! TryMatch(uriParser.parse("/sample/path/with/12/id?p1=foo&p2=bar"), HttpRequest(), gatherer.ref)
      gatherer.expectMsgPF() { case MatchFound(_, RequestMatch(_, List(("partyId", "12")), List(("p1", "foo"), ("p2", "bar")))) => () }

      matcherActor1 ! TryMatch(uriParser.parse("/sample/path/with/12/id/aswell?p1=foo&p2=bar"), HttpRequest(), gatherer.ref)
      gatherer.expectMsg(MatchNotFound)

      matcherActor2 ! TryMatch(uriParser.parse("/sample/path/with/12/id?p1=foo&p2=bar"), HttpRequest(), gatherer.ref)
      gatherer.expectMsg(MatchNotFound)

      matcherActor3 ! TryMatch(uriParser.parse("/sample/path/without/id"), HttpRequest(), gatherer.ref)
      gatherer.expectMsgPF() { case MatchFound(_, RequestMatch(_, List(), List())) => () }
    }

    "support {} as path parameter indication" in {
      val gatherer = TestProbe()
      val matcherActor = system.actorOf(MatcherActor.props(List("a", "{bparm}", "c"), ActorRef.noSender))
      val parsedUri = new ParsedUri("", Path("/a/b/c"), List())

      matcherActor ! TryMatch(parsedUri, HttpRequest(), gatherer.ref)

      gatherer.expectMsgPF() { case MatchFound(_, RequestMatch(_, List(("bparm", "b")), List())) => () }
    }

    "support $ as path parameter indication" in {
      val gatherer = TestProbe()
      val matcherActor = system.actorOf(MatcherActor.props(List("a", "b", "$c"), ActorRef.noSender))
      val parsedUri = new ParsedUri("", Path("/a/b/cval"), List())

      matcherActor ! TryMatch(parsedUri, HttpRequest(), gatherer.ref)

      gatherer.expectMsgPF() { case MatchFound(_, RequestMatch(_, List(("c", "cval")), List())) => () }
    }

    "support mixing {} and $ as path parameter" in {
      val gatherer = TestProbe()
      val matcherActor = system.actorOf(MatcherActor.props(List("a", "{bparm}", "$c"), ActorRef.noSender))
      val parsedUri = new ParsedUri("", Path("/a/b/cval"), List())

      matcherActor ! TryMatch(parsedUri, HttpRequest(), gatherer.ref)

      gatherer.expectMsgPF() { case MatchFound(_, RequestMatch(_, paramList, List()))
        if paramList.contains(("bparm", "b")) && paramList.contains(("c", "cval")) => ()
      }
    }
  }
}
