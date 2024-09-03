package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import com.github.gchudnov.mtg.internal.domain.*
import java.time.temporal.ChronoUnit
import java.time.*

/**
  * Char Domain
  * 
  * Can be used to use characters as values in the interval.
  * 
  * NOTE: the example is 'unsafe', prone to overflows and should not be used in production.
  */
final class CharDomain extends AnyDomain[Char] with Ordering[Char]:
  override def compare(x: Char, y: Char): Int = 
    x.compareTo(y)
  
  override def succ(x: Char): Char = 
    (x.toInt + 1).toChar
  
  override def pred(x: Char): Char = 
    (x.toInt - 1).toChar

  override def count(start: Char, end: Char): Long = 
    end.toInt - start.toInt

/**
 * Custom Char Domain
 *
 * {{{
 *    [******************]                    | [a,n] : 0
 *                 [**********************]   | [j,z] : 1
 *   -+------------+-----+----------------+-- |
 *    a            j     n                z   |
 * }}}
 */
object CustomCharDomain extends App:
  // custom domain with a resolution of 1 minute
  given customDomain: Domain[Char] = new CharDomain

  val a = Interval.closed('a', 'n')
  val b = Interval.closed('j', 'z')

  val renderer = AsciiRenderer.make[Char]()
  val diagram = Diagram
    .empty[Char]
    .withSection { s =>
      List(a, b).zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${k}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
 