package castalia.metrics

import akka.actor.{Actor, ActorLogging, Props}
import castalia.metrics.MetricsCollectorActor._
import castalia.model.Messages.{EndpointCalled, EndpointMetricsGet, EndpointMetricsInit}
import castalia.model.Model.EndpointMetrics

object MetricsCollectorActor {

  val metricNumberOfCalls = "calls"

  def props: Props = Props(new MetricsCollectorActor)
}

/**
  * Actor that aggregates various metrics specific to endpoints.
  */
class MetricsCollectorActor extends Actor with ActorLogging {

  override def receive: Receive = active(new MetricsRegistry())

  def active(metricsRegistry: MetricsRegistry): Receive = {

    case EndpointMetricsInit(endpoint) => context.become(active(metricsRegistry.reset(endpoint, metricNumberOfCalls)))

    case EndpointCalled(endpoint) => context.become(active(metricsRegistry.increment(endpoint, metricNumberOfCalls)))

    case EndpointMetricsGet => sender ! EndpointMetrics(metricsRegistry.metricsByEndpoint)
  }

}
