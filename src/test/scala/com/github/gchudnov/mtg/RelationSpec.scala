package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Relation.*
import com.github.gchudnov.mtg.ordering.OptionPartialOrdering
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

import scala.math.PartialOrdering

final class RelationSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 100.0)

  private val pOrd: PartialOrdering[Option[Int]] = summon[PartialOrdering[Option[Int]]]

  "Relation" when {

    /**
     * Preceeds, IsPreceededBy
     *
     * {{{
     *   AAA]
     *        [BBB
     * }}}
     */
    "preceeds & isPreceededBy" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.preceeds(wz)) {
            // println(s"p: ${(xy, wz)}")

            assertRelation("p", xy, wz)

            val isYltW = pOrd.lt(oy, ow)
            val isYeqW = pOrd.equiv(oy, ow)

            (isYltW || (isYeqW && !(iy && iw))) mustBe (true)
          }
        }
      }
    }

    /**
     * Meets, IsMetBy
     *
     * {{{
     *   AAA]
     *      [BBB
     * }}}
     */
    "meets & isMetBy" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.meets(wz)) {
            // println(s"m: ${(xy, wz)}")

            assertRelation("m", xy, wz)

            val isYeqW = pOrd.equiv(oy, ow)

            (isYeqW && (iy && iw)) mustBe (true)
          }
        }
      }
    }

    /**
     * Overlaps, IsOverlapedBy
     *
     * {{{
     *   [AAA]
     *     [BBB]
     * }}}
     */
    "overlaps & isOverlapedBy" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.overlaps(wz)) {
            // println(s"o: ${(xy, wz)}")

            assertRelation("o", xy, wz)

            val isYgtW = pOrd.gt(oy, ow)
            val isYltZ = pOrd.lt(oy, oz)
            val isYeqZ = pOrd.equiv(oy, oz)
            val isXltW = pOrd.lt(ox, ow)
            val isXeqW = pOrd.equiv(ox, ow)

            (isYgtW && ((ox.isEmpty && oz.isEmpty) || (isXltW && oz.isEmpty) || (isXeqW && ix && !iw && oz.isEmpty) || (ox.isEmpty && isYltZ)
              || (ox.isEmpty && isYeqZ && !iy && iz) || ((isXltW || (isXeqW && ix && !iw)) && (isYltZ || (isYeqZ && !iy && iz))))) mustBe (true)
          }
        }
      }
    }
  }

  private def makeRelations[T: Ordering] =
    Map(
      "p" -> ((ab: Interval[T], cd: Interval[T]) => ab.preceeds(cd)),
      "P" -> ((ab: Interval[T], cd: Interval[T]) => ab.isPreceededBy(cd)),
      "m" -> ((ab: Interval[T], cd: Interval[T]) => ab.meets(cd)),
      "M" -> ((ab: Interval[T], cd: Interval[T]) => ab.isMetBy(cd)),
      "o" -> ((ab: Interval[T], cd: Interval[T]) => ab.overlaps(cd)),
      "O" -> ((ab: Interval[T], cd: Interval[T]) => ab.isOverlapedBy(cd))
    )

  private def assertRelation[T: Ordering](r: String, xy: Interval[T], wz: Interval[T]): Unit =
    val relations = makeRelations[T]

    val fk = r
    val bk = r.map(_.toUpper)
    val ks = List(fk, bk)

    val fwd  = relations(fk)
    val bck  = relations(bk)
    val rest = relations.filterNot { case (k, _) => ks.contains(k) }

    fwd(xy, wz) mustBe (true)
    bck(wz, xy) mustBe (true)

    rest.foreach { case (_, fn) =>
      fn(xy, wz) mustBe (false)
      fn(wz, xy) mustBe (false)
    }

/*
We can posit prop­er­ties like these:

Given any three time­stamps f1, f2, and f3, if [f1,f2] con­tains f3, then f3 >= f1 and f3 <= f2. Else, f3 < f1 or f3 > f2.

Given any two time­stamps f1 and f2, if (,f1) over­laps (f2,), then f1 > f2. Else, f1 <= f2.
 */
