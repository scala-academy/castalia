package castalia

import akka.http.scaladsl.server.{RequestContext, StandardRoute}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.actor.Actor.Receive
import akka.actor._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.pattern.ask
import akka.pattern.pipe
import scala.concurrent.duration._

object Receptionist {
  case class UpSertEndpoint(stubConfigJSON: String)
  case object CreateEndPointActor

  def props: Props = Props[Receptionist]
}

class Receptionist extends Actor with ActorLogging {

  import  Receptionist._

  implicit val timeout = Timeout(5.seconds)

  val endpointActor = createEndPointActor()

  def createEndPointActor(): ActorRef = {
    context.actorOf(EndpointActor.props, "default")
  }

  override def receive: Receive = {
    case UpSertEndpoint(stubConfigJSON: String) =>
      log.info(s"UpSertEndpoint.")
    case CreateEndPointActor =>
      log.info(s"CreateEndPointActor.")
    case requestContext : RequestContext   =>
      log.info(s"stubsRoute.")
      // TODO wrap in correct case class
      endpointActor.forward(requestContext)
  }


  def getActor(path : String) : ActorRef = {
    val uriSegments = path.split("/")

    val actor = if (uriSegments.isEmpty) {
      endpointActor
    } else {
      log.debug("eerste path" + uriSegments(1))
      endpointActor // some work here
    }
    actor
  }

  val stubsRoute = {
    get {
      extract(_.request) { request =>
        val eventualResponse = getActor(request.uri.path.toString()) ? request
        onSuccess(eventualResponse) {
          response => {
            complete(response.asInstanceOf[String])
          }
        }
      }
    }
  }
}


