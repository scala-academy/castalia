
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.PathMatchers.Slash
import akka.http.scaladsl.server.{PathMatcher0, PathMatcher}
import castalia.matcher._

val uri = "http://localhost:1234/sample/path/with/12/id?p1=foo&p2=bar"

val uriParser = new UriParser()

val uri2: Uri = uri

uri2.authority
uri2.path
uri2.queryString()


val parseResult = uriParser.parse(uri)

val matcher: PathMatcher0 = "sample"
val matcher2: PathMatcher0 = "path"

(Slash ~ matcher ~ Slash ~ matcher2).apply(parseResult.path)

(Slash ~ "sample" ~ Slash ~ "path").apply(parseResult.path)

val m2 = matcher

/*
Plans:

Matcher object has Segments (List[String])

 do a map over the list
  if segment is fixed string (foobar)
    attempt to match uri path
      if success -> proceed else -> fail
  if segment is param string ({param})
    match always, place actual segment value in pathParm map (key = param, value = actual segment value)
    -> proceed
  if no more elements -> success (return collected data as RequestMatch object)

 The endpoint matcher asks each Matcher for a result and returns
 the first (only?) one that reports a match
 (potential concurrency here, but first impl. can be a map over the list
 that shortcircuits as soon as one Matcher reports success)
 */