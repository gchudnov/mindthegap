package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.diagram.internal.BasicTranslator

/**
 * Translator
 */
trait Translator[T: Domain]:
  def translate(i: Interval[T]): Span

object Translator:
  def make[T: Domain](view: View[T], canvas: Canvas)(using Ordering[Mark[T]]): Translator[T] =
    new BasicTranslator[T](view, canvas)
