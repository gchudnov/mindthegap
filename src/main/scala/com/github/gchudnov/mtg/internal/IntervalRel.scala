package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval

/**
 * Interval Relations
 */
transparent trait IntervalRel[+T]:
  a: Interval[T] =>

  /**
   * Before, Preceeds (b)
   *
   * {{{
   *   PP (Point-Point):
   *   {p}; {q}
   *   p < q
   *
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   p < a-
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
   */
  final def before[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lt(a.right, b.left)

  /**
   * After, IsPreceededBy (B)
   */
  final def after[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.before(a)

  /**
   * Meets (m)
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
   */
  final def meets[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.isProper && b.isProper && bOrd.equiv(a.right, b.left)

  /**
   * IsMetBy (M)
   */
  final def isMetBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.meets(a)

  /**
   * Overlaps (o)
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
   */
  final def overlaps[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.isProper && b.isProper && bOrd.lt(a.left, b.left) && bOrd.lt(b.left, a.right) && bOrd.lt(a.right, b.right)

  /**
   * IsOverlapedBy (O)
   */
  final def isOverlapedBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.overlaps(a)

  /**
   * During (d)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   a- < p < a+
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
   */
  final def during[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.isProper && bOrd.lt(b.left, a.left) && bOrd.lt(a.right, b.right)

  /**
   * Contains (D)
   */
  final def contains[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.during(a)

  /**
   * Starts (s)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   p = a-
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
   */
  final def starts[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.isProper && bOrd.equiv(a.left, b.left) && bOrd.lt(a.right, b.right)

  /**
   * IsStartedBy (S)
   */
  final def isStartedBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    b.starts(a)

// /**
//  * Finishes (f)
//  *
//  * {{{
//  *   PI (Point-Interval):
//  *   {p}; {a-, a+}
//  *   p = a+
//  *
//  *   II (Interval-Interval):
//  *   {a-, a+}; {b-; b+}
//  *   a- > b-
//  *   a- < b+
//  *   a+ > b-
//  *   a+ = b+
//  *
//  *   b- < a- < a+ = b+
//  * }}}
//  */
// final def finishes[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   (a, b) match
//     case (Degenerate(_), Proper(_, _)) =>
//       bOrd.equiv(a.right, b.right)
//     case (Proper(_, _), Proper(_, _)) =>
//       bOrd.equiv(a.right, b.right) && bOrd.lt(b.left, a.left)
//     case (_, _) =>
//       false

// /**
//  * IsFinishedBy (F)
//  */
// final def isFinishedBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   b.finishes(a)

// /**
//  * Equals (e)
//  *
//  * A = B
//  *
//  * {{{
//  *   PP (Point-Point):
//  *   {p}; {q}
//  *   p = q
//  *
//  *   II (Interval-Interval):
//  *   {a-, a+}; {b-; b+}
//  *   a- = b-
//  *   a- < b+
//  *   a+ > b-
//  *   a+ = b+
//  *
//  *   a- = b- < a+ = b+
//  * }}}
//  */
// final def equalsTo[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.nonEmpty && b.nonEmpty && bOrd.equiv(a.left, b.left) && bOrd.equiv(a.right, b.right)

// /**
//  * IsSubset
//  *
//  * Checks whether A is a subset of B
//  *
//  * A ⊆ B
//  *
//  * {{{
//  *   a- >= b-
//  *   a+ <= b+
//  * }}}
//  *
//  * {{{
//  *   - A starts B   | s
//  *   - A during B   | d
//  *   - A finishes B | f
//  *   - A equals B   | e
//  * }}}
//  */
// final def isSubset[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.nonEmpty && b.nonEmpty && bOrd.gteq(a.left, b.left) && bOrd.lteq(a.right, b.right)

// /**
//  * Checks whether A is a superset of B
//  *
//  * {{{
//  *   - A is-started-by B  | S
//  *   - A contains B       | D
//  *   - A is-finished-by B | F
//  *   - A equals B         | e
//  * }}}
//  */
// final def isSuperset[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.isStartedBy(b) || a.contains(b) || a.isFinishedBy(b) || a.equalsTo(b)

// /**
//  * Checks if there A and B are disjoint.
//  *
//  * A and B are disjoint if A does not intersect B.
//  *
//  * {{{
//  *    before | b
//  *    after  | B
//  * }}}
//  */
// final def isDisjoint[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.before(b) || a.after(b)

// /**
//  * Checks whether A is less-or-equal to B
//  *
//  * A ≤ B
//  *
//  * {{{
//  *   a- <= b-
//  *   a+ <= b+
//  * }}}
//  *
//  * {{{
//  *   - before         | b
//  *   - meets          | m
//  *   - overlaps       | o
//  *   - starts         | s
//  *   - is-finished-by | F
//  *   - equal          | e
//  * }}}
//  */
// final def isLessEqual[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.nonEmpty && b.nonEmpty && bOrd.lteq(a.left, b.left) && bOrd.lteq(a.right, b.right)

// /**
//  * Less
//  *
//  * Checks whether A less-than B (Order Relation)
//  *
//  * A < B
//  *
//  * {{{
//  *   a- < b-
//  *   a+ < b+
//  * }}}
//  *
//  * {{{
//  *   - before         | b
//  *   - meets          | m
//  *   - overlaps       | o
//  * }}}
//  */
// final def isLess[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.nonEmpty && b.nonEmpty && bOrd.lt(a.left, b.left) && bOrd.lt(a.right, b.right)

// /**
//  * Checks whether A greater-than-or-equal-to B (Order Relation)
//  *
//  * A ≥ B
//  *
//  * {{{
//  *   a- >= b-
//  *   a+ >= b+
//  *
//  *   A ≥ B <=> r3 ∧ (¬r2 ∨ ¬r4)
//  * }}}
//  *
//  * {{{
//  *   - after            | B
//  *   - is-met-by        | M
//  *   - is-overlapped-by | O
//  *   - finishes         | f
//  *   - is-started-by    | S
//  *   - equal            | e
//  * }}}
//  */
// final def isGreaterEqual[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.nonEmpty && b.nonEmpty && bOrd.gteq(a.left, b.left) && bOrd.gteq(a.right, b.right)

// /**
//  * Checks whether A succeeds B (Order Relation)
//  *
//  * A > B
//  *
//  * {{{
//  *   - after | B
//  * }}}
//  */
// final def isGreater[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.nonEmpty && b.nonEmpty && bOrd.gt(a.left, b.left) && bOrd.gt(a.right, b.right)

// /**
//  * Precedes or IsEqual
//  *
//  * TODO: ADD TESTS
//  *
//  * A ≼ B
//  *
//  * {{{
//  *   a+ <= b-
//  * }}}
//  */
// final def isBeforeEqual[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
//   a.nonEmpty && b.nonEmpty && bOrd.lt(a.right, b.left)
