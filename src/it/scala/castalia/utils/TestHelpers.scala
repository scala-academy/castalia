package castalia.utils

import com.twitter.finagle
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.{Await, Future}

import scala.util.Try

object TestHelpers {

  /**
    * Check if we can make a call to the server.
    *
    * @param serverAddress the server to respond
    * @return if the server is running or not
    */
  def isServerRunningFuture(serverAddress: String): Future[Response] = {
    val client = finagle.Http.newService(serverAddress)
    val request = Request(Method.Get, "/")
    request.host = serverAddress
    client(request)
  }

  /**
    * Try to get an response from the given server address.
    * The response should not be `NotFound` (or timed out).
    *
    * @param serverAddress The server to listen to.
    * @return Try telling the server is up or not.
    */
  def isServerRunning(serverAddress: String): Try[Boolean] = {
    Try {
      val eventualResponse = isServerRunningFuture(serverAddress)
      Await.result(eventualResponse.map(_.status != Status.NotFound))
    }
  }
}
