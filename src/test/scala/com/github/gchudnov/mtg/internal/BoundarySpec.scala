package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Values
import com.github.gchudnov.mtg.Value
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class BoundarySpec extends TestSpec:
  import Values.integralValue

  given valueDouble: Value[Double] = Values.fractionalValue[Double](0.01)
  given valueOffsetDateTime: Value[OffsetDateTime] = Values.offsetDateTimeValue(ChronoUnit.MINUTES)

  "Boundary" when {
    "LeftBoundary" should {
      "get an effective value for Int" in {
        LeftBoundary[Int](None, isInclude = true).effectiveValue mustBe(None)
        LeftBoundary[Int](None, isInclude = false).effectiveValue mustBe(None)        
        LeftBoundary[Int](Some(1), isInclude = true).effectiveValue mustBe(Some(1))
        LeftBoundary[Int](Some(1), isInclude = false).effectiveValue mustBe(Some(2))
      }

      "get an effective value for Double" in {
        LeftBoundary[Double](None, isInclude = true).effectiveValue mustBe(None)
        LeftBoundary[Double](None, isInclude = false).effectiveValue mustBe(None)        
        LeftBoundary[Double](Some(1.0), isInclude = true).effectiveValue mustBe(Some(1.0))
        LeftBoundary[Double](Some(1.0), isInclude = false).effectiveValue mustBe(Some(1.01))        
      }

      "get an effective value for OffsetDateTime" in {
        LeftBoundary[OffsetDateTime](None, isInclude = true).effectiveValue mustBe(None)
        LeftBoundary[OffsetDateTime](None, isInclude = false).effectiveValue mustBe(None)        
        LeftBoundary[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), isInclude = true).effectiveValue mustBe(Some(OffsetDateTime.parse("2017-07-02T12:34Z")))
        LeftBoundary[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), isInclude = false).effectiveValue mustBe(Some(OffsetDateTime.parse("2017-07-02T12:35Z")))
      }      
    }

    "RightBoundary" should {
      "get an effective value for Int" in {
        RightBoundary[Int](None, isInclude = true).effectiveValue mustBe(None)
        RightBoundary[Int](None, isInclude = false).effectiveValue mustBe(None)        
        RightBoundary[Int](Some(1), isInclude = true).effectiveValue mustBe(Some(1))
        RightBoundary[Int](Some(1), isInclude = false).effectiveValue mustBe(Some(0))
      }

      "get an effective value for Double" in {
        RightBoundary[Double](None, isInclude = true).effectiveValue mustBe(None)
        RightBoundary[Double](None, isInclude = false).effectiveValue mustBe(None)        
        RightBoundary[Double](Some(1.0), isInclude = true).effectiveValue mustBe(Some(1.0))
        RightBoundary[Double](Some(1.0), isInclude = false).effectiveValue mustBe(Some(0.99))
      }      
    }    
  }
