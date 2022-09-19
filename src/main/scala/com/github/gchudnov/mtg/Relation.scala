package com.github.gchudnov.mtg

/**
 * Relations
 *
 * {{{
 *   AAA              | A before (preceeds) B            | (b)
 *        BBB         | B after  (is-predeeded-by) A     | (B)
 *
 *   AAAA             | A meets B                        | (m)
 *      BBBB          | B is-met-by A                    | (M)
 *
 *   AAAA             | A overlaps B                     | (o)
 *     BBBB           | B is-overlapped-by A             | (O)
 *
 *     AA             | A during B                       | (d)
 *   BBBBBB           | B contains A                     | (D)
 *
 *   AAA              | A starts B                       | (s)
 *   BBBBBB           | B is-started-by A                | (S)
 *
 *      AAA           | A finishes B                     | (f)
 *   BBBBBB           | B is-finished-by A               | (F)
 *
 *   AAA              | A equals B                       | (e)
 *   BBB              | B equals A                       | (E)
 *
 *
 *  Relation         Abbr.    AAAAA
 *  before(a,b)      b|B      :   : BBBBBBBBB
 *  meets(a,b)       m|M      :   BBBBBBBBB
 *  overlaps(a,b)    o|O      : BBBBBBBBB
 *  starts(a,b)      s|S      BBBBBBBBB
 *  during(a,b)      d|D    BBBBBBBBB
 *  finishes(a,b)    f|F  BBBBBBBBB
 * }}}
 */
object Relation:

  extension [T: Ordering: Domain](a: Interval[T])(using bOrd: Ordering[Boundary[T]])

    /**
     * Before (b), Preceeds (p)
     *
     * After (B), IsPreceededBy (P)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- < b-
     *   a- < b+
     *   a+ < b-
     *   a+ < b+
     *
     *   a- < a+ < b- < b+
     * }}}
     *
     * {{{
     *   A before B
     *   B after A
     *   A preceeds B
     *   B is-predeeded-by A
     * }}}
     *
     * {{{
     *   A <b> B
     *   B <B> A
     * }}}
     *
     * {{{
     *   AAA
     *        BBB
     * }}}
     */
    def before(b: Interval[T]): Boolean =
      a.nonEmpty && b.nonEmpty && bOrd.lt(a.right, b.left)

    def after(b: Interval[T]): Boolean =
      b.before(a)

    def preceeds(b: Interval[T]): Boolean =
      a.before(b)

    def isPreceededBy(b: Interval[T]): Boolean =
      b.before(a)

    /**
     * Meets (m)
     *
     * IsMetBy (M)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- < b-
     *   a- < b+
     *   a+ = b-
     *   a+ < b+
     *
     *   a- < a+ = b- < b+
     * }}}
     *
     * {{{
     *   A meets B
     *   A is-met-by B
     * }}}
     *
     * {{{
     *   A <m> B
     *   B <M> A
     * }}}
     *
     * {{{
     *   AAAA
     *      BBBB
     * }}}
     */
    def meets(b: Interval[T]): Boolean =
      a.isProper && b.isProper && bOrd.equiv(a.right, b.left)

    def isMetBy(b: Interval[T]): Boolean =
      b.meets(a)

    /**
     * Overlaps (o)
     *
     * IsOverlapedBy (O)
     *
     *   - If any of the intervals is Empty, there is no overlapping.
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- < b-
     *   a- < b+
     *   a+ > b-
     *   a+ < b+
     *
     *   a- < b- < a+ < b+
     * }}}
     *
     * {{{
     *   A overlaps B
     *   B is-overlapped-by A
     * }}}
     *
     * {{{
     *   A <o> B
     *   B <O> A
     * }}}
     *
     * {{{
     *   AAAA
     *     BBBB
     * }}}
     */
    def overlaps(b: Interval[T]): Boolean =
      a.isProper && b.isProper && bOrd.lt(a.left, b.left) && bOrd.lt(b.left, a.right) && bOrd.lt(a.right, b.right)

    def isOverlapedBy(b: Interval[T]): Boolean =
      b.overlaps(a)

    /**
     * During (d)
     *
     * Contains (D), Includes (I)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- > b-
     *   a- < b+
     *   a+ > b-
     *   a+ < b+
     *
     *   b- < a- < a+ < b+
     * }}}
     *
     * {{{
     *   A during B
     *   B contains A
     *   B includes A
     * }}}
     *
     * {{{
     *   A <d> B
     *   B <D> A
     * }}}
     *
     * {{{
     *     AA
     *   BBBBBB
     * }}}
     */
    def during(b: Interval[T]): Boolean =
      ???
      // (a, b) match
      //   case (Degenerate(_), Proper(None, None, _, _)) =>
      //     true
      //   case (Degenerate(x), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.gt(x, y1) && ordT.lt(x, y2))
      //   case (Degenerate(x), Proper(Some(y1), None, includeY1, _)) =>
      //     val ordT = summon[Ordering[T]]
      //     ordT.gt(x, y1)
      //   case (Degenerate(x), Proper(None, Some(y2), _, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     ordT.lt(x, y2)
      //   case (Proper(ox1, ox2, includeX1, includeX2), Proper(None, None, includeY1, includeY2)) =>
      //     (ox1.isDefined && ox2.isDefined) || (ox1.isEmpty && (!includeX1 && includeY1)) || (ox2.isEmpty && (!includeX2 && includeY2))
      //   case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), None, includeY1, _)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.lt(y1, x1) || (ordT.equiv(y1, x1) && includeY1 && !includeX1))
      //   case (Proper(Some(x1), None, includeX1, includeX2), Proper(Some(y1), None, includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.lt(y1, x1) || (ordT.equiv(y1, x1) && includeY1 && !includeX1)) &&
      //     (!includeX2 && includeY2)
      //   case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(None, Some(y2), _, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
      //   case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.lt(y1, x1) || (ordT.equiv(y1, x1) && includeY1 && !includeX1)) &&
      //     (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
      //   case _ =>
      //     false

    def contains(b: Interval[T]): Boolean =
      b.during(a)

    def includes(b: Interval[T]): Boolean =
      b.during(a)

    /**
     * Starts (s)
     *
     * IsStartedBy (S)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- = b-
     *   a- < b+
     *   a+ > b-
     *   a+ < b+
     *
     *   a- = b- < a+ < b+
     * }}}
     *
     * {{{
     *   A starts B
     *   B is-started-by A
     * }}}
     *
     * {{{
     *   A <s> B
     *   B <S> A
     * }}}
     *
     * {{{
     *   AAA
     *   BBBBBB
     * }}}
     */
    def starts(b: Interval[T]): Boolean =
      ???
      // (a, b) match
      //   case (Degenerate(x), Proper(Some(y1), _, includeY1, _)) =>
      //     val ordT = summon[Ordering[T]]
      //     ordT.equiv(x, y1) && includeY1
      //   case (Proper(None, Some(_), includeX1, _), Proper(None, None, includeY1, _)) =>
      //     val ordT = summon[Ordering[T]]
      //     (includeX1 == includeY1)
      //   case (Proper(None, Some(x2), includeX1, includeX2), Proper(None, Some(y2), includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (includeX1 == includeY1) && (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
      //   case (Proper(None, None, includeX1, includeX2), Proper(None, None, includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (includeX1 && includeY1 && !includeX2 && includeY2)
      //   case (Proper(Some(x1), Some(_), includeX1, _), Proper(Some(y1), None, includeY1, _)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.equiv(x1, y1) && (includeX1 == includeY1))
      //   case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.equiv(x1, y1) && (includeX1 == includeY1)) && (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
      //   case _ =>
      //     false

    def isStartedBy(b: Interval[T]): Boolean =
      b.starts(a)

    /**
     * Finishes (f)
     *
     * IsFinishedBy (F)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- > b-
     *   a- < b+
     *   a+ > b-
     *   a+ = b+
     *
     *   b- < a- < a+ = b+
     * }}}
     *
     * {{{
     *   A finishes B
     *   B is-finished-by A
     * }}}
     *
     * {{{
     *   A <f> B
     *   B <F> A
     * }}}
     *
     * {{{
     *      AAA
     *   BBBBBB
     * }}}
     */
    def finishes(b: Interval[T]): Boolean =
      ???
      // (a, b) match
      //   case (Degenerate(x), Proper(_, Some(y2), _, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     ordT.equiv(x, y2) && includeY2
      //   case (Proper(Some(_), Some(x2), _, includeX2), Proper(None, Some(y2), _, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.equiv(x2, y2) && includeX2 == includeY2)
      //   case (Proper(Some(x1), None, includeX1, includeX2), Proper(Some(y1), None, includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (includeX2 == includeY2) && (ordT.gt(x1, y1) || (ordT.equiv(x1, y1) && !includeX1 && includeY1))
      //   case (Proper(Some(_), None, _, includeX2), Proper(None, None, _, includeY2)) =>
      //     (includeX2 == includeY2)
      //   case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[T]]
      //     (ordT.equiv(x2, y2) && includeX2 == includeY2) && (ordT.gt(x1, y1) || (ordT.equiv(x1, y1) && !includeX1 && includeY1))
      //   case _ =>
      //     false

    def isFinishedBy(b: Interval[T]): Boolean =
      b.finishes(a)

    /**
     * Equals (e)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- = b-
     *   a- < b+
     *   a+ > b-
     *   a+ = b+
     *
     *   a- = b- < a+ = b+
     * }}}
     *
     * {{{
     *   A equalsTo B
     * }}}
     *
     * {{{
     *   A <e> B
     * }}}
     *
     * {{{
     *   AAAA
     *   BBBB
     * }}}
     */
    def equalsTo(b: Interval[T]): Boolean =
      ???
      // import com.github.gchudnov.mtg.ordering.OptionOrdering
      // (a, b) match
      //   case (Empty, Empty) =>
      //     true
      //   case (Degenerate(x), Degenerate(y)) =>
      //     val ordT = summon[Ordering[T]]
      //     ordT.equiv(x, y)
      //   case (Proper(ox1, ox2, includeX1, includeX2), Proper(oy1, oy2, includeY1, includeY2)) =>
      //     val ordT = summon[Ordering[Option[T]]]
      //     (ordT.equiv(ox1, oy1) && (includeX1 == includeY1)) &&
      //     (ordT.equiv(ox2, oy2) && (includeX2 == includeY2))
      //   case _ =>
      //     false
