package castalia.matcher

import akka.http.scaladsl.model.Uri
import castalia.matcher.types.Segments


/**
  * Created by Jean-Marc van Leerdam on 2016-01-07
  */
class UriParser {

  def test(uri:String): String = {
    uri + "done"
  }

  def parse( uriString: String): ParsedUri = {
    val uri: Uri = uriString

    val result = new ParsedUri( uriString, uri.path, uri.query().toList)

    result
  }
}
