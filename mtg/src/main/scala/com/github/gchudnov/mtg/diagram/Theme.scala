package com.github.gchudnov.mtg.diagram

/**
 * Theme
 *
 * Used to customize the look of the diagram.
 */
final case class Theme(
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
  label: Theme.Label,
):
  def leftBound(isInclude: Boolean): Char =
    if isInclude then leftClosed else leftOpen

  def rightBound(isInclude: Boolean): Char =
    if isInclude then rightClosed else rightOpen

object Theme:
  enum Label:
    case None      // draw labels as-is on one line
    case NoOverlap // draw sorted labels that are non-overlapping, some of the labels might be skipped
    case Stacked   // draw all labels, but stack them onto multiple lines

  val default: Theme =
    Theme(
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
      label = Label.NoOverlap,
    )
