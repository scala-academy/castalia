package castalia.matcher

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.matcher.RequestMatcherActor.{AddMatcher, FindMatchAndForward}
import castalia.matcher.types._

/**
  * Created by m06f791 on 4-2-2016.
  */

object RequestMatcherActor {

  case class FindMatchAndForward(httpRequest: HttpRequest, origin: ActorRef)

  case class AddMatcher(segments: Segments, handler: ActorRef)

  def props(): Props = Props(new RequestMatcherActor with RequestMatcherActorCreatorImplementation)
}

class RequestMatcherActor extends Actor with ActorLogging {
  this: RequestMatcherActorCreator =>

  def receive: Receive = normal(Set.empty[(ActorRef, Segments)])

  def normal(matchers: Set[(ActorRef, Segments)]): Receive = {
    // Add matcher. If there is already a matcher with the same segments, kill that and replace with new one
    case AddMatcher(segments, handler) =>
      val matcher = createRequestMatcherActor(context, segments, handler)
      log.debug(s"Added matcher $matcher to RequestMatcherActor ${self.toString()}")
      matchers.find(_._2.equals(segments)).map(_._1 ! Kill)
      context.become(normal(matchers.filter(!_._2.equals(segments)) + ((matcher, segments))))

    // Foward match request to all registered matchers. Create result gatherer to gather and handle results
    case FindMatchAndForward(httpRequest, origin) =>
      val parsedUri = new ParsedUri(httpRequest.uri.toString().replace(';', '&'), httpRequest.uri.path, httpRequest.uri.query().toList)
      log.debug(s"RequestMatcherActor received http request $parsedUri from $sender")
      val gatherer = context.actorOf(MatchResultGatherer.props(matchers.size, origin))
      matchers.foreach(matcher => {
        log.debug(s"sending to ${matcher._1} ${RespondIfMatched(parsedUri, httpRequest, gatherer)}")
        matcher._1 ! RespondIfMatched(parsedUri, httpRequest, gatherer)
      })
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
