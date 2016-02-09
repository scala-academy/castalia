package castalia.actors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.TestProbe
import castalia.management.Manager
import castalia.model.Messages.{Done, EndpointMetricsGet, UpsertEndpoint}
import castalia.model.Model.{ResponseConfig, StubConfig}

import scala.language.postfixOps

class ManagerSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("ManagerSpec"))

  "Manager actor" must {

    val receptionistProbe = TestProbe()
    val manager = system.actorOf(Manager.props(receptionistProbe.ref))
    val stubConfig = StubConfig("my/endpoint", None, Some(List(ResponseConfig(None, None, OK.intValue, None))), None)

    "ask receptionist and send back Done message" in {
      manager ! stubConfig
      receptionistProbe.expectMsg(UpsertEndpoint(stubConfig))
      receptionistProbe.reply(Done(stubConfig.endpoint))
    }

    "ask receptionist to get endpoint metrics" in {
      manager ! EndpointMetricsGet
      receptionistProbe.expectMsg(EndpointMetricsGet)
    }
  }
}
