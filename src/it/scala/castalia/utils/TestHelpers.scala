package castalia.utils

import castalia.Main
import com.twitter.finagle
import com.twitter.finagle.ChannelWriteException
import com.twitter.finagle.http.{Response, Method, Request}
import com.twitter.util.{Duration, Future, Await}
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
      val response = isServerRunningFuture(serverAddress)
      val r = Await.result(response)
      r.statusCode != 404
    }
    catch {
      case ex: ChannelWriteException => println("ChannelWriteException: not running")
        false
    }
  }

}
