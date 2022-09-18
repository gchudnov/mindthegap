package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Domains

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class BoundarySpec extends TestSpec:
  import Domains.integralDomain

  given domainDouble: Domain[Double]                 = Domains.fractionalDomain[Double](0.01)
  given domainOffsetDateTime: Domain[OffsetDateTime] = Domains.offsetDateTimeDomain(ChronoUnit.MINUTES)
  given domainInstant: Domain[Instant]               = Domains.instantDomain(ChronoUnit.MINUTES)

  "Boundary" when {
    "LeftBoundary" should {

      "get an effective value of an Int" in {
        LeftBoundary[Int](None, isInclude = true).effectiveValue mustBe (None)
        LeftBoundary[Int](None, isInclude = false).effectiveValue mustBe (None)
        LeftBoundary[Int](Some(1), isInclude = true).effectiveValue mustBe (Some(1))
        LeftBoundary[Int](Some(1), isInclude = false).effectiveValue mustBe (Some(2))
      }

      "get an effective value of an Double" in {
        LeftBoundary[Double](None, isInclude = true).effectiveValue mustBe (None)
        LeftBoundary[Double](None, isInclude = false).effectiveValue mustBe (None)
        LeftBoundary[Double](Some(1.0), isInclude = true).effectiveValue mustBe (Some(1.0))
        LeftBoundary[Double](Some(1.0), isInclude = false).effectiveValue mustBe (Some(1.01))
      }

      "get an effective value of an OffsetDateTime" in {
        LeftBoundary[OffsetDateTime](None, isInclude = true).effectiveValue mustBe (None)
        LeftBoundary[OffsetDateTime](None, isInclude = false).effectiveValue mustBe (None)
        LeftBoundary[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), isInclude = true).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:34Z")))
        LeftBoundary[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), isInclude = false).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:35Z")))
      }

      "get an effective value of an Instant" in {
        LeftBoundary[Instant](None, isInclude = true).effectiveValue mustBe (None)
        LeftBoundary[Instant](None, isInclude = false).effectiveValue mustBe (None)
        LeftBoundary[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), isInclude = true).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:34:00Z")))
        LeftBoundary[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), isInclude = false).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:35:00Z")))
      }
    }

    "RightBoundary" should {
      "get an effective value of an Int" in {
        RightBoundary[Int](None, isInclude = true).effectiveValue mustBe (None)
        RightBoundary[Int](None, isInclude = false).effectiveValue mustBe (None)
        RightBoundary[Int](Some(1), isInclude = true).effectiveValue mustBe (Some(1))
        RightBoundary[Int](Some(1), isInclude = false).effectiveValue mustBe (Some(0))
      }

      "get an effective value of an Double" in {
        RightBoundary[Double](None, isInclude = true).effectiveValue mustBe (None)
        RightBoundary[Double](None, isInclude = false).effectiveValue mustBe (None)
        RightBoundary[Double](Some(1.0), isInclude = true).effectiveValue mustBe (Some(1.0))
        RightBoundary[Double](Some(1.0), isInclude = false).effectiveValue mustBe (Some(0.99))
      }

      "get an effective value of an OffsetDateTime" in {
        RightBoundary[OffsetDateTime](None, isInclude = true).effectiveValue mustBe (None)
        RightBoundary[OffsetDateTime](None, isInclude = false).effectiveValue mustBe (None)
        RightBoundary[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), isInclude = true).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:34Z")))
        RightBoundary[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), isInclude = false).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:33Z")))
      }

      "get an effective value of an Instant" in {
        RightBoundary[Instant](None, isInclude = true).effectiveValue mustBe (None)
        RightBoundary[Instant](None, isInclude = false).effectiveValue mustBe (None)
        RightBoundary[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), isInclude = true).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:34:00Z")))
        RightBoundary[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), isInclude = false).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:33:00Z")))
      }
    }
  }
