package castalia.matcher

import akka.http.scaladsl.model.Uri

/**
  * Takes a list of endpoint matchers and determines which one matches.
  *
  * Created by Jean-Marc van Leerdam on 2016-01-09
  */
class RequestMatcher(myMatchers: List[Matcher]) {
  val uriParser = new UriParser()

  def matchRequest(uriString: String): Option[Matcher] = {
    val parsedUri = uriParser.parse(uriString)


    None
  }
}
