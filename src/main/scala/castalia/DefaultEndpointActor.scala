package castalia

import akka.actor.{ActorLogging, Props}

object DefaultEndpointActor {
  def props(): Props = Props(new DefaultEndpointActor())
}

class DefaultEndpointActor extends Receptionist with ActorLogging {
  override def receive : Receive = {
    case x => {
      sender ! "Some body123"
    }
  }
}