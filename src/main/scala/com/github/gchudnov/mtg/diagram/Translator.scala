package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.diagram.internal.BasicTranslator

/**
 * Translator
 */
trait Translator[T]:
  def translate(i: Value[T]): Int

object Translator:
  def make[T: Domain](view: View[T], canvas: Canvas)(using Ordering[Mark[T]]): Translator[T] =
    new BasicTranslator[T](view, canvas)
