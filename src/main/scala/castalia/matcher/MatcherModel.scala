package castalia.matcher

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.Uri.Path.{Empty, Segment, Slash}
import castalia.matcher.types._

import scala.annotation.tailrec

package object types{
  type Segments = List[String]
  type Params = List[(String, String)]
}

/**
  * Result of a successful match of a request uri by a MatcherActor
 *
  * @param httpRequest the original HttpRequest
  * @param pathParams the path parameters that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  */
case class RequestMatch(httpRequest: HttpRequest, pathParams: Params, queryParams: Params)

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
