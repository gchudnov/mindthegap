package com.github.gchudnov.mtg

/**
 * Relations
 *
 * {{{
 *   [AAA]              | A preceeds B            | (p)
 *         [BBB]        | B is-predeeded-by A     | (P)
 *
 *   [AAA]              | A meets B               | (m)
 *       [BBB]          | B is-met-by A           | (M)
 *
 *   [AAA]              | A overlaps-with B       | (o)
 *     [BBB]            | B is-overlapped-by A    | (O)
 *
 *   [AAA]              | A starts B              | (s)
 *   [BBBBBB]           | B is-started-by A       | (S)
 *
 *     [AA]             | A during B              | (d)
 *   [BBBBBB]           | B contains A            | (D)
 *
 *      [AAA]           | A finishes B            | (f)
 *   [BBBBBB]           | B is-finished-by A      | (F)
 *
 *   [AAA]              | A equals B              | (e)
 *   [BBB]              |                         |
 * }}}
 */
object Relation:

  /**
   * Preceeds (p)
   *
   * A preceeds B
   *
   * A p B
   *
   * {{{
   *  [AAA]
   *        [BBB]
   * }}}
   */
  def preceeds[T: Ordering](a: Interval[T], b: Interval[T]): Boolean =
    ???

  /**
   * IsPreceededBy (P)
   *
   * B is-predeeded-by A
   *
   * B P A
   *
   * {{{
   *  [AAA]
   *        [BBB]
   * }}}
   */
  def isPreceededBy[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
    preceeds(a, b)

  /**
   * Meets (m)
   *
   * A meets B
   *
   * A m B
   *
   * {{{
   *  [AAA]
   *      [BBB]
   * }}}
   */
  def meets[T: Ordering](a: Interval[T], b: Interval[T]): Boolean =
    a match {
      case Empty =>
        ???
      case Degenerate(x) =>
        ???
      case Proper(x, y, ix, iy) =>
        ???
    }

    // (a, b) match {
    //   case (Proper[T](_, _, _, _), Proper[T](_, _, _, _)) =>
    //     ???
    //   case _ =>
    //     ???
    // }
    ???

  /**
   * IsMetBy (M)
   *
   * B is-met-by A
   *
   * B M A
   *
   * {{{
   *  [AAA]
   *      [BBB]
   * }}}
   */
  def isMetBy[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
    meets(a, b)

  /**
   * Overlaps (o)
   *
   * A overlaps-with B
   *
   * A o B
   *
   * {{{
   *  [AAA]
   *    [BBB]
   * }}}
   */
  def overlapsWith[T: Ordering](a: Interval[T], b: Interval[T]): Boolean =
    ???

  /**
   * IsOverlapedBy (O)
   *
   * B is-overlapped-by A
   *
   * B O A
   *
   * {{{
   *  [AAA]
   *    [BBB]
   * }}}
   */
  def isOverlapedBy[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
    overlapsWith(a, b)

  /**
   * Starts (s)
   *
   * A starts B
   *
   * A s B
   *
   * {{{
   *  [AAA]
   *  [BBBBBB]
   * }}}
   */
  def starts[T: Ordering](a: Interval[T], b: Interval[T]): Boolean =
    ???

  /**
   * IsStartedBy (S)
   *
   * B is-started-by A
   *
   * B S A
   *
   * {{{
   *  [AAA]
   *  [BBBBBB]
   * }}}
   */
  def isStartedBy[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
    starts(a, b)

  /**
   * During (d)
   *
   * A during B
   *
   * A d B
   *
   * {{{
   *    [AA]
   *  [BBBBBB]
   * }}}
   */
  def during[T: Ordering](a: Interval[T], b: Interval[T]): Boolean =
    ???

  /**
   * Contains (D)
   *
   * B contains A
   *
   * D D A
   *
   * {{{
   *    [AA]
   *  [BBBBBB]
   * }}}
   */
  def contains[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
    during(a, b)

  /**
   * Finishes (d)
   *
   * A finishes B
   *
   * A f B
   *
   * {{{
   *     [AAA]
   *  [BBBBBB]
   * }}}
   */
  def finishes[T: Ordering](a: Interval[T], b: Interval[T]): Boolean =
    ???

  /**
   * IsFinishedBy (F)
   *
   * B is-finished-by A
   *
   * B F A
   *
   * {{{
   *     [AAA]
   *  [BBBBBB]
   * }}}
   */
  def isFinishedBy[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
    finishes(a, b)

  /**
   * Equals (e)
   *
   * A equals B
   *
   * A e B
   *
   * {{{
   *  [AAA]
   *  [BBB]
   * }}}
   */
  def equals[T: Ordering](a: Interval[T], b: Interval[T]): Boolean =
    ???
