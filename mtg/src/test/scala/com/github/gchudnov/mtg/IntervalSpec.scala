package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

final class IntervalSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  val ordM: Ordering[Mark[Int]]     = summon[Domain[Int]].ordMark
  val ordI: Ordering[Interval[Int]] = summon[Ordering[Interval[Int]]]

  "Interval" when {

    "make" should {

      /**
       * {{{
       *   Given that a < b:
       *
       *   - Empty      | [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
       *   - Point      | [a, a] = {a}
       *   - Proper     | otherwise
       * }}}
       */
      "create intervals" in {
        forAll(genAnyIntArgs) { case args =>
          val actual = Interval.make(args.left, args.right)

          if ordM.gt(args.left, args.right) then
            actual.isEmpty mustBe (true)
            actual.nonEmpty mustBe (false)
            actual.isPoint mustBe (false)
            actual.nonPoint mustBe (true)
            actual.isProper mustBe (false)
            actual.nonProper mustBe (true)
          else if ordM.equiv(args.left, args.right) then
            actual.isEmpty mustBe (false)
            actual.nonEmpty mustBe (true)
            actual.isPoint mustBe (true)
            actual.nonPoint mustBe (false)
            actual.isProper mustBe (false)
            actual.nonProper mustBe (true)
          else
            actual.isEmpty mustBe (false)
            actual.nonEmpty mustBe (true)
            actual.isPoint mustBe (false)
            actual.nonPoint mustBe (true)
            actual.isProper mustBe (true)
            actual.nonProper mustBe (false)
        }
      }

      "handle edge cases" in {
        // [0, 0)
        Interval.make(Mark.at(0), Mark.pred(0)).isEmpty mustBe true

        // [-inf, +inf]
        Interval.make[Int](Mark.at(Value.infNeg), Mark.at(Value.infPos)).isProper mustBe true

        // (-inf, +inf)
        Interval.make[Int](Mark.succ(Value.infNeg), Mark.pred(Value.infPos)).isProper mustBe true

        // (3, 5)
        Interval.make(Mark.succ(3), Mark.pred(5)).isPoint mustBe true

        // [4, 4]
        Interval.make(Mark.at(4), Mark.at(4)).isPoint mustBe true

        // (3, 4]
        Interval.make(Mark.succ(3), Mark.at(4)).isPoint mustBe true

        // [4, 5)
        Interval.make(Mark.at(4), Mark.pred(5)).isPoint mustBe true
      }

      "create an open interval from Option[T]" in {
        val table = Table(
          ("left", "right", "expected"),
          (Some(1), Some(5), Interval.open(1, 5)),
          (Some(1), None, Interval.leftOpen(1)),
          (None, Some(5), Interval.rightOpen(5)),
          (None, None, Interval.unbounded[Int]),
        )

        forAll(table) { case (left, right, expected) =>
          val actual = Interval.open(left, right)
          actual mustBe expected
        }
      }

      "create a closed interval from Option[T]" in {
        val table = Table(
          ("left", "right", "expected"),
          (Some(1), Some(5), Interval.closed(1, 5)),
          (Some(1), None, Interval.leftClosed(1)),
          (None, Some(5), Interval.rightClosed(5)),
          (None, None, Interval.unbounded[Int]),
        )

        forAll(table) { case (left, right, expected) =>
          val actual = Interval.closed(left, right)
          actual mustBe expected
        }
      }
    }

    "factory methods" should {

      "Interval.empty" in {
        val a = Interval.empty[Int]

        a.isEmpty mustBe (true)
        a.isPoint mustBe (false)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (false)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (true)

        a.left mustBe Mark.at(Value.infPos)
        a.right mustBe Mark.at(Value.infNeg)
      }

      "Interval.point(x)" in {
        val a = Interval.point(5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (true)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (false)
        a.nonProper mustBe (true)

        ordM.equiv(a.left, Mark.at(5)) mustBe true
        ordM.equiv(a.right, Mark.at(5)) mustBe true
      }

      "Interval.proper(x, y)" in {
        // (1, 5)
        val a = Interval.proper(Mark.succ(1), Mark.pred(5))

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordI.equiv(a, Interval.closed(2, 4)) mustBe (true)
      }

      "Interval.unbounded" in {
        val a = Interval.unbounded[Int]

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.at(Value.infNeg)) mustBe true
        ordM.equiv(a.right, Mark.at(Value.infPos)) mustBe true
      }

      "Interval.open(x, y)" in {
        val a = Interval.open(1, 5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.succ(1)) mustBe true
        ordM.equiv(a.right, Mark.pred(5)) mustBe true
      }

      "Interval.closed(x, y)" in {
        val a = Interval.closed(1, 5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.at(1)) mustBe true
        ordM.equiv(a.right, Mark.at(5)) mustBe true
      }

      "Interval.leftOpen(x)" in {
        val a = Interval.leftOpen(1)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.succ(1)) mustBe true
        ordM.equiv(a.right, Mark.at(Value.infPos)) mustBe true
      }

      "Interval.leftClosed(x)" in {
        val a = Interval.leftClosed(5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.at(5)) mustBe true
        ordM.equiv(a.right, Mark.at(Value.infPos)) mustBe true
      }

      "Interval.rightOpen(x)" in {
        val a = Interval.rightOpen(1)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.at(Value.infNeg)) mustBe true
        ordM.equiv(a.right, Mark.pred(1)) mustBe true
      }

      "Interval.rightClosed(x)" in {
        val a = Interval.rightClosed(5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.at(Value.infNeg)) mustBe true
        ordM.equiv(a.right, Mark.at(5)) mustBe true
      }

      "Interval.leftClosedRightOpen(x, y)" in {
        val a = Interval.leftClosedRightOpen(1, 10)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.at(1)) mustBe true
        ordM.equiv(a.right, Mark.pred(10)) mustBe true
      }

      "Interval.leftOpenRightClosed(x, y)" in {
        val a = Interval.leftOpenRightClosed(1, 10)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        ordM.equiv(a.left, Mark.succ(1)) mustBe true
        ordM.equiv(a.right, Mark.at(10)) mustBe true
      }

    }

    "canonical" should {
      "represent an interval" in {
        // (1, 5) = [2, 4]
        Interval.open(1, 5).canonical mustBe Interval.closed(2, 4)

        // [2, 5) = [2, 4]
        Interval.leftClosedRightOpen(2, 5).canonical mustBe Interval.closed(2, 4)

        // (1, 4] = [2, 4]
        Interval.leftOpenRightClosed(1, 4).canonical mustBe Interval.closed(2, 4)

        // [2, 4] = [2, 4]
        Interval.closed(2, 4).canonical mustBe Interval.closed(2, 4)

        // {2} = {2}
        Interval.point(2).canonical mustBe Interval.point(2)
        Interval.make(Mark.at(2), Mark.at(2)).canonical mustBe Interval.point(2)
        Interval.make(Mark.succ(1), Mark.pred(3)).canonical mustBe Interval.point(2)
      }

      "double canonical is canonical" in {
        Interval.open(1, 5).canonical.canonical mustBe Interval.closed(2, 4)
      }
    }

    "normalize" should {
      "provide a normal form of the intervals" in {
        // [1, 5] -> [1, 5]
        Interval.make(Mark.at(1), Mark.at(5)).normalize mustBe Interval.closed(1, 5)

        // )1, 5] -> [0, 5]
        Interval.make(Mark.pred(1), Mark.at(5)).normalize mustBe Interval.closed(0, 5)

        // ))1, 5] -> [-1, 5]
        Interval.make(Mark.pred(Mark.pred(1)), Mark.at(5)).normalize mustBe Interval.closed(-1, 5)

        // (1, 5] -> (1, 5]
        Interval.make(Mark.succ(1), Mark.at(5)).normalize mustBe Interval.leftOpenRightClosed(1, 5)

        // ((1, 5] -> (2, 5]
        Interval.make(Mark.succ(Mark.succ(1)), Mark.at(5)).normalize mustBe Interval.leftOpenRightClosed(2, 5)

        // [1, 5) -> [1, 5)
        Interval.make(Mark.at(1), Mark.pred(5)).normalize mustBe Interval.leftClosedRightOpen(1, 5)

        // [1, 5)) -> [1, 4)
        Interval.make(Mark.at(1), Mark.pred(Mark.pred(5))).normalize mustBe Interval.leftClosedRightOpen(1, 4)

        // [1, 5( -> [1, 6]
        Interval.make(Mark.at(1), Mark.succ(5)).normalize mustBe Interval.closed(1, 6)

        // [1, 5(( -> [1, 7]
        Interval.make(Mark.at(1), Mark.succ(Mark.succ(5))).normalize mustBe Interval.closed(1, 7)

        // ))1, 5((
        Interval.make(Mark.pred(Mark.pred(1)), Mark.succ(Mark.succ(5))).normalize mustBe Interval.closed(-1, 7)
      }
    }

    "swap" should {
      "swap left and right in positive interval" in {
        val a = Interval.make(Mark.at(1), Mark.at(5))

        val actual   = a.swap
        val expected = Interval.make(Mark.at(5), Mark.at(1))

        a.isEmpty mustBe (false)
        actual.isEmpty mustBe (true)

        actual mustBe (expected)
      }

      "swap left and right in negative interval" in {
        val a = Interval.make(Mark.at(5), Mark.at(1))

        val actual   = a.swap
        val expected = Interval.make(Mark.at(1), Mark.at(5))

        a.isEmpty mustBe (true)
        actual.isEmpty mustBe (false)

        actual mustBe (expected)
      }

      "double-swap restores the original interval" in {
        val a = Interval.make(Mark.at(5), Mark.at(1))

        val actual   = a.swap.swap
        val expected = a

        actual mustBe (expected)
      }
    }

    "inflate" should {
      "make an interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.inflate.canonical
        val expected = Interval.closed(0, 2)

        actual mustBe (expected)
      }

      "expand a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflate.canonical
        val expected = Interval.closed(0, 3)

        actual mustBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.inflate.canonical
        val expected = Interval.closed(1, 2)

        actual mustBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflate.deflate.canonical
        val expected = a

        actual mustBe (expected)
      }
    }

    "inflateLeft" should {
      "make a real out of point" in {
        val a = Interval.point(1)

        val actual   = a.inflateLeft.canonical
        val expected = Interval.closed(0, 1)

        actual mustBe (expected)
      }

      "expand a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateLeft.canonical
        val expected = Interval.closed(0, 2)

        actual mustBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.inflateLeft.canonical
        val expected = Interval.point(1)

        actual mustBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateLeft.deflateLeft.canonical
        val expected = a.canonical

        actual mustBe (expected)
      }
    }

    "inflateRight" should {
      "make a real out of point" in {
        val a = Interval.point(1)

        val actual   = a.inflateRight.canonical
        val expected = Interval.closed(1, 2)

        actual mustBe (expected)
      }

      "expand a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateRight.canonical
        val expected = Interval.closed(1, 3)

        actual mustBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.inflateRight.canonical
        val expected = Interval.point(2)

        actual mustBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateRight.deflateRight.canonical
        val expected = a.canonical

        actual mustBe (expected)
      }
    }

    "deflate" should {
      "make an empty interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.deflate.canonical
        val expected = Interval.closed(0, 2).swap

        actual mustBe (expected)
      }

      "shrink a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflate.canonical
        val expected = Interval.closed(1, 2).swap

        actual mustBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.deflate.canonical
        val expected = Interval.closed(0, 3).swap

        actual mustBe (expected)
      }

      "deflate, inflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflate.inflate.canonical
        val expected = a.canonical

        actual mustBe (expected)
      }
    }

    "deflateLeft" should {
      "make an empty interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.deflateLeft.canonical
        val expected = Interval.closed(1, 2).swap.canonical

        actual mustBe (expected)
      }

      "shrink a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateLeft.canonical
        val expected = Interval.point(2)

        actual mustBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.deflateLeft.canonical
        val expected = Interval.closed(1, 3).swap

        actual mustBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateLeft.inflateLeft.canonical
        val expected = a.canonical

        actual mustBe (expected)
      }
    }

    "deflateRight" should {
      "make an empty interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.deflateRight.canonical
        val expected = Interval.closed(0, 1).swap.canonical

        actual mustBe (expected)
      }

      "shrink a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateRight.canonical
        val expected = Interval.point(1)

        actual mustBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.deflateRight.canonical
        val expected = Interval.closed(0, 2).swap

        actual mustBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateRight.inflateRight.canonical
        val expected = a.canonical

        actual mustBe (expected)
      }
    }

    "size" should {
      "return the size of an interval" in {
        val t = Table(
          ("interval", "size"),
          (Interval.point(1), Some(1)),
          (Interval.closed(1, 2), Some(2)),
          (Interval.open(1, 4), Some(2)),
          (Interval.leftOpen(1), None),
          (Interval.rightOpen(2), None),
          (Interval.empty[Int], None),
          (Interval.unbounded[Int], None),
        )

        forAll(t) { (interval, size) =>
          interval.size mustBe (size)
        }
      }
    }
  }
