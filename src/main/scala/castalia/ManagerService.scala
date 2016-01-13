package castalia

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import castalia.Manager.Done
import castalia.model.StubConfig
import scala.concurrent.duration._
import scala.language.postfixOps

class ManagerService(managerActor: ActorRef)(implicit val system: ActorSystem) extends Routes {

  override protected def serviceName: String = "manager"

  implicit val timeout = Timeout(2 second)
  import scala.concurrent.ExecutionContext.Implicits.global

  override def routes: Route =
    path("castalia" / "manager" / "endpoints") {
      post {
        entity(as[StubConfig]) {
          stubConfig =>

            complete {
              (managerActor ? stubConfig)
                .mapTo[Done]
                .map(result => s"${result.endpoint}")
            }
        }
      }
    }

}
