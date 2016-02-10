package castalia.matcher

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import castalia.matcher.MatcherActor.TryMatch
import castalia.matcher.RequestMatcherActor.{AddMatcher, FindMatch}
import castalia.matcher.types._

object RequestMatcherActor {

  case class FindMatch(httpRequest: HttpRequest, origin: ActorRef)

  case class AddMatcher(segments: Segments, handler: ActorRef)

  def props(): Props = Props(new RequestMatcherActor with RequestMatcherActorCreatorImplementation)
}

class RequestMatcherActor extends Actor with ActorLogging {
  this: RequestMatcherActorCreator =>

  def receive: Receive = normal(Map.empty[Segments, ActorRef])

  def normal(matchers: Map[Segments, ActorRef]): Receive = {
    // Add matcher. If there is already a matcher with the same segments, stop that and replace with new one
    case AddMatcher(segments, handler) =>
      val matcher = createRequestMatcherActor(context, segments, handler)
      log.debug(s"Added matcher $matcher to RequestMatcherActor ${self.toString()}")
      val matcherOnSameSegments = matchers.get(segments)
      matcherOnSameSegments.foreach { matcherRef => context.stop(matcherRef) }
      val newSetOfMatchers = matchers.updated(segments, matcher)
      context.become(normal(newSetOfMatchers))

    // Foward match request to all registered matchers. Create result gatherer to gather and handle results
    case FindMatch(httpRequest, origin) =>
      val parsedUri = new ParsedUri(httpRequest.uri.toString().replace(';', '&'), httpRequest.uri.path, httpRequest.uri.query().toList)
      log.debug(s"RequestMatcherActor received http request $parsedUri from $sender")
      val gatherer = context.actorOf(MatchResultGatherer.props(matchers.size, origin))
      matchers.foreach{case (segments, matcher) => {
        log.debug(s"sending to ${matcher} ${TryMatch(parsedUri, httpRequest, gatherer)}")
        matcher ! TryMatch(parsedUri, httpRequest, gatherer)
      }}

    // unexpected messages
    case x =>
      log.info("RequestMatcherActor received unexpected message: " + x.toString)
  }
}

/**
  * This trait allows us to inject test probes
  */
trait RequestMatcherActorCreator {
  def createRequestMatcherActor(context: ActorContext, segments: Segments, handler: ActorRef): ActorRef
}

trait RequestMatcherActorCreatorImplementation extends RequestMatcherActorCreator {
  def createRequestMatcherActor(context: ActorContext, segments: Segments, handler: ActorRef): ActorRef = {
    context.actorOf(MatcherActor.props(segments, handler))
  }
}
