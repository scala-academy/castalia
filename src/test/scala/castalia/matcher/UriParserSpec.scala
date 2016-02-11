package castalia.matcher

import castalia.UnitSpecBase
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by Jean-Marc van Leerdam on 2016-01-08
  */
class UriParserSpec extends UnitSpecBase {

  "An UniParser object" when {
    "presented with a correct URI String" should {
      "properly parse it into a ParsedUri object" in {
        val parser = new UriParser()

        val result = parser.parse("http://aa.com/a/b/c/d?e=f&g=h")

        result.path.toString().shouldBe("/a/b/c/d")
        result.queryParams.contains(("e", "f")).shouldBe(true)
        result.queryParams.contains(("g", "h")).shouldBe(true)
        result.queryParams.contains(("g", "i")).shouldBe(false)
        result.uri.shouldBe("http://aa.com/a/b/c/d?e=f&g=h")
      }

      "support semicolons as query param separators" in {
        val parser = new UriParser()

        val result = parser.parse("/a/b?p1=12;p2=13;p3")
        result.queryParams.contains(("p1", "12")).shouldBe(true)
        result.queryParams.contains(("p2", "13")).shouldBe(true)
        result.queryParams.contains(("p3", "")).shouldBe(true)
        result.queryParams.contains(("p1", "13")).shouldBe(false)

      }

      "support mixing ampersand and semicolons as query param separators" in {
        val parser = new UriParser()

        val result = parser.parse("/a/b?p1=12;p2=13&p3")
        result.queryParams.contains(("p1", "12")).shouldBe(true)
        result.queryParams.contains(("p2", "13")).shouldBe(true)
        result.queryParams.contains(("p3", "")).shouldBe(true)
        result.queryParams.contains(("p1", "13")).shouldBe(false)

      }

    }
  }
}
