package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import castalia.model.CastaliaConfig

object Main extends App with Config {
  protected val serviceName = "Main"
  protected implicit val system: ActorSystem = ActorSystem()
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  val castaliaConfig = if (args.length > 0) {
    CastaliaConfig.parse(args(0))
  } else {
    throw new IllegalArgumentException("Please specify a config file as first argument")
  }

  val stubConfigs = StubConfigParser().parseStubConfigs(castaliaConfig.stubs)

  val services = List(
    new StatusService,
    new StubService(stubConfigs)
  )

  val routes = services.map(f => f.routes).reduceLeft(_ ~ _)

  Http().bindAndHandle(routes, httpInterface, castaliaConfig.httpPort)
}
