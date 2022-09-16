package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Values.given

final class ValuesSpec extends TestSpec:

  "Values" when {
    "Int" should {
      "get predecessor and successor values" in {
        val intValue = summon[Value[Int]]

        val x: Int = 10

        val xp = intValue.pred(x)
        val xn = intValue.succ(x)

        xp mustEqual (9)
        xn mustEqual (11)
      }
    }

    "Long" should {
      "get predecessor and successor values" in {
        val intValue = summon[Value[Long]]

        val x: Long = 10L

        val xp = intValue.pred(x)
        val xn = intValue.succ(x)

        xp mustEqual (9L)
        xn mustEqual (11L)
      }
    }
  }
