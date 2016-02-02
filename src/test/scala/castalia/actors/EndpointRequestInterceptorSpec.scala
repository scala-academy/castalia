package castalia.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.contrib.pattern.ReceivePipeline
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.TestProbe
import castalia.matcher.RequestMatch
import castalia.model.Messages.{Done, EndpointCalled, EndpointMetricsInit}
import castalia.model.Model.StubConfig

class EndpointRequestInterceptorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("EndpointRequestInterceptor"))

  "EndpointRequestInterceptor" must {

    val stubConfig = StubConfig("my endpoint", None, None, None)
    val metricsCollector = new TestProbe(_system)

    "send message to reset stats and collect stats" in {

      val actorWithInterceptor = system.actorOf(Props(new AnActor(stubConfig, metricsCollector.ref)
        with EndpointRequestInterceptor))

      // send initial reset stats endpoint metrics
      metricsCollector.expectMsg(EndpointMetricsInit(stubConfig.endpoint))

      actorWithInterceptor ! RequestMatch(new HttpRequest(), List(), List(), metricsCollector.ref)

      // metrics provider will intercept RequestMatch message and add it's own behaviour on top of Done message back
      expectMsg(Done(stubConfig.endpoint))
      metricsCollector.expectMsg(EndpointCalled(stubConfig.endpoint))
    }

  }

  class AnActor(myStubConfig: StubConfig, myMetricsCollector: ActorRef) extends Actor with ReceivePipeline {
    def stubConfig: StubConfig = myStubConfig
    def metricsCollector: ActorRef = myMetricsCollector

    def receive: Receive = {
      case m: RequestMatch ⇒ sender ! Done(stubConfig.endpoint)
    }
  }
}
