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
 * An encoded overlapping relation between two intervals A and B
 *
 * (r1, r2, r3, r4) <=> (bit3, bit2, bit1, bit0)
 */
final case class Relation(repr: Byte):

  /**
   * Checks whether A is a subset of B
   *
   * {{{
   *   - A starts B   | s
   *   - A during B   | d
   *   - A finishes B | f
   *   - A equals B   | e
   * }}}
   *
   * A ⊆ B <=> r3 ∧ r4
   */
  def isSubset: Boolean =
    (r3 & r4) == 1

  /**
   * Checks whether A is a superset of B
   *
   * {{{
   *   - A is-started-by B  | S
   *   - A contains B       | D
   *   - A is-finished-by B | F
   *   - A equals B         | e
   * }}}
   *
   * A ⊇ B <=> (r1 ⊕ r3 ) ∧ (r2 ⊕ r4 )
   */
  def isSuperset: Boolean =
    ((r1 ^ r3) & (r2 ^ r4)) == 1

  /**
   * Checks if there A and B are disjoint.
   *
   * A and B are disjoint if A does not intersect B.
   *
   * {{{
   *    before | b
   *    after  | B
   * }}}
   *
   * A ∩ B = ∅ <=> ¬r1 ∧ ¬r2 ∧ (r3 ⊕ r4 )
   */
  def isDisjoint: Boolean =
    (~r1 & ~r2 & (r3 ^ r4)) == 1

  /**
   * Checks whether A is less-or-equal to B
   *
   * {{{
   *   - before         | b
   *   - meets          | m
   *   - overlaps       | o
   *   - starts         | s
   *   - finishes       | f
   *   - is-finished-by | F
   *   - equal          | e
   * }}}
   *
   * A ≤ B <=> r4 ∧ (¬r1 ∨ ¬r3)
   */
  def isLessEqual: Boolean =
    (r4 & (~r1 | ~r3)) == 1

// TODO: impl set relations

  def isBefore: Boolean =
    // 0 0 0 1
    repr == 0x1

  def isAfter: Boolean =
    // 0 0 1 0
    repr == 0x2

  def isMeets: Boolean =
    // 0 1 0 1
    repr == 0x5

  def isMetBy: Boolean =
    // 1 0 1 0
    repr == 0xa

  def isOverlaps: Boolean =
    // 1 1 0 1
    repr == 0xd

  def isOverlapedBy: Boolean =
    // 1 1 1 0
    repr == 0xe

  def isDuring: Boolean =
    // 1 1 1 1
    repr == 0xf

  def isContains: Boolean =
    // 1 1 0 0
    repr == 0xc

  def isStarts: Boolean =
    // 0 1 1 1
    repr == 0x7

  def isStartedBy: Boolean =
    // 0 1 1 0
    repr == 0x6

  def isFinishes: Boolean =
    // 1 0 1 1
    repr == 0xb

  def isFinishedBy: Boolean =
    // 1 0 0 1
    repr == 0x9

  /**
   * A = B <=> ¬r1 ∧ ¬r2 ∧ r3 ∧ r4
   */
  def isEqualsTo: Boolean =
    // 0 0 1 1
    repr == 0x3

  @inline private def r1: Int =
    (repr >> 3) & 1

  @inline private def r2: Int =
    (repr >> 2) & 1

  @inline private def r3: Int =
    (repr >> 1) & 1

  @inline private def r4: Int =
    repr & 1

object Relation:

  /**
   * Calculate the overlapping relation between two intervals
   */
  def make[T: Ordering: Domain](a: Interval[T], b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Relation =
    if a.isEmpty || b.isEmpty then Relation(0) // (0 0 0 0)
    else
      // ((a- != b-) && (a+ != b+))
      val t1 = !bOrd.equiv(a.left, b.left)
      val t2 = !bOrd.equiv(a.right, b.right)
      val rx = (t1 && t2)

      // (a- != b-) ^ ( ((a+ <= b-) || (a- > b+)) && ((a- != b-) && (a+ != b+)) )
      val r1: Int =
        val lhs: Int = if t1 then 1 else 0
        val rhs: Int = if ((bOrd.lteq(a.right, b.left) || bOrd.gt(a.left, b.right)) && rx) then 1 else 0
        (lhs ^ rhs)

      // (a+ != b+) ^ ( ((a+ < b-) || (a- >= b+)) && ((a- != b-) && (a+ != b+)) )
      val r2: Int =
        val lhs: Int = if t2 then 1 else 0
        val rhs: Int = if ((bOrd.lt(a.right, b.left) || bOrd.gteq(a.left, b.right)) && rx) then 1 else 0
        (lhs ^ rhs)

      // (a- >= b-)
      val r3: Int = if bOrd.gteq(a.left, b.left) then 1 else 0

      // (a+ <= b+)
      val r4: Int = if bOrd.lteq(a.right, b.right) then 1 else 0

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
      a.nonEmpty && b.nonEmpty && bOrd.equiv(a.left, b.left) && bOrd.equiv(a.right, b.right)

    /**
     * Checks whether A is a subset of B
     *
     * {{{
     *   - A starts B   | s
     *   - A during B   | d
     *   - A finishes B | f
     *   - A equals B   | e
     * }}}
     */
    def isSubset(b: Interval[T]): Boolean =
      a.starts(b) || a.during(b) || a.finishes(b) || a.equalsTo(b)

    /**
     * Checks whether A is a superset of B
     *
     * {{{
     *   - A is-started-by B  | S
     *   - A contains B       | D
     *   - A is-finished-by B | F
     *   - A equals B         | e
     * }}}
     */
    def isSuperset(b: Interval[T]): Boolean =
      a.isStartedBy(b) || a.contains(b) || a.isFinishedBy(b) || a.equalsTo(b)

    /**
     * Checks if there A and B are disjoint.
     *
     * A and B are disjoint if A does not intersect B.
     *
     * {{{
     *    before | b
     *    after  | B
     * }}}
     */
    def isDisjoint(b: Interval[T]): Boolean =
      a.before(b) || a.after(b)

    /**
     * Checks whether A is less-or-equal to B
     *
     * {{{
     *   - before         | b
     *   - meets          | m
     *   - overlaps       | o
     *   - starts         | s
     *   - finishes       | f
     *   - is-finished-by | F
     *   - equal          | e
     * }}}
     *
     * A ≤ B <=> r4 ∧ (¬r1 ∨ ¬r3)
     */
    def isLessEqual(b: Interval[T]): Boolean =
      a.before(b) || a.meets(b) || a.overlaps(b) || a.starts(b) || a.finishes(b) || a.isFinishedBy(b) || a.equalsTo(b)
