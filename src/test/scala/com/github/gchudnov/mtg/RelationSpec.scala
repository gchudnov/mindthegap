package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Relation.*

final class RelationSpec extends TestSpec:

  "Relation" when {
    "IntIntervals" should {

      /**
        * 
        */
      "meet & metBy" in {
        // forAll(t) { (a, b, expected) =>
        //   a.meets(b) mustBe expected
        //   b.isMetBy(a) mustBe expected
        // }
      }

    }
  }
 

/*
We can posit prop­er­ties like these:

Given any three time­stamps f1, f2, and f3, if [f1,f2] con­tains f3, then f3 >= f1 and f3 <= f2. Else, f3 < f1 or f3 > f2.

Given any two time­stamps f1 and f2, if (,f1) over­laps (f2,), then f1 > f2. Else, f1 <= f2.
*/
