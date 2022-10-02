package com.github.gchudnov.mtg
import com.github.gchudnov.mtg.Arbitraries.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class IntervalSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  "Interval" when {

    "make" should {

      "create an empty interval" in {
        val a = Interval.empty[Int]

        a.isEmpty mustBe (true)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (false)

        a.isUnbounded mustBe (false)

        assertThrows[NoSuchElementException] {
          a.left
        }

        assertThrows[NoSuchElementException] {
          a.right
        }
      }

      "create an unbounded interval" in {
        val a = Interval.unbounded[Int]

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (true)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "create a degenerate interval" in {
        val a = Interval.degenerate(5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (true)
        a.isProper mustBe (false)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "create a proper interval" in {
        val a = Interval.proper(Some(1), Some(5), false, false)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "create an open interval" in {
        val a = Interval.open(1, 5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "create a closed interval" in {
        val a = Interval.closed(1, 5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "create a leftOpen interval" in {
        val a = Interval.leftOpen(1)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "create a leftClosed interval" in {
        val a = Interval.leftClosed(5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "create a rightOpen interval" in {
        val a = Interval.rightOpen(1)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "create a rightClosed interval" in {
        val a = Interval.rightClosed(5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "create a leftClosedRightOpen interval" in {
        val a = Interval.leftClosedRightOpen(1, 10)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "create a leftOpenRightClosed interval" in {
        val a = Interval.leftOpenRightClosed(1, 10)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

    }
  }
