package com.github.gchudnov.mtg

trait Timeline[A: Ordering]:
  def intervals: List[Interval[A]]
