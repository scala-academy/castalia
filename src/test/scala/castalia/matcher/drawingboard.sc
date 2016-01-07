
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.PathMatchers.Slash
import akka.http.scaladsl.server.{PathMatcher0, PathMatcher}
import castalia.matcher._

val uri = "http://localhost:1234/sample/path/with/12/id?p1=foo&p2=bar"

val uriParser = new UriParser()

val result = uriParser.test(uri)

val uri2: Uri = uri

uri2.authority
uri2.path
uri2.queryString()


val parseResult = uriParser.parse(uri)

val matcher: PathMatcher0 = "sample"
val matcher2: PathMatcher0 = "path"

(Slash ~ matcher ~ Slash ~ matcher2).apply(parseResult.path)

val m2 = matcher