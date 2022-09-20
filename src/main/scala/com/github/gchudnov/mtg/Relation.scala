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

/**
 * Relation
 * 
 * An encoded overlapping relation between two intervals
 */
final case class Relation(repr: Byte)

object Relation:

  /**
   * Calculate the overlapping relation between two intervals
   */
  def make[T: Ordering: Domain](a: Interval[T], b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Relation =
    if a.isEmpty || b.isEmpty then Relation(0)
    else
      // TODO: IMPL IT

      val r1: Byte = 0 // TODO: impl it

      val r2: Byte = 0  // TODO: impl it

      // (a- >= b-)
      val r3: Byte = if bOrd.gteq(a.left, b.left) then 1 else 0

      // (a+ <= b+)
      val r4: Byte = if bOrd.lteq(a.right, b.right) then 1 else 0 

      val r = (r1 << 3 | r2 << 2 | r3 << 1 | r4)

      Relation(r.toByte)

  extension [T: Ordering: Domain](a: Interval[T])(using bOrd: Ordering[Boundary[T]])

    /**
     * Before (b), Preceeds (p)
     *
     * After (B), IsPreceededBy (P)
     *
     * {{{
     *   PP (Point-Point relations):
     *   {p}; {q}
     *   p < q
     *
     *   PI (Point-Interval relations):
     *   {p}; {i-, i+}
     *   p < i-
     *
     *   II (Interval-Interval relations):
     *   {a-, a+}; {b-, b+}
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
     *   p    q
     * ----------------
     *   p
     *        III
     * ----------------
     *   AAA
     *        BBB
     * }}}
     */
    def before(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(_), Degenerate(_)) =>
          bOrd.lt(a.left, b.left)
        case (Degenerate(_), Proper(_, _)) =>
          bOrd.lt(a.left, b.left)
        case (Proper(_, _), Degenerate(_)) =>
          bOrd.lt(a.right, b.left)
        case (Proper(_, _), Proper(_, _)) =>
          bOrd.lt(a.right, b.left)
        case (_, _) =>
          false

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
     *   II (Interval-Interval relations):
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
     *   II (Interval-Interval relations):
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
     *   PI (Point-Interval relations):
     *   {p}; {i-, i+}
     *   i- < p < i+
     *
     *   II (Interval-Interval relations):
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
     *     p
     *    III
     * ----------------
     *     AA
     *   BBBBBB
     * }}}
     */
    def during(b: Interval[T]): Boolean =
      a.nonEmpty && b.isProper && bOrd.lt(b.left, a.left) && bOrd.lt(a.right, b.right)

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
     *   PI (Point-Interval relations):
     *   {p}; {i-, i+}
     *   p = i-
     *
     *   II (Interval-Interval relations):
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
     *   p
     *   III
     * ----------------
     *   AAA
     *   BBBBBB
     * }}}
     */
    def starts(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(_), Proper(_, _)) =>
          bOrd.equiv(a.left, b.left)
        case (Proper(_, _), Proper(_, _)) =>
          bOrd.equiv(a.left, b.left) && bOrd.lt(a.right, b.right)
        case (_, _) =>
          false

    def isStartedBy(b: Interval[T]): Boolean =
      b.starts(a)

    /**
     * Finishes (f)
     *
     * IsFinishedBy (F)
     *
     * {{{
     *   PI (Point-Interval relations):
     *   {p}; {i-, i+}
     *   p = i+
     *
     *   II (Interval-Interval relations):
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
     *     p
     *   III
     * ----------------
     *      AAA
     *   BBBBBB
     * }}}
     */
    def finishes(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(_), Proper(_, _)) =>
          bOrd.equiv(a.right, b.right)
        case (Proper(_, _), Proper(_, _)) =>
          bOrd.equiv(a.right, b.right) && bOrd.lt(b.left, a.left)
        case (_, _) =>
          false

    def isFinishedBy(b: Interval[T]): Boolean =
      b.finishes(a)

    /**
     * Equals (e)
     *
     * {{{
     *   PP (Point-Point relations):
     *   {p}; {q}
     *   p = q
     *
     *   II (Interval-Interval relations):
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
      bOrd.equiv(a.left, b.left) && bOrd.equiv(a.right, b.right)
