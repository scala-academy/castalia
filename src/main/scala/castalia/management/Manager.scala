package castalia.management

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import castalia.model.Messages.{Done, UpsertEndpoint}
import castalia.model.Model.StubConfig

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Actor that delegates request to adjust the endpoint configuration to a receptionist actor.
  */
case object Manager {
  def props(receptionist: ActorRef): Props = Props(new Manager(receptionist))
}

class Manager(receptionist: ActorRef) extends Actor with ActorLogging {

  private implicit val timeout = Timeout(2 seconds)

  override def receive: Receive = {
    case config: StubConfig =>
      log.debug(s"received message to adjust configuration for '${config.endpoint}'")
      receptionist ! UpsertEndpoint(config)
    case done: Done =>
      log.debug(s"received confirmation that endpoint '${done.endpoint}' was added")
      //context.parent ! Done(done.endpoint)
    case x => log.debug("Unexpected message received: " + x.toString)
  }
}
