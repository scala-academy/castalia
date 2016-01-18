package castalia

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.HttpResponse
import castalia.management.{Manager, ManagerService}
import castalia.model.CastaliaConfig
import castalia.model.Messages.UpsertEndpoint

//import akka.http.scaladsl.Http
//import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Main extends App with Config with ManagerService {
  //  protected val serviceName = "Main"
  implicit val timeout = Timeout(2.seconds)
  implicit val system: ActorSystem = ActorSystem()
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  val receptionist: ActorRef = system.actorOf(Receptionist.props, "stubsApi")
  override val managerActor : ActorRef = system.actorOf(Manager.props(receptionist), "manager")

  println("Starting stubserver, there are " + args.length + " arguments...")

  val castaliaConfig: CastaliaConfig = {
    if (args.length > 0) {
      println("parsing " + args(0))
      CastaliaConfig.parse(args(0))
    } else {
      CastaliaConfig()
    }
  }

  println("Starting stubserver, attempt to load " + castaliaConfig.stubs.length + " stubs...")
  castaliaConfig.stubs foreach {
    stub =>
      val stubConfig = StubConfigParser.parseStubConfig(stub)
      managerActor ! stubConfig
  }

  val stubRoute: Route = {
          requestContext => {
            val theResponse = receptionist ? requestContext

            theResponse.mapTo[RouteResult]
          }
        }


  val stubServer: Future[ServerBinding] =
    Http().bindAndHandle(stubRoute, httpInterface, httpPort)

  val managerService =
    Http().bindAndHandle(managementRoute, managementHttpInterface, managementHttpPort)
}
