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

  val castaliaConfig = CastaliaConfig.parse("castalia.json")

  //val stubsByEndPoint: StubConfigsByEndpoint = StubConfigParser.readAndParseStubConfigFiles(args)
  val stubsByEndPoint = {
    if (args.length > 0)
      StubConfigParser.readAndParseStubConfigFiles(args)
    else throw new IllegalArgumentException("Please specify a config file as first argument")
  }

  val services = List(
    new StatusService,
    new StubService(stubsByEndPoint)
  )

  val routes = services.map(f => f.routes).reduceLeft(_ ~ _)

  Http().bindAndHandle(routes, httpInterface, castaliaConfig.httpPort)
}
