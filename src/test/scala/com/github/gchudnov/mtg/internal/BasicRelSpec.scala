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
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          assertAny(xx, yy)
        }
      }

      "manual check" in {
        val intervals = List(
          (Interval.rightOpen(5), Interval.rightOpen(5)) // (-inf, 5)  (5, +inf)
        )

        intervals.foreach { case (xx, yy) => assertAny(xx, yy) }
      }
    }
  }
