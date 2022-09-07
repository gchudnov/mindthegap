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

      "edge cases" in {
        Interval.open(1, 10).overlaps(Interval.open(5, 100)) mustBe(true)
        Interval.open(1, 10).overlaps(Interval.open(11, 100)) mustBe(false)


        // TODO: impl it, see https://github.com/Breinify/brein-time-utilities/blob/master/test/com/brein/time/timeintervals/intervals/TestInterval.java


        /*
        Assert.assertFalse(new LongInterval(1L, 10L).overlaps(new DoubleInterval(10.1, 10.2)));
        Assert.assertTrue(new LongInterval(1L, 10L).overlaps(new LongInterval(10L, 10L)));
        Assert.assertTrue(new LongInterval(1L, 10L).overlaps(new LongInterval(-10L, 12L)));

        Assert.assertTrue(new DoubleInterval(1.0, 5.0, true, true).overlaps(new DoubleInterval(1.0, 5.0, true, true)));
        Assert.assertFalse(new DoubleInterval(1.0, 4.9, true, true).overlaps(new DoubleInterval(4.9, 5.0, true, true)));
        Assert.assertTrue(new DoubleInterval(1.0, 4.9, true, false)
                .overlaps(new DoubleInterval(4.9, 5.0, false, true)));
        */
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
      "E" -> ((ab: Interval[T], cd: Interval[T]) => ab.same(cd)),      
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

    // NOTE: for Proper intervals only one of the relations holds.
    //       for Degenerate multiple relations might hold, e.g. { Meets, Starts } or { Meets, Equals } at the same time.
    if xy.isProper && wz.isProper then
      rest.foreach { case (_, fn) =>
        fn(xy, wz) mustBe (false)
        fn(wz, xy) mustBe (false)
      }
