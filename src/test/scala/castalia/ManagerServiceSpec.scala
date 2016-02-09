package castalia

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.TestActor.{AutoPilot, NoAutoPilot}
import akka.testkit.TestProbe
import castalia.management.{Manager, ManagerService}
import castalia.model.Messages.{Done, EndpointMetricsGet, UpsertEndpoint, UpsertResponse}
import castalia.model.Model.{EndpointMetrics, EndpointResponseConfig, ResponseConfig, StubConfig}

class ManagerServiceSpec extends ServiceSpecBase with SprayJsonSupport {

  val receptionistMock = TestProbe()
  val actor = system.actorOf(Manager.props(receptionistMock.ref))
  val parentSystem = system
  val service = new ManagerService {
    override protected def managerActor: ActorRef = actor

    override protected implicit val system: ActorSystem = parentSystem
  }

 "ManagerService" should {

    "accept endpoint insert/update" in {
      val stubConfig = new StubConfig("my/endpoint", None, Some(List(ResponseConfig(None, None, OK.intValue, None))), None)

      receptionistMock.setAutoPilot(new AutoPilot {
        override def run(sender: ActorRef, msg: Any): AutoPilot = msg match {
          case UpsertEndpoint(config) => sender ! Done(config.endpoint)
            NoAutoPilot
        }
      })

      Post("/castalia/manager/endpoints", stubConfig) ~> service.managementRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldBe stubConfig.endpoint
      }
    }

   "accept request insert/update" in {
     val responseConfig = new EndpointResponseConfig("my/endpoint", ResponseConfig(None, None, OK.intValue, None))

     receptionistMock.setAutoPilot(new AutoPilot {
       override def run(sender: ActorRef, msg: Any): AutoPilot = msg match{
         case UpsertResponse(config) => sender ! Done(config.endpoint)
           NoAutoPilot
       }
     })

     Post("/castalia/manager/endpoints/responses", responseConfig) ~> service.managementRoute ~> check {
       status shouldBe OK
       responseAs[String] shouldBe responseConfig.endpoint
     }
   }

    "accept get metrics request" in {
     val metrics = Map("some/$1/endpoint" -> Map("calls" -> 1))

     receptionistMock.setAutoPilot(new AutoPilot {
       override def run(sender: ActorRef, msg: Any): AutoPilot = msg match {
         case EndpointMetricsGet => sender ! EndpointMetrics(metrics)
           NoAutoPilot
       }
     })

     Get("/castalia/manager/endpoints/metrics") ~> service.managementRoute ~> check {
       status shouldBe OK
       responseAs[EndpointMetrics] shouldBe EndpointMetrics(metrics)
     }
    }
  }
}
