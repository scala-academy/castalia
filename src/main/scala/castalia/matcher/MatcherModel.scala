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
  * @param handler ActorRef of the actor that can process this request
  */
case class Matcher(segments: Segments, handler: ActorRef)

/**
  * Result of a successful match of a request uri by a Matcher
  * @param uri the original uri
  * @param path the path that was extracted from the uri
  * @param pathParams the path parameters that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  */
case class RequestMatch(uri: String, path: Path, pathParams: Params, queryParams: Params)

/**
  * Parsed uri, where the path has been split into segments and the query parameters have been converted into a Params object
  * @param uri the original uri
  * @param path the segments that were extracted from the uri
  * @param queryParams the query parameters that were extracted from the uri
  */
case class ParsedUri(uri: String, path: Path, queryParams: Params)
