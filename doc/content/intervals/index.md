+++
title = "Intervals"
description = "Intervals"
date = 2022-11-19
draft = false

[extra]
+++

## Intervals

An **interval** is defined by a pair `{a-, a+}`, whose elements represent a starting `a-` and ending `a+` values on a discrete linear infinite domain `D`.
Interval is a convex set that contains all elements between `a-` and `a+`.

![domain.svg](./domain.svg)

For each element of the domain, there is only one _successor_ and _predecessor_ and no other elements in-between.

A pair `{a-, a+} ∈ D × D` corresponds to a point on a two-dimensional plane.

Given _successor_, `succ()` and _predecessor_, `pred` concept, we can define intervals as:

- closed interval `[a-, a+]` is represented as-is `[a-, a+]`
- right-open interval`[a-, a+)` is represented as `[a-, pred(a+)]`
- left-open interval `(a-, a+]` is represented as `[succ(a-), a+]`
- open interval `(a-, a+)` is represented as `[succ(a-), pred(a+)]`

In this way we consider a closed interval as a _canonical_ form.

## Non-Empty and Empty

A pair `{a-, a+} ∈ D × D` represents a non-empty interval if `a- ≤ a+`; otherwise, the interval is empty.
If left boundary `a-` is equal to the right boundary, `a+`, we call it as a _degenerate_ interval or a _point_.

One can distinguish an _empty_, _point_ and _proper_ intervals:

- A _proper_ interval is an ordered pair of elements where the first element is less than the second: `a- < a+`.
- A _point_ is a degenerate interval where first element is equal to the second: `a- = a+`.
- An _empty_ interval is the one where first element is greater than the second: `a- > a+`.

Looking at the two-dimensional plane on the diagram above, _proper_ intervals correspond the are above the line `a+ = a-`, _point_ intervals
are located on the line `a+ = a-` and all empty intervals are below the line `a+ = a-`.

## Classification

Intervals could be further classifed in the following way:

- empty : `[a+, a-] = (a+, a-) = [a+, a-) = (a+, a-] = (a-, a-) = [a-, a-) = (a-, a-] = {} = ∅`
- point : `{x} = {x | a- = x = a+}`
- proper
  - bounded
    - open : `(a-, a+) = {x | a- < x < a+}`
    - closed : `[a-, a+] = {x | a- <= x <= a+}`
    - left-closed, right-open : `[a-, a+) = {x | a- <= x < a+}`
    - left-open, right-closed : `(a-, a+] = {x | a- < x <= a+}`
  - left-bounded, right-unbounded
    - left-open : `(a-, +∞) = {x | x > a-}`
    - left-closed : `[a-, +∞) = {x | x >= a-}`
  - left-unbounded, right-bounded
    - right-open : `(-∞, a+) = {x | x < a+}`
    - right-closed : `(-∞, a+] = {x | x < a+}`
  - unbounded : `(-∞, +∞) = R`

## Creation

To create an interval one of the factory functions can be used:

```scala
import com.github.gchudnov.mtg.*

Interval.empty[Int]                 // ∅ = (+∞, -∞)
Interval.point(5)                   // {5}
Interval.open(1, 5)                 // (1, 5)
Interval.closed(1, 5)               // [1, 5]
Interval.leftClosedRightOpen(1, 5)  // [1, 5)
Interval.leftOpenRightClosed(1, 5)  // (1, 5]
Interval.leftOpen(1)                // (1, +∞)
Interval.leftClosed(5)              // [5, +∞)
Interval.rightOpen(1)               // (-∞, 1)
Interval.rightClosed(5)             // (-∞, 5]
Interval.unbounded[Int]             // (-∞, +∞)
```

A special factory low-level method, `Interval.make` can be used to create an interval by providing boundaries.

```scala
Interval.make(Mark.at(0), Mark.pred(0))   // [0, 0)
Interval.make(Mark.succ(3), Mark.pred(5)) // (3, 5)
```

## Operations

`a.isEmpty`, `a.isPoint`, `a.isProper` (`a.nonEmpty`, `a.nonPoint`, `a.nonProper`) are used to check the type of an interval.

```scala
Interval.open(1, 5).isEmpty  // false
Interval.open(1, 5).isProper // true
```

`a.canonical` - converts an an interval to the canonical form where left and right boundaries on an interval are closed.

```scala
Interval.open(1, 5).canonical      // (1, 5) -> [2, 4]
Interval.closed(1, 5).canonical    // [1, 5] -> [1, 5]
```

`a.normalize` can be used to optimize an interval, reducing the amoung of _successors_ (`succ`) and _predecessors_ `pred`, that might be produced by some of the algorithms. The method is rarely needed to be called explicitly.

```scala
Interval.make(Mark.pred(Mark.pred(1)), Mark.at(5)).normalize // [pred(pred(1)), 5] -> [-1, 5]
Interval.make(Mark.succ(Mark.succ(1)), Mark.at(5)).normalize // [succ(succ(1)), 5] -> (2, 5]
```

`a.swap` is used to swap left and right boudary, e.g. to convert an empty to non-empty interval or vice versa.

```scala
Interval.closed(1, 5).swap // [1, 5] -> [5, 1]
```

`a.inflate` can be used to inflate an interval, extending its size. In addition there are methods `.inflateLeft` and `.inflateRight` that can be applied to the left and right ends of an interval.

```scala
Interval.closed(1, 2).inflate // [1, 2] -> [0, 3]
```

`a.deflate` can be used to deflate an interval, reducing its size. In addition there are methods `.deflateLeft` and `.deflateRight` that can be applied to the left and right ends of an interval.

```scala
Interval.closed(1, 2).deflate // [1, 2] -> [2, 1]
```

## Show

To pretty-print an interval, `Show` can be used:

```scala
import com.github.gchudnov.mtg.Show.given

val a = Interval.empty[Int]
val b = Interval.point(5)
val c = Interval.proper(None, true, Some(2), false)

a.show // ∅
b.show // {5}
c.show // [-∞,2)
```

## Display

A collection of intervals can be displayed:

```scala
val a = Interval.closed(100, 500)
val b = Interval.closed(150, 600)
val c = Interval.closed(200, 700)
val d = Interval.closed(250, 800)
val e = Interval.closed(300, 900)
val f = Interval.closed(600, 1000)

val canvas: Canvas  = Canvas.make(40)
val view: View[Int] = View.empty[Int]
val theme: Theme    = Theme.default

val diagram = Diagram.make(List(a, b, c, d, e, f), view, canvas)

Diagram.render(diagram, theme.copy(label = Theme.Label.NoOverlap)) // List[String]
```

When printed, will produce the output:

```text
  [***************]
    [****************]
      [******************]
        [********************]
          [**********************]
                     [***************]
--+-+-+-+-+-------+--+---+---+---+---+--
 100 200 300     500    700 800 900
```

### Theme

`Theme` has a number of attributes that can specify the way the diagram is displayed:

- `label: Theme.Label` used to set the way labels are displayed:
  - `Theme.Label.None` - draw labels as-is on one line, labels can overlap;
  - `Theme.Label.NoOverlap` - draw sorted labels that are non-overlapping, some of the labels might be skipped (default);
  - `Theme.Label.Stacked` - draw all labels, but stack them onto multiple lines;
- `legend: Boolean` used to specify whether to display a legend or not (default: true)

When legend is specified:

```scala
val a = Interval.closed(1, 5)
val b = Interval.closed(5, 10)
val c = Interval.rightClosed(15)
val d = Interval.leftOpen(2)

val canvas: Canvas  = Canvas.make(40)
val view: View[Int] = View.empty[Int]
val theme: Theme    = Theme.default

val diagram = Diagram.make(List(a, b, c, d), view, canvas)

Diagram.render(diagram, theme.copy(legend = true)) // List[String]
```

It will produce the following output:

```text
  [*********]                            | [1,5]
            [************]               | [5,10]
(************************************]   | (-∞,15]
     (*********************************) | (2,+∞)
+-+--+------+------------+-----------+-+ |
-∞1  2      5           10          15+∞ |
```

When intervals are using `OffsetDateTime` or `Instant`, import `Diagram.given` to make a diagram:

```scala
import Diagram.given

val a = Interval.closed(
  OffsetDateTime.parse("2020-07-02T12:34Z"),
  OffsetDateTime.parse("2021-07-02T12:34Z")
)

val canvas: Canvas  = Canvas.make(40)
val theme: Theme    = Theme.default

val diagram = Diagram.make[OffsetDateTime](List(a), canvas)

Diagram.render(diagram, theme.copy(label = Theme.Label.Stacked)) // List[String]
```

```text
  [**********************************]
--+----------------------------------+--
2020-07-02T12:34Z      2021-07-02T12:34Z
```

### View

`View` is used to specify a window to display on a canvas:

```scala
val a       = Interval.closed[Int](5, 10) // [5, 10]

val canvas: Canvas  = Canvas.make(40)                       // width 40 chars
val view: View[Int] = View(left = Some(0), right = Some(7)) // [0, 7]
val theme: Theme    = Theme.default

val diagram = Diagram.make(List(a), view, canvas)

Diagram.render(diagram, theme) // List[String]
```

It will display a view `[0, 7]` into an interval `[5, 10]` on a canvas of width `40`:

```text
                           [************
--+------------------------+---------+--
  0                        5         7
```

Here we can see that only a portion of a closed interval `[5, 10]` is displayed, since the right boundary of the view is `7`.

### Canvas

`Canvas` is used to specify the width of the text buffer to draw a diagram on.

```scala
val canvas1 = Canvas.default   // creates a default canvas (width: 40)
val canvas2 = Canvas.make(100) // creates a canvas of width 100
```

## Domain

To work with intervals, a `given` instance of `Domain[T]` is needed.

`Domain[T]` is defined as:

```scala
trait Domain[T]:
  def succ(x: T): T
  def pred(x: T): T
```

where `succ(x)` and `pred(x)` are used to get the next and the previous value for `x`.

By default `Domain[T]` is implemented for `Integral[T]` types (e.g. `Int`, `Long`), `OffsetDateTime`, and `Instant`.

## Ordering

Intervals can be ordered.

```scala
val a = Interval.closed(0, 10)   // [0, 10]
val b = Interval.closed(20, 30)  // [20, 30]

List(b, a).sorted // List(a, b)  // [0, 10], [20, 30]
```
