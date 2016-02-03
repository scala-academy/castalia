package castalia.metrics

import castalia._

class MetricsRegistry(val metricsByEndpoint: Map[Endpoint, Metrics]) {

  def this() = this(Map())

  def reset(endpoint: Endpoint, metric: String): MetricsRegistry = {
    add(endpoint, metric, 0)
  }

  def increment(endpoint: Endpoint, metric: String): MetricsRegistry = {
    val value = metricsByEndpoint.getOrElse(endpoint, Map()).getOrElse(metric, 0) + 1
    add(endpoint, metric, value)
  }

  private def add(endpoint: Endpoint, metric: String, value: Int) = {
    val metrics = metricsByEndpoint.getOrElse(endpoint, Map()) ++ Map(metric -> value)
    new MetricsRegistry(metricsByEndpoint + (endpoint -> metrics))
  }
}
