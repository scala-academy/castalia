package castalia.matcher

import akka.actor._
import castalia.matcher.MatchResultGatherer.MatchNotFound
import castalia.model.Model.StubResponse
import akka.http.scaladsl.model.StatusCodes._

/**
  * Created by Jordi on 4-2-2016.
  */
object MatchResultGatherer {
  case object MatchNotFound
  def props(nOfMatchers: Int, origin: ActorRef): Props = Props(new MatchResultGatherer(nOfMatchers, origin))
}

class MatchResultGatherer(nrOfMatchers: Int, origin: ActorRef) extends Actor with ActorLogging {

  def receive: Receive = awaitResponses(false, nrOfMatchers)

  def awaitResponses(responseSent: Boolean, responsesToGet: Int): Receive = {
    case MatchNotFound if responsesToGet > 1 => context.become(awaitResponses(responseSent, responsesToGet - 1))
    case MatchNotFound => {
      origin ! StubResponse(NotFound.intValue, NotFound.reason)
      self ! Kill
    }
    case anyResponse if !responseSent && responsesToGet > 0 => {
      log.debug(s"Sending response to origin (${origin})")
      origin ! anyResponse
      context.become(awaitResponses(true, responsesToGet - 1))
    }
    case anyResponse if responsesToGet > 1 => {
      context.become(awaitResponses(true, responsesToGet - 1))
    }
    case _ =>
      self ! Kill
  }
}
