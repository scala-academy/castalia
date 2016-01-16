package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import castalia.management.Manager
import castalia.model.Messages.{Done, UpsertEndpoint}
import castalia.model.Model.{ResponseConfig, StubConfig}
import org.scalatest._

import scala.concurrent.duration._
import scala.language.postfixOps

class ManagerSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("ManagerSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private implicit val timeout = Timeout(2 seconds)

  "Manager actor" must {

    val receptionistProbe = TestProbe()
    val manager = system.actorOf(Manager.props(receptionistProbe.ref))
    val stubConfig = StubConfig("my/endpoint", List(ResponseConfig(None, None, OK.intValue, None)))

    "ask receptionist and send back Done message" in {
      manager ! stubConfig
      receptionistProbe.expectMsg(UpsertEndpoint(stubConfig))
      receptionistProbe.reply(Done(stubConfig.endpoint))
      expectMsg(Done(stubConfig.endpoint))
    }
  }
}
