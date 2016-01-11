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
  * @param handler String containing the name of the actor that can process this request
  */
case class Matcher(segments: Segments, handler: String) {
  /**
    * Compare the segments, matching the literals and collecting the parameters on the fly
    * @param requestSegments containing the path segments from the request
    */
  def matchPath(requestSegments: Segments): Option[Params] = {
    def marp( requestSeg: Segments, matchSeg: Segments, params: Params): Option[Params] =
      (requestSeg, matchSeg) match {
        case (rSeg, mSeg) if (rSeg.isEmpty && mSeg.isEmpty) => Some(params)
        case (rSeg, mSeg) if (rSeg.isEmpty || mSeg.isEmpty) => None
        case (rSeg, mSeg) if (isParam(mSeg.head)) => marp(requestSeg.tail, matchSeg.tail, (paramName(matchSeg.head), requestSeg.head)::params)
        case (rSeg, mSeg) if (rSeg.head.equals(mSeg.head)) => marp(requestSeg.tail, matchSeg.tail, params)
        case (_, _) => None
    }

    marp( requestSegments, segments, List[(String, String)]())
  }

  private def isParam(segment: String): Boolean = {
    segment.startsWith("{") && segment.endsWith("}") || segment.startsWith("$")
  }

  private def paramName( segment: String): String = segment match {
    case seg if(seg.startsWith("{")) => seg.substring(1, segment.length - 1)
    case seg if (seg.startsWith("$")) => seg.substring(1, segment.length)
    case seg => seg
  }


}

/**
  * Result of a successful match of a request uri by a Matcher
  * @param uri the original uri
  * @param path the path that was extracted from the uri
  * @param pathParams the path parameters that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  * @param handler the name of the handler actor that should process the request
  */
case class RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params, handler: String)

/**
  * Parsed uri, where the path has been split into segments and the query parameters have been converted into a Params object
  * @param uri the original uri
  * @param path the segments that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  */
case class ParsedUri(uri: String, path: Path, queryParams: Params) {
  def pathList = {
    def myPathList(p: Path, segments: Segments): Segments = {
      if (p.isEmpty) return segments
      if (p.startsWithSlash) return myPathList(p.tail, segments)
      return myPathList(p.tail, p.head.toString :: segments)
    }
    myPathList(path, List[String]()).reverse
  }
}
