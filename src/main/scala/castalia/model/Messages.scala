package castalia.model

import castalia.model.Model.{EndpointResponseConfig, StubConfig}

object Messages {
  case class UpsertEndpoint(config: StubConfig)
  case class UpsertResponse(config: EndpointResponseConfig)
  case class Done(endpoint: String)
}
