package castalia.management

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import castalia.model.Messages.{EndpointMetricsGet, UpsertEndpoint, UpsertResponse}
import castalia.model.Model.{EndpointResponseConfig, StubConfig}

import scala.concurrent.duration._
import scala.language.postfixOps

case object Manager {
  def props(receptionist: ActorRef): Props = Props(new Manager(receptionist))
}

/**
  * Actor that delegates request to adjust the endpoint configuration to a receptionist actor.
  */
class Manager(receptionist: ActorRef) extends Actor with ActorLogging {

  private implicit val timeout = Timeout(2 seconds)

  override def receive: Receive = {
    case config: StubConfig =>
      log.debug(s"received message to adjust configuration for '${config.endpoint}'")
      receptionist forward UpsertEndpoint(config)
    case EndpointMetricsGet =>
      log.debug("received message to fetch metrics")
      receptionist forward EndpointMetricsGet
    case config: EndpointResponseConfig =>
      log.debug(s"received message to adjust response for '${config.endpoint}'")
      receptionist forward UpsertResponse(config)
    case x => log.debug("Unexpected message received: " + x.toString)
  }
}
