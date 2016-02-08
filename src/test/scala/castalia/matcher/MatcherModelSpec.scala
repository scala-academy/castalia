package castalia.matcher

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path.Empty
import akka.testkit.TestProbe
import castalia.actors.ActorSpecBase

/**
  * Created by Jean-Marc van Leerdam on 2016-01-10
  */
class MatcherModelSpec (_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("MatcherModelSpec"))

  "MatcherModel PathUri class" should {

    "implement correct pathList method " in {
      val resList = List("a", "b", "c")
      val uri:Uri = "http://example.com/a/b/c?d=e&f-g"

      val parsedUri = new ParsedUri( uri.toString(), uri.path, uri.query().toList)

      assert( resList.equals(parsedUri.pathList))
    }

    "return an empty list when no path is given" in {
      val uri:Uri = "http://example.com/"

      val parsedUri = new ParsedUri( uri.toString(), uri.path, uri.query().toList)

      assert(parsedUri.pathList.equals(List()))
    }
  }
}
