package castalia

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.TestProbe
import castalia.management.{Manager, ManagerService}
import castalia.model.Messages.{Done, UpsertEndpoint}
import castalia.model.Model.{ResponseConfig, StubConfig}

class ManagerServiceSpec extends ServiceSpecBase with SprayJsonSupport {

  val receptionistMock = TestProbe()
  val actor = system.actorOf(Manager.props(receptionistMock.ref))
  val parentSystem = system
  val service = new ManagerService {
    override protected def managerActor: ActorRef = actor

    override protected implicit val system: ActorSystem = parentSystem
  }

  "posting new route" should {
    "result in status HTTP 200" in {
      val stubConfig = new StubConfig("my/endpoint", List(ResponseConfig(None, None, OK.intValue, None)))
      receptionistMock.expectMsg(UpsertEndpoint(stubConfig))
      receptionistMock.reply(Done(stubConfig.endpoint))

      Post("/castalia/manager/endpoints", stubConfig) ~> service.managementRoute ~> check {
        // TODO
        status shouldBe OK
        responseAs[String] shouldBe stubConfig.endpoint
      }
    }
  }

}
