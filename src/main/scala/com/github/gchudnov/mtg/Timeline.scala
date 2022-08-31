package com.github.gchudnov.mtg

/**
  * Timeline is a collection of intervals
  * 
  * Example:
  * {{{
  * 
  *   [--)  [------]  (---->
  * 
  *  -----------------------------------------> t
  * }}}
  * 
  */
trait Timeline[A: Ordering]:
  def intervals: List[Interval[A]]
