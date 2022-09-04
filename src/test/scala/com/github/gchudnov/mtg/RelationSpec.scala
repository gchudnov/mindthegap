package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Relation.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class RelationSpec extends TestSpec:

  given intRange: IntRange = intRange5

  "Relation" when {

    "meets & metBy" should {

      /**
       * {{{
       *  Meets:
       *  [AAA]
       *      [BBB]
       *
       * IsMetBy
       *  [BBB]
       *      [AAA]
       * }}}
       */
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((x, y), ix, iy), ((w, z), iw, iz)) =>
          val xy = Interval.make(x, y, ix, iy)
          val wz = Interval.make(w, z, iw, iz)

          // println((xy, wz))

          if xy.meets(wz) then
            wz.isMetBy(xy) mustBe (true)
            (y, w) match
              case (Some(u), Some(v)) =>
                (iy && iw) mustBe (true)
                u mustEqual (v)
              case _ =>
                fail("When two intervals are met, both boundaries must be finite.")
          else
            (y, w) match
              case (Some(u), Some(v)) =>
                val isEq = (u == v)
                (!isEq || ((iy && iw) == false)) mustBe true
              case _ =>
                succeed
        }
      }

    }
  }

/*
We can posit prop­er­ties like these:

Given any three time­stamps f1, f2, and f3, if [f1,f2] con­tains f3, then f3 >= f1 and f3 <= f2. Else, f3 < f1 or f3 > f2.

Given any two time­stamps f1 and f2, if (,f1) over­laps (f2,), then f1 > f2. Else, f1 <= f2.
 */
