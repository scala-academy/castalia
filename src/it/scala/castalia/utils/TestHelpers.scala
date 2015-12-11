package castalia.utils

import com.twitter.finagle
import com.twitter.finagle.ChannelWriteException
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.{Await, Future}

/**
  * Created by jens on 06-12-15.
  */
object TestHelpers {

  /**
    * Check if we can make a call to the server.
    * @param serverAddress the server to respond
    * @return if the server is running or not
    */
  def isServerRunningFuture(serverAddress: String): Future[Response] = {
    val client = finagle.Http.newService(serverAddress)
    val request = Request(Method.Get, "/")
    request.host = serverAddress
    client(request)
  }

  def isServerRunning(serverAddress: String): Boolean = {
    try {
      val eventualResponse = isServerRunningFuture(serverAddress)
      val response = Await.result(eventualResponse)
      response.status != Status.NotFound
    } catch {
      case e : ChannelWriteException => false
    }
  }
}
