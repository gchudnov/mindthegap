package com.github.gchudnov.mtg.examples.util

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

object Output:

  /**
   * Print Diagram to Console
   *
   * @param xs
   *   intervals
   * @param canvasWidth
   *   width of the canvas
   */
  def printDiagram[T: Domain](xs: List[Interval[T]], canvasWidth: Int): Unit =
    val canvas: Canvas = Canvas.make(canvasWidth, 2)
    val view: View[T]  = View.all[T]
    val diagram        = Diagram.make(xs, view, canvas)

    val diag = Diagram.render(diagram)
    diag.foreach(println)

  /**
   * Print Data to Console
   */
  def printInOut[T: Domain](input: List[Interval[T]], interval: Interval[T], output: List[Interval[T]]): Unit =
    println("input:    " + asString(input))
    // println("interval: " + interval.asString) // TODO: recover
    println("output:   " + asString(output))

  /**
   * Print Data to Console
   */
  def printInOut[T: Domain](input: List[Interval[T]], output: List[Interval[T]]): Unit =
    println("input:    " + asString(input))
    println("output:   " + asString(output))

  /**
   * Convert Intervals to String
   *
   * @param intervals
   * @return
   */
  def asString[T: Domain](intervals: List[Interval[T]]): String =
    // intervals.map(i => i.asString).mkString("[", ",", "]")  // TODO: recover
    ""
