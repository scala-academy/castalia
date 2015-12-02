package castalia


import com.twitter.finagle
import com.twitter.finagle.http.{Method, Request}
import com.twitter.util.Await
import org.scalatest.{Matchers, WordSpec}

class StubServerTest extends WordSpec with Matchers {
  // TODO: other way than starting application using empty string array in main?
  Main.main(Array.empty[String])

  val client = finagle.Http.newService(s"localhost:9000")

  "Spinning up the stubserver" should {
    "result in a working server" in {
      val request = Request(Method.Get, "/stubs/hardcodeddummystub")
      request.host = "localhost:9000"
      val response = client(request)

      assert(Await.result(response).statusCode == 200)

    }
  }

}
