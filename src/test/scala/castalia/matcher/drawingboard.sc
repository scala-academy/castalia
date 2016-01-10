import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.PathMatchers.Slash
import akka.http.scaladsl.server.{PathMatcher0, PathMatcher}
import castalia.matcher._
val uriString = "http://localhost:1234/sample/path/with/12/id?p1=foo&p2=bar"
val uriParser = new UriParser()
val parseResult = uriParser.parse(uriString)
(Slash ~ "sample" ~ Slash ~ "path").apply(parseResult.path)

val s1: List[String] = List("sample", "path")
val s2 = List("another", "path")

val m1 = new Matcher(s1, "a1")
val m2 = new Matcher(s2, "a2")

val matchers = List(m1, m2)

val uriMatcher = new RequestMatcher(matchers)

uriMatcher.matchRequest(uriString)

parseResult.path

parseResult.path.tail.head

val realUri: Uri = uriString

def pathList(path:Path): List[String] = {
  if (path.isEmpty) return List[String]()
  if (path.startsWithSlash) return pathList(path.tail)
  path.head.toString :: pathList(path.tail)
}

pathList(realUri.path)

pathList(parseResult.path)

parseResult.pathList

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