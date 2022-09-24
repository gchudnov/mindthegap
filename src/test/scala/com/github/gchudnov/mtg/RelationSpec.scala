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

    "instance" when {

      "before" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.before(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (0)
              bit2(r.repr) mustBe (0)
              bit3(r.repr) mustBe (0)

              assertOne("b", r)
            }
          }
        }
      }

      "after" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.after(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (0)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (0)
              bit3(r.repr) mustBe (0)

              assertOne("B", r)
            }
          }
        }
      }

      "meets" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.meets(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (0)
              bit2(r.repr) mustBe (1)
              bit3(r.repr) mustBe (0)

              assertOne("m", r)
            }
          }
        }
      }

      "isMetBy" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.isMetBy(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (0)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (0)
              bit3(r.repr) mustBe (1)

              assertOne("M", r)
            }
          }
        }
      }

      "overlaps" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.overlaps(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (0)
              bit2(r.repr) mustBe (1)
              bit3(r.repr) mustBe (1)

              assertOne("o", r)
            }
          }
        }
      }

      "isOverlapedBy" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.isOverlapedBy(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (0)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (1)
              bit3(r.repr) mustBe (1)

              assertOne("O", r)
            }
          }
        }
      }

      "starts" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.starts(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (1)
              bit3(r.repr) mustBe (0)

              assertOne("s", r)
            }
          }
        }
      }

      "isStartedBy" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.isStartedBy(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (0)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (1)
              bit3(r.repr) mustBe (0)

              assertOne("S", r)
            }
          }
        }
      }

      "during" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.during(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (1)
              bit3(r.repr) mustBe (1)

              assertOne("d", r)
            }
          }
        }
      }

      "contains" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.contains(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (0)
              bit1(r.repr) mustBe (0)
              bit2(r.repr) mustBe (1)
              bit3(r.repr) mustBe (1)

              assertOne("D", r)
            }
          }
        }
      }

      "finishes" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.finishes(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (0)
              bit3(r.repr) mustBe (1)

              assertOne("f", r)
            }
          }
        }
      }

      "isFinishedBy" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.isFinishedBy(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (0)
              bit2(r.repr) mustBe (0)
              bit3(r.repr) mustBe (1)

              assertOne("F", r)
            }
          }
        }
      }

      "equalsTo" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            whenever(xx.equalsTo(yy)) {
              val r = Relation.make(xx, yy)

              bit0(r.repr) mustBe (1)
              bit1(r.repr) mustBe (1)
              bit2(r.repr) mustBe (0)
              bit3(r.repr) mustBe (0)

              assertOne("e", r)
            }
          }
        }
      }

      "manual check" in {
        val t = Table(
          ("ab", "bit3", "bit2", "bit1", "bit0"),
          // before
          ((Interval.closed(3, 5), Interval.closed(8, 10)), 0, 0, 0, 1),  // [3, 5]  [8, 10]
          ((Interval.closed(3, 5), Interval.degenerate(8)), 0, 0, 0, 1),  // [3, 5]  {8}
          ((Interval.degenerate(1), Interval.closed(3, 5)), 0, 0, 0, 1),  // {1}  [3, 5]
          ((Interval.degenerate(5), Interval.degenerate(8)), 0, 0, 0, 1), // {5}  {8}
          // after
          ((Interval.closed(8, 10), Interval.closed(3, 5)), 0, 0, 1, 0),  // [8, 10]  [3, 5]
          ((Interval.degenerate(8), Interval.closed(3, 5)), 0, 0, 1, 0),  // {8}  [3, 5]
          ((Interval.closed(3, 5), Interval.degenerate(1)), 0, 0, 1, 0),  // [3, 5]  {1}
          ((Interval.degenerate(8), Interval.degenerate(5)), 0, 0, 1, 0), // {8}  {5}
          // meets
          ((Interval.closed(3, 5), Interval.closed(5, 10)), 0, 1, 0, 1), // [3, 5]  [5, 10]
          // is-met-by
          ((Interval.closed(5, 10), Interval.closed(3, 5)), 1, 0, 1, 0), // [5, 10]  [3, 5]
          // overlaps
          ((Interval.closed(3, 7), Interval.closed(5, 10)), 1, 1, 0, 1), // [3, 7]  [5, 10]
          // is-overlapped-by
          ((Interval.closed(5, 10), Interval.closed(3, 7)), 1, 1, 1, 0), // [5, 10]  [3, 7]
          // starts
          ((Interval.closed(3, 5), Interval.closed(3, 10)), 0, 1, 1, 1), // [3, 5]  [3, 10]
          ((Interval.degenerate(1), Interval.closed(1, 5)), 0, 1, 1, 1), // {1}  [1, 5]
          // is-srarted-by
          ((Interval.closed(3, 10), Interval.closed(3, 5)), 0, 1, 1, 0), // [3, 10]  [3, 5]
          ((Interval.closed(1, 5), Interval.degenerate(1)), 0, 1, 1, 0), // [1, 5]  {1}
          // during
          ((Interval.closed(3, 5), Interval.closed(1, 10)), 1, 1, 1, 1), // [3, 5]  [1, 10]
          ((Interval.degenerate(3), Interval.closed(1, 5)), 1, 1, 1, 1), // {3}  [1, 5]
          // contains
          ((Interval.closed(1, 10), Interval.closed(3, 5)), 1, 1, 0, 0), // [1, 10]  [3, 5]
          ((Interval.closed(1, 5), Interval.degenerate(3)), 1, 1, 0, 0), // [1, 5]  {3}
          // finishes
          ((Interval.closed(5, 10), Interval.closed(3, 10)), 1, 0, 1, 1), // [5, 10]  [3, 10]
          ((Interval.degenerate(5), Interval.closed(1, 5)), 1, 0, 1, 1),  // {5}  [1, 5]
          // is-finished-by
          ((Interval.closed(3, 10), Interval.closed(5, 10)), 1, 0, 0, 1), // [3, 10]  [5, 10]
          ((Interval.closed(1, 5), Interval.degenerate(5)), 1, 0, 0, 1),  // [1, 5]  {5}
          // equals
          ((Interval.closed(3, 5), Interval.closed(3, 5)), 0, 0, 1, 1),   // [3, 5]  [3, 5]
          ((Interval.degenerate(5), Interval.degenerate(5)), 0, 0, 1, 1), // {5}  {5}

          // empty relations
          ((Interval.degenerate(5), Interval.empty[Int]), 0, 0, 0, 0), // {5}  {}
          ((Interval.empty[Int], Interval.empty[Int]), 0, 0, 0, 0)     // {}  {}
        )

        forAll(t) { case (ab, expectedBit3, expectedBit2, expectedBit1, expectedBit0) =>
          val r = Relation.make(ab._1, ab._2)
          bit0(r.repr) mustBe (expectedBit0)
          bit1(r.repr) mustBe (expectedBit1)
          bit2(r.repr) mustBe (expectedBit2)
          bit3(r.repr) mustBe (expectedBit3)
        }
      }

      "isSubset" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val r = Relation.make(xx, yy)

            whenever(r.isSubset) {
              assertOneOf(Set("s", "d", "f", "e"), r)
            }
          }
        }
      }

      "isSuperset" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val r = Relation.make(xx, yy)

            whenever(r.isSuperset) {
              assertOneOf(Set("S", "D", "F", "e"), r)
            }
          }
        }
      }

      "isDisjoint" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val r = Relation.make(xx, yy)

            whenever(r.isDisjoint) {
              assertOneOf(Set("b", "B"), r)
            }
          }
        }
      }
    }

    /**
     * Before, After (Preceeds, IsPreceededBy)
     *
     * {{{
     *   AAA
     *        BBB
     * }}}
     */
    "before (preceeds) & after (isPreceededBy)" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.preceeds(yy)) {
            assertFwdBck("b", xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].before(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].before(Interval.degenerate(1)) mustBe (false)
        Interval.empty[Int].before(Interval.closed(1, 4)) mustBe (false)
        Interval.empty[Int].before(Interval.open(1, 4)) mustBe (false)
        Interval.empty[Int].before(Interval.unbounded[Int]) mustBe (false)

        // Degenerate (Point)
        Interval.degenerate(5).before(Interval.empty[Int]) mustBe (false)
        Interval.degenerate(5).before(Interval.degenerate(5)) mustBe (false)
        Interval.degenerate(5).before(Interval.degenerate(6)) mustBe (true)
        Interval.degenerate(6).after(Interval.degenerate(5)) mustBe (true)
        Interval.degenerate(5).before(Interval.degenerate(10)) mustBe (true)
        Interval.degenerate(5).before(Interval.open(5, 10)) mustBe (true)
        Interval.degenerate(5).before(Interval.closed(5, 10)) mustBe (false)
        Interval.degenerate(5).before(Interval.closed(6, 10)) mustBe (true)
        Interval.degenerate(5).before(Interval.leftClosed(5)) mustBe (false)
        Interval.degenerate(5).before(Interval.leftClosed(6)) mustBe (true)
        Interval.degenerate(5).before(Interval.unbounded[Int]) mustBe (false)

        // [-∞,0], {4}
        Interval.degenerate(4).after(Interval.proper[Int](None, Some(0), true, true)) mustBe (true)
        Interval.proper[Int](None, Some(0), true, true).before(Interval.degenerate(4)) mustBe (true)

        // Proper
        Interval.open(4, 7).before(Interval.open(4, 7)) mustBe (false)
        Interval.open(1, 4).before(Interval.empty[Int]) mustBe (false)
        Interval.open(1, 4).preceeds(Interval.open(5, 8)) mustBe (true)
        Interval.open(5, 8).isPreceededBy(Interval.open(1, 4)) mustBe (true)
        Interval.open(1, 4).preceeds(Interval.open(3, 6)) mustBe (true)
        Interval.open(1, 4).preceeds(Interval.closed(5, 6)) mustBe (true)

        // Infinity
        // (1, 4)  (3, +inf)
        Interval.open(1, 4).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  (3, 6)
        Interval.rightOpen(2).before(Interval.open(3, 6)) mustBe (true)

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
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.meets(yy)) {
            assertFwdBck("m", xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].meets(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].meets(Interval.degenerate(0)) mustBe (false)
        Interval.empty[Int].meets(Interval.closed(5, 10)) mustBe (false)
        Interval.empty[Int].meets(Interval.unbounded[Int]) mustBe (false)

        // Degenerate
        // 5  {}
        Interval.degenerate(6).meets(Interval.empty[Int]) mustBe (false)

        // 5  5
        Interval.degenerate(5).meets(Interval.degenerate(5)) mustBe (false)

        // 5  6
        Interval.degenerate(5).meets(Interval.degenerate(6)) mustBe (false)

        // 6  5
        Interval.degenerate(6).meets(Interval.degenerate(5)) mustBe (false)

        // Proper
        // [1, 5]  [5, 10]
        Interval.closed(1, 5).meets(Interval.closed(5, 10)) mustBe (true)

        // [1, 5)  (3, 10]
        Interval.leftClosedRightOpen(1, 5).meets(Interval.leftOpenRightClosed(3, 10)) mustBe (true)

        // Infinity
        // [1, 5]  [5, +inf)
        Interval.closed(1, 5).meets(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5]  [5, 10]
        Interval.rightClosed(5).meets(Interval.closed(5, 10)) mustBe (true)

        // (-inf, 5]  [5, +inf)
        Interval.rightClosed(5).meets(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5)  (5, +inf)
        Interval.rightOpen(5).meets(Interval.leftOpen(5)) mustBe (false)

        // (-inf, 6)  [5, +inf)
        Interval.rightOpen(6).meets(Interval.leftClosed(5)) mustBe (true)
      }
    }

    /**
     * Overlaps, IsOverlapedBy
     *
     * {{{
     *   AAAA
     *     BBBB
     * }}}
     */
    "overlaps & isOverlapedBy" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.overlaps(yy)) {
            assertFwdBck("o", xx, yy)
          }
        }
      }

      "manual check" in {

        // Empty
        // {}  (-inf, +inf)
        Interval.empty[Int].overlaps(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].overlaps(Interval.empty[Int]) mustBe (false)

        // {} [1, 2]
        Interval.empty[Int].overlaps(Interval.closed(1, 2)) mustBe (false)
        Interval.closed(1, 2).overlaps(Interval.empty[Int]) mustBe (false)

        // Degenerate (Point)
        Interval.degenerate(5).overlaps(Interval.closed(1, 10)) mustBe (false)
        Interval.degenerate(5).overlaps(Interval.closed(1, 3)) mustBe (false)
        Interval.degenerate(5).overlaps(Interval.closed(7, 10)) mustBe (false)
        Interval.degenerate(5).overlaps(Interval.empty[Int]) mustBe (false)
        Interval.degenerate(5).overlaps(Interval.unbounded[Int]) mustBe (false)

        // Proper
        // (1, 10)  (5, 20)
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)

        // (1, 10)  (2, 11)
        Interval.open(1, 10).overlaps(Interval.open(2, 11)) mustBe (true)

        // (1, 10)  (11, 20)
        Interval.open(1, 10).overlaps(Interval.open(11, 20)) mustBe (false)

        // (1, 10)  (1, 11)
        Interval.open(1, 10).overlaps(Interval.open(1, 11)) mustBe (false)

        // (1, 10)  (20, 30)
        Interval.open(1, 10).overlaps(Interval.open(20, 30)) mustBe (false)

        // (1, 10)  {10}
        Interval.open(1, 10).overlaps(Interval.degenerate(10)) mustBe (false)

        // [1, 10], {10}
        Interval.closed(1, 10).overlaps(Interval.degenerate(10)) mustBe (false)

        // (1, 10)  (-10, 20)
        Interval.open(1, 10).overlaps(Interval.open(-10, 20)) mustBe (false)

        // (1, 10)  (2, 11)
        Interval.open(1, 10).isOverlapedBy(Interval.open(2, 11)) mustBe (false)

        // (1, 10)  (2, 10)
        Interval.open(1, 10).isOverlapedBy(Interval.open(2, 10)) mustBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

        // (1, 12)  (1, 10)
        Interval.open(1, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

        // (1, 10)  (5, 20)
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)

        // Infinity
        // [1, 5]  [3, +inf)
        Interval.closed(1, 5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // (-inf, 5]  [3, 10]
        Interval.rightClosed(5).overlaps(Interval.closed(3, 10)) mustBe (true)

        // (-inf, 5]  [3, +inf)
        Interval.rightClosed(5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // [-inf, 1)  (-inf, +inf)
        Interval.proper(None, Some(1), true, false).overlaps(Interval.unbounded[Int]) mustBe (true)

        // (-inf, +inf)  (0, +inf]
        Interval.unbounded[Int].overlaps(Interval.proper(Some(0), None, false, true)) mustBe (true)

        // (-inf, +inf)  [0, +inf]
        Interval.unbounded[Int].overlaps(Interval.proper(Some(0), None, true, true)) mustBe (true)

        // (-inf, +inf) (1, 10)
        Interval.unbounded[Int].overlaps(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).overlaps(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isOverlapedBy(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

        // (-inf, +inf) {2}
        Interval.unbounded[Int].isOverlapedBy(Interval.degenerate(2)) mustBe (false)
        Interval.degenerate(2).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

        // (-inf, 2)  (-2, +inf)
        Interval.rightOpen(2).overlaps(Interval.leftOpen(-2)) mustBe (true)
        Interval.leftOpen(-2).isOverlapedBy(Interval.rightOpen(2)) mustBe (true)
      }
    }

    /**
     * During, Contains
     *
     * {{{
     *     AA
     *   BBBBBB
     * }}}
     */
    "during & contains (includes)" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.during(yy)) {
            assertFwdBck("d", xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].during(Interval.open(5, 10)) mustBe (false)
        Interval.empty[Int].during(Interval.degenerate(0)) mustBe (false)
        Interval.empty[Int].during(Interval.unbounded[Int]) mustBe (false)

        // Degenerate
        // {5}  (2, 9)
        Interval.degenerate(5).during(Interval.open(2, 9)) mustBe (true)
        Interval.degenerate(5).during(Interval.closed(2, 9)) mustBe (true)

        // (2, 9)  {5}
        Interval.open(2, 9).contains(Interval.degenerate(5)) mustBe (true)

        Interval.degenerate(5).during(Interval.closed(5, 10)) mustBe (false)
        Interval.degenerate(5).during(Interval.open(5, 10)) mustBe (false)
        Interval.degenerate(6).during(Interval.open(5, 10)) mustBe (false)
        Interval.degenerate(7).during(Interval.open(5, 10)) mustBe (true)

        // Proper
        Interval.closed(5, 7).during(Interval.closed(2, 10)) mustBe (true)
        Interval.open(5, 8).during(Interval.closed(5, 8)) mustBe (true)

        // Infinity
        // [5, 7]  [3, +inf)
        Interval.closed(5, 7).during(Interval.leftClosed(3)) mustBe (true)

        // [5, 7]  (-inf, 10]
        Interval.closed(5, 7).during(Interval.rightClosed(10)) mustBe (true)

        // [5, 7] (-inf, +inf)
        Interval.closed(5, 7).during(Interval.unbounded[Int]) mustBe (true)

        // [-∞,0]  [-∞,+∞)
        Interval.proper(None, Some(0), true, true).during(Interval.proper[Int](None, None, true, false)) mustBe (false)

        // [0, 2)  [-∞,+∞]
        Interval.proper(Some(0), Some(2), true, false).during(Interval.proper[Int](None, None, true, true)) mustBe (true)

        // [0]  [-∞,+∞)
        Interval.degenerate(0).during(Interval.proper[Int](None, None, true, false)) mustBe (true)

        // (3,+∞), (0,+∞]
        Interval.leftOpen(3).during(Interval.proper(Some(0), None, false, true)) mustBe (true)
        Interval.proper(Some(0), None, false, true).contains(Interval.leftOpen(3)) mustBe (true)
      }
    }

    /**
     * Starts, IsStartedBy
     *
     * {{{
     *   AAA
     *   BBBBBB
     * }}}
     */
    "starts & isStartedBy" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.starts(yy)) {
            assertFwdBck("s", xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].starts(Interval.degenerate(0)) mustBe (false)
        Interval.empty[Int].starts(Interval.closed(0, 1)) mustBe (false)
        Interval.empty[Int].starts(Interval.unbounded[Int]) mustBe (false)

        // Degenerate
        Interval.degenerate(5).starts(Interval.closed(5, 10)) mustBe (true)
        Interval.degenerate(5).starts(Interval.open(4, 10)) mustBe (true)
        Interval.degenerate(5).starts(Interval.open(5, 10)) mustBe (false)
        Interval.degenerate(5).starts(Interval.empty[Int]) mustBe (false)
        Interval.degenerate(5).starts(Interval.unbounded[Int]) mustBe (false)

        // Proper
        // [1, 2]  [1, 10]
        Interval.closed(1, 2).starts(Interval.closed(1, 10)) mustBe (true)
        Interval.closed(1, 10).isStartedBy(Interval.closed(1, 2)) mustBe (true)

        // (1, 3)  (1, 10)
        Interval.open(1, 4).starts(Interval.open(1, 10)) mustBe (true)

        // Infinity
        // [1, 5] [1, +inf)
        Interval.closed(1, 5).starts(Interval.leftClosed(1)) mustBe (true)

        // (-inf, 5]  (-inf, 10]
        Interval.rightClosed(5).starts(Interval.rightClosed(10)) mustBe (true)

        // (-inf, 5)  (-inf, +inf)
        Interval.rightClosed(5).starts(Interval.unbounded[Int]) mustBe (true)

        //  [5, 10)  [5, +inf)
        Interval.leftClosedRightOpen(5, 10).starts(Interval.leftClosed(5)) mustBe (true)
        Interval.leftClosedRightOpen(5, 10).isStartedBy(Interval.leftClosed(5)) mustBe (false)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].starts(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isStartedBy(Interval.unbounded[Int]) mustBe (false)

        // (0,2]  (0,+∞]
        Interval.leftOpenRightClosed(0, 2).starts(Interval.leftOpen(0)) mustBe (true)
      }
    }

    /**
     * Finishes, IsFinishedBy
     *
     * {{{
     *      AAA
     *   BBBBBB
     * }}}
     */
    "finishes & isFinishedBy" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.finishes(yy)) {
            assertFwdBck("f", xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].finishes(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].finishes(Interval.degenerate(0)) mustBe (false)
        Interval.empty[Int].finishes(Interval.closed(0, 5)) mustBe (false)

        // Degenerate
        Interval.degenerate(5).finishes(Interval.empty[Int]) mustBe (false)
        Interval.degenerate(5).finishes(Interval.degenerate(5)) mustBe (false)
        Interval.degenerate(5).finishes(Interval.closed(1, 5)) mustBe (true)
        Interval.degenerate(1).finishes(Interval.closed(1, 5)) mustBe (false)

        // Proper
        // [0,5)  [-1,5)
        Interval.leftClosedRightOpen(0, 5).finishes(Interval.leftClosedRightOpen(-1, 5)) mustBe (true)

        // (5, 10]  (2, 10]
        Interval.leftOpenRightClosed(5, 10).finishes(Interval.leftOpenRightClosed(2, 10)) mustBe (true)

        // Infinity
        // [5, 10]  (-inf, 10]
        Interval.closed(5, 10).finishes(Interval.rightClosed(10)) mustBe (true)

        // [10, +inf)  [5, +inf)
        Interval.leftClosed(10).finishes(Interval.leftClosed(5)) mustBe (true)

        // [5, +inf)  (-inf, +inf)
        Interval.leftClosed(5).finishes(Interval.unbounded[Int]) mustBe (true)

        // (0, 3)  (-inf, 3)
        Interval.open(0, 3).finishes(Interval.rightOpen(3)) mustBe (true)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].finishes(Interval.unbounded[Int]) mustBe (false)
      }
    }

    /**
     * Equals
     *
     * {{{
     *   AAAA
     *   BBBB
     * }}}
     */
    "equals" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.equalsTo(yy)) {
            assertFwdBck("e", xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].equalsTo(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.degenerate(0)) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.closed(0, 1)) mustBe (false)

        // Degenerate
        Interval.degenerate(5).equalsTo(Interval.degenerate(5)) mustBe (true)
        Interval.degenerate(5).equalsTo(Interval.empty[Int]) mustBe (false)

        // Proper
        Interval.open(4, 7).equalsTo(Interval.open(4, 7)) mustBe (true)
        Interval.open(0, 5).equalsTo(Interval.open(0, 5)) mustBe (true)
        Interval.closed(0, 5).equalsTo(Interval.closed(0, 5)) mustBe (true)
        Interval.leftOpenRightClosed(0, 5).equalsTo(Interval.leftOpenRightClosed(0, 5)) mustBe (true)
        Interval.leftClosedRightOpen(0, 5).equalsTo(Interval.leftClosedRightOpen(0, 5)) mustBe (true)

        // Infinity
        // [5, +inf)  [5, +inf)
        Interval.leftClosed(5).equalsTo(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5]  (-inf, 5]
        Interval.rightClosed(5).equalsTo(Interval.rightClosed(5)) mustBe (true)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) mustBe (true)

        // (-inf, 5)  (-inf, 5)
        Interval.rightOpen(5).equalsTo(Interval.rightOpen(5)) mustBe (true)
      }
    }

    "isSubset" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.isSubset(yy)) {
            assertOneOf(Set("s", "d", "f", "e"), xx, yy)
          }
        }
      }
    }

    "isSuperset" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.isSuperset(yy)) {
            assertOneOf(Set("S", "D", "F", "e"), xx, yy)
          }
        }
      }
    }

    "isDisjoint" should {
      "auto check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          whenever(xx.isDisjoint(yy)) {
            assertOneOf(Set("b", "B"), xx, yy)
          }
        }
      }
    }

    "satisfy" should {
      "one relation only" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ox2, ix1, ix2)
          val yy = Interval.make(oy1, oy2, iy1, iy2)

          assertAnySingle(xx, yy)
        }
      }

      "one relation only for edge cases" in {
        val intervals = List(
          (Interval.rightOpen(5), Interval.rightOpen(5))
        )

        intervals.foreach { case (xx, yy) => assertAnySingle(xx, yy) }
      }
    }
  }

  private def makeIntervalRelationsCheckMap[T: Ordering: Domain](using bOrd: Ordering[Boundary[T]]) =
    Map(
      "b" -> ((xx: Interval[T], yy: Interval[T]) => xx.preceeds(yy)),
      "B" -> ((xx: Interval[T], yy: Interval[T]) => xx.isPreceededBy(yy)),
      "m" -> ((xx: Interval[T], yy: Interval[T]) => xx.meets(yy)),
      "M" -> ((xx: Interval[T], yy: Interval[T]) => xx.isMetBy(yy)),
      "o" -> ((xx: Interval[T], yy: Interval[T]) => xx.overlaps(yy)),
      "O" -> ((xx: Interval[T], yy: Interval[T]) => xx.isOverlapedBy(yy)),
      "d" -> ((xx: Interval[T], yy: Interval[T]) => xx.during(yy)),
      "D" -> ((xx: Interval[T], yy: Interval[T]) => xx.contains(yy)),
      "s" -> ((xx: Interval[T], yy: Interval[T]) => xx.starts(yy)),
      "S" -> ((xx: Interval[T], yy: Interval[T]) => xx.isStartedBy(yy)),
      "f" -> ((xx: Interval[T], yy: Interval[T]) => xx.finishes(yy)),
      "F" -> ((xx: Interval[T], yy: Interval[T]) => xx.isFinishedBy(yy)),
      "e" -> ((xx: Interval[T], yy: Interval[T]) => xx.equalsTo(yy)),
      "E" -> ((xx: Interval[T], yy: Interval[T]) => xx.equalsTo(yy))
    )

  private val relationsCheckMap =
    Map(
      "b" -> ((r: Relation) => r.isBefore),
      "B" -> ((r: Relation) => r.isAfter),
      "m" -> ((r: Relation) => r.isMeets),
      "M" -> ((r: Relation) => r.isMetBy),
      "o" -> ((r: Relation) => r.isOverlaps),
      "O" -> ((r: Relation) => r.isOverlapedBy),
      "d" -> ((r: Relation) => r.isDuring),
      "D" -> ((r: Relation) => r.isContains),
      "s" -> ((r: Relation) => r.isStarts),
      "S" -> ((r: Relation) => r.isStartedBy),
      "f" -> ((r: Relation) => r.isFinishes),
      "F" -> ((r: Relation) => r.isFinishedBy),
      "e" -> ((r: Relation) => r.isEqualsTo),
      "E" -> ((r: Relation) => r.isEqualsTo)
    )

  /**
   * Finds name of the relations two itervals are satisfying
   *
   *   - A ? B
   */
  private def findSatisfyRelations[T: Ordering: Domain](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Set[String] =
    val relations = makeIntervalRelationsCheckMap[T]
    val satisfied = relations.foldLeft(Set.empty[String]) { case (acc, (k, fn)) =>
      val res = fn(xx, yy)
      if res then acc + k
      else acc
    }
    satisfied

  private def findSatisfyRelations(x: Relation): Set[String] =
    val satisfied = relationsCheckMap.foldLeft(Set.empty[String]) { case (acc, (k, fn)) =>
      val res = fn(x)
      if res then acc + k
      else acc
    }
    satisfied

  private def assertOneOf[T: Ordering: Domain](rs: Set[String], xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val trues = findSatisfyRelations(xx, yy) - "E" // "E" is a duplicate of "e"

    if trues.size != 1 || !rs.contains(trues.head) then
      fail(
        s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| should satisfy one of ${rs.mkString("[", ",", "]")} relations, however it satisfies ${trues.mkString("[", ",", "]")} instead"
      )

  private def assertOneOf(rs: Set[String], r: Relation): Unit =
    val trues = findSatisfyRelations(r) - "E" // "E" is a duplicate of "e"

    if trues.size != 1 || !rs.contains(trues.head) then
      fail(
        s"r: ${r} should satisfy one of ${rs.mkString("[", ",", "]")} relations, however it satisfies ${trues.mkString("[", ",", "]")} instead"
      )

  private def assertOne(k: String, r: Relation): Unit =
    val trues = findSatisfyRelations(r) - "E" // "E" is a duplicate of "e"

    if trues.size != 1 || !trues.contains(k) then
      fail(
        s"r: ${r} should satisfy only [${k}] relation, however it satisfies ${trues.mkString("[", ",", "]")} instead"
      )

  private def assertFwdBck[T: Ordering: Domain](r: String, xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val relations = makeIntervalRelationsCheckMap[T]

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

  private def assertAnySingle[T: Ordering: Domain](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val trues = findSatisfyRelations(xx, yy) - "E" // "E" is a duplicate of "e"

    val isNonEmpty = !(xx.isEmpty || yy.isEmpty)
    if isNonEmpty && trues.size != 1 then
      fail(s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| satisfies ${trues.size} relations: ${trues.mkString("[", ",", "]")}, expected only one relation")

  private def bit0(value: Byte): Int =
    bitN(0, value)

  private def bit1(value: Byte): Int =
    bitN(1, value)

  private def bit2(value: Byte): Int =
    bitN(2, value)

  private def bit3(value: Byte): Int =
    bitN(3, value)

  private def bitN(n: Byte, value: Byte): Int =
    if ((value & (1 << n)) > 0) then 1 else 0
