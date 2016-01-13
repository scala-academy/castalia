package castalia

import akka.actor.{Props, ActorSystem}
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.Http.ServerBinding
//import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.io.IO
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.can.Http
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import castalia.model.CastaliaConfig
import akka.pattern.ask
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App with Config {
  protected implicit val system: ActorSystem = ActorSystem()
  val stubService = system.actorOf(Props[Receptionist], "stubsApi")

  implicit val timeout = Timeout(2.seconds)
  val future = (IO(Http) ? Http.Bind(stubService, httpInterface, httpPort)).map {
    case _: Http.Bound => true
    case _ => false
  }

}
