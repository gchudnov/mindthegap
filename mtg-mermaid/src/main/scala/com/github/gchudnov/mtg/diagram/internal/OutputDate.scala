package com.github.gchudnov.mtg.diagram.internal

trait OutputDate[T] {
  def format(value: T): String
}

object OutputDate {
  
}