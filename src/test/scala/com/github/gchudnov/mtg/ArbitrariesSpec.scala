package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class ArbitrariesSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  "Arbitraties" when {

    "genEmptyIntArgs" should {
      "a.isEmpty == true" in {
        forAll(genEmptyIntArgs) { case (args) =>
          val actual = Interval.make(args.left, args.right)

          actual.isEmpty mustBe (true)
          actual.isPoint mustBe (false)
          actual.isProper mustBe (false)
        }
      }
    }

    "genPointIntArgs" should {
      "a.isPoint == true" in {
        forAll(genPointIntArgs) { case (args) =>
          val actual = Interval.make(args.left, args.right)

          actual.isEmpty mustBe (false)
          actual.isPoint mustBe (true)
          actual.isProper mustBe (false)
        }
      }
    }

    "genProperIntArgs" should {
      "a.isProper == true" in {
        forAll(genProperIntArgs) { case (args) =>
          val actual = Interval.make(args.left, args.right)

          actual.isEmpty mustBe (false)
          actual.isPoint mustBe (false)
          actual.isProper mustBe (true)
        }
      }
    }
  }
