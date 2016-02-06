package castalia.matcher

// import java.net.URLDecoder

import akka.http.scaladsl.model.Uri


/**
  * Helper class that will parse an URI string into a ParsedUri object which is easier to manipulate
  *
  * Created by Jean-Marc van Leerdam on 2016-01-07
  */
class UriParser {

  /**
    * Preprocesses the request string, to ensure query part is using ampersand as parameter separator
    * @param input uri string
    * @return string with correct query param separators
    */
  def preprocess( input: String): String = {
    // val decoded = URLDecoder.decode(input, "UTF-8")
    val decoded = input
    if (decoded.contains(';')){
      return decoded.replace(';', '&')
    }
    return decoded
  }

  def parse( uriString: String): ParsedUri = {
    val uri: Uri = preprocess(uriString)

    val result = new ParsedUri( uriString, uri.path, uri.query().toList)

    result
  }
}
