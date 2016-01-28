package castalia.matcher

import akka.actor.ActorRef
import akka.http.scaladsl.model.Uri.Path
import castalia.matcher.types._

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
    * @param requestSegments containing the path segments from the request
    */
  def matchPath(requestSegments: Segments): Option[Params] = {
    def marp( requestSeg: Segments, matchSeg: Segments, params: Params): Option[Params] =
      (requestSeg, matchSeg) match {
        case (Nil, Nil)  => Some(params)
        case (Nil, _) => None
        case (_, Nil) => None
        case (rSeg, mSeg) if isParam(mSeg.head) => marp(rSeg.tail, mSeg.tail, (paramName(mSeg.head), rSeg.head)::params)
        case (rSeg, mSeg) if rSeg.head.equals(mSeg.head) => marp(rSeg.tail, mSeg.tail, params)
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
  * @param uri the original uri
  * @param path the path that was extracted from the uri
  * @param pathParams the path parameters that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  * @param handler the ActorRef of the handler actor that should process the request
  */
case class RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params, handler: ActorRef)

/**
  * Parsed uri, where the path has been split into segments and the query parameters have been converted into a Params object
  * @param uri the original uri
  * @param path the segments that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  */
case class ParsedUri(uri: String, path: Path, queryParams: Params) {
  def pathList: Segments = {
    def myPathList(path: Path, segments: Segments): Segments =
      (path, path.startsWithSlash) match {
        case (p, _) if p.isEmpty => segments
        case (p, true) => myPathList(p.tail, segments)
        case (p, _) => myPathList(p.tail, p.head.toString :: segments)
    }

    myPathList(path, List[String]()).reverse
  }
}
