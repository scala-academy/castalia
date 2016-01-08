package castalia.matcher

import akka.http.scaladsl.model.Uri
import castalia.matcher.types.Segments


/**
  * Helper class that will parse an URI string into a ParsedUri object which is easier to manipulate
  *
  * Created by Jean-Marc van Leerdam on 2016-01-07
  */
class UriParser {

  def parse( uriString: String): ParsedUri = {
    val uri: Uri = uriString

    val result = new ParsedUri( uriString, uri.path, uri.query().toList)

    result
  }
}
