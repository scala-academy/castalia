package castalia

import castalia.model.Model.{ResponseConfig, DefaultResponseConfig, StubConfig}


object StubConfigParser {

  def parseStubConfig(jsonFile: String): StubConfig = {
    val initialConfig = JsonConverter.parseJson[StubConfig](jsonFile)
    (initialConfig.default) match {
      case (Some(default)) =>
        initialConfig.responses match {
          case None =>
            initialConfig
          case Some(initialResponses) => StubConfig(
            initialConfig.endpoint,
            initialConfig.default,
            Some(addDefaults(default, initialResponses)),
            initialConfig.responseprovider)
        }
      case _ => initialConfig
    }

  }

  def parseStubConfigs(jsonFiles: List[String]): List[StubConfig] = {
    jsonFiles.map(parseStubConfig(_))
  }

  private def addDefaults(default: DefaultResponseConfig, responses: List[ResponseConfig]): List[ResponseConfig] =
    (responses) match {
      case (Nil) => Nil
      case (first :: rest) => mix(default, first) :: addDefaults(default, rest)
    }

  private def mix(default: DefaultResponseConfig, response: ResponseConfig) = {
    val delayOption = (default.delay, response.delay) match {
      case (_, Some(delay)) => Some(delay)
      case (default, _) => default
    }
    // future extension: httpStatusCode and response in ResponseConfig become Option, and default can be mixed in

    ResponseConfig(response.ids, delayOption, response.httpStatusCode, response.response)
  }
}
