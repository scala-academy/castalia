package castalia

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import castalia.actors.{JsonEndpointActor, JsonResponseProviderEndpointActor, JsonResponsesEndpointActor}
import castalia.matcher.{Matcher, RequestMatcher}
import castalia.metrics.MetricsCollectorActor
import castalia.model.Messages.{Done, EndpointMetricsGet, UpsertEndpoint, UpsertResponse}
import castalia.model.Model.{StubConfig, StubResponse}

object Receptionist {
  def props: Props = Props[Receptionist]
}

class Receptionist extends Actor with ActorLogging {

  val metricsCollector = createMetricsCollector

  private def upsertEndPointActor(stubConfig: StubConfig, endpointMatcher : RequestMatcher) = {

    def endpointActorFactory(stubConfig: StubConfig): JsonEndpointActor = {
      stubConfig match {
        case StubConfig(endpoint, _, _, Some(config)) => new JsonResponseProviderEndpointActor(endpoint, config, metricsCollector)
        case StubConfig(endpoint, _, Some(responses), _) => new JsonResponsesEndpointActor(endpoint, responses, metricsCollector)
        case _ => throw new UnsupportedOperationException
      }
    }

    val actor = context.actorOf(Props(endpointActorFactory(stubConfig)))
    context.become(receiveWithMatcher(endpointMatcher.addOrReplaceMatcher(new Matcher(stubConfig.segments, actor))))
  }

  override def receive: Receive = receiveWithMatcher(new RequestMatcher(Nil))

  def receiveWithMatcher(endpointMatcher : RequestMatcher) : Receive = {
    // Request to modify config
    case UpsertEndpoint(stubConfig) =>
      log.info(s"receptionist received UpsertEndpoint message, adding endpoint " + stubConfig.endpoint)
      upsertEndPointActor(stubConfig, endpointMatcher)
      sender ! Done(stubConfig.endpoint)

    // Request to modify response of an endpoint
    case UpsertResponse(responseConfig) =>
      log.info(s"receptionist received UpsertResponse message, adding response to " + responseConfig.endpoint)
      val requestMatchOption = endpointMatcher.matchEndpoint(responseConfig.endpoint)
      log.info(s"receptionist attempted to match, result = " + requestMatchOption)
      requestMatchOption match {
        case Some(requestMatch) => requestMatch.handler forward UpsertResponse(responseConfig)
        case _ => sender ! StubResponse(NotFound.intValue, NotFound.reason)
      }

    // Real request
    case request: HttpRequest =>
      log.info(s"receptionist received message [" + request.uri.toString() + "]")
      val requestMatchOption = endpointMatcher.matchRequest(request)
      log.info(s"receptionist attempted to match, result = " + requestMatchOption)
      requestMatchOption match {
        case Some(requestMatch) => requestMatch.handler forward requestMatch
        case _ => sender ! StubResponse(NotFound.intValue, NotFound.reason)
      }

    case EndpointMetricsGet =>
      log.info("fetching metrics for all endpoints")
      metricsCollector forward EndpointMetricsGet

      // unexpected messages
    case x =>
      log.info("Receptionist received unexpected message: " + x.toString)
  }

  def createMetricsCollector: ActorRef = context.actorOf(MetricsCollectorActor.props, "metricsCollector")
}


