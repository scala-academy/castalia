package castalia

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

//import akka.http.scaladsl.Http
//import akka.http.scaladsl.Http.ServerBinding
//import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Main extends App with Config {
  //  protected val serviceName = "Main"
  protected implicit val system: ActorSystem = ActorSystem()
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  val receptionist: ActorRef = system.actorOf(Receptionist.props, "stubsApi")

  val route: Route = {
    context => (receptionist ? context).mapTo[RouteResult]
  }

  implicit val timeout = Timeout(2.seconds)
  val future: Future[ServerBinding] =
    Http().bindAndHandle(route, httpInterface, httpPort)
}
