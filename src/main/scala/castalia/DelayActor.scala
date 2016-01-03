package castalia


import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import castalia.DelayActor.{DelayRoute, LatencyTick}

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration


object DelayActor {

  case class LatencyTick(promise: Promise[akka.http.scaladsl.server.RouteResult], routeResult:akka.http.scaladsl.server.RouteResult)
  case class DelayRoute(duration:FiniteDuration, routeResult: akka.http.scaladsl.server.RouteResult)

}

class DelayActor extends Actor with ActorLogging {

  log.info(s"Constructor of DelayActor => ${this}")
  def receive: PartialFunction[Any, Unit] = {
    case LatencyTick(promise, routeResult) =>
      log.info(s"LatencyTick($promise, $routeResult) => ${this}")

      promise.success(routeResult)

    case DelayRoute(duraton, routeResult) =>
      import scala.concurrent.ExecutionContext.Implicits.global
      log.info(s"DelayRoute($duraton, $routeResult) => ${this}")

      val promise = Promise[akka.http.scaladsl.server.RouteResult]()
      //val ticker = context.system.scheduler.scheduleOnce(duraton, self, LatencyTick(promise,routeResult))
      promise.future pipeTo sender
  }
}