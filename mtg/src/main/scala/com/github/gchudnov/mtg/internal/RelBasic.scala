package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Basic Interval Relations
 *
 * {{{
 *   Relation         Symbol          AAAAA
 *                                    :   :
 *   a.before(b)         b            :   : BBBBBBBBB  | a+ < b-
 *   a.meets(b)          m            :   BBBBBBBBB    | a+ = b-
 *   a.overlaps(b)       o            : BBBBBBBBB      | a- < b- < a+ < b+
 *   a.starts(b)         s            BBBBBBBBB        | a- = b- ; a+ < b+
 *   a.during(b)         d          BBBBBBBBB          | a- > b- ; a+ < b+
 *   a.finishes(b)       f        BBBBBBBBB            | a+ = b+ ; a- > b-
 *   a.after(b)          B  BBBBBBBBB :   :            | a- > b+
 *   a.isMetBy(b)        M    BBBBBBBBB   :            | a- = b+
 *   a.isOverlappedBy(b) O      BBBBBBBBB :            | b- < a- < b+ < a+
 *   a.isStartedBy(b)    S            BBB :            | a- = b- ; b+ < a+
 *   a.contains(b)       D            : B :            | a- < b- ; b+ < a+
 *   a.isFinishedBy(b)   F            : BBB            | a+ = b+ ; a- < b-
 *   a.equalsTo(b)       e            BBBBB            | a- = b- ; a+ = b+
 * }}}
 */
private[mtg] transparent trait RelBasic[T: Domain]:
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
  final def before(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.nonEmpty && ordM.lt(a.right, b.left)

  /**
   * After, IsPrecededBy (B)
   */
  final def after(b: Interval[T]): Boolean =
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
  final def meets(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.isProper && b.isProper && ordM.equiv(a.right, b.left)

  /**
   * IsMetBy (M)
   */
  final def isMetBy(b: Interval[T]): Boolean =
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
  final def overlaps(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.isProper && b.isProper && ordM.lt(a.left, b.left) && ordM.lt(b.left, a.right) && ordM.lt(a.right, b.right)

  /**
   * IsOverlappedBy (O)
   */
  final def isOverlappedBy(b: Interval[T]): Boolean =
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
  final def during(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.isProper && ordM.lt(b.left, a.left) && ordM.lt(a.right, b.right)

  /**
   * Contains, ProperlyIncludes (D)
   */
  final def contains(b: Interval[T]): Boolean =
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
  final def starts(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.isProper && ordM.equiv(a.left, b.left) && ordM.lt(a.right, b.right)

  /**
   * IsStartedBy (S)
   */
  final def isStartedBy(b: Interval[T]): Boolean =
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
  final def finishes(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.isProper && ordM.equiv(a.right, b.right) && ordM.lt(b.left, a.left)

  /**
   * IsFinishedBy (F)
   */
  final def isFinishedBy(b: Interval[T]): Boolean =
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
  final def equalsTo(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    ordM.equiv(a.left, b.left) && ordM.equiv(a.right, b.right)
