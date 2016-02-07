package castalia

import akka.actor._
import akka.http.scaladsl.model.HttpRequest
import castalia.actors.{JsonEndpointActor, JsonResponseProviderEndpointActor, JsonResponsesEndpointActor}
import castalia.matcher.RequestMatcherActor
import castalia.matcher.RequestMatcherActor.{AddMatcher, FindMatchAndForward}
import castalia.metrics.MetricsCollectorActor
import castalia.model.Messages.{Done, EndpointMetricsGet, UpsertEndpoint}
import castalia.model.Model.StubConfig

object Receptionist {
  def props: Props = Props[Receptionist]
}

class Receptionist extends Actor with ActorLogging {

  val metricsCollector = createMetricsCollector

  val requestMatcherActor = context.actorOf(RequestMatcherActor.props())

  private def upsertEndPointActor(stubConfig: StubConfig) = {
    def endpointActorFactory(stubConfig: StubConfig): JsonEndpointActor = {
      if (stubConfig.responseprovider.isDefined) new JsonResponseProviderEndpointActor(stubConfig, metricsCollector)
      else if (stubConfig.responses.isDefined) new JsonResponsesEndpointActor(stubConfig, metricsCollector)
      else throw new UnsupportedOperationException
    }

    val actor = context.actorOf(Props(endpointActorFactory(stubConfig)))
    requestMatcherActor ! AddMatcher(stubConfig.segments, actor)
    log.debug(s"Registering matcher with segments ${stubConfig.segments}")
  }

  def receive: Receive = {
    // Request to modify config
    case UpsertEndpoint(stubConfig) =>
      log.info(s"receptionist received UpsertEndpoint message, adding endpoint " + stubConfig.endpoint)
      upsertEndPointActor(stubConfig)
      sender ! Done(stubConfig.endpoint)

    // Real request
    case request: HttpRequest =>
      log.info(s"receptionist received message [" + request.uri.toString() + "]")
      requestMatcherActor ! FindMatchAndForward(request, sender)

    case EndpointMetricsGet =>
      log.info("fetching metrics for all endpoints")
      metricsCollector forward EndpointMetricsGet

    // unexpected messages
    case x =>
      log.info("Receptionist received unexpected message: " + x.toString)
  }

  def createMetricsCollector: ActorRef = context.actorOf(MetricsCollectorActor.props, "metricsCollector")
}


