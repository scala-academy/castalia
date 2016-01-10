package castalia.matcher

import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

/**
  * Created by Jean-Marc van Leerdam on 2016-01-09
  */
class RequestMatcherTest extends WordSpec with Matchers with BeforeAndAfterEach{

  private var uriMatcher: RequestMatcher = _

  override def beforeEach() {
    val s1 = List("sample", "path", "with", "{partyId}", "id")
    val s2 = List("another", "path", "without", "id")

    val m1 = new Matcher(s1, "a1")
    val m2 = new Matcher(s2, "a2")

    val matchers = List(m1, m2)

    uriMatcher = new RequestMatcher(matchers)
  }

  "RequestMatcherTest" should {

    "not find a match for an incorrect Request" in {

      val r1 = uriMatcher.matchRequest("foo")
      r1.shouldBe(None)

    }

    "find a match for a correct Request" in {
      val r1 = uriMatcher.matchRequest("http://localhost:1234/sample/path/with/12/id?p1=foo&p2=bar")
      r1.get.handler.shouldBe("a1")
    }

    "find a match for another correct Request" in {
      val r1 = uriMatcher.matchRequest("http://localhost:1234/another/path/without/id?p1=foo&p2=bar")
      r1.get.handler.shouldBe("a2")
    }
  }
}
