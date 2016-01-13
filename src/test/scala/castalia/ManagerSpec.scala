package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit.{TestProbe, ImplicitSender, TestActors, TestKit}
import akka.util.Timeout
import castalia.Manager.{UpsertEndpoint, Done}
import castalia.model.{ResponseConfig, StubConfig}
import org.scalatest._
import akka.pattern.ask
import scala.language.postfixOps
import scala.concurrent.duration._

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
    val actor = system.actorOf(Manager.props(receptionistProbe.ref))
    val stubConfig = StubConfig("my/endpoint", List(ResponseConfig(None, None, OK.intValue, None)))

    "ask receptionist and send back Done message" in {
      actor ! stubConfig
      receptionistProbe.expectMsg(UpsertEndpoint(stubConfig))
      receptionistProbe.reply(stubConfig.endpoint)
      expectMsg(Done(stubConfig.endpoint))
    }

  }
}
