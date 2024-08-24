package com.github.gchudnov.mtg.diagram

/**
 * AsciiTheme
 *
 * Used to customize the look of the diagram.
 */
final case class AsciiTheme(
  space: Char,
  interval: AsciiTheme.IntervalTheme,
  axis: AsciiTheme.AxisTheme,
  legend: AsciiTheme.LegendTheme,
)

object AsciiTheme:

  final case class IntervalTheme(
    leftOpen: Char,
    leftClosed: Char,
    rightOpen: Char,
    rightClosed: Char,
    fill: Char,
  ):
    def leftBoundary(isInclude: Boolean): Char =
      if isInclude then leftClosed else leftOpen

    def rightBoundary(isInclude: Boolean): Char =
      if isInclude then rightClosed else rightOpen

  object IntervalTheme:
    lazy val default: IntervalTheme =
      IntervalTheme(
        leftOpen = '(',
        leftClosed = '[',
        rightOpen = ')',
        rightClosed = ']',
        fill = '*',
      )

  final case class AxisTheme(
    line: Char,
    tick: Char,
  )

  object AxisTheme:
    lazy val default: AxisTheme =
      AxisTheme(
        line = '-',
        tick = '+',
      )

  final case class LegendTheme(
    border: Char,
    separator: Char,
  )

  object LegendTheme:
    lazy val default: LegendTheme =
      LegendTheme(
        border = '|',
        separator = ':',
      )

  val default: AsciiTheme =
    AsciiTheme(
      space = ' ',
      interval = IntervalTheme.default,
      axis = AxisTheme.default,
      legend = LegendTheme.default,
    )
