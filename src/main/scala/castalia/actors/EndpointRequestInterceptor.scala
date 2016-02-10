package castalia.actors

import akka.actor.ActorRef
import akka.contrib.pattern.ReceivePipeline
import akka.contrib.pattern.ReceivePipeline.Inner
import castalia.matcher.RequestMatch
import castalia.model.Messages._
import castalia.model.Model.StubConfig

/**
  * Triggers registration of endpoint stats by intercepting RequestMatch messages
  * send to the actors extending this trait.
  */
trait EndpointRequestInterceptor {

  this: ReceivePipeline =>

  def endpoint: String
  def metricsCollector: ActorRef

  metricsCollector ! EndpointMetricsInit(endpoint)

  pipelineOuter {
    case rm: RequestMatch =>
      // intercept request match request
      metricsCollector ! EndpointCalled(endpoint)
      // continue with actor receive
      Inner(rm)
    case other => Inner(other)
  }

}
