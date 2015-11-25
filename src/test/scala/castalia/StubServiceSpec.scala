package castalia

import com.twitter.finagle.http.Response
import com.twitter.finagle.{Http, http}
import com.twitter.util.{Await, Future}

/**
  * Created by Jens Kat on 25-11-2015.
  */
class StubServiceSpec extends ServiceTestBase {

  "A request to the endpoint hardcodeddummystub" when {
    "I do a HTTP GET" should {
      "return HTTP status code 200" in {

        val client = Http.newService("localhost:9000")
       // val x = Http.newClient()
        val request = http.Request(http.Method.Get, "/stubs/hardcodeddummystub")
        request.host = "http://localhost:9000"
        val response: Future[Response] = client(request)

        response.onSuccess{
          resp => assert(resp.getStatusCode() == 404)
            println(resp)
        }

        response.onFailure(
          resp => println(resp.getCause)
        )

      }
    }
  }
}
