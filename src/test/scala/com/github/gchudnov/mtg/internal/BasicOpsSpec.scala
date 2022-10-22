package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Diagram
import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.Theme

final class BasicOpsSpec extends TestSpec:

  "BasicOps" when {

    "intersection" should {
      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[a-, a+] if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(1, 5)

        actual mustBe expected
      }

      "[a-, a+] if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 7)

        actual mustBe expected
      }

      "[a-, a+] if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 10)

        actual mustBe expected
      }

      "[a-, a+] if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 10)

        actual mustBe expected
      }

      "[a-, b+] if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 7)

        actual mustBe expected
      }

      "[a-, b+] if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.intersection(b)
        val expected = Interval.point(5)

        actual mustBe expected
      }

      "[a-, b+] if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.intersection(b)
        val expected = Interval.closed(1, 5)

        actual mustBe expected
      }

      "[b-, a+] in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.intersection(b)
        val expected = Interval.point(5)

        actual mustBe expected
      }

      "[b-, a+] in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.intersection(b)
        val expected = Interval.closed(7, 10)

        actual mustBe expected
      }

      "[b-, a+] in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(7, 10)

        actual mustBe expected
      }

      "[b-, b+] if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 7)

        actual mustBe expected
      }

      "draw an intersection" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val c = a.intersection(b)

        val canvas: Canvas = Canvas.make(40)
        val diagram        = Diagram.make(List(a, b, c), canvas)

        val actual = Diagram.render(diagram, Theme.default.copy(legend = true))

        val expected = List(
          "                  [******************]   | [5,10]",
          "  [**********************]               | [1,7]",
          "                  [******]               | [5,7]",
          "--+---------------+------+-----------+-- |",
          "  1               5      7          10   |"
        )

        actual mustBe expected
      }
    }

    "XXXXX" should {

    }
  }
