package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class ArbitrariesSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  "Arbitraries" when {

    "genEmptyIntArgs" should {
      "a.isEmpty == true" in {
        forAll(genEmptyIntArgs) { case (args) =>
          val actual = Interval.make(args.left, args.right)

          actual.isEmpty shouldBe (true)
          actual.isPoint shouldBe (false)
          actual.isProper shouldBe (false)
        }
      }
    }

    "genPointIntArgs" should {
      "a.isPoint == true" in {
        forAll(genPointIntArgs) { case (args) =>
          val actual = Interval.make(args.left, args.right)

          actual.isEmpty shouldBe (false)
          actual.isPoint shouldBe (true)
          actual.isProper shouldBe (false)
        }
      }
    }

    "genProperIntArgs" should {
      "a.isProper == true" in {
        forAll(genProperIntArgs) { case (args) =>
          val actual = Interval.make(args.left, args.right)

          actual.isEmpty shouldBe (false)
          actual.isPoint shouldBe (false)
          actual.isProper shouldBe (true)
        }
      }
    }
  }
