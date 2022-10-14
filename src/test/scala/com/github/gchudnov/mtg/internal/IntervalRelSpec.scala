package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IntervalRelSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IntervalRel" when {

//   "isSuperset" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ix1, ox2, ix2)
//         val yy = Interval.make(oy1, iy1, oy2, iy2)

//         whenever(xx.isSuperset(yy)) {
//           assertOneOf(Set("S", "D", "F", "e"), xx, yy)
//         }
//       }
//     }
//   }

//   "isDisjoint" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ix1, ox2, ix2)
//         val yy = Interval.make(oy1, iy1, oy2, iy2)

//         whenever(xx.isDisjoint(yy)) {
//           assertOneOf(Set("b", "B"), xx, yy)
//         }
//       }
//     }
//   }

//   "isLessEqual" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ix1, ox2, ix2)
//         val yy = Interval.make(oy1, iy1, oy2, iy2)

//         whenever(xx.isLessEqual(yy)) {
//           assertOneOf(Set("b", "m", "o", "s", "F", "e"), xx, yy)
//         }
//       }
//     }
//   }

//   "isLess" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ix1, ox2, ix2)
//         val yy = Interval.make(oy1, iy1, oy2, iy2)

//         whenever(xx.isLess(yy)) {
//           assertOneOf(Set("b", "m", "o"), xx, yy)
//         }
//       }
//     }
//   }

//   "isGreaterEqual" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ix1, ox2, ix2)
//         val yy = Interval.make(oy1, iy1, oy2, iy2)

//         whenever(xx.isGreaterEqual(yy)) {
//           assertOneOf(Set("B", "M", "O", "f", "S", "e"), xx, yy)
//         }
//       }
//     }
//   }

//   "isGreater" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ix1, ox2, ix2)
//         val yy = Interval.make(oy1, iy1, oy2, iy2)

//         whenever(xx.isGreater(yy)) {
//           assertOneOf(Set("B", "M", "O"), xx, yy)
//         }
//       }
//     }
//   }

    "satisfy on relation only" should {
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
