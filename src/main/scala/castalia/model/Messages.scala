package castalia.model

import castalia.model.Model.StubConfig

object Messages {
  case class UpsertEndpoint(stubConfig: StubConfig)
  case class Done(endpoint: String)
}
