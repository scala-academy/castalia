package castalia.plugins

import akka.http.scaladsl.model.StatusCodes._
import castalia.matcher.RequestMatch
import castalia.matcher.types.Params
import castalia.model.Model.StubResponse
import spray.json.DefaultJsonProtocol

import scala.concurrent.{Future, Promise}


/**
  * TODO: Place this File in another projectr. This project must have a dependecy to this castalia project
  *
  **/
object ProgrammedStub extends DefaultJsonProtocol{

  case class Response(result: String)

  implicit val responseConfigFormat = jsonFormat1(Response)
}

class ProgrammedStub {
  private def findParamOrDefaultValue(params : Params, name : String, default : String) : String = {
    findParamValue(params, name) getOrElse(default)
  }
  private def findParamValue(params : Params, name : String) : Option[String] = {
    val param = params find { case (paramName, paramValue) => paramName.equals(name) }
    param map { case (paramName, paramValue) => paramValue }
  }
  def process1(rc : RequestMatch) : Future[StubResponse] = {
    val promise = Promise[StubResponse]
    val v1 = findParamOrDefaultValue(rc.pathParams, "1", "<nil>")
    val v2 = findParamOrDefaultValue(rc.pathParams, "2", "<nil>")
    val result = ProgrammedStub.Response(s"$v1 with $v2")
    promise.success(new StubResponse(OK.intValue, result.toJson.toString()))
    promise.future
  }
  def process2(rc : RequestMatch) : Future[StubResponse] = {
    val promise = Promise[StubResponse]
    promise.failure(new Exception("some expected failure"))
    promise.future
  }
}