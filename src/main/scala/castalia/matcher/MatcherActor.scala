package castalia.matcher

import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import akka.http.scaladsl.model.HttpRequest
import castalia.matcher.MatcherActor.ForwardIfMatched
import castalia.matcher.types.{Params, Segments}

import scala.annotation.tailrec

/**
  * Created by m06f791 on 4-2-2016.
  */
object MatcherActor {
  case class ForwardIfMatched(parsedUri: ParsedUri, httpRequest: HttpRequest)
  def props(segments: Segments, handler: ActorRef): Props = Props(new MatcherActor(segments, handler))
}
class MatcherActor(segments: Segments, handler: ActorRef) extends Actor with ActorLogging {

  val parsedSegments = segments.filter(isParam(_)).map{segment => (segment -> paramName(segment))}.toMap[String, String]

  def receive: Receive = {
    case ForwardIfMatched(parsedUri, httpRequest) => {
      log.debug(s"MatcherActor received ForwardIfMatched with ${parsedUri}")
      matchPath(parsedUri.pathList).map { params =>
        log.debug(s"MatcherActor found match: ${params}. Forwarding request to ${handler}")
        handler forward new RequestMatch(httpRequest, params, parsedUri.queryParams, handler)
      }
    }
  }


  /**
    * Compare the segments, matching the literals and collecting the parameters on the fly
    *
    * @param requestSegments containing the path segments from the request
    */
  def matchPath(requestSegments: Segments): Option[Params] = {
    @tailrec
    def marp( requestSeg: Segments, matchSeg: Segments, params: Params): Option[Params] =
      (requestSeg, matchSeg) match {
        case (Nil, Nil)  => Some(params)
        case (Nil, _) => None
        case (_, Nil) => None
        //        case (rhead::rtail, mhead::mtail) if isParam(mhead) => marp(rtail, mtail, (paramName(mhead), rhead)::params)
        case (rhead::rtail, mhead::mtail) if parsedSegments.contains(mhead) => marp(rtail, mtail, (parsedSegments(mhead), rhead)::params)
        case (rhead::rtail, mhead::mtail) if rhead.equals(mhead) => marp(rtail, mtail, params)
        case (_, _) => None
      }

    marp( requestSegments, segments, List[(String, String)]())
  }

  private def isParam(segment: String): Boolean = {
    segment.startsWith("{") && segment.endsWith("}") || segment.startsWith("$")
  }

  private def paramName( segment: String): String =
    (segment, segment.startsWith("{"), segment.startsWith("$")) match {
      case (seg, true, _)    => seg.substring(1, segment.length - 1)
      case (seg, _,    true) => seg.substring(1, segment.length)
      case (seg, _,    _)    => seg
    }
}
