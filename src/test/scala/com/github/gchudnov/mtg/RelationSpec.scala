package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
import com.github.gchudnov.mtg.Relation.*
import com.github.gchudnov.mtg.Show.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class RelationSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  "Relation" when {

    /**
     * Before, After (Preceeds, IsPreceededBy)
     *
     * {{{
     *   AAA
     *        BBB
     * }}}
     */
    "before (preceeds) & after (isPreceededBy)" should {
      // "check" in {
      //   forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
      //     val xx = Interval.make(ox1, ox2, ix1, ix2)
      //     val yy = Interval.make(oy1, oy2, iy1, iy2)

      //     whenever(xx.preceeds(yy)) {
      //       // println(s"p: ${(xx, yy)}")

      //       assertRelation("b", xx, yy)
      //       assertOneRelation(xx, yy)
      //     }
      //   }
      // }

      "check edge cases" in {
        // Empty
        Interval.empty[Int].before(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].before(Interval.degenerate(1)) mustBe (false)
        Interval.empty[Int].before(Interval.closed(1, 2)) mustBe (false)
        Interval.empty[Int].before(Interval.open(1, 2)) mustBe (false)
        Interval.empty[Int].before(Interval.unbounded[Int]) mustBe (false)

        // Degenerate (Point)
        Interval.degenerate(5).before(Interval.empty[Int]) mustBe (false)
        Interval.degenerate(5).before(Interval.degenerate(5)) mustBe (false)
        Interval.degenerate(5).before(Interval.degenerate(6)) mustBe (true)
        Interval.degenerate(5).before(Interval.degenerate(10)) mustBe (true)
        Interval.degenerate(5).before(Interval.open(5, 10)) mustBe (true)
        Interval.degenerate(5).before(Interval.closed(5, 10)) mustBe (false)
        Interval.degenerate(5).before(Interval.closed(6, 10)) mustBe (true)
        Interval.degenerate(5).before(Interval.leftClosed(5)) mustBe (false)
        Interval.degenerate(5).before(Interval.leftClosed(6)) mustBe (true)
        Interval.degenerate(5).before(Interval.unbounded[Int]) mustBe (false)

        // Proper
        Interval.open(1, 2).before(Interval.empty[Int]) mustBe (false)
        Interval.open(1, 2).preceeds(Interval.open(5, 6)) mustBe (true)
        Interval.open(5, 6).isPreceededBy(Interval.open(1, 2)) mustBe (true)
        Interval.open(1, 2).preceeds(Interval.open(2, 3)) mustBe (true)
        Interval.open(1, 2).preceeds(Interval.closed(5, 6)) mustBe (true)

        // Infinity
        // (1, 2)  (3, +inf)
        Interval.open(1, 2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  (3, 5)
        Interval.rightOpen(2).before(Interval.open(3, 5)) mustBe (true)

        // (-inf, 2)  (3, +inf)
        Interval.rightOpen(2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2]  (3, +inf)
        Interval.rightClosed(2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  [3, +inf)
        Interval.rightOpen(2).before(Interval.leftClosed(3)) mustBe (true)
      }
    }

    /**
     * Meets, IsMetBy
     *
     * {{{
     *   AAA
     *      BBB
     * }}}
     */
    "meets & isMetBy" should {
      // "check" in {
      //   forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
      //     val xx = Interval.make(ox1, ox2, ix1, ix2)
      //     val yy = Interval.make(oy1, oy2, iy1, iy2)

      //     whenever(xx.meets(yy)) {
      //       // println(s"m: ${(xx, yy)}")

      //       assertRelation("m", xx, yy)
      //       assertOneRelation(xx, yy)

      //       val isX2eqY1 = pOrd.equiv(ox2, oy1)

      //       (isX2eqY1 && (ix2 && iy1)) mustBe (true)
      //     }
      //   }
      // }

      "check edge cases" in {

        // TODO: add more tests

        // Proper
        // [1, 5]  [5, 10]
        Interval.closed(1, 5).meets(Interval.closed(5, 10)) mustBe (true)

        // Infinity
        // [1, 5]  [5, +inf)
        Interval.closed(1, 5).meets(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5]  [5, 10]
        Interval.rightClosed(5).meets(Interval.closed(5, 10)) mustBe (true)

        // (-inf, 5]  [5, +inf)
        Interval.rightClosed(5).meets(Interval.leftClosed(5)) mustBe (true)
      }
    }

//     /**
//      * Overlaps, IsOverlapedBy
//      *
//      * {{{
//      *   AAAA
//      *     BBBB
//      * }}}
//      */
//     "overlaps & isOverlapedBy" should {
//       "check" in {
//         forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//           val xx = Interval.make(ox1, ox2, ix1, ix2)
//           val yy = Interval.make(oy1, oy2, iy1, iy2)

//           whenever(xx.overlaps(yy)) {
//             // println(s"o: ${(xx, yy)}")

//             assertRelation("o", xx, yy)
//             assertOneRelation(xx, yy)

//             val isX2gtY1 = pOrd.gt(ox2, oy1)
//             val isX2ltY2 = pOrd.lt(ox2, oy2)
//             val isX2eqY2 = pOrd.equiv(ox2, oy2)
//             val isX1ltY1 = pOrd.lt(ox1, oy1)
//             val isX1eqY1 = pOrd.equiv(ox1, oy1)

//             (isX2gtY1 && ((ox1.isEmpty && oy2.isEmpty) || (isX1ltY1 && oy2.isEmpty) || (isX1eqY1 && ix1 && !iy1 && oy2.isEmpty) || (ox1.isEmpty && isX2ltY2)
//               || (ox1.isEmpty && isX2eqY2 && !ix2 && iy2) || ((isX1ltY1 || (isX1eqY1 && ix1 && !iy1)) && (isX2ltY2 || (isX2eqY2 && !ix2 && iy2))))) mustBe (true)
//           }
//         }
//       }

//       "check edge cases" in {
//         // Empty
//         // TODO: add tests for https://stackoverfloy1.com/questions/325933/determine-whether-two-date-ranges-overlap

//         // {}  (-inf, +inf)
//         Interval.empty[Int].overlaps(Interval.unbounded[Int]) mustBe (false)
//         Interval.unbounded[Int].overlaps(Interval.empty[Int]) mustBe (false)

//         // {} [1, 2]
//         Interval.empty[Int].overlaps(Interval.closed(1, 2)) mustBe (false)
//         Interval.closed(1, 2).overlaps(Interval.empty[Int]) mustBe (false)

//         // Degenerate (Point)

//         // Proper
//         // (1, 10)  (5, 20)
//         Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)

//         // (1, 10)  (2, 11)
//         Interval.open(1, 10).overlaps(Interval.open(2, 11)) mustBe (true)

//         // (1, 10)  (11, 20)
//         Interval.open(1, 10).overlaps(Interval.open(11, 20)) mustBe (false)

//         // (1, 10)  (1, 11)
//         Interval.open(1, 10).overlaps(Interval.open(1, 11)) mustBe (false)

//         // (1, 10)  (20, 30)
//         Interval.open(1, 10).overlaps(Interval.open(20, 30)) mustBe (false)

//         // (1, 10)  {10}
//         Interval.open(1, 10).overlaps(Interval.degenerate(10)) mustBe (false)

//         // [1, 10], {10}
//         Interval.closed(1, 10).overlaps(Interval.degenerate(10)) mustBe (false)

//         // (1, 10)  (-10, 20)
//         Interval.open(1, 10).overlaps(Interval.open(-10, 20)) mustBe (false)

//         // (1, 10)  (2, 11)
//         Interval.open(1, 10).isOverlapedBy(Interval.open(2, 11)) mustBe (false)

//         // (1, 10)  (2, 10)
//         Interval.open(1, 10).isOverlapedBy(Interval.open(2, 10)) mustBe (false)

//         // (2, 12)  (1, 10)
//         Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

//         // (1, 12)  (1, 10)
//         Interval.open(1, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (false)

//         // (2, 12)  (1, 10)
//         Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

//         // (1, 10)  (5, 20)
//         Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)

//         // Infinity
//         // [1, 5]  [3, +inf)
//         Interval.closed(1, 5).overlaps(Interval.leftClosed(3)) mustBe (true)

//         // (-inf, 5]  [3, 10]
//         Interval.rightClosed(5).overlaps(Interval.closed(3, 10)) mustBe (true)

//         // (-inf, 5]  [3, +inf)
//         Interval.rightClosed(5).overlaps(Interval.leftClosed(3)) mustBe (true)

//         // [-inf, 1)  (-inf, +inf)
//         Interval.proper(None, Some(1), true, false).overlaps(Interval.unbounded[Int]) mustBe (true)

//         // (-inf, +inf)  (0, +inf]
//         Interval.unbounded[Int].overlaps(Interval.proper(Some(0), None, false, true)) mustBe (true)

//         // (-inf, +inf)  [0, +inf]
//         Interval.unbounded[Int].overlaps(Interval.proper(Some(0), None, true, true)) mustBe (true)

//         // (-inf, +inf) (1, 10)
//         Interval.unbounded[Int].overlaps(Interval.open(1, 10)) mustBe (false)
//         Interval.open(1, 10).overlaps(Interval.unbounded[Int]) mustBe (false)
//         Interval.unbounded[Int].isOverlapedBy(Interval.open(1, 10)) mustBe (false)
//         Interval.open(1, 10).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

//         // (-inf, +inf) {2}
//         Interval.unbounded[Int].isOverlapedBy(Interval.degenerate(2)) mustBe (false)
//         Interval.degenerate(2).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

//         // (-inf, 2)  (-2, +inf)
//         Interval.rightOpen(2).overlaps(Interval.leftOpen(-2)) mustBe (true)
//         Interval.leftOpen(-2).isOverlapedBy(Interval.rightOpen(2)) mustBe (true)
//       }
//     }

//     /**
//      * During, Contains
//      *
//      * {{{
//      *     AA
//      *   BBBBBB
//      * }}}
//      */
//     "during & contains (includes)" should {
//       "check" in {
//         forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//           val xx = Interval.make(ox1, ox2, ix1, ix2)
//           val yy = Interval.make(oy1, oy2, iy1, iy2)

//           whenever(xx.during(yy)) {
//             // println(s"d: ${(xx, yy)}")

//             assertRelation("d", xx, yy)
//             assertOneRelation(xx, yy)

//             val isX1gtY1 = pOrd.gt(ox1, oy1)
//             val isX1eqY1 = pOrd.equiv(ox1, oy1)

//             val isX2ltY2 = pOrd.lt(ox2, oy2)
//             val isX2eqY2 = pOrd.equiv(ox2, oy2)

//             (((isX1gtY1 || (isX1eqY1 && iy1 && !ix1)) && (isX2ltY2 || (isX2eqY2 && iy2 && !ix2))) ||
//               ((isX1gtY1 || (isX1eqY1 && iy1 && !ix1)) && oy2.isEmpty) ||
//               ((isX2ltY2 || (isX2eqY2 && iy2 && !ix2)) && oy1.isEmpty) ||
//               (ox1.isDefined && ox2.isDefined && oy1.isEmpty && oy2.isEmpty)) mustBe (true)
//           }
//         }
//       }

//       "check edge cases" in {
//         // Proper
//         // {5}  (2, 9)
//         Interval.degenerate(5).during(Interval.open(2, 9)) mustBe (true)

//         // (2, 9)  {5}
//         Interval.open(2, 9).contains(Interval.degenerate(5)) mustBe (true)

//         // Infinity
//         // [5, 7]  [3, +inf)
//         Interval.closed(5, 7).during(Interval.leftClosed(3)) mustBe (true)

//         // [5, 7]  (-inf, 10]
//         Interval.closed(5, 7).during(Interval.rightClosed(10)) mustBe (true)

//         // [5, 7] (-inf, +inf)
//         Interval.closed(5, 7).during(Interval.unbounded[Int]) mustBe (true)

//         // [-∞,0]  [-∞,+∞)
//         Interval.proper(None, Some(0), true, true).during(Interval.proper[Int](None, None, true, false)) mustBe (false)

//         // [0, 1)  [-∞,+∞]
//         Interval.proper(Some(0), Some(1), true, false).during(Interval.proper[Int](None, None, true, true)) mustBe (true)

//         // [0]  [-∞,+∞)
//         Interval.degenerate(0).during(Interval.proper[Int](None, None, true, false)) mustBe (true)

//         // (3,+∞), (0,+∞]
//         Interval.leftOpen(3).during(Interval.proper(Some(0), None, false, true)) mustBe (true)
//         Interval.proper(Some(0), None, false, true).contains(Interval.leftOpen(3)) mustBe (true)
//       }
//     }

//     /**
//      * Starts, IsStartedBy
//      *
//      * {{{
//      *   AAA
//      *   BBBBBB
//      * }}}
//      */
//     "starts & isStartedBy" should {
//       "check" in {
//         forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//           val xx = Interval.make(ox1, ox2, ix1, ix2)
//           val yy = Interval.make(oy1, oy2, iy1, iy2)

//           whenever(xx.starts(yy)) {
//             // println(s"s: ${(xx, yy)}")

//             assertRelation("s", xx, yy)
//             assertOneRelation(xx, yy)

//             val isX1eqY1 = pOrd.equiv(ox1, oy1)

//             val isX2ltY2 = pOrd.lt(ox2, oy2)
//             val isX2eqY2 = pOrd.equiv(ox2, oy2)

//             (isX1eqY1 && ((ox2.isDefined && oy2.isEmpty) || (isX2ltY2 || (isX2eqY2 && (!ix2 && iy2))))) mustBe (true)
//           }
//         }
//       }

//       "check edge cases" in {
//         // Proper
//         // [1, 2]  [1, 10]
//         Interval.closed(1, 2).starts(Interval.closed(1, 10)) mustBe (true)
//         Interval.closed(1, 10).isStartedBy(Interval.closed(1, 2)) mustBe (true)

//         // (1, 2)  (1, 10)
//         Interval.open(1, 2).starts(Interval.open(1, 10)) mustBe (true)

//         // Infinity
//         // [1, 5] [1, +inf)
//         Interval.closed(1, 5).starts(Interval.leftClosed(1)) mustBe (true)

//         // (-inf, 5]  (-inf, 10]
//         Interval.rightClosed(5).starts(Interval.rightClosed(10)) mustBe (true)

//         // (-inf, 5)  (-inf, +inf)
//         Interval.rightClosed(5).starts(Interval.unbounded[Int]) mustBe (true)

//         //  [5, 10)  [5, +inf)
//         Interval.leftClosedRightOpen(5, 10).starts(Interval.leftClosed(5)) mustBe (true)
//         Interval.leftClosedRightOpen(5, 10).isStartedBy(Interval.leftClosed(5)) mustBe (false)

//         // (-inf, +inf)  (-inf, +inf)
//         Interval.unbounded[Int].starts(Interval.unbounded[Int]) mustBe (false)
//         Interval.unbounded[Int].isStartedBy(Interval.unbounded[Int]) mustBe (false)

//         // (0,1]  (0,+∞]
//         Interval.leftOpenRightClosed(0, 1).starts(Interval.leftOpen(0)) mustBe (true)
//       }
//     }

//     /**
//      * Finishes, IsFinishedBy
//      *
//      * {{{
//      *      AAA
//      *   BBBBBB
//      * }}}
//      */
//     "finishes & isFinishedBy" should {
//       "check" in {
//         forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//           val xx = Interval.make(ox1, ox2, ix1, ix2)
//           val yy = Interval.make(oy1, oy2, iy1, iy2)

//           whenever(xx.finishes(yy)) {
//             // println(s"f: ${(xx, yy)}")

//             assertRelation("f", xx, yy)
//             assertOneRelation(xx, yy)

//             val isX2eqY2 = pOrd.equiv(ox2, oy2)

//             val isX1gtY1 = pOrd.gt(ox1, oy1)
//             val isX1eqY1 = pOrd.equiv(ox1, oy1)

//             (isX2eqY2 && ((ox1.isDefined && oy1.isEmpty) || (isX1gtY1 || (isX1eqY1 && (iy1 && !ix1))))) mustBe (true)
//           }
//         }
//       }

//       "check edge cases" in {
//         // Proper
//         // [0,5)  [-1,5)
//         Interval.leftClosedRightOpen(0, 5).finishes(Interval.leftClosedRightOpen(-1, 5)) mustBe (true)

//         // (5, 10]  (2, 10]
//         Interval.leftOpenRightClosed(5, 10).finishes(Interval.leftOpenRightClosed(2, 10)) mustBe (true)

//         // Infinity
//         // [5, 10]  (-inf, 10]
//         Interval.closed(5, 10).finishes(Interval.rightClosed(10)) mustBe (true)

//         // [10, +inf)  [5, +inf)
//         Interval.leftClosed(10).finishes(Interval.leftClosed(5)) mustBe (true)

//         // [5, +inf)  (-inf, +inf)
//         Interval.leftClosed(5).finishes(Interval.unbounded[Int]) mustBe (true)

//         // (0, 3)  (-inf, 3)
//         Interval.open(0, 3).finishes(Interval.rightOpen(3)) mustBe (true)

//         // (-inf, +inf)  (-inf, +inf)
//         Interval.unbounded[Int].finishes(Interval.unbounded[Int]) mustBe (false)
//       }
//     }

//     /**
//      * Equals
//      *
//      * {{{
//      *   AAAA
//      *   BBBB
//      * }}}
//      */
//     "equals" should {
//       "check" in {
//         forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//           val xx = Interval.make(ox1, ox2, ix1, ix2)
//           val yy = Interval.make(oy1, oy2, iy1, iy2)

//           whenever(xx.equalsTo(yy)) {
//             // println(s"e: ${(xx, yy)}")

//             assertRelation("e", xx, yy)
//             assertOneRelation(xx, yy)

//             val isX1eqY1 = pOrd.equiv(ox1, oy1)
//             val isX2eqY2 = pOrd.equiv(ox2, oy2)

//             ((isX1eqY1 && isX2eqY2 && (ix1 == iy1) && (ix2 == iy2)) ||
//               (xx.isEmpty && yy.isEmpty)) mustBe (true)
//           }
//         }
//       }

//       "check edge cases" in {
//         // Proper
//         Interval.open(0, 5).equalsTo(Interval.open(0, 5)) mustBe (true)
//         Interval.closed(0, 5).equalsTo(Interval.closed(0, 5)) mustBe (true)
//         Interval.leftOpen(0, 5).equalsTo(Interval.leftOpen(0, 5)) mustBe (true)
//         Interval.rightOpen(0, 5).equalsTo(Interval.rightOpen(0, 5)) mustBe (true)
//         Interval.leftClosed(0, 5).equalsTo(Interval.leftClosed(0, 5)) mustBe (true)
//         Interval.rightClosed(0, 5).equalsTo(Interval.rightClosed(0, 5)) mustBe (true)

//         // Infinity
//         // [5, +inf)  [5, +inf)
//         Interval.leftClosed(5).equalsTo(Interval.leftClosed(5)) mustBe (true)

//         // (-inf, 5]  (-inf, 5]
//         Interval.rightClosed(5).equalsTo(Interval.rightClosed(5)) mustBe (true)

//         // (-inf, +inf)  (-inf, +inf)
//         Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) mustBe (true)

//         // (-inf, 5)  (-inf, 5)
//         Interval.rightOpen(5).equalsTo(Interval.rightOpen(5)) mustBe (true)
//       }
//     }

//     "satisfy" should {
//       "one relation only" in {
//         forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//           val xx = Interval.make(ox1, ox2, ix1, ix2)
//           val yy = Interval.make(oy1, oy2, iy1, iy2)

//           assertOneRelation(xx, yy)
//         }
//       }

//       "one relation only for edge cases" in {
//         val intervals = List(
//           (Interval.rightOpen(5), Interval.rightOpen(5))
//         )

//         intervals.foreach { case (xx, yy) => assertOneRelation(xx, yy) }
//       }
//     }
  }

  private def makeRelations[T: Ordering: Domain](using bOrd: Ordering[Boundary[T]]) =
    Map(
      "b" -> ((ab: Interval[T], cd: Interval[T]) => ab.preceeds(cd)),
      "B" -> ((ab: Interval[T], cd: Interval[T]) => ab.isPreceededBy(cd)),
      "m" -> ((ab: Interval[T], cd: Interval[T]) => ab.meets(cd)),
      "M" -> ((ab: Interval[T], cd: Interval[T]) => ab.isMetBy(cd)),
      "o" -> ((ab: Interval[T], cd: Interval[T]) => ab.overlaps(cd)),
      "O" -> ((ab: Interval[T], cd: Interval[T]) => ab.isOverlapedBy(cd)),
      "d" -> ((ab: Interval[T], cd: Interval[T]) => ab.during(cd)),
      "D" -> ((ab: Interval[T], cd: Interval[T]) => ab.contains(cd)),
      "s" -> ((ab: Interval[T], cd: Interval[T]) => ab.starts(cd)),
      "S" -> ((ab: Interval[T], cd: Interval[T]) => ab.isStartedBy(cd)),
      "f" -> ((ab: Interval[T], cd: Interval[T]) => ab.finishes(cd)),
      "F" -> ((ab: Interval[T], cd: Interval[T]) => ab.isFinishedBy(cd)),
      "e" -> ((ab: Interval[T], cd: Interval[T]) => ab.equalsTo(cd)),
      "E" -> ((ab: Interval[T], cd: Interval[T]) => ab.equalsTo(cd))
    )

  private def assertRelation[T: Ordering: Domain](r: String, xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val relations = makeRelations[T]

    val fk = r
    val bk = r.map(_.toUpper)
    val ks = List(fk, bk)

    val fwd  = relations(fk)
    val bck  = relations(bk)
    val rest = relations.filterNot { case (k, _) => ks.contains(k) }

    fwd(xx, yy) mustBe (true)
    bck(yy, xx) mustBe (true)

    rest.foreach { case (k, fn) =>
      if fn(xx, yy) then fail(s"xx: ${xx}, yy: ${yy}: ${fk}|${xx.show}, ${yy.show}| == true; ${k}|${xx.show}, ${yy.show}| mustBe false, got true")
      if fn(yy, xx) then fail(s"xx: ${xx}, yy: ${yy}: ${fk}|${xx.show}, ${yy.show}| == true; ${k}|${yy.show}, ${xx.show}| mustBe false, got true")
    }

  private def assertOneRelation[T: Ordering: Domain](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val relations = makeRelations[T]

    val trues = relations.foldLeft(Set.empty[String]) { case (acc, (k, fn)) =>
      val res = fn(xx, yy)
      if res then acc + k
      else acc
    }

    val isNonEmpty = !(xx.isEmpty || yy.isEmpty)

    if isNonEmpty then
      // it is OK if xx, yy satisfy both [e, E]
      val expectedSize = if trues.contains("e") || trues.contains("E") then 2 else 1
      if trues.size != expectedSize then
        fail(s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| satisfies ${trues.size} relations: ${trues.mkString("[", ",", "]")}, expected: ${expectedSize}")
