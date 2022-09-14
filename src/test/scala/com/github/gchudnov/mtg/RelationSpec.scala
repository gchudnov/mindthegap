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
     * Before, After (Preceeds, IsPreceededBy)
     *
     * {{{
     *   AAA
     *        BBB
     * }}}
     */
    "before (preceeds) & after (isPreceededBy)" should {
      "check" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          whenever(xy.preceeds(wz)) {
            // println(s"p: ${(xy, wz)}")

            assertRelation("b", xy, wz)

            val isYltW = pOrd.lt(oy, ow)
            val isYeqW = pOrd.equiv(oy, ow)

            (isYltW || (isYeqW && !(iy && iw))) mustBe (true)
          }
        }
      }

      "check edge cases" in {
        // (1, 2)  (5, 6)
        Interval.open(1, 2).preceeds(Interval.open(5, 6)) mustBe (true)
        Interval.open(5, 6).isPreceededBy(Interval.open(1, 2)) mustBe (true)

        // Infinity

        // (1, 2)  (3, +inf)
        Interval.open(1, 2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  (3, 5)
        Interval.rightOpen(2).before(Interval.open(3, 5)) mustBe (true)

        // (-inf, 2)  (3, +inf)
        Interval.rightOpen(2).before(Interval.leftOpen(3)) mustBe (true)
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
        // [1, 5]  [5, 10]
        Interval.closed(1, 5).meets(Interval.closed(5, 10)) mustBe (true)

        // Infinity

        // [1, 5]  [5, +inf)
        Interval.closed(1, 5).meets(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5]  [5, 10]
        Interval.rightClosed(5).meets(Interval.closed(5, 10)) mustBe (true)

        // (-inf, 5]  [5, +inf)
        Interval.rightClosed(5).meets(Interval.leftClosed(5)) mustBe (true)
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

        // Infinity

        // [1, 5]  [3, +inf)
        Interval.closed(1, 5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // (-inf, 5]  [3, 10]
        Interval.rightClosed(5).overlaps(Interval.closed(3, 10)) mustBe (true)

        // (-inf, 5]  [3, +inf)
        Interval.rightClosed(5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // Empty

        // TODO: add tests for https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap

        // {}  (-inf, +inf)
        Interval.empty[Int].overlaps(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].overlaps(Interval.empty[Int]) mustBe (false)

        // {} [1, 2]
        Interval.empty[Int].overlaps(Interval.closed(1, 2)) mustBe (false)
        Interval.closed(1, 2).overlaps(Interval.empty[Int]) mustBe (false)

        // TODO: add tests for empty intervals
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
        // {5}  (2, 9)
        Interval.degenerate(5).during(Interval.open(2, 9)) mustBe (true)

        // (2, 9)  {5}
        Interval.open(2, 9).contains(Interval.degenerate(5)) mustBe (true)

        // [-∞,0]  [-∞,+∞)
        Interval.proper(None, Some(0), true, true).during(Interval.proper[Int](None, None, true, false)) mustBe (false)

        // [0, 1)  [-∞,+∞]
        Interval.proper(Some(0), Some(1), true, false).during(Interval.proper[Int](None, None, true, true)) mustBe (true)

        // Infinity

        // [5, 7]  [3, +inf)
        Interval.closed(5, 7).during(Interval.leftClosed(3)) mustBe (true)

        // [5, 7]  (-inf, 10]
        Interval.closed(5, 7).during(Interval.rightClosed(10)) mustBe (true)

        // [5, 7] (-inf, +inf)
        Interval.closed(5, 7).during(Interval.unbounded[Int]) mustBe (true)
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

        // Infinity

        // [1, 5] [1, +inf)
        Interval.closed(1, 5).starts(Interval.leftClosed(1)) mustBe (true)

        // (-inf, 5]  (-inf, 10]
        Interval.rightClosed(5).starts(Interval.rightClosed(10)) mustBe (true)

        // (-inf, 5)  (-inf, +inf)
        Interval.rightClosed(5).starts(Interval.unbounded[Int]) mustBe (true)
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
        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].finishes(Interval.unbounded[Int]) mustBe (false)

        // (5, 10]  (2, 10]
        Interval.leftOpenRightClosed(5, 10).finishes(Interval.leftOpenRightClosed(2, 10)) mustBe (true)

        // Infinity

        // [5, 10]  (-inf, 10]
        Interval.closed(5, 10).finishes(Interval.rightClosed(10)) mustBe (true)

        // [10, +inf)  [5, +inf)
        Interval.leftClosed(10).finishes(Interval.leftClosed(5)) mustBe (true)

        // [5, +inf)  (-inf, +inf)
        Interval.leftClosed(5).finishes(Interval.unbounded[Int]) mustBe (true)
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

          whenever(xy.equalsTo(wz)) {
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
        Interval.open(0, 5).equalsTo(Interval.open(0, 5)) mustBe (true)
        Interval.closed(0, 5).equalsTo(Interval.closed(0, 5)) mustBe (true)
        Interval.leftOpen(0, 5).equalsTo(Interval.leftOpen(0, 5)) mustBe (true)
        Interval.rightOpen(0, 5).equalsTo(Interval.rightOpen(0, 5)) mustBe (true)
        Interval.leftClosed(0, 5).equalsTo(Interval.leftClosed(0, 5)) mustBe (true)
        Interval.rightClosed(0, 5).equalsTo(Interval.rightClosed(0, 5)) mustBe (true)

        // Infinity

        // [5, +inf)  [5, +inf)
        Interval.leftClosed(5).equalsTo(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5]  (-inf, 5]
        Interval.rightClosed(5).equalsTo(Interval.rightClosed(5)) mustBe (true)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) mustBe (true)
      }
    }

    "satisfy" should {
      "one relation only" in {
        forAll(genOneIntTuple, genOneIntTuple) { case (((ox, oy), ix, iy), ((ow, oz), iw, iz)) =>
          val xy = Interval.make(ox, oy, ix, iy)
          val wz = Interval.make(ow, oz, iw, iz)

          val relations = makeRelations[Int]

          val trues = relations.foldLeft(Set.empty[String]) { case (acc, (k, fn)) =>
            val res = fn(xy, wz)
            if res then acc + k
            else acc
          }

          trues.nonEmpty mustBe (true)
          trues.size mustBe (1)
        }
      }
    }
  }

  private def makeRelations[T: Ordering] =
    Map(
      "b" -> ((ab: Interval[T], cd: Interval[T]) => ab.preceeds(cd)),
      "B" -> ((ab: Interval[T], cd: Interval[T]) => ab.isPreceededBy(cd)),
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
      "e" -> ((ab: Interval[T], cd: Interval[T]) => ab.equalsTo(cd)),
      "E" -> ((ab: Interval[T], cd: Interval[T]) => ab.equalsTo(cd))
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

    rest.foreach { case (k, fn) =>
      if fn(xy, wz) then println(s"${fk}|${xy}, ${wz}| == true; ${k}|${xy}, ${wz}| mustBe false, got true")
      if fn(wz, xy) then println(s"${fk}|${xy}, ${wz}| == true; ${k}|${wz}, ${xy}| mustBe false, got true")

      fn(xy, wz) mustBe (false)
      fn(wz, xy) mustBe (false)
    }
