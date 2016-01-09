package castalia.matcher

import akka.http.scaladsl.model.Uri

/**
  * Takes a list of endpoint matchers and determines which one matches.
  *
  * Created by Jean-Marc van Leerdam on 2016-01-09
  */
class RequestMatcher(myMatchers: List[Matcher]) {

  def matchRequest(uriString: String): Option[Matcher] = {
    val uri: Uri = uriString

    Some(myMatchers.head)
    //None
  }
}
