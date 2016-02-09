package castalia

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import castalia.management.{Manager, ManagerService}
import castalia.model.Model.{CastaliaConfig, StubResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Main extends App with Config with ManagerService {

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
            val futureResponse: Future[StubResponse] = (receptionist ? requestContext.request).mapTo[StubResponse]
            futureResponse map { stubResponse =>
              RouteResult.Complete(HttpResponse(status = stubResponse.status,
                entity = HttpEntity(ContentTypes.`application/json`, stubResponse.body)))
            }
          }
        }


  val stubServer: Future[ServerBinding] =
    Http().bindAndHandle(stubRoute, httpInterface, httpPort)

  val managerService =
    Http().bindAndHandle(managementRoute, managementHttpInterface, managementHttpPort)
}
