package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  *
  * Created by Erik on 12/24/2015.
  */
//http://localhost:9001/admin/deployConfig?configfile=castalia.json
//http://localhost:9001/admin/undeploy


class AdminService()(implicit val system: ActorSystem) extends Routes {

  implicit val timeout = Timeout(30.seconds)
  val adminActorRef = system.actorSelection("akka://default/user/AdminActor")
  protected val serviceName = "AdminRoutes"

  override def routes: Route = {
    pathPrefix("admin") {
      handleRejections(totallyMissingHandler) {
        path("deployConfig") {
          parameter("configfile") {
            aConfigFile =>
            if ((Await.result(adminActorRef ? AdminActor.UpdateConfig(aConfigFile), timeout.duration).asInstanceOf[Boolean])) {
              complete(200, "Updating configuration successfully")
            } else {
              complete(500, "Updating configuration failed")
            }
          }
        } ~
        path("undeploy") {
          if ((Await.result(adminActorRef ? AdminActor.Undeploy, timeout.duration).asInstanceOf[Boolean])) {
            complete(200, "Undeploy successfully")
          } else {
            complete(500, "There was nothing to undeploy")
          }
        }
      }
    }
  }

  def handleUpdateConfig(): Boolean = {


    val tx = adminActorRef ? "castalia.json"

    val eventualResult = adminActorRef ? AdminActor.UpdateConfig("castalia.json")
    Await.result(eventualResult, timeout.duration).asInstanceOf[Boolean]

    //  val eventualString = slowFunction
    //  val system = ActorSystem("mySystem")
    //    val adminActor = system.actorOf(Props[AdminActor],"AdminActor")
    //    val tx  = adminActor ? "config.json"
    //
    //    val x: String = Await.result(tx, 3.seconds).asInstanceOf[String]
    //
    //    complete(200, x)

  }


}