package castalia

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import castalia.Manager.{Done, UpsertEndpoint}
import castalia.model.StubConfig
import akka.pattern.pipe
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Actor that delegates request to adjust the endpoint configuration to a receptionist actor.
  */
case object Manager {

  case class UpsertEndpoint(stubConfig: StubConfig)
  case class Done(endpoint: String)

  def props(receptionist: ActorRef): Props = Props(new Manager(receptionist))
}

class Manager(receptionist: ActorRef) extends Actor with ActorLogging {

  private implicit val timeout = Timeout(2 seconds)

  import context.dispatcher

  override def receive: Receive = {
    case config: StubConfig =>
      log.debug(s"received message to adjust configuration for '${config.endpoint}'")

      receptionist ? UpsertEndpoint(config) map {
        case endpoint: String => Done(endpoint)
      } pipeTo sender
  }
}
