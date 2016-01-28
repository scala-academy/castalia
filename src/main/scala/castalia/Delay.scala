package castalia

import akka.actor.Scheduler
import akka.pattern.after
import probability_monad.Distribution

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

/**
  * Delay trait: used for mixin with actors.
  */
trait Delay {
  def future[T](f: Future[T],
                delay: FiniteDuration
                )(implicit ec: ExecutionContext, s: Scheduler): Future[T] =
    after(delay, s)(f)
}


trait DelayedDistribution extends Delay {
  def normalDistribution[T](mean: Double,
                            stdev: Double,
                            dist: Distribution[Double]
                          ): Distribution[Double] =
    dist.map(_*stdev + mean)

  def gammaDistribution[T](k: Double, theta: Double): Distribution[Double] = {
    Distribution.gamma(k, theta)
  }
}