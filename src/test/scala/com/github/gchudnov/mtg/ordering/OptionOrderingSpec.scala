package com.github.gchudnov.mtg

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import scala.math.PartialOrdering

final class OptionOrderingSpec extends TestSpec:

  "OptionOrdering" when {
    import com.github.gchudnov.mtg.ordering.OptionOrdering

    "(Option[T], Option[T]) are compared" should {
      "gt" in {
        val t = Table(
          ("x", "y", "expected"),
          (Some(5), Some(3), true),
          (Some(3), Some(5), false),
          (None, Some(5), false),
          (Some(3), None, true),
          (None, None, false)
        )

        forAll(t) { case (x, y, expected) =>
          Ordering[Option[Int]].gt(x, y) mustEqual (expected)
        }
      }
    }
  }

  "OptionPartialOrdering" when {
    import com.github.gchudnov.mtg.ordering.OptionPartialOrdering

    "(Option[T], Option[T]) are partially compared" should {
      "gt" in {
        val t = Table(
          ("x", "y", "expected"),
          (Some(5), Some(3), true),
          (Some(3), Some(5), false),
          (None, Some(5), false),
          (Some(3), None, false),
          (None, None, false)
        )

        forAll(t) { case (x, y, expected) =>
          PartialOrdering[Option[Int]].gt(x, y) mustEqual (expected)
        }
      }
    }
  }
