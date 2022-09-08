package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Relation.*
import com.github.gchudnov.mtg.ordering.OptionPartialOrdering
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

import scala.math.PartialOrdering

final class RelationSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  private val pOrd: PartialOrdering[Option[Int]] = summon[PartialOrdering[Option[Int]]]

  "Relation" when {

    /**
     * Preceeds, IsPreceededBy
     *
     * {{{
     *   AAA
     *        BBB
     * }}}
     */
    "preceeds (before) & isPreceededBy (after)" should {
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

      "check edge cases" in {
        Interval.open(1, 2).preceeds(Interval.open(5, 6)) mustBe (true)
        Interval.open(5, 6).isPreceededBy(Interval.open(1, 2)) mustBe (true)
      }
    }

    /**
     * Meets, IsMetBy
     *
     * {{{
     *   AAA
     *      BBB
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

      "check edge cases" in {
        Interval.closed(1, 5).meets(Interval.closed(5, 10)) mustBe (true)
      }
    }

    /**
     * Overlaps, IsOverlapedBy
     *
     * {{{
     *   AAAA
     *     BBBB
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

      "check edge cases" in {
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)
        Interval.open(1, 10).overlaps(Interval.open(11, 20)) mustBe (false)
        Interval.open(1, 10).overlaps(Interval.open(1, 11)) mustBe (false)
        Interval.open(1, 10).overlaps(Interval.open(20, 30)) mustBe (false)

        Interval.open(1, 10).overlaps(Interval.degenerate(10)) mustBe (false)
        Interval.closed(1, 10).overlaps(Interval.degenerate(10)) mustBe (false)

        Interval.open(1, 10).overlaps(Interval.open(-10, 20)) mustBe (false)

        Interval.open(1, 10).overlaps(Interval.open(2, 11)) mustBe (true)
        Interval.open(1, 10).isOverlapedBy(Interval.open(2, 11)) mustBe (false)
        Interval.open(1, 10).isOverlapedBy(Interval.open(2, 10)) mustBe (false)
        Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

        Interval.unbounded[Int].overlaps(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).overlaps(Interval.unbounded[Int]) mustBe (false)

        Interval.unbounded[Int].isOverlapedBy(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

        Interval.unbounded[Int].isOverlapedBy(Interval.degenerate(2)) mustBe (false)
        Interval.degenerate(2).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

        Interval.open(1, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (false)
        Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

        Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)

        Interval.rightOpen(2).overlaps(Interval.leftOpen(-2)) mustBe (true)
        Interval.leftOpen(-2).isOverlapedBy(Interval.rightOpen(2)) mustBe (true)
      }
    }

    /**
     * During, Contains
     *
     * {{{
     *     AA
     *   BBBBBB
     * }}}
     */
    "during & contains (includes)" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.during(wz)) {
            // println(s"d: ${(xy, wz)}")

            assertRelation("d", xy, wz)

            val isXgtW = pOrd.gt(ox, ow)
            val isXeqW = pOrd.equiv(ox, ow)

            val isYltZ = pOrd.lt(oy, oz)
            val isYeqZ = pOrd.equiv(oy, oz)

            (((isXgtW || (isXeqW && iw && !ix)) && (isYltZ || (isYeqZ && iz && !iy))) ||
              ((isXgtW || (isXeqW && iw && !ix)) && oz.isEmpty) ||
              ((isYltZ || (isYeqZ && iz && !iy)) && ow.isEmpty)) mustBe (true)
          }
        }
      }

      "check edge cases" in {
        Interval.degenerate(5).during(Interval.open(2, 9)) mustBe (true)
        Interval.open(2, 9).contains(Interval.degenerate(5)) mustBe (true)
      }
    }

    /**
     * Starts, IsStartedBy
     *
     * {{{
     *   AAA
     *   BBBBBB
     * }}}
     */
    "starts & isStartedBy" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.starts(wz)) {
            // println(s"s: ${(xy, wz)}")

            assertRelation("s", xy, wz)

            val isXeqW = pOrd.equiv(ox, ow)

            val isYltZ = pOrd.lt(oy, oz)
            val isYeqZ = pOrd.equiv(oy, oz)

            (isXeqW && ((oy.isDefined && oz.isEmpty) || (isYltZ || (isYeqZ && (!iy && iz))))) mustBe (true)
          }
        }
      }

      "check edge cases" in {
        Interval.leftClosedRightOpen(5, 10).starts(Interval.leftClosed(5)) mustBe (true)
        Interval.leftClosedRightOpen(5, 10).isStartedBy(Interval.leftClosed(5)) mustBe (false)

        Interval.unbounded[Int].starts(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isStartedBy(Interval.unbounded[Int]) mustBe (false)

        Interval.closed(1, 2).starts(Interval.closed(1, 10)) mustBe (true)
        Interval.closed(1, 10).isStartedBy(Interval.closed(1, 2)) mustBe (true)
      }
    }

    /**
     * Finishes, IsFinishedBy
     *
     * {{{
     *      AAA
     *   BBBBBB
     * }}}
     */
    "finishes & isFinishedBy" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.finishes(wz)) {
            // println(s"f: ${(xy, wz)}")

            assertRelation("f", xy, wz)

            val isYeqZ = pOrd.equiv(oy, oz)

            val isXgtW = pOrd.gt(ox, ow)
            val isXeqW = pOrd.equiv(ox, ow)

            (isYeqZ && ((ox.isDefined && ow.isEmpty) || (isXgtW || (isXeqW && (iw && !ix))))) mustBe (true)
          }
        }
      }

      "check edge cases" in {
        Interval.unbounded[Int].finishes(Interval.unbounded[Int]) mustBe (false)

        Interval.leftOpenRightClosed(5, 10).finishes(Interval.leftOpenRightClosed(2, 10)) mustBe (true)
      }
    }

    /**
     * Equals
     *
     * {{{
     *   AAAA
     *   BBBB
     * }}}
     */
    "equals" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.same(wz)) {
            // println(s"e: ${(xy, wz)}")

            assertRelation("e", xy, wz)

            val isXeqW = pOrd.equiv(ox, ow)
            val isYeqZ = pOrd.equiv(oy, oz)

            ((isXeqW && isYeqZ && (ix == iw) && (iy == iz)) ||
              (xy.isEmpty && wz.isEmpty)) mustBe (true)
          }
        }
      }

      "check edge cases" in {
        Interval.unbounded[Int].same(Interval.unbounded[Int]) mustBe (true)
        Interval.open(0, 5).same(Interval.open(0, 5)) mustBe (true)
        Interval.closed(0, 5).same(Interval.closed(0, 5)) mustBe (true)
        Interval.leftOpen(0, 5).same(Interval.leftOpen(0, 5)) mustBe (true)
        Interval.rightOpen(0, 5).same(Interval.rightOpen(0, 5)) mustBe (true)
        Interval.leftClosed(0, 5).same(Interval.leftClosed(0, 5)) mustBe (true)
        Interval.rightClosed(0, 5).same(Interval.rightClosed(0, 5)) mustBe (true)
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
      "O" -> ((ab: Interval[T], cd: Interval[T]) => ab.isOverlapedBy(cd)),
      "d" -> ((ab: Interval[T], cd: Interval[T]) => ab.during(cd)),
      "D" -> ((ab: Interval[T], cd: Interval[T]) => ab.contains(cd)),
      "s" -> ((ab: Interval[T], cd: Interval[T]) => ab.starts(cd)),
      "S" -> ((ab: Interval[T], cd: Interval[T]) => ab.isStartedBy(cd)),
      "f" -> ((ab: Interval[T], cd: Interval[T]) => ab.finishes(cd)),
      "F" -> ((ab: Interval[T], cd: Interval[T]) => ab.isFinishedBy(cd)),
      "e" -> ((ab: Interval[T], cd: Interval[T]) => ab.same(cd)),
      "E" -> ((ab: Interval[T], cd: Interval[T]) => ab.same(cd))
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
