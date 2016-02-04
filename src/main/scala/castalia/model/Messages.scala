package castalia.model

import castalia.Endpoint
import castalia.model.Model.StubConfig

object Messages {
  case class UpsertEndpoint(stubConfig: StubConfig)
  case class Done(endpoint: String)

  case class EndpointCalled(endpoint: Endpoint)
  case class EndpointMetricsInit(endpoint: Endpoint)
  case object EndpointMetricsGet
}
