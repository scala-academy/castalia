package castalia.matcher

import akka.http.scaladsl.model.Uri
import castalia.matcher.types.Segments

/**
  * Takes a list of endpoint matchers and determines which one matches.
  *
  * Created by Jean-Marc van Leerdam on 2016-01-09
  */
class RequestMatcher(myMatchers: List[Matcher]) {
  val uriParser = new UriParser()

  def matchRequest(uriString: String): Option[RequestMatch] = {
    val parsedUri = uriParser.parse(uriString)

    def findMatch( segments: Segments, matchers: List[Matcher]): Option[RequestMatch] =
      (segments, matchers) match {
        case (_, Nil) => None
        case (seg, head :: _) if head.matchPath(seg).isDefined =>
          Some(new RequestMatch(uriString, parsedUri.path, head.matchPath(seg).get, parsedUri.queryParams, head.handler))
        case (seg, _ :: tail) => findMatch(seg, tail)
      }

    println( "looking for [" + parsedUri.pathList + "] in [" + myMatchers + "]")
    findMatch(parsedUri.pathList, myMatchers)
  }

  def addOrReplaceMatcher(newMatcher: Matcher): RequestMatcher = {
    new RequestMatcher( newMatcher :: myMatchers.filterNot(_.segments == newMatcher.segments))
  }
}
