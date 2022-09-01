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
 * }}}
 */
object Relation:

  /**
   * Preceeds (p)
   *
   * A preceeds B
   *
   * A < B
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
   * B > A
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
  def IsOverlapedBy[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
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
  def IsStartedBy[T: Ordering](b: Interval[T], a: Interval[T]): Boolean =
    starts(a, b)
