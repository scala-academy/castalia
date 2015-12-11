package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import castalia.model.CastaliaConfig

object Main extends App with Config with Routes {
  override protected implicit val system: ActorSystem = ActorSystem()
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()


  val castaliaConfig = CastaliaConfig.parse("castalia.json")

  val stubsByEndPoint = readAndParseStubConfigFiles(args)

  Http().bindAndHandle(routes, httpInterface, castaliaConfig.httpPort)
}
