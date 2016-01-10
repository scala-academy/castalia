package castalia

import akka.actor.{ActorLogging, Props}

object DefaultActor {
  def props(): Props = Props(new DefaultActor())
}

class DefaultActor extends StubsActor with ActorLogging {
  override def receive = {
    case x => {
      sender ! "Some body123"
    }
  }
}