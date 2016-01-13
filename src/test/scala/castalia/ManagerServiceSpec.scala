package castalia

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.TestProbe
import castalia.management.{Manager, ManagerService}
import castalia.model.Model.{ResponseConfig, StubConfig}

class ManagerServiceSpec extends ServiceTestBase with SprayJsonSupport {

  val actor = system.actorOf(Manager.props(TestProbe().ref))
  val service = new ManagerService {
    override protected def managerActor: ActorRef = actor

    override protected implicit val system: ActorSystem = system
  }

  "posting new route" should {
    "result in status HTTP 200" in {
      val stubConfig = StubConfig("my/endpoint", List(ResponseConfig(None, None, OK.intValue, None)))
      Post("/castalia/manager/endpoints", stubConfig) ~> service.managementRoute ~> check {
        // TODO
        //status shouldBe OK
        //responseAs[String] shouldBe stubConfig.endpoint
      }
    }
  }

}
