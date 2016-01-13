package castalia

import java.lang.management.ManagementFactory

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import castalia.model.Model.CastaliaStatusResponse

class StatusService()(implicit val system: ActorSystem) extends Routes {
  protected val serviceName = "StatusService"
  override def routes: Route = pathPrefix("status") {
    get {
      complete(StatusCodes.OK, CastaliaStatusResponse(ManagementFactory.getRuntimeMXBean.getUptime))
    }
  }
}
