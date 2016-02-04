package castalia.metrics

import akka.actor.ActorSystem
import castalia.actors.ActorSpecBase
import castalia.metrics.MetricsCollectorActor._
import castalia.model.Messages._
import castalia.model.Model.EndpointMetrics

class MetricsCollectorActorSpec(_system: ActorSystem) extends ActorSpecBase(_system) {

  def this() = this(ActorSystem("MetricsCollectorActor"))

  "MetricsCollector actor" must {

    "reset metrics for endpoint" in {
      val metricsCollectorActor = system.actorOf(MetricsCollectorActor.props)

      val endpoint = "my/endpoint/reset"

      metricsCollectorActor ! EndpointMetricsInit(endpoint)

      metricsCollectorActor ! EndpointMetricsGet
      expectMsg(EndpointMetrics(Map(endpoint -> Map(metricNumberOfCalls -> 0))))
    }

    "increment metrics for an endpoint" in {
      val metricsCollectorActor = system.actorOf(MetricsCollectorActor.props)

      val endpoint = "my/endpoint/increment"

      metricsCollectorActor ! EndpointCalled(endpoint)
      metricsCollectorActor ! EndpointCalled(endpoint)

      metricsCollectorActor ! EndpointMetricsGet
      expectMsg(EndpointMetrics(Map(endpoint -> Map(metricNumberOfCalls -> 2))))
    }
  }

}
