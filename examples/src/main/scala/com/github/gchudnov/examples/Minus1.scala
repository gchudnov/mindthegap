package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Munus #1
 *
 * {{{
 * Given
 *     [*********)                            | [0.0,2.0) : a
 *                    [****)                  | [3.0,4.0) : b
 *                              [*********)   | [5.0,7.0) : c
 *                                            |
 * And Interval To Remove                     |
 *          [************************)        | [1.0,6.0) : d
 *                                            |
 * When Subtracted                            |
 *                                            |
 *     [****)                                 | [0.0,1.0) : e
 *                                   [****)   | [6.0,7.0) : f
 *   --+----+----+----+----+----+----+----+-- |
 *    0.0  1.0  2.0  3.0  4.0  5.0  6.0  7.0  |
 * }}}
 */
object Minus1 extends App:
  given domainDouble: Domain[Double] = Domain.makeFractional(0.1)

  val a = Interval.leftClosedRightOpen(0.0, 2.0)
  val b = Interval.leftClosedRightOpen(3.0, 4.0)
  val c = Interval.leftClosedRightOpen(5.0, 7.0)
  val d = Interval.leftClosedRightOpen(1.0, 6.0)

  val intervals = List(a, b, c)

  val result = intervals.flatMap { x =>
    if x.intersects(d) then Interval.minus(x, d)
    else List(x)
  }

  val renderer = AsciiRenderer.make[Double]()
  val diagram = Diagram
    .empty[Double]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('e' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
