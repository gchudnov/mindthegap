package com.github.gchudnov.mtg.internal.domain

import com.github.gchudnov.mtg.Domain

private[internal] final class NothingDomain extends AnyDomain[Nothing]:

  override def succ(x: Nothing): Nothing =
    x

  override def pred(x: Nothing): Nothing =
    x

  override def count(start: Nothing, end: Nothing): Long =
    0

  override def compare(x: Nothing, y: Nothing): Int =
    0
