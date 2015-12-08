package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Main extends App with Config with Routes {
  override protected implicit val system: ActorSystem = ActorSystem()
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  def getJsonFiles = Array("C:\\FTJ\\Projects\\castalia\\src\\main\\resources\\example.json")//args

  Http().bindAndHandle(routes, httpInterface, httpPort)
}
