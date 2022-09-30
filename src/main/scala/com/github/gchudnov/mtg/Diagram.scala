package com.github.gchudnov.mtg

final case class SpanStyle(start: Char, sep: Char, end: Char)

final case class Span(x0: Int, x1: Int, y: Int, style: SpanStyle)

final case class Diagram(spans: List[Span])

object Diagram:

  val empty: Diagram =
    Diagram(spans = List.empty[Span])

  def prepare[T: Numeric](intervals: List[Interval[T]], width: Int = 80)(using bOrd: Ordering[Boundary[T]]): Diagram =
    val tNum = summon[Numeric[T]]

    val xs = intervals.filter(_.nonEmpty)

    if xs.isEmpty then Diagram.empty
    else
      val bs = xs.flatMap(it => List(it.left, it.right))

      val bMin = bs.min // might be -inf
      val bMax = bs.max // might be +inf

      val optfMin = bs.filter(_.isBounded).minOption // != -inf
      val optfMax = bs.filter(_.isBounded).maxOption // != +inf

      ///////////////////
      val padding  = 2
      val cxMinInf = 0
      val cxMaxInf = width - 1
      val cxMin    = cxMinInf + padding
      val cxMax    = cxMaxInf - padding

      val cw = cxMax - cxMin
      val fw = optfMin.flatMap(fMin => optfMax.map(fMax => tNum.minus(fMax.value.get, fMin.value.get)))
      val ok = fw.map(w => cw.toDouble / tNum.toDouble(w))

      // translates the coordindate into position on the canvas
      def translateX(value: Option[T], bound: Either[Unit, Unit]): Int =
        value.fold(bound.fold(_ => cxMinInf, _ => cxMaxInf))(x => ok.fold(bound.fold(_ => cxMin, _ => cxMax))(k => (k * tNum.toDouble(x) + cxMin).toInt))

      def toStyle[T](i: Interval[T]): SpanStyle =
        SpanStyle(start = Show.leftBound(i.left.isInclude), sep = '*', end = Show.rightBound(i.right.isInclude))

      val (_, spans) = xs.foldLeft((0, List.empty[Span])) { case (acc, i) =>
        val (y, ss) = acc

        val x0 = translateX(i.left.value, Left(()))
        val x1 = translateX(i.right.value, Right(()))

        (y + 1, ss :+ Span(x0 = x0, x1 = x1, y = y, style = toStyle(i)))
      }

      Diagram(spans = spans)
