package castalia.actors

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * Base class for actor tests,
  * to be used when testing actors
  * @param _system - actor system to be used in the test
  */
class ActorSpecBase(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  protected implicit val timeout: Timeout = Timeout(2, TimeUnit.SECONDS)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
