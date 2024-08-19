package com.github.gchudnov.mtg.diagram.internal

// TODO: make external so that it can be used in mermaidjs and ascii

trait OutputFormat[T] {
  def format(value: T): String
}

object OutputFormat {
  
}