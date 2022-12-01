package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval

/**
 * Basic Interval Relations
 */
private[mtg] transparent trait BasicRel[T]:
  a: Interval[T] =>

  /**
   * Before, Precedes (b)
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
   *
   *   Relation                  AAAAA
   *   before(a,b)      b        :   : BBBBBBBBB  |  a+ < b-
   * }}}
   */
  final def before(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && ordM.lt(a.right, b.left)

  /**
   * After, IsPrecededBy (B)
   */
  final def after(b: Interval[T])(using Ordering[Mark[T]]): Boolean =
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
   *
   *   Relation                  AAAAA
   *   meets(a,b)       m        :   BBBBBBBBB    |  a+ = b-
   * }}}
   */
  final def meets(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.isProper && b.isProper && ordM.equiv(a.right, b.left)

  /**
   * IsMetBy (M)
   */
  final def isMetBy(b: Interval[T])(using Ordering[Mark[T]]): Boolean =
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
   *
   *   Relation                  AAAAA
   *   overlaps(a,b)    o        : BBBBBBBBB      |  a- < b- < a+ ; a+ < b+
   * }}}
   */
  final def overlaps(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.isProper && b.isProper && ordM.lt(a.left, b.left) && ordM.lt(b.left, a.right) && ordM.lt(a.right, b.right)

  /**
   * IsOverlappedBy (O)
   */
  final def isOverlappedBy(b: Interval[T])(using Ordering[Mark[T]]): Boolean =
    b.overlaps(a)

  /**
   * During, ProperlyIncludedIn (d)
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
   *
   *   Relation                  AAAAA
   *   during(a,b)      d|D    BBBBBBBBB          |  a- > b- ; a+ < b+
   * }}}
   */
  final def during(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.isProper && ordM.lt(b.left, a.left) && ordM.lt(a.right, b.right)

  /**
   * Contains, ProperlyIncludes (D)
   */
  final def contains(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    b.during(a)

  /**
   * Starts, Begins (s)
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
   *
   *   Relation                  AAAAA
   *   starts(a,b)      s|S      BBBBBBBBB        |  a- = b- ; a+ < b+
   * }}}
   */
  final def starts(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.isProper && ordM.equiv(a.left, b.left) && ordM.lt(a.right, b.right)

  /**
   * IsStartedBy (S)
   */
  final def isStartedBy(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    b.starts(a)

  /**
   * Finishes, Ends (f)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   p = a+
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- > b-
   *   a- < b+
   *   a+ > b-
   *   a+ = b+
   *
   *   b- < a- < a+ = b+
   *
   *   Relation                  AAAAA
   *   finishes(a,b)    f|F  BBBBBBBBB            |  a+ = b+ ; a- > b-
   * }}}
   */
  final def finishes(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.isProper && ordM.equiv(a.right, b.right) && ordM.lt(b.left, a.left)

  /**
   * IsFinishedBy (F)
   */
  final def isFinishedBy(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
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
   *
   *   Relation                  AAAAA
   *   equalsTo(a, b)   e        BBBBB            |  a- = b- ; a+ = b+
   * }}}
   */
  final def equalsTo(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.equiv(a.left, b.left) && ordM.equiv(a.right, b.right)
