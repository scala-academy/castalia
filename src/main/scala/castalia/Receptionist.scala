package castalia

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RouteResult, RequestContext, Route}
import akka.pattern.ask
import akka.util.Timeout
import castalia.actors.JsonEndpointActor
import castalia.matcher.{RequestMatcher, Matcher}
import castalia.model.Messages.{Done, UpsertEndpoint}
import castalia.model.Model.{StubResponse, StubConfig}

import scala.concurrent.duration._

object Receptionist {
  def props: Props = Props[Receptionist]
}

class Receptionist extends Actor with ActorLogging {

  var endpointMatcher: RequestMatcher = new RequestMatcher(Nil)

  private def upsertEndPointActor(stubConfig: StubConfig) = {
    val actor = context.actorOf(Props(new JsonEndpointActor(stubConfig)))
    endpointMatcher = endpointMatcher.addOrReplaceMatcher(new Matcher(stubConfig.segments, actor))
  }

  override def receive: Receive = {
    // Request to modify config
    case UpsertEndpoint(stubConfig) =>
      log.info(s"receptionist received UpsertEndpoint message, adding endpoint " + stubConfig.endpoint)
      upsertEndPointActor(stubConfig)
      sender ! Done(stubConfig.endpoint)

    // Real request
    case request: HttpRequest =>
      log.info(s"receptionist received message [" + request.uri.toString() + "]")
      val requestMatchOption = endpointMatcher.matchRequest(request.uri.toString())
      log.info(s"receptionist attempted to match, result = " + requestMatchOption)
      requestMatchOption match {
        case Some(requestMatch) => requestMatch.handler forward requestMatch
        case _ => sender ! StubResponse(NotFound.intValue, "From receptionist " + NotFound.reason)
      }

      // unexpected messages
    case x =>
      log.info("Receptionist received unexpected message: " + x.toString)
  }

}


