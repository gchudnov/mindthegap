package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (true)
              r.isLessEqual mustBe (true)
              r.isLess mustBe (true)
              r.isGreaterEqual mustBe (false)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (true)
              r.isLessEqual mustBe (false)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (true)
              r.isGreater mustBe (true)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (true)
              r.isLess mustBe (true)
              r.isGreaterEqual mustBe (false)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (false)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (true)
              r.isGreater mustBe (true)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (true)
              r.isLess mustBe (true)
              r.isGreaterEqual mustBe (false)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (false)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (true)
              r.isGreater mustBe (true)
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

              r.isSubset mustBe (true)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (true)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (false)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (true)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (false)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (true)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (true)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (false)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (false)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (true)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (false)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (false)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (true)
              r.isSuperset mustBe (false)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (false)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (true)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (false)
              r.isSuperset mustBe (true)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (true)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (false)
              r.isGreater mustBe (false)
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

              r.isSubset mustBe (true)
              r.isSuperset mustBe (true)
              r.isDisjoint mustBe (false)
              r.isLessEqual mustBe (true)
              r.isLess mustBe (false)
              r.isGreaterEqual mustBe (true)
              r.isGreater mustBe (false)
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

      "isLessEqual" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val r = Relation.make(xx, yy)

            whenever(r.isLessEqual) {
              assertOneOf(Set("b", "m", "o", "s", "F", "e"), r)
            }
          }
        }
      }

      "isLess" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val r = Relation.make(xx, yy)

            whenever(r.isLess) {
              assertOneOf(Set("b", "m", "o"), r)
            }
          }
        }
      }

      "isGreaterEqual" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val r = Relation.make(xx, yy)

            whenever(r.isGreaterEqual) {
              assertOneOf(Set("B", "M", "O", "f", "S", "e"), r)
            }
          }
        }
      }

      "isGreater" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val r = Relation.make(xx, yy)

            whenever(r.isGreater) {
              assertOneOf(Set("B", "M", "O"), r)
            }
          }
        }
      }
    }

    "intersection" should {
      "empty" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Relation.intersection(xx, yy)

            whenever(zz.isEmpty) {
              val r = Relation.make(xx, yy)
              if xx.nonEmpty && yy.nonEmpty then assertOneOf(Set("b", "B"), r)
            }
          }
        }
      }

      "[a-, a+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Relation.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == xx.left) && (zz.right == xx.right)) {
              val r = Relation.make(xx, yy)
              assertOneOf(Set("s", "d", "f", "e"), r)
            }
          }
        }
      }

      "[a-, b+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Relation.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == xx.left) && (zz.right == yy.right)) {
              val r = Relation.make(xx, yy)
              assertOneOf(Set("O", "M", "S", "f", "e"), r)
            }
          }
        }

        "manual check" in {
          // (-∞,0), [0,+∞)
          val xx = Interval.make(LeftBoundary(None, false), RightBoundary(Some(0), false))
          val yy = Interval.make(LeftBoundary(Some(0), true), RightBoundary(None, false))

          val zz = Relation.intersection(xx, yy)

          zz.isEmpty mustBe (true)
        }
      }

      "[b-, a+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Relation.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == yy.left) && (zz.right == xx.right)) {
              val r = Relation.make(xx, yy)
              assertOneOf(Set("m", "o", "F", "s", "e"), r)
            }
          }
        }
      }

      "[b-, b+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Relation.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == yy.left) && (zz.right == yy.right)) {
              val r = Relation.make(xx, yy)
              assertOneOf(Set("D", "F", "S", "e"), r)
            }
          }
        }
      }
    }
  }

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

  private def findSatisfyRelations(x: Relation): Set[String] =
    val satisfied = relationsCheckMap.foldLeft(Set.empty[String]) { case (acc, (k, fn)) =>
      val res = fn(x)
      if res then acc + k
      else acc
    }
    satisfied

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
