package castalia.actors

import java.lang.reflect.Method

import castalia.matcher.RequestMatch
import castalia.model.Model.{StubConfig, StubResponse}
import scala.concurrent.Future
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Actor that provides answers based on the programmed class mentioned in the json configuration that is used to create this actor
  *
  * Created on 2016-01-23
  */
class JsonResponseProviderEndpointActor(myStubConfig: StubConfig) extends JsonEndpointActor(myStubConfig) {

  private val programmedStub: (Class[_], Method) =
    myStubConfig.responseprovider match {
      case Some(responseProvider) =>
        val `class` = ClassLoader.getSystemClassLoader.loadClass(responseProvider.`class`)
        val member = `class`.getDeclaredMethod(responseProvider.member, classOf[RequestMatch])

        (`class`, member)
      case None => throw new Exception(s"stubconfig doesn't contain a responseProvider")
    }
  protected def invokeProgrammedStub(requestMatch: RequestMatch): Future[StubResponse] = {
    val instance = programmedStub._1.newInstance
    val res = programmedStub._2.invoke(instance, requestMatch).asInstanceOf[Future[StubResponse]]
    res
  }

  override def receive: Receive = {
    case request: RequestMatch =>
      invokeProgrammedStub(request) pipeTo sender
  }
}
