package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec

final class MarkOrderingSpec extends TestSpec:

  val ordM: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "MarkOrdering" when {
    "ordM.compare(a, b)" should {
      "return the expected results" in {
        val a = Mark.at(1)
        val b = Mark.succ(a)
        val c = Mark.pred(a)

        ordM.compare(a, a) mustBe (0)
        ordM.compare(a, b) mustBe (-1)
        ordM.compare(a, c) mustBe (1)
        ordM.compare(b, a) mustBe (1)
        ordM.compare(b, b) mustBe (0)
        ordM.compare(b, c) mustBe (1)
        ordM.compare(c, a) mustBe (-1)
        ordM.compare(c, b) mustBe (-1)
        ordM.compare(c, c) mustBe (0)
      }
    }

    "complex values" should {
      "order them" in {
        val xs = List[Mark[Int]](Mark.at(1), Mark.succ(Mark.succ(1)), Mark.pred(1), Mark.pred(Mark.pred(1)))

        val actual   = xs.sorted
        val expected = List[Mark[Int]](Mark.pred(Mark.pred(1)), Mark.pred(1), Mark.at(1), Mark.succ(Mark.succ(1)))

        actual must contain theSameElementsAs (expected)
      }
    }
  }
