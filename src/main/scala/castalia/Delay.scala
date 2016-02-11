package castalia

import akka.actor.Scheduler
import akka.pattern.after
import probability_monad.Distribution

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration
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
  def normalDistribution(mean: Double,
                            stdev: Double,
                            dist: Distribution[Double]
                           ): Distribution[Double] =
    dist.map(_ * stdev + mean)

  def gammaDistribution(k: Double, theta: Double): Distribution[Double] = {
    Distribution.gamma(k, theta)
  }

  /**
    * continuous probability distributions defined on the interval [0, 1]
    * parametrized by two positive shape parameters, denoted by α and β,
    * that appear as exponents of the random variable and control the shape of the distribution.
    *
    * @param mu mean
    * @param sigma stdev
    * @return Distribution obeying beta: for use scaling should be applied
    */
  def betaDistribution(mu: Double, sigma: Double): Distribution[Double] = {
    val (a, b) = getBetaParametersFromMeanAndSigma(mu, sigma)
    Distribution.beta(a, b)
  }

  def getBetaParametersFromMeanAndSigma(mu: Double, sigma: Double): (Double, Double) = {
    val a = ((1 - mu)/math.pow(sigma,2)-(1/mu))* math.pow(mu,2)
    val b = a * ((1/mu) - 1)
    (a, b)
  }

  /**
    *
    * @param p95 the value to satisfy p95
    * @param p99 the value to satisfy p99
    * @return the WeibullDistribution
    */
  def getWeibullParametersFromPercentiles(p95: Double, p99: Double): (Double, Double) = {

    def getGammaFromPercentiles: Double = {
      (math.log(-math.log(1 - 0.99)) - math.log(-math.log(1 - 0.95))) / (math.log(p99) - math.log(p95))
    }

    def getBetaFromGamma(gamma: Double): Double = {
      p95 / math.pow(-math.log(1 - 0.95), 1 / gamma)
    }

    val gamma = getGammaFromPercentiles
    val beta = getBetaFromGamma(gamma)
    (gamma, beta)
  }

  /**
    * Generate weibull distribution
    *
    * @param p95
    * @param p99
    * @return
    */
  def weibullDistribution(p95: Double, p99: Double): Distribution[Double] = {
    val (g, b) = getWeibullParametersFromPercentiles(p95, p99)
    Distribution.weibull(g, b)
  }


  /**
    * Bisection method (Interval Halving method)
    *
    * @param f Function to find root
    * @param x left edge
    * @param y right edge
    * @param tolerance precision
    * @return The value at which the function's root is.
    */
  @tailrec
  final def halveTheIntervalFP(f: Double => Double,
                               x: Double,
                               y: Double,
                               tolerance: Double): Double = {
    if (Math.abs(x - y) <= tolerance) {
      x
    }
    else {
      val nextGuess = (x + y) / 2.0
      if (signsAreOpposite(f(nextGuess), f(x))) {
        halveTheIntervalFP(f, x, nextGuess, tolerance)
      }
      else {
        halveTheIntervalFP(f, nextGuess, y, tolerance)
      }
    }
  }

  /**
    * The "signs are opposite" helper function.
    */
  def signsAreOpposite(x: Double, y: Double): Boolean = {
    (x < 0 && y > 0) || (x > 0 && y < 0)
  }

  /**
    * Default gamma ratios of 0.5 and 0.9
    *
    * @param x1 value at probability 50%
    * @param x2 value at probability 90%
    * @return The shape and the scale parameter of the gamma distribution that
    *         satisfies the given probabilities and values.
    */
  def gammaRatios(x1: Double, x2: Double): (Double, Double) = {
    gammaRatios(0.5, x1, 0.9, x2)
  }

  /**
    * Function to find roots for gamma distribution parameters
    *
    * @param p1 probalitity of the first term [0,1]
    * @param x1 value at that probability
    * @param p2 probalitity of the second term [0,1]
    * @param x2 value at that probability
    * @return The shape and the scale parameter of the gamma distribution that
    *         satisfies the given probabilities and values.
    */
  def gammaRatios(p1: Double, x1: Double, p2: Double, x2: Double): (Double, Double) = {
    // We don't care about scaling theta -> set it to 1
    // only shape K is relevant
    val theta = 1
    val ratio = x2 / x1

    def f(shape: Double) = jdistlib.Gamma.quantile(p2, shape, theta, true, false) /
      jdistlib.Gamma.quantile(p1, shape, theta, true, false) - ratio

    val left = 0.1
    // arbitrary right side starting point
    val right = 10.0
    val tolerance = 0.0001
    val shape = halveTheIntervalFP(f, left, right, tolerance)

    val scale = x1 / jdistlib.Gamma.quantile(p1, shape, theta, true, false)
    (shape, scale)
  }
}
