package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domains.given

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.BoundaryOrdering
import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Show.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.LeftBoundary
import com.github.gchudnov.mtg.RightBoundary

final class IntervalAlgSpec extends TestSpec with IntervalRelAssert:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  "IntervalAlg" when {
    "intersection" should {
      "empty" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Interval.intersection(xx, yy)

            whenever(zz.isEmpty) {
              if xx.nonEmpty && yy.nonEmpty then assertOneOf(Set("b", "B"), xx, yy)
            }
          }
        }
      }

      "[a-, a+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Interval.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == xx.left) && (zz.right == xx.right)) {
              assertOneOf(Set("s", "d", "f", "e"), xx, yy)
            }
          }
        }
      }

      "[a-, b+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Interval.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == xx.left) && (zz.right == yy.right)) {
              assertOneOf(Set("O", "M", "S", "f", "e"), xx, yy)
            }
          }
        }

        "manual check" in {
          // (-∞,0), [0,+∞)
          val xx = Interval.make(LeftBoundary(None, false), RightBoundary(Some(0), false))
          val yy = Interval.make(LeftBoundary(Some(0), true), RightBoundary(None, false))

          val zz = Interval.intersection(xx, yy)

          zz.isEmpty mustBe (true)
        }
      }

      "[b-, a+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Interval.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == yy.left) && (zz.right == xx.right)) {
              assertOneOf(Set("m", "o", "F", "s", "e"), xx, yy)
            }
          }
        }
      }

      "[b-, b+]" should {
        "auto check" in {
          forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
            val xx = Interval.make(ox1, ox2, ix1, ix2)
            val yy = Interval.make(oy1, oy2, iy1, iy2)

            val zz = Interval.intersection(xx, yy)

            whenever(xx.nonEmpty && yy.nonEmpty && zz.nonEmpty && (zz.left == yy.left) && (zz.right == yy.right)) {
              assertOneOf(Set("D", "F", "S", "e"), xx, yy)
            }
          }
        }
      }
    }
  }
