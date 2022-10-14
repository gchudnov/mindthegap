package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
// import com.github.gchudnov.mtg.Boundary
// import com.github.gchudnov.mtg.BoundaryOrdering
// import com.github.gchudnov.mtg.Domains.given
import com.github.gchudnov.mtg.Interval
// import com.github.gchudnov.mtg.Show.*
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IntervalRelSpec extends TestSpec: // with IntervalRelAssert {}

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IntervalRel" when {

//   /**
//    * Finishes, IsFinishedBy
//    *
//    * {{{
//    *      AAA
//    *   BBBBBB
//    * }}}
//    */
//   "finishes & isFinishedBy" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.finishes(yy)) {
//           assertFwdBck("f", xx, yy)
//         }
//       }
//     }

//     "manual check" in {
//       // Empty
//       Interval.empty[Int].finishes(Interval.empty[Int]) mustBe (false)
//       Interval.empty[Int].finishes(Interval.degenerate(0)) mustBe (false)
//       Interval.empty[Int].finishes(Interval.closed(0, 5)) mustBe (false)

//       // Degenerate
//       Interval.degenerate(5).finishes(Interval.empty[Int]) mustBe (false)
//       Interval.degenerate(5).finishes(Interval.degenerate(5)) mustBe (false)
//       Interval.degenerate(5).finishes(Interval.closed(1, 5)) mustBe (true)
//       Interval.degenerate(1).finishes(Interval.closed(1, 5)) mustBe (false)

//       // Proper
//       // [0,5)  [-1,5)
//       Interval.leftClosedRightOpen(0, 5).finishes(Interval.leftClosedRightOpen(-1, 5)) mustBe (true)

//       // (5, 10]  (2, 10]
//       Interval.leftOpenRightClosed(5, 10).finishes(Interval.leftOpenRightClosed(2, 10)) mustBe (true)

//       // Infinity
//       // [5, 10]  (-inf, 10]
//       Interval.closed(5, 10).finishes(Interval.rightClosed(10)) mustBe (true)

//       // [10, +inf)  [5, +inf)
//       Interval.leftClosed(10).finishes(Interval.leftClosed(5)) mustBe (true)

//       // [5, +inf)  (-inf, +inf)
//       Interval.leftClosed(5).finishes(Interval.unbounded[Int]) mustBe (true)

//       // (0, 3)  (-inf, 3)
//       Interval.open(0, 3).finishes(Interval.rightOpen(3)) mustBe (true)

//       // (-inf, +inf)  (-inf, +inf)
//       Interval.unbounded[Int].finishes(Interval.unbounded[Int]) mustBe (false)
//     }
//   }

//   /**
//    * Equals
//    *
//    * {{{
//    *   AAAA
//    *   BBBB
//    * }}}
//    */
//   "equals" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.equalsTo(yy)) {
//           assertFwdBck("e", xx, yy)
//         }
//       }
//     }

//     "manual check" in {
//       // Empty
//       Interval.empty[Int].equalsTo(Interval.empty[Int]) mustBe (false)
//       Interval.empty[Int].equalsTo(Interval.degenerate(0)) mustBe (false)
//       Interval.empty[Int].equalsTo(Interval.closed(0, 1)) mustBe (false)

//       // Degenerate
//       Interval.degenerate(5).equalsTo(Interval.degenerate(5)) mustBe (true)
//       Interval.degenerate(5).equalsTo(Interval.empty[Int]) mustBe (false)

//       // Proper
//       Interval.open(4, 7).equalsTo(Interval.open(4, 7)) mustBe (true)
//       Interval.open(0, 5).equalsTo(Interval.open(0, 5)) mustBe (true)
//       Interval.closed(0, 5).equalsTo(Interval.closed(0, 5)) mustBe (true)
//       Interval.leftOpenRightClosed(0, 5).equalsTo(Interval.leftOpenRightClosed(0, 5)) mustBe (true)
//       Interval.leftClosedRightOpen(0, 5).equalsTo(Interval.leftClosedRightOpen(0, 5)) mustBe (true)

//       // Infinity
//       // [5, +inf)  [5, +inf)
//       Interval.leftClosed(5).equalsTo(Interval.leftClosed(5)) mustBe (true)

//       // (-inf, 5]  (-inf, 5]
//       Interval.rightClosed(5).equalsTo(Interval.rightClosed(5)) mustBe (true)

//       // (-inf, +inf)  (-inf, +inf)
//       Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) mustBe (true)

//       // (-inf, 5)  (-inf, 5)
//       Interval.rightOpen(5).equalsTo(Interval.rightOpen(5)) mustBe (true)
//     }
//   }

//   "isSubset" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.isSubset(yy)) {
//           assertOneOf(Set("s", "d", "f", "e"), xx, yy)
//         }
//       }
//     }
//   }

//   "isSuperset" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.isSuperset(yy)) {
//           assertOneOf(Set("S", "D", "F", "e"), xx, yy)
//         }
//       }
//     }
//   }

//   "isDisjoint" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.isDisjoint(yy)) {
//           assertOneOf(Set("b", "B"), xx, yy)
//         }
//       }
//     }
//   }

//   "isLessEqual" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.isLessEqual(yy)) {
//           assertOneOf(Set("b", "m", "o", "s", "F", "e"), xx, yy)
//         }
//       }
//     }
//   }

//   "isLess" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.isLess(yy)) {
//           assertOneOf(Set("b", "m", "o"), xx, yy)
//         }
//       }
//     }
//   }

//   "isGreaterEqual" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.isGreaterEqual(yy)) {
//           assertOneOf(Set("B", "M", "O", "f", "S", "e"), xx, yy)
//         }
//       }
//     }
//   }

//   "isGreater" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.isGreater(yy)) {
//           assertOneOf(Set("B", "M", "O"), xx, yy)
//         }
//       }
//     }
//   }

//   "satisfy" should {
//     "one relation only" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         assertAnySingle(xx, yy)
//       }
//     }

//     "one relation only for edge cases" in {
//       val intervals = List(
//         (Interval.rightOpen(5), Interval.rightOpen(5))
//       )

//       intervals.foreach { case (xx, yy) => assertAnySingle(xx, yy) }
//     }
//   }
  }
