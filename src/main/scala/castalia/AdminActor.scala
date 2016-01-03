package castalia


import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import castalia.AdminActor.{Undeploy, UpdateConfig}
import castalia.model.CastaliaConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object AdminActor {

  case class UpdateConfig(configFile: String)

  case class Undeploy()

}

class AdminActor extends Actor with Config with ActorLogging {
  implicit val timeout = Timeout(3.seconds)
  protected implicit val system: ActorSystem = ActorSystem()
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()
  var stubServerBinding: Option[ServerBinding] = None

  log.info(s"Constructor of AdminActor => ${this}")

  def receive: PartialFunction[Any, Unit] = {
    case Undeploy =>
      log.info("Undeploy message received")

      stubServerBinding match {
        case Some(aStubServerBinding) => {
          aStubServerBinding.unbind()
          stubServerBinding = None
          sender ! true
        }
        case _ => {
          log.info("No stubserver was running yet")
          sender ! false
        }
      }
    case UpdateConfig(configFile) =>
      log.info(s"UpdateConfig message received with parameter: $configFile")

      val castaliaConfig = CastaliaConfig.parse(configFile)
      val stubsByEndPoint = StubConfigParser.readAndParseStubConfigFiles(castaliaConfig.stubs)
      val services = List(
        //new StatusService,
        new StubService(stubsByEndPoint)
      )
      val routes = services.map(f => f.routes).reduceLeft(_ ~ _)

      val eventualServerBinding = Http().bindAndHandle(routes, httpInterface, 9002)
      stubServerBinding = Some(Await.result(eventualServerBinding, 3.seconds))
      //      aServerBinding match {
      //        case Some(aFutureBinding) => {
      //          val x: ServerBinding = Await.result(aFutureBinding, 3.seconds)
      //        }
      //        case _ => {
      //          None
      //        }
      //      }


      sender ! true
  }

}
