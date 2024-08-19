package com.github.gchudnov.mtg.diagram

/**
 * AsciiTheme
 *
 * Used to customize the look of the diagram.
 */
final case class AsciiTheme(
  space: Char,
  fill: Char,
  leftOpen: Char,
  leftClosed: Char,
  rightOpen: Char,
  rightClosed: Char,
  axis: Char,
  tick: Char,
  border: Char,
  comment: Char,
  legend: Boolean,
  annotations: Boolean,
  labelPosition: AsciiLabelPosition,
):
  def leftBound(isInclude: Boolean): Char =
    if isInclude then leftClosed else leftOpen

  def rightBound(isInclude: Boolean): Char =
    if isInclude then rightClosed else rightOpen

object AsciiTheme:

  val default: AsciiTheme =
    AsciiTheme(
      space = ' ',
      fill = '*',
      leftOpen = '(',
      leftClosed = '[',
      rightOpen = ')',
      rightClosed = ']',
      axis = '-',
      tick = '+',
      border = '|',
      comment = ':',
      legend = true,
      annotations = true,
      labelPosition = AsciiLabelPosition.NoOverlap,
    )
