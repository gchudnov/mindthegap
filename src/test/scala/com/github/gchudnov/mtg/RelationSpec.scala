package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Relation.*
import com.github.gchudnov.mtg.ordering.OptionPartialOrdering
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

import scala.math.PartialOrdering

final class RelationSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 200)

  val pOrd: PartialOrdering[Option[Int]] = summon[PartialOrdering[Option[Int]]]

  "Relation" when {

    "meets & metBy" should {

      /**
       * Meets, IsMetBy
       *
       * {{{
       *   AAA]
       *      [BBB
       * }}}
       */
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          if xy.meets(wz) then
            val isYeqW = pOrd.equiv(oy, ow)

            println((xy, wz))

            wz.isMetBy(xy) mustBe (true)

            isYeqW mustEqual (true)
            (iy && iw) mustBe (true)
          else
            val isYeqW = pOrd.equiv(oy, ow)
            val isXgtY = pOrd.gt(ox, oy)
            val isWgtZ = pOrd.gt(ow, oz)
            val isXeqY = pOrd.eq(ox, oy)
            val isWeqZ = pOrd.eq(ow, oz)

            val a = (!isYeqW || (iy && iw) == false)
            val b = isXgtY
            val c = isWgtZ
            val d = (isXeqY || (ix && iy) == false)
            val e = (isWeqZ || (iw && iz) == false)

            (a || b || c || d || e) mustBe (true)
        }
      }

    }
  }

/*
We can posit prop­er­ties like these:

Given any three time­stamps f1, f2, and f3, if [f1,f2] con­tains f3, then f3 >= f1 and f3 <= f2. Else, f3 < f1 or f3 > f2.

Given any two time­stamps f1 and f2, if (,f1) over­laps (f2,), then f1 > f2. Else, f1 <= f2.
 */
