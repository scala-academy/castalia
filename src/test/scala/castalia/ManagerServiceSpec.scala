package castalia

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.TestProbe
import castalia.model.{ResponseConfig, StubConfig}

class ManagerServiceSpec extends ServiceTestBase with Protocol with SprayJsonSupport {

  val actor = system.actorOf(Manager.props(TestProbe().ref))
  val service = new ManagerService(actor)

  "posting new route" should {
    "result in status HTTP 200" in {
      val stubConfig = StubConfig("my/endpoint", List(ResponseConfig(None, None, OK.intValue, None)))
      Post("/castalia/manager/endpoints", stubConfig) ~> service.routes ~> check {
        // TODO
        //status shouldBe OK
        //responseAs[String] shouldBe stubConfig.endpoint
      }
    }
  }

}
