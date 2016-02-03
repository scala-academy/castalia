package castalia.matcher

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.Uri.Path.{Empty, Slash, Segment}
import castalia.matcher.types._

import scala.annotation.tailrec

package object types{
  type Segments = List[String]
  type Params = List[(String, String)]
}


/**
  * Holds an endpoint matching structure and an ActorRef that can process the request
  *
  * @param segments containing the path segment matches and path parameters
  * @param handler ActorRef containing the name of the actor that can process this request
  */
case class Matcher(segments: Segments, handler: ActorRef) {
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
        case (rhead::rtail, mhead::mtail) if isParam(mhead) => marp(rtail, mtail, (paramName(mhead), rhead)::params)
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

/**
  * Result of a successful match of a request uri by a Matcher
 *
  * @param httpRequest the original HttpRequest
  * @param pathParams the path parameters that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  * @param handler the ActorRef of the handler actor that should process the request
  */
case class RequestMatch(httpRequest: HttpRequest, pathParams: Params, queryParams: Params, handler: ActorRef)

/**
  * Result of a successful match of a endpoint uri by a Matcher
 *
  * @param endpoint the original endpoint id string
  * @param pathParams the path parameters that were extracted from the uri
  * @param handler the ActorRef of the handler actor that should process the request
  */
case class EndpointMatch(endpoint: String, pathParams: Params, queryParams: Params, handler: ActorRef)

/**
  * Parsed uri, where the path has been split into segments and the query parameters have been converted into a Params object
  *
  * @param uri the original uri
  * @param path the segments that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  */
case class ParsedUri(uri: String, path: Path, queryParams: Params) {
  def pathList: Segments = {
    @tailrec
    def myPathList(path: Path, segments: Segments): Segments =
      path match {
        case Empty => segments
        case (Slash(tail)) => myPathList(tail, segments)
        case (Segment(head, tail)) => myPathList(tail, head :: segments)
    }

    myPathList(path, List[String]()).reverse
  }
}
