package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.internal.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

final class IntervalSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint
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
        forAll(genAnyIntArgs) { args =>
          val actual = Interval.make(args.left, args.right)

          if ordE.gt(args.left, args.right) then
            actual.isEmpty shouldBe (true)
            actual.nonEmpty shouldBe (false)
            actual.isPoint shouldBe (false)
            actual.nonPoint shouldBe (true)
            actual.isProper shouldBe (false)
            actual.nonProper shouldBe (true)
          else if ordE.equiv(args.left, args.right) then
            actual.isEmpty shouldBe (false)
            actual.nonEmpty shouldBe (true)
            actual.isPoint shouldBe (true)
            actual.nonPoint shouldBe (false)
            actual.isProper shouldBe (false)
            actual.nonProper shouldBe (true)
          else
            actual.isEmpty shouldBe (false)
            actual.nonEmpty shouldBe (true)
            actual.isPoint shouldBe (false)
            actual.nonPoint shouldBe (true)
            actual.isProper shouldBe (true)
            actual.nonProper shouldBe (false)
        }
      }

      "handle edge cases" in {
        // [0, 0)
        Interval.make(Endpoint.at(0), Endpoint.pred(0)).isEmpty shouldBe true

        // [-∞, +∞]
        Interval.make[Int](Endpoint.at(Value.InfNeg), Endpoint.at(Value.InfPos)).isProper shouldBe true

        // (-∞, +∞)
        Interval.make[Int](Endpoint.succ(Value.InfNeg), Endpoint.pred(Value.InfPos)).isProper shouldBe true

        // (3, 5)
        Interval.make(Endpoint.succ(3), Endpoint.pred(5)).isPoint shouldBe true

        // [4, 4]
        Interval.make(Endpoint.at(4), Endpoint.at(4)).isPoint shouldBe true

        // (3, 4]
        Interval.make(Endpoint.succ(3), Endpoint.at(4)).isPoint shouldBe true

        // [4, 5)
        Interval.make(Endpoint.at(4), Endpoint.pred(5)).isPoint shouldBe true
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
          actual shouldBe expected
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
          actual shouldBe expected
        }
      }
    }

    "factory methods" should {

      "Interval.empty" in {
        val a = Interval.empty[Int]

        a.isEmpty shouldBe (true)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (false)

        a.nonEmpty shouldBe (false)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (true)

        a.leftEndpoint shouldBe Endpoint.at(Value.InfPos)
        a.rightEndpoint shouldBe Endpoint.at(Value.InfNeg)
      }

      "Interval.point(x)" in {
        val a = Interval.point(5)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (true)
        a.isProper shouldBe (false)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (false)
        a.nonProper shouldBe (true)

        ordE.equiv(a.leftEndpoint, Endpoint.at(5)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.at(5)) shouldBe true
      }

      "Interval.proper(x, y) if endpoints are provided" in {
        // (1, 5)
        val a = Interval.proper(Endpoint.succ(1), Endpoint.pred(5))

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordI.equiv(a, Interval.closed(2, 4)) shouldBe (true)
      }

      "Interval.proper(x, y) if values are provided" in {
        // (1, 5) == [2, 4]
        val a = Interval.proper(2, 4)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordI.equiv(a, Interval.closed(2, 4)) shouldBe (true)
      }

      "Interval.proper(x, y) throws an exception if left endpoint is greater than right endpoint" in {
        assertThrows[IllegalArgumentException] {
          Interval.proper(5, 1)
        }
      }

      "Interval.unbounded" in {
        val a = Interval.unbounded[Int]

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.at(Value.InfNeg)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.at(Value.InfPos)) shouldBe true
      }

      "Interval.open(x, y)" in {
        val a = Interval.open(1, 5)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.succ(1)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.pred(5)) shouldBe true
      }

      "Interval.closed(x, y)" in {
        val a = Interval.closed(1, 5)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.at(1)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.at(5)) shouldBe true
      }

      "Interval.leftOpen(x)" in {
        val a = Interval.leftOpen(1)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.succ(1)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.at(Value.InfPos)) shouldBe true
      }

      "Interval.leftClosed(x)" in {
        val a = Interval.leftClosed(5)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.at(5)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.at(Value.InfPos)) shouldBe true
      }

      "Interval.rightOpen(x)" in {
        val a = Interval.rightOpen(1)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.at(Value.InfNeg)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.pred(1)) shouldBe true
      }

      "Interval.rightClosed(x)" in {
        val a = Interval.rightClosed(5)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.at(Value.InfNeg)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.at(5)) shouldBe true
      }

      "Interval.leftClosedRightOpen(x, y)" in {
        val a = Interval.leftClosedRightOpen(1, 10)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.at(1)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.pred(10)) shouldBe true
      }

      "Interval.leftOpenRightClosed(x, y)" in {
        val a = Interval.leftOpenRightClosed(1, 10)

        a.isEmpty shouldBe (false)
        a.isPoint shouldBe (false)
        a.isProper shouldBe (true)

        a.nonEmpty shouldBe (true)
        a.nonPoint shouldBe (true)
        a.nonProper shouldBe (false)

        ordE.equiv(a.leftEndpoint, Endpoint.succ(1)) shouldBe true
        ordE.equiv(a.rightEndpoint, Endpoint.at(10)) shouldBe true
      }

    }

    "canonical" should {
      "represent an interval" in {
        // (1, 5) = [2, 4]
        Interval.open(1, 5).canonical shouldBe Interval.closed(2, 4)

        // [2, 5) = [2, 4]
        Interval.leftClosedRightOpen(2, 5).canonical shouldBe Interval.closed(2, 4)

        // (1, 4] = [2, 4]
        Interval.leftOpenRightClosed(1, 4).canonical shouldBe Interval.closed(2, 4)

        // [2, 4] = [2, 4]
        Interval.closed(2, 4).canonical shouldBe Interval.closed(2, 4)

        // {2} = {2}
        Interval.point(2).canonical shouldBe Interval.point(2)
        Interval.make(Endpoint.at(2), Endpoint.at(2)).canonical shouldBe Interval.point(2)
        Interval.make(Endpoint.succ(1), Endpoint.pred(3)).canonical shouldBe Interval.point(2)
      }

      "double canonical is canonical" in {
        Interval.open(1, 5).canonical.canonical shouldBe Interval.closed(2, 4)
      }
    }

    "normalize" should {
      "provide a normal form of the intervals" in {
        val a = Interval.make(Endpoint.at(1), Endpoint.at(5))
        val b = Interval.make(Endpoint.pred(1), Endpoint.at(5))
        val c = Interval.make(Endpoint.pred(Endpoint.pred(1)), Endpoint.at(5))
        val d = Interval.make(Endpoint.succ(1), Endpoint.at(5))
        val e = Interval.make(Endpoint.succ(Endpoint.succ(1)), Endpoint.at(5))
        val f = Interval.make(Endpoint.at(1), Endpoint.pred(5))
        val g = Interval.make(Endpoint.at(1), Endpoint.pred(Endpoint.pred(5)))
        val h = Interval.make(Endpoint.at(1), Endpoint.succ(5))
        val i = Interval.make(Endpoint.at(1), Endpoint.succ(Endpoint.succ(5)))
        val j = Interval.make(Endpoint.pred(Endpoint.pred(1)), Endpoint.succ(Endpoint.succ(5)))

        // [1, 5] -> [1, 5]
        a.normalize shouldBe Interval.closed(1, 5)

        // )1, 5] -> [0, 5]
        b.normalize shouldBe Interval.closed(0, 5)

        // ))1, 5] -> [-1, 5]
        c.normalize shouldBe Interval.closed(-1, 5)

        // (1, 5] -> (1, 5]
        d.normalize shouldBe Interval.leftOpenRightClosed(1, 5)

        // ((1, 5] -> (2, 5]
        e.normalize shouldBe Interval.leftOpenRightClosed(2, 5)

        // [1, 5) -> [1, 5)
        f.normalize shouldBe Interval.leftClosedRightOpen(1, 5)

        // [1, 5)) -> [1, 4)
        g.normalize shouldBe Interval.leftClosedRightOpen(1, 4)

        // [1, 5( -> [1, 6]
        h.normalize shouldBe Interval.closed(1, 6)

        // [1, 5(( -> [1, 7]
        i.normalize shouldBe Interval.closed(1, 7)

        // ))1, 5((
        j.normalize shouldBe Interval.closed(-1, 7)
      }
    }

    "swap" should {
      "swap left and right in positive interval" in {
        val a = Interval.make(Endpoint.at(1), Endpoint.at(5))

        val actual   = a.swap
        val expected = Interval.make(Endpoint.at(5), Endpoint.at(1))

        a.isEmpty shouldBe (false)
        actual.isEmpty shouldBe (true)

        actual shouldBe (expected)
      }

      "swap left and right in negative interval" in {
        val a = Interval.make(Endpoint.at(5), Endpoint.at(1))

        val actual   = a.swap
        val expected = Interval.make(Endpoint.at(1), Endpoint.at(5))

        a.isEmpty shouldBe (true)
        actual.isEmpty shouldBe (false)

        actual shouldBe (expected)
      }

      "double-swap restores the original interval" in {
        val a = Interval.make(Endpoint.at(5), Endpoint.at(1))

        val actual   = a.swap.swap
        val expected = a

        actual shouldBe (expected)
      }
    }

    "inflate" should {
      "make an interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.inflate.canonical
        val expected = Interval.closed(0, 2)

        actual shouldBe (expected)
      }

      "expand a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflate.canonical
        val expected = Interval.closed(0, 3)

        actual shouldBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.inflate.canonical
        val expected = Interval.closed(1, 2)

        actual shouldBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflate.deflate.canonical
        val expected = a

        actual shouldBe (expected)
      }
    }

    "inflateLeft" should {
      "make a real out of point" in {
        val a = Interval.point(1)

        val actual   = a.inflateLeft.canonical
        val expected = Interval.closed(0, 1)

        actual shouldBe (expected)
      }

      "expand a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateLeft.canonical
        val expected = Interval.closed(0, 2)

        actual shouldBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.inflateLeft.canonical
        val expected = Interval.point(1)

        actual shouldBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateLeft.deflateLeft.canonical
        val expected = a.canonical

        actual shouldBe (expected)
      }
    }

    "inflateRight" should {
      "make a real out of point" in {
        val a = Interval.point(1)

        val actual   = a.inflateRight.canonical
        val expected = Interval.closed(1, 2)

        actual shouldBe (expected)
      }

      "expand a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateRight.canonical
        val expected = Interval.closed(1, 3)

        actual shouldBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.inflateRight.canonical
        val expected = Interval.point(2)

        actual shouldBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.inflateRight.deflateRight.canonical
        val expected = a.canonical

        actual shouldBe (expected)
      }
    }

    "deflate" should {
      "make an empty interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.deflate.canonical
        val expected = Interval.closed(0, 2).swap

        actual shouldBe (expected)
      }

      "shrink a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflate.canonical
        val expected = Interval.closed(1, 2).swap

        actual shouldBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.deflate.canonical
        val expected = Interval.closed(0, 3).swap

        actual shouldBe (expected)
      }

      "deflate, inflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflate.inflate.canonical
        val expected = a.canonical

        actual shouldBe (expected)
      }
    }

    "deflateLeft" should {
      "make an empty interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.deflateLeft.canonical
        val expected = Interval.closed(1, 2).swap.canonical

        actual shouldBe (expected)
      }

      "shrink a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateLeft.canonical
        val expected = Interval.point(2)

        actual shouldBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.deflateLeft.canonical
        val expected = Interval.closed(1, 3).swap

        actual shouldBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateLeft.inflateLeft.canonical
        val expected = a.canonical

        actual shouldBe (expected)
      }
    }

    "deflateRight" should {
      "make an empty interval out of point" in {
        val a = Interval.point(1)

        val actual   = a.deflateRight.canonical
        val expected = Interval.closed(0, 1).swap.canonical

        actual shouldBe (expected)
      }

      "shrink a real interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateRight.canonical
        val expected = Interval.point(1)

        actual shouldBe (expected)
      }

      "expand an empty interval" in {
        val a = Interval.closed(1, 2).swap

        val actual   = a.deflateRight.canonical
        val expected = Interval.closed(0, 2).swap

        actual shouldBe (expected)
      }

      "inflate, deflate produces the original interval" in {
        val a = Interval.closed(1, 2)

        val actual   = a.deflateRight.inflateRight.canonical
        val expected = a.canonical

        actual shouldBe (expected)
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
          interval.size shouldBe (size)
        }
      }
    }

    "left" should {
      "return the left endpoint value" in {
        val t = Table(
          ("interval", "left"),
          (Interval.point(1), Some(1)),
          (Interval.closed(1, 2), Some(1)),
          (Interval.open(1, 4), Some(1)),
          (Interval.leftOpen(1), Some(1)),
          (Interval.rightOpen(2), None),
          (Interval.empty[Int], None),
          (Interval.unbounded[Int], None),
        )

        forAll(t) { (interval, left) =>
          interval.left shouldBe (left)
        }
      }
    }

    "right" should {
      "return the right endpoint value" in {
        val t = Table(
          ("interval", "right"),
          (Interval.point(1), Some(1)),
          (Interval.closed(1, 2), Some(2)),
          (Interval.open(1, 4), Some(4)),
          (Interval.leftOpen(1), None),
          (Interval.rightOpen(2), Some(2)),
          (Interval.empty[Int], None),
          (Interval.unbounded[Int], None),
        )

        forAll(t) { (interval, right) =>
          interval.right shouldBe (right)
        }
      }
    }

    "isLeftOpen" should {
      "return true if the left endpoint is open" in {
        val t = Table(
          ("interval", "isLeftOpen"),
          (Interval.point(1), false),
          (Interval.closed(1, 2), false),
          (Interval.open(1, 4), true),
          (Interval.leftOpen(1), true),
          (Interval.rightOpen(2), false),
          (Interval.empty[Int], false),
          (Interval.unbounded[Int], false),
        )

        forAll(t) { (interval, isLeftOpen) =>
          interval.isLeftOpen shouldBe (isLeftOpen)
        }
      }
    }

    "isLeftClosed" should {
      "return true if the left endpoint is closed" in {
        val t = Table(
          ("interval", "isLeftClosed"),
          (Interval.point(1), true),
          (Interval.closed(1, 2), true),
          (Interval.open(1, 4), false),
          (Interval.leftOpen(1), false),
          (Interval.rightOpen(2), false),
          (Interval.empty[Int], false),
          (Interval.unbounded[Int], false),
        )

        forAll(t) { (interval, isLeftClosed) =>
          interval.isLeftClosed shouldBe (isLeftClosed)
        }
      }
    }

    "isRightOpen" should {
      "return true if the right endpoint is open" in {
        val t = Table(
          ("interval", "isRightOpen"),
          (Interval.point(1), false),
          (Interval.closed(1, 2), false),
          (Interval.open(1, 4), true),
          (Interval.leftOpen(1), false),
          (Interval.rightOpen(2), true),
          (Interval.empty[Int], false),
          (Interval.unbounded[Int], false),
        )

        forAll(t) { (interval, isRightOpen) =>
          interval.isRightOpen shouldBe (isRightOpen)
        }
      }
    }

    "isRightClosed" should {
      "return true if the right endpoint is closed" in {
        val t = Table(
          ("interval", "isRightClosed"),
          (Interval.point(1), true),
          (Interval.closed(1, 2), true),
          (Interval.open(1, 4), false),
          (Interval.leftOpen(1), false),
          (Interval.rightOpen(2), false),
          (Interval.empty[Int], false),
          (Interval.unbounded[Int], false),
        )

        forAll(t) { (interval, isRightClosed) =>
          interval.isRightClosed shouldBe (isRightClosed)
        }
      }
    }

    "toString" should {

      "represent an Empty interval" in {
        val i = Interval.empty[Int]

        val actual   = i.toString
        val expected = "∅"

        actual shouldBe expected
      }

      "represent a Point interval" in {
        val i = Interval.point(1)

        val actual   = i.toString
        val expected = "{1}"

        actual shouldBe expected
      }

      "represent a Proper interval" in {
        // NOTE: it is not possible to have closed boundaries for infinity: [-∞,+∞]
        val t = Table(
          ("x", "expected"),
          (Interval.proper(Endpoint.at(1), Endpoint.at(2)), "[1,2]"),
          (Interval.proper(Endpoint.at(1), Endpoint.pred(3)), "[1,3)"),
          (Interval.proper(Endpoint.succ(1), Endpoint.at(3)), "(1,3]"),
          (Interval.proper(Endpoint.succ(1), Endpoint.pred(4)), "(1,4)"),
          (Interval.proper(Endpoint.at(Value.InfNeg), Endpoint.at(2)), "(-∞,2]"),
          (Interval.proper(Endpoint.succ(Value.InfNeg), Endpoint.pred(2)), "(-∞,2)"),
          (Interval.proper(Endpoint.at(1), Endpoint.at(Value.InfPos)), "[1,+∞)"),
          (Interval.proper(Endpoint.succ(1), Endpoint.pred(Value.InfPos)), "(1,+∞)"),
          (Interval.proper[Int](Endpoint.at(Value.InfNeg), Endpoint.at(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Endpoint.at(Value.InfNeg), Endpoint.pred(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Endpoint.succ(Value.InfNeg), Endpoint.at(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Endpoint.succ(Value.InfNeg), Endpoint.pred(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper(Endpoint.pred(1), Endpoint.at(3)), "[0,3]"),
          (Interval.proper(Endpoint.at(1), Endpoint.succ(2)), "[1,3]"),
        )

        forAll(t) { (xx, expected) =>
          val actual = xx.toString
          actual shouldBe expected
        }
      }
    }

    "toDebugString" should {
      "represent an interval" in {
        val i = Interval.closed(1, 2)

        val actual   = i.toDebugString
        val expected = "AnyInterval(At(Finite(1)), At(Finite(2)))"

        actual shouldBe expected
      }
    }
  }
