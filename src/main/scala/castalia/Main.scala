package castalia

import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import castalia.model.CastaliaConfig
import akka.pattern.ask
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App with Config {
  protected val serviceName = "Main"
  protected implicit val system: ActorSystem = ActorSystem()
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val timeout = Timeout(3.seconds)

  val adminRef = system.actorOf(Props[AdminActor],"AdminActor")

  val castaliaConfig: CastaliaConfig = if (args.length > 0) {
    CastaliaConfig.parse(args(0))
  }
  else {
    throw new IllegalArgumentException("Please specify a config file as first argument")
  }
//
  val stubsByEndPoint = StubConfigParser.readAndParseStubConfigFiles(castaliaConfig.stubs)

  val services = List(
    new StatusService,
    new StubService(stubsByEndPoint)
  )

  val routes = services.map(f => f.routes).reduceLeft(_ ~ _)

  val x: Future[ServerBinding] = Http().bindAndHandle(routes, httpInterface, castaliaConfig.httpPort)


  val adminServices: List[Routes] = List(
    new StatusService,
    new AdminService()
  )

  val adminRoutes = adminServices.map(f => f.routes).reduceLeft(_ ~ _)



  val y: Future[ServerBinding] = Http().bindAndHandle(adminRoutes, httpInterface, 9001)


}
