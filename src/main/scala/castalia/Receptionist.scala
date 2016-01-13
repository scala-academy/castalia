package castalia

import akka.actor._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RequestContext
import akka.pattern.ask
import akka.util.Timeout
import castalia.model.Messages.{Done, UpsertEndpoint}

import scala.concurrent.duration._

object Receptionist {
  def props: Props = Props[Receptionist]
}

class Receptionist extends Actor with ActorLogging {
  implicit val timeout = Timeout(5.seconds)
  val endpointActor = createEndPointActor()

  def createEndPointActor(): ActorRef = {
    context.actorOf(EndpointActor.props, "default")
  }

  override def receive: Receive = {
    case UpsertEndpoint(stubConfig) =>
      log.info(s"UpSertEndpoint.")
      // TODO update config
      sender() ! Done(stubConfig.endpoint)
    // Real request
    case requestContext: RequestContext =>
      log.info(s"stubsRoute.")
      // TODO wrap in correct case class
      endpointActor.forward(requestContext)
  }


  def getActor(path: String): ActorRef = {
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


