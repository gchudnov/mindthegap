package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.*

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class BoundarySpec extends TestSpec:

  given domainDouble: Domain[Double]                 = Domain.makeFractional[Double](0.01)
  given domainOffsetDateTime: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.MINUTES)
  given domainInstant: Domain[Instant]               = Domain.makeInstant(ChronoUnit.MINUTES)

  "Boundary" when {

    "sorted" should {
      "order the boundaries" in {
        val bs = List(
          Boundary.Left(Some(1), false),
          Boundary.Left(Some(1), true),
          Boundary.Right(Some(4), false),
          Boundary.Right(Some(4), true)
        )

        val expected = List(
          Boundary.Left(Some(1), true),
          Boundary.Left(Some(1), false),
          Boundary.Right(Some(4), false),
          Boundary.Right(Some(4), true)
        )

        bs.sorted mustBe expected
      }
    }

    "Boundary.Left" should {
      "effective value of an Int" in {
        Boundary.Left[Int](None, true).effectiveValue mustBe (None)
        Boundary.Left[Int](None, false).effectiveValue mustBe (None)
        Boundary.Left[Int](Some(1), true).effectiveValue mustBe (Some(1))
        Boundary.Left[Int](Some(1), false).effectiveValue mustBe (Some(2))
      }

      "effective value of an Double" in {
        Boundary.Left[Double](None, true).effectiveValue mustBe (None)
        Boundary.Left[Double](None, false).effectiveValue mustBe (None)
        Boundary.Left[Double](Some(1.0), true).effectiveValue mustBe (Some(1.0))
        Boundary.Left[Double](Some(1.0), false).effectiveValue mustBe (Some(1.01))
      }

      "effective value of an OffsetDateTime" in {
        Boundary.Left[OffsetDateTime](None, true).effectiveValue mustBe (None)
        Boundary.Left[OffsetDateTime](None, false).effectiveValue mustBe (None)
        Boundary.Left[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), true).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:34Z")))
        Boundary.Left[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), false).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:35Z")))
      }

      "effective value of an Instant" in {
        Boundary.Left[Instant](None, true).effectiveValue mustBe (None)
        Boundary.Left[Instant](None, false).effectiveValue mustBe (None)
        Boundary.Left[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), true).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:34:00Z")))
        Boundary.Left[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), false).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:35:00Z")))
      }

      "canonical" in {
        Boundary.Left[Int](None, true).canonical mustBe Boundary.Left[Int](None, true)
        Boundary.Left[Int](None, false).canonical mustBe Boundary.Left[Int](None, true)
        Boundary.Left[Int](Some(1), true).canonical mustBe Boundary.Left[Int](Some(1), true)
        Boundary.Left[Int](Some(1), false).canonical mustBe Boundary.Left[Int](Some(2), true)
      }

      "succ" in {
        Boundary.Left[Int](None, true).succ mustBe Boundary.Left[Int](None, true)
        Boundary.Left[Int](None, false).succ mustBe Boundary.Left[Int](None, false)
        Boundary.Left[Int](Some(1), true).succ mustBe Boundary.Left[Int](Some(2), true)
        Boundary.Left[Int](Some(1), false).succ mustBe Boundary.Left[Int](Some(2), false)
      }

      "pred" in {
        Boundary.Left[Int](None, true).pred mustBe Boundary.Left[Int](None, true)
        Boundary.Left[Int](None, false).pred mustBe Boundary.Left[Int](None, false)
        Boundary.Left[Int](Some(1), true).pred mustBe Boundary.Left[Int](Some(0), true)
        Boundary.Left[Int](Some(1), false).pred mustBe Boundary.Left[Int](Some(0), false)
      }

      "left" in {
        Boundary.Left[Int](None, true).left mustBe Boundary.Left[Int](None, true)
        Boundary.Left[Int](None, false).left mustBe Boundary.Left[Int](None, false)
        Boundary.Left[Int](Some(1), true).left mustBe Boundary.Left[Int](Some(1), true)
        Boundary.Left[Int](Some(1), false).left mustBe Boundary.Left[Int](Some(1), false)
      }

      "right" in {
        Boundary.Left[Int](None, true).right mustBe Boundary.Right[Int](None, true)
        Boundary.Left[Int](None, false).right mustBe Boundary.Right[Int](None, true)
        Boundary.Left[Int](Some(1), true).right mustBe Boundary.Right[Int](Some(1), true)
        Boundary.Left[Int](Some(1), false).right mustBe Boundary.Right[Int](Some(2), true)
      }
    }

    "Boundary.Right" should {
      "effective value of an Int" in {
        Boundary.Right[Int](None, true).effectiveValue mustBe (None)
        Boundary.Right[Int](None, false).effectiveValue mustBe (None)
        Boundary.Right[Int](Some(1), true).effectiveValue mustBe (Some(1))
        Boundary.Right[Int](Some(1), false).effectiveValue mustBe (Some(0))
      }

      "effective value of an Double" in {
        Boundary.Right[Double](None, true).effectiveValue mustBe (None)
        Boundary.Right[Double](None, false).effectiveValue mustBe (None)
        Boundary.Right[Double](Some(1.0), true).effectiveValue mustBe (Some(1.0))
        Boundary.Right[Double](Some(1.0), false).effectiveValue mustBe (Some(0.99))
      }

      "effective value of an OffsetDateTime" in {
        Boundary.Right[OffsetDateTime](None, true).effectiveValue mustBe (None)
        Boundary.Right[OffsetDateTime](None, false).effectiveValue mustBe (None)
        Boundary.Right[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), true).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:34Z")))
        Boundary.Right[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), false).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:33Z")))
      }

      "effective value of an Instant" in {
        Boundary.Right[Instant](None, true).effectiveValue mustBe (None)
        Boundary.Right[Instant](None, false).effectiveValue mustBe (None)
        Boundary.Right[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), true).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:34:00Z")))
        Boundary.Right[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), false).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:33:00Z")))
      }

      "canonical" in {
        Boundary.Right[Int](None, true).canonical mustBe Boundary.Right[Int](None, true)
        Boundary.Right[Int](None, false).canonical mustBe Boundary.Right[Int](None, true)
        Boundary.Right[Int](Some(1), true).canonical mustBe Boundary.Right[Int](Some(1), true)
        Boundary.Right[Int](Some(1), false).canonical mustBe Boundary.Right[Int](Some(0), true)
      }

      "succ" in {
        Boundary.Right[Int](None, true).succ mustBe Boundary.Right[Int](None, true)
        Boundary.Right[Int](None, false).succ mustBe Boundary.Right[Int](None, false)
        Boundary.Right[Int](Some(1), true).succ mustBe Boundary.Right[Int](Some(2), true)
        Boundary.Right[Int](Some(1), false).succ mustBe Boundary.Right[Int](Some(2), false)
      }

      "pred" in {
        Boundary.Right[Int](None, true).pred mustBe Boundary.Right[Int](None, true)
        Boundary.Right[Int](None, false).pred mustBe Boundary.Right[Int](None, false)
        Boundary.Right[Int](Some(1), true).pred mustBe Boundary.Right[Int](Some(0), true)
        Boundary.Right[Int](Some(1), false).pred mustBe Boundary.Right[Int](Some(0), false)
      }

      "left" in {
        Boundary.Right[Int](None, true).left mustBe Boundary.Left[Int](None, true)
        Boundary.Right[Int](None, false).left mustBe Boundary.Left[Int](None, true)
        Boundary.Right[Int](Some(1), true).left mustBe Boundary.Left[Int](Some(1), true)
        Boundary.Right[Int](Some(1), false).left mustBe Boundary.Left[Int](Some(0), true)
      }

      "right" in {
        Boundary.Right[Int](None, true).right mustBe Boundary.Right[Int](None, true)
        Boundary.Right[Int](None, false).right mustBe Boundary.Right[Int](None, false)
        Boundary.Right[Int](Some(1), true).right mustBe Boundary.Right[Int](Some(1), true)
        Boundary.Right[Int](Some(1), false).right mustBe Boundary.Right[Int](Some(1), false)
      }
    }
  }
