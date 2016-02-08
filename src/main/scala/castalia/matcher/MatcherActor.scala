package castalia.matcher

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.model.HttpRequest
import castalia.matcher.MatchResultGatherer.{MatchFound, MatchNotFound}
import castalia.matcher.MatcherActor.RespondIfMatched
import castalia.matcher.types.{Params, Segments}

import scala.annotation.tailrec

object MatcherActor {

  case class RespondIfMatched(parsedUri: ParsedUri, httpRequest: HttpRequest, gatherer: ActorRef)

  def props(segments: Segments, handler: ActorRef): Props = Props(new MatcherActor(segments, handler))
}

class MatcherActor(segments: Segments, handler: ActorRef) extends Actor with ActorLogging {

  def receive: Receive = {
    case RespondIfMatched(parsedUri, httpRequest, gatherer) =>
      matchPath(parsedUri.pathList) match {
        case Some(params) =>
          log.debug(s"Match found for $segments and $parsedUri: $params to be handled by $handler")
          val requestMatch = new RequestMatch(httpRequest, params, parsedUri.queryParams)
          gatherer ! MatchFound(handler, requestMatch)
        case None =>
          log.debug(s"Match not found for $segments and $parsedUri")
          gatherer ! MatchNotFound
      }
    // unexpected messages
    case x =>
      log.info("Receptionist received unexpected message: " + x.toString)
  }

  /**
    * Compare the segments, matching the literals and collecting the parameters on the fly
    *
    * @param requestSegments containing the path segments from the request
    */
  def matchPath(requestSegments: Segments): Option[Params] = {
    @tailrec
    def marp(requestSeg: Segments, matchSeg: Segments, params: Params): Option[Params] =
      (requestSeg, matchSeg) match {
        case (Nil, Nil) => Some(params)
        case (Nil, _) => None
        case (_, Nil) => None
        case (rhead :: rtail, mhead :: mtail) if isParam(mhead) => marp(rtail, mtail, (paramName(mhead), rhead) :: params)
        case (rhead :: rtail, mhead :: mtail) if rhead.equals(mhead) => marp(rtail, mtail, params)
        case (_, _) => None
      }

    marp(requestSegments, segments, List[(String, String)]())
  }

  private def isParam(segment: String): Boolean = {
    segment.startsWith("{") && segment.endsWith("}") || segment.startsWith("$")
  }

  private def paramName(segment: String): String =
    (segment, segment.startsWith("{"), segment.startsWith("$")) match {
      case (seg, true, _) => seg.substring(1, segment.length - 1)
      case (seg, _, true) => seg.substring(1, segment.length)
      case (seg, _, _) => seg
    }
}
