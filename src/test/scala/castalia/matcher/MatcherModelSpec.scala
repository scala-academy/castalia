package castalia.matcher

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path
import org.scalatest.{BeforeAndAfterEach, WordSpec}

/**
  * Created by Jean-Marc van Leerdam on 2016-01-10
  */
class MatcherModelSpec extends WordSpec with BeforeAndAfterEach {

  override def beforeEach() {

  }

  "MatcherModelSpec" should {

    "pathList" in {
      val resList = List("a", "b", "c")
      val uri:Uri = "http://example.com/a/b/c?d=e&f-g"

      val parsedUri = new ParsedUri( uri.toString(), uri.path, uri.query().toList)

      assert( resList.equals(parsedUri.pathList))
    }

  }
}
