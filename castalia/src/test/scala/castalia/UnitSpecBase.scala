package castalia

import org.scalatest.{Matchers, WordSpec}

/**
  * Base trait for unit tests,
  * to be used when testing classes and objects
  */
trait UnitSpecBase extends WordSpec with Matchers
