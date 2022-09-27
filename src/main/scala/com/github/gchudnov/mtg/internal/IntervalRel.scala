package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.BoundaryOrdering
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Degenerate
import com.github.gchudnov.mtg.Proper

/**
 * Interval Relations
 */
trait IntervalRel[+T]:
  a: Interval[T] =>

  /**
   * Before / Preceeds (b)
   *
   * After / IsPreceededBy (B)
   *
   * {{{
   *   PP (Point-Point):
   *   {p}; {q}
   *   p < q
   *
   *   PI (Point-Interval):
   *   {p}; {i-, i+}
   *   p < i-
   *
   *   II (Interval-Interval):
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
  final def before[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
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

  final def after[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.before(a)

  /**
   * Meets (m)
   *
   * IsMetBy (M)
   *
   * {{{
   *   II (Interval-Interval):
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
   *   B is-met-by A
   * }}}
   *
   * {{{
   *   AAAA
   *      BBBB
   * }}}
   */
  final def meets[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.isProper && b.isProper && bOrd.equiv(a.right, b.left)

  final def isMetBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.meets(a)

  /**
   * Overlaps (o)
   *
   * IsOverlapedBy (O)
   *
   *   - If any of the intervals is Empty, there is no overlapping.
   *
   * {{{
   *   II (Interval-Interval):
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
   *   AAAA
   *     BBBB
   * }}}
   */
  final def overlaps[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.isProper && b.isProper && bOrd.lt(a.left, b.left) && bOrd.lt(b.left, a.right) && bOrd.lt(a.right, b.right)

  final def isOverlapedBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.overlaps(a)

  /**
   * During (d)
   *
   * Contains / Includes (D)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {i-, i+}
   *   i- < p < i+
   *
   *   II (Interval-Interval):
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
  final def during[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.isProper && bOrd.lt(b.left, a.left) && bOrd.lt(a.right, b.right)

  final def contains[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.during(a)

  /**
   * Starts (s)
   *
   * IsStartedBy (S)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {i-, i+}
   *   p = i-
   *
   *   II (Interval-Interval):
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
   *   p
   *   III
   * ----------------
   *   AAA
   *   BBBBBB
   * }}}
   */
  final def starts[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    (a, b) match
      case (Degenerate(_), Proper(_, _)) =>
        bOrd.equiv(a.left, b.left)
      case (Proper(_, _), Proper(_, _)) =>
        bOrd.equiv(a.left, b.left) && bOrd.lt(a.right, b.right)
      case (_, _) =>
        false

  final def isStartedBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.starts(a)

  /**
   * Finishes (f)
   *
   * IsFinishedBy (F)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {i-, i+}
   *   p = i+
   *
   *   II (Interval-Interval):
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
   *     p
   *   III
   * ----------------
   *      AAA
   *   BBBBBB
   * }}}
   */
  final def finishes[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    (a, b) match
      case (Degenerate(_), Proper(_, _)) =>
        bOrd.equiv(a.right, b.right)
      case (Proper(_, _), Proper(_, _)) =>
        bOrd.equiv(a.right, b.right) && bOrd.lt(b.left, a.left)
      case (_, _) =>
        false

  final def isFinishedBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.finishes(a)

  /**
   * Equals (e)
   *
   * A = B
   *
   * {{{
   *   PP (Point-Point):
   *   {p}; {q}
   *   p = q
   *
   *   II (Interval-Interval):
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
  final def equalsTo[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.equiv(a.left, b.left) && bOrd.equiv(a.right, b.right)

  /**
   * Checks whether A is a subset of B
   *
   * A ⊆ B
   *
   * {{{
   *   a- >= b-
   *   a+ <= b+
   * }}}
   *
   * {{{
   *   - A starts B   | s
   *   - A during B   | d
   *   - A finishes B | f
   *   - A equals B   | e
   * }}}
   */
  final def isSubset[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.gteq(a.left, b.left) && bOrd.lteq(a.right, b.right)

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
  final def isSuperset[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
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
  final def isDisjoint[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.before(b) || a.after(b)

  /**
   * Checks whether A is less-or-equal to B
   *
   * A ≤ B
   *
   * {{{
   *   a- <= b-
   *   a+ <= b+
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
  final def isLessEqual[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lteq(a.left, b.left) && bOrd.lteq(a.right, b.right)

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
   * }}}
   *
   * {{{
   *   - before         | b
   *   - meets          | m
   *   - overlaps       | o
   * }}}
   */
  final def isLess[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lt(a.left, b.left) && bOrd.lt(a.right, b.right)

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
  final def isGreaterEqual[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.gteq(a.left, b.left) && bOrd.gteq(a.right, b.right)

  /**
   * Checks whether A succeeds B (Order Relation)
   *
   * A > B
   *
   * {{{
   *   - after | B
   * }}}
   */
  final def isGreater[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.gt(a.left, b.left) && bOrd.gt(a.right, b.right)

  /**
   * Precedes or IsEqual
   *
   * TODO: ADD TESTS
   *
   * A ≼ B
   *
   * {{{
   *   a+ <= b-
   * }}}
   */
  final def isBeforeEqual[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lt(a.right, b.left)
