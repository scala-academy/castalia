package castalia.model

import castalia.Endpoint
import castalia.model.Model.{EndpointResponseConfig, StubConfig}

object Messages {
  case class UpsertEndpoint(config: StubConfig)
  case class UpsertResponse(config: EndpointResponseConfig)
  case class Done(endpoint: String)

  case class EndpointCalled(endpoint: Endpoint)
  case class EndpointMetricsInit(endpoint: Endpoint)
  case object EndpointMetricsGet
}
