package castalia

import akka.actor.{ActorRef, ActorLogging, Props}

object EndpointActor {
  def props(): Props = Props(new EndpointActor())
}

class EndpointActor extends Receptionist with ActorLogging {
  override def receive = {
    case x => {
      sender ! "Some body123"
    }
  }
}