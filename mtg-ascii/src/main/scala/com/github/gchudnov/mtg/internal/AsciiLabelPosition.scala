package com.github.gchudnov.mtg.internal

/**
  * Position of the labels in the diagram.
  */
enum AsciiLabelPosition:
  case None      // draw labels as-is on one line
  case NoOverlap // draw sorted labels that are non-overlapping, some of the labels might be skipped
  case Stacked   // draw all labels, but stack them onto multiple lines
