package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Relation.*
import com.github.gchudnov.mtg.ordering.OptionPartialOrdering
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

import scala.math.PartialOrdering

final class RelationSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 100.0)

  val pOrd: PartialOrdering[Option[Int]] = summon[PartialOrdering[Option[Int]]]

  "Relation" when {

    /**
     * Preceeds, IsPreceededBy
     *
     * {{{
     *   AAA]
     *        [BBB
     * }}}
     */
    "preceeds & isPreceededBy" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.preceeds(wz)) {
            // println(s"p: ${(xy, wz)}")

            wz.isPreceededBy(xy) mustBe (true)

            xy.meets(wz) mustBe (false)
            wz.isMetBy(xy) mustBe (false)

            val isYltW = pOrd.lt(oy, ow)
            val isYeqW = pOrd.equiv(oy, ow)

            (isYltW || (isYeqW && !(iy && iw))) mustBe (true)
          }
        }
      }
    }

    /**
     * Meets, IsMetBy
     *
     * {{{
     *   AAA]
     *      [BBB
     * }}}
     */
    "meets & metBy" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.meets(wz)) {
            // println(s"m: ${(xy, wz)}")

            wz.isMetBy(xy) mustBe (true)

            xy.preceeds(wz) mustBe (false)
            wz.isPreceededBy(xy) mustBe (false)

            val isYeqW = pOrd.equiv(oy, ow)

            (isYeqW && (iy && iw)) mustBe (true)
          }
        }
      }

    }
  }

/*
We can posit prop­er­ties like these:

Given any three time­stamps f1, f2, and f3, if [f1,f2] con­tains f3, then f3 >= f1 and f3 <= f2. Else, f3 < f1 or f3 > f2.

Given any two time­stamps f1 and f2, if (,f1) over­laps (f2,), then f1 > f2. Else, f1 <= f2.
 */
