package com.github.gchudnov.mtg

/**
 * Relations
 *
 * {{{
 *   AAA              | A before (preceeds) B                   (A ≺ B) | (b)
 *        BBB         | B after  (succeeds, is-predeeded-by) A  (B ≻ A) | (B)
 *
 *   AAAA             | A meets B                                       | (m)
 *      BBBB          | B is-met-by A                                   | (M)
 *
 *   AAAA             | A overlaps B                                    | (o)
 *     BBBB           | B is-overlapped-by A                            | (O)
 *
 *     AA             | A during B                                      | (d)
 *   BBBBBB           | B contains A                                    | (D)
 *
 *   AAA              | A starts B                                      | (s)
 *   BBBBBB           | B is-started-by A                               | (S)
 *
 *      AAA           | A finishes B                                    | (f)
 *   BBBBBB           | B is-finished-by A                              | (F)
 *
 *   AAA              | A equals B                                      | (e)
 *   BBB              | B equals A                                      | (E)
 *
 *
 *  Relation         Abbr.    AAAAA
 *  before(a,b)      b|B      :   : BBBBBBBBB
 *  meets(a,b)       m|M      :   BBBBBBBBB
 *  overlaps(a,b)    o|O      : BBBBBBBBB
 *  starts(a,b)      s|S      BBBBBBBBB
 *  during(a,b)      d|D    BBBBBBBBB
 *  finishes(a,b)    f|F  BBBBBBBBB
 *  equals(a, b)     e        BBBBB
 *
 *
 * =======================
 *  A is-a-subset-of B (A ⊆ B):
 *                            AAAAA
 *  starts(a,b)      s        BBBBBBBBB
 *  during(a,b)      d      BBBBBBBBB
 *  finishes(a,b)    f    BBBBBBBBB
 *  equals(a, b)     e        BBBBB
 *
 *
 *
 *
 * }}}
 */

/**
 * Relation
 *
 * An encoded overlapping relation between two intervals A and B
 *
 * {{{
 *   r1 = (a- != b-) ⊕ ( ((a+ ≤ b-) ∨ (a- > b+)) ∧ ((a- != b-) ∧ (a+ != b+)) )
 *   r2 = (a+ != b+) ⊕ ( ((a+ < b-) ∨ (a- ≥ b+)) ∧ ((a- != b-) ∧ (a+ != b+)) )
 *   r3 = (a- >= b-)
 *   r4 = (a+ <= b+)
 *
 *   (r1, r2, r3, r4) <=> (bit3, bit2, bit1, bit0)
 *
 *   r1: TODO: ADD EXPLANATION ;; what is XOR?
 *
 *
 *   r2: TODO: ADD EXPLANATION
 *
 *
 *   r3:      a- |////////
 *       \\\\\\\\| b-
 *
 *   r4: ////////| a+
 *            b+ |\\\\\\\\
 * }}}
 */
final case class Relation(repr: Byte):

  /**
   * Checks whether A is-a-subset-of B (Set Relation)
   *
   * A is a subset of a set B if all elements of A are also elements of B. if they are unequal, then A is a proper subset of B.
   *
   * A ⊆ B
   *
   * {{{
   *   a- >= b-
   *   a+ <= b+
   *
   *   A ⊆ B <=> r3 ∧ r4
   * }}}
   *
   * {{{
   *   - A starts B   | s
   *   - A during B   | d
   *   - A finishes B | f
   *   - A equals B   | e
   * }}}
   */
  def isSubset: Boolean =
    (r3 & r4) == 1

  /**
   * Checks whether A is a superset of B (Set Relation)
   *
   * A ⊇ B
   *
   * {{{
   *   A ⊇ B <=> (r1 ⊕ r3 ) ∧ (r2 ⊕ r4 )
   * }}}
   *
   * {{{
   *   - A is-started-by B  | S
   *   - A contains B       | D
   *   - A is-finished-by B | F
   *   - A equals B         | e
   * }}}
   */
  def isSuperset: Boolean =
    ((r1 ^ r3) & (r2 ^ r4)) == 1

  /**
   * Checks if there A and B are disjoint (Set Relation)
   *
   * A ∩ B
   *
   * A and B are disjoint if A does not intersect B.
   *
   * {{{
   *   A ∩ B = ∅ <=> ¬r1 ∧ ¬r2 ∧ (r3 ⊕ r4 )
   * }}}
   *
   * {{{
   *   - before | b
   *   - after  | B
   * }}}
   */
  def isDisjoint: Boolean =
    (~r1 & ~r2 & (r3 ^ r4)) == 1

  /**
   * Checks whether A is less-than-or-equal-to B (Order Relation)
   *
   * A ≤ B
   *
   * {{{
   *   a- <= b-
   *   a+ <= b+
   *
   *   A ≤ B <=> r4 ∧ (¬r1 ∨ ¬r3)
   * }}}
   *
   * {{{
   *   - before         | b
   *   - meets          | m
   *   - overlaps       | o
   *   - starts         | s
   *   - is-finished-by | F
   *   - equal          | e
   * }}}
   */
  def isLessEqual: Boolean =
    (r4 & (~r1 | ~r3)) == 1

  /**
   * Less
   *
   * Checks whether A less-than B (Order Relation)
   *
   * A < B
   *
   * {{{
   *   a- < b-
   *   a+ < b+
   *
   *   A < B <=> (¬r1 ∨ r2) ∧ ¬r3 ∧ r4
   * }}}
   *
   * {{{
   *   - before         | b
   *   - meets          | m
   *   - overlaps       | o
   * }}}
   */
  def isLess: Boolean =
    ((~r1 | r2) & (~r3) & r4) == 1

  /**
   * Checks whether A greater-than-or-equal-to B (Order Relation)
   *
   * A ≥ B
   *
   * {{{
   *   a- >= b-
   *   a+ >= b+
   *
   *   A ≥ B <=> r3 ∧ (¬r2 ∨ ¬r4)
   * }}}
   *
   * {{{
   *   - after            | B
   *   - is-met-by        | M
   *   - is-overlapped-by | O
   *   - finishes         | f
   *   - is-started-by    | S
   *   - equal            | e
   * }}}
   */
  def isGreaterEqual: Boolean =
    (r3 & (~r2 | ~r4)) == 1

  /**
   * Checks whether A succeeds B (Order Relation)
   *
   * A > B
   *
   * {{{
   *   a- > b-
   *   a+ > b+
   *
   *   A > B <=>
   * }}}
   *
   * {{{
   *   - after            | B
   *   - is-met-by        | M
   *   - is-overlapped-by | O
   * }}}
   */
  def isGreater: Boolean =
    ((r1 | ~r2) & r3 & (~r4)) == 1

  /**
   * Preceeds (Before)
   *
   * A ≺ B
   *
   * {{{
   *   a+ < b-
   *
   *   A ≺ B <=> ¬r1 ∧ ¬r2 ∧ ¬r3 ∧ r4
   * }}}
   */
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
   * EqualsTo
   *
   * A = B
   *
   * {{{
   *   a- = b-
   *   a+ = b+
   *
   *   A = B <=> ¬r1 ∧ ¬r2 ∧ r3 ∧ r4
   * }}}
   */
  def isEqualsTo: Boolean =
    // 0 0 1 1
    repr == 0x3

  /**
   * Precedes or IsEqual
   *
   * A ≼ B
   *
   * {{{
   *   a+ <= b-
   *
   *   A ≼ B <=> ¬r1 ∧ r4 ∧ (¬r3 ∨ (r3 ∧ isDegenerate(A)))
   * }}}
   */
  def isBeforeEqual: Boolean =
    ??? // TODO: IMPL IT; + THERE ARE NO TESTS

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
      val t1 = !bOrd.equiv(a.left, b.left)   // (a- != b-)
      val t2 = !bOrd.equiv(a.right, b.right) // (a+ != b+)
      val t3 = (t1 && t2)

      val t4 = bOrd.lteq(a.right, b.left)
      val t5 = bOrd.gt(a.left, b.right)
      val t6 = bOrd.lt(a.right, b.left)
      val t7 = bOrd.gteq(a.left, b.right)

      val t8 = bOrd.gteq(a.left, b.left)
      val t9 = bOrd.lteq(a.right, b.right)

      // (a- != b-) ^ ( ((a+ <= b-) || (a- > b+)) && ((a- != b-) && (a+ != b+)) )
      val r1: Int =
        val lhs: Int = if t1 then 1 else 0
        val rhs: Int = if ((t4 || t5) && t3) then 1 else 0
        (lhs ^ rhs)

      // (a+ != b+) ^ ( ((a+ < b-) || (a- >= b+)) && ((a- != b-) && (a+ != b+)) )
      val r2: Int =
        val lhs: Int = if t2 then 1 else 0
        val rhs: Int = if ((t6 || t7) && t3) then 1 else 0
        (lhs ^ rhs)

      // (a- >= b-)
      val r3: Int = if t8 then 1 else 0

      // (a+ <= b+)
      val r4: Int = if t9 then 1 else 0

      val r = (r1 << 3 | r2 << 2 | r3 << 1 | r4)

      Relation(r.toByte)

  /**
   * Intersection
   *
   * The intersection A ∩ B
   *
   * {{{
   *          | ∅        if ¬r1 ∧ ¬r2 ∧ ¬(r3 ∧ r4)  | b, B
   *          | [a-, a+] if r3 ∧ r4                 | s, d, f, e
   * A ∩ B := | [a-, b+] if r3 ∧ ¬r4                | O, M, S
   *          | [b-, a+] if ¬r3 ∧ r4                | m, o, F
   *          | [b-, b+] if ¬r3 ∧ ¬r4               | D
   * }}}
   */
  def intersection[T: Ordering: Domain](a: Interval[T], b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else
      val r = make(a, b)

      if (~r.r1 & ~r.r2 & ~(r.r3 & r.r4)) == 1 then Interval.empty[T]
      else
        (r.r3, r.r4) match
          case (1, 1) =>
            Interval.make(a.left, a.right)
          case (1, 0) =>
            Interval.make(a.left, b.right)
          case (0, 1) =>
            Interval.make(b.left, a.right)
          case (0, 0) =>
            Interval.make(b.left, b.right)


  // -------------------------------------------------
  // TODO: see below, extract it to IntervalRel class

  extension [T: Ordering: Domain](a: Interval[T])(using bOrd: Ordering[Boundary[T]])

      // TODO: ^^ THERE ARE NO TESTS

    /**
     * Intersection of two intervals
     */
    def intersection(b: Interval[T]): Interval[T] =
      Relation.intersection(a, b)
