package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class BasicRelSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "BasicRel" when {
    "satisfy one relation only" should {
      import IntervalRelAssert.*

      "auto check" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          assertAny(xx, yy)
        }
      }

      "valid in special cases" in {
        val intervals = List(
          (Interval.rightOpen(5), Interval.rightOpen(5)) // (-inf, 5)  (5, +inf)
        )

        intervals.foreach { case (xx, yy) => assertAny(xx, yy) }
      }
    }
  }
