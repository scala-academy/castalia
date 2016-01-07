
import akka.http.scaladsl.model.Uri
import castalia.matcher._

val uri = "/sample/path/with/12/id?p1=foo&p2=bar"

val uriParser = new UriParser()

val result = uriParser.test(uri)

val uri2: Uri = "/sample/path/with/12/id?p1=foo&p2=bar"

uri2.authority
uri2.path
uri2.queryString()


val parseResult = uriParser.parse(uri)
