package castalia.matcher

import akka.http.scaladsl.model.{HttpRequest, Uri}
import castalia.matcher.types.Segments

/**
  * Takes a list of endpoint matchers and determines which one matches.
  *
  * Created by Jean-Marc van Leerdam on 2016-01-09
  */
class RequestMatcher(myMatchers: List[Matcher]) {
  val uriParser = new UriParser()

  def matchRequest(httpRequest: HttpRequest): Option[RequestMatch] = {
    val parsedUri = uriParser.parse(httpRequest.uri.toString())

    def findMatch( segments: Segments, matchers: List[Matcher]): Option[RequestMatch] = {
      if (matchers.isEmpty) return None

      val result = matchers.head.matchPath(segments)
      if (result.isDefined) return Some(new RequestMatch(httpRequest, result.get, parsedUri.queryParams, matchers.head.handler))

      // no match yet, look at the rest of the matchers
      findMatch(segments, matchers.tail)
    }

    println( "looking for [" + parsedUri.pathList + "] in [" + myMatchers + "]")
    findMatch(parsedUri.pathList, myMatchers)
  }

  def addOrReplaceMatcher(newMatcher: Matcher): RequestMatcher = {
    new RequestMatcher( newMatcher :: myMatchers.filterNot(_.segments == newMatcher.segments))
  }
}
