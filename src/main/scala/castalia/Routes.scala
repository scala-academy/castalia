package castalia

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route}

/**
  * Created by jml on 11/29/15.
  */
trait Routes extends Protocol with SprayJsonSupport {
  def routes:Route


  protected def system: ActorSystem
  protected def serviceName: String
  protected def log: LoggingAdapter = Logging(system, serviceName)

  // Rejected routes should be handled:
  // http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0-RC3/
  // scala/http/routing-dsl/directives/execution-directives/handleRejections.html#handlerejections
  val totallyMissingHandler = RejectionHandler.newBuilder()
    .handleNotFound { complete(StatusCodes.NotFound, "Oh man, what you are looking for is long gone.") }
    .result()
}
