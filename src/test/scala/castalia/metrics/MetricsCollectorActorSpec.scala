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

      metricsCollectorActor ! EndpointMetricsGet(None)
      expectMsg(EndpointMetrics(Map(endpoint -> Map(metricNumberOfCalls -> 0))))
    }

    "increment metrics for an endpoint" in {
      val metricsCollectorActor = system.actorOf(MetricsCollectorActor.props)

      val endpoint = "my/endpoint/increment"

      metricsCollectorActor ! EndpointCalled(endpoint)
      metricsCollectorActor ! EndpointCalled(endpoint)

      metricsCollectorActor ! EndpointMetricsGet(None)
      expectMsg(EndpointMetrics(Map(endpoint -> Map(metricNumberOfCalls -> 2))))
    }

    "get metrics for a single endpoint" in {
      val metricsCollectorActor = system.actorOf(MetricsCollectorActor.props)

      val endpoint1 = "my/endpoint/1"
      val endpoint2 = "my/endpoint/2"

      metricsCollectorActor ! EndpointCalled(endpoint1)
      metricsCollectorActor ! EndpointCalled(endpoint2)
      metricsCollectorActor ! EndpointCalled(endpoint2)

      metricsCollectorActor ! EndpointMetricsGet(None)
      expectMsg(EndpointMetrics(Map(endpoint1 -> Map(metricNumberOfCalls -> 1),
        endpoint2 -> Map(metricNumberOfCalls -> 2))))

      metricsCollectorActor ! EndpointMetricsGet(Some(endpoint2))
      expectMsg(EndpointMetrics(Map(endpoint2 -> Map(metricNumberOfCalls -> 2))))
    }
  }

}
