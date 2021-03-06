package castalia.actors

import java.lang.reflect.Method

import akka.actor.ActorRef
import akka.pattern.pipe
import castalia.matcher.RequestMatch
import castalia.model.Model.{StubConfig, ResponseProviderConfig, StubResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.existentials

/**
  * Actor that provides answers based on the programmed class mentioned in the json configuration that is used to create this actor
  *
  * Created on 2016-01-23
  */
class JsonResponseProviderEndpointActor(override val endpoint:String, val responseProvider: ResponseProviderConfig, override val metricsCollector: ActorRef)
  extends JsonEndpointActor {
  private case class ResponseProvider(clazz: Class[_], member: Method)

  private val programmedStub: ResponseProvider = {
    val clazz = Class.forName(responseProvider.clazz)
    val member = clazz.getDeclaredMethod(responseProvider.member, classOf[RequestMatch])
    ResponseProvider(clazz, member)
  }
  protected def invokeProgrammedStub(requestMatch: RequestMatch): Future[StubResponse] = {
    val instance = programmedStub.clazz.newInstance
    programmedStub.member.invoke(instance, requestMatch).asInstanceOf[Future[StubResponse]]
  }

  override def receive: Receive = {
    case request: RequestMatch => invokeProgrammedStub(request) pipeTo sender
    case _@msg => log.error("received unexpected message [" + msg + "]")
  }
}
