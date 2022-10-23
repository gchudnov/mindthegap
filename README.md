# mindthegap

> Intervals, Relations and Algorithms

[![Build](https://github.com/gchudnov/mindthegap/actions/workflows/ci.yml/badge.svg)](https://github.com/gchudnov/mindthegap/actions/workflows/ci.yml)

<br clear="right" /><!-- Turn off the wrapping for the logo image. -->

## Usage

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.github.gchudnov" %% "mindthegap" % "0.2.0"
```

## Intervals

One can distinguish an _empty_, _point_ and _proper_ intervals:

- A _proper_ interval is an ordered pair of points with the first point less than the second.
- A _point_ is a degenerate interval wherefirst point is equal to the second.
- An _empty_ interval is the one where first point is greater than the second.

For any interval `a`, We denote the lesser endpoint is denoted by `a-` and the greater by `a+`.
We assume for a proper interval `a` that `a- < a+`.

Intervals might be open / closed on both ends, bounded and unbonded:

- Empty | `[a+, a-] = (a+, a-) = [a+, a-) = (a+, a-] = (a-, a-) = [a-, a-) = (a-, a-] = {} = ∅`
- Point | `{x} = {x | a- = x = a+}`
- Proper and Bounded
  - Open | `(a-, a+) = {x | a- < x < a+}`
  - Closed | `[a-, a+] = {x | a- <= x <= a+}`
  - LeftClosedRightOpen | `[a-, a+) = {x | a- <= x < a+}`
  - LeftOpenRightClosed | `(a-, a+] = {x | a- < x <= a+}`
- LeftBounded and RightUnbounded
  - LeftOpen | `(a-, +∞) = {x | x > a-}`
  - LeftClosed | `[a-, +∞) = {x | x >= a-}`
- LeftUnbounded and RightBounded
  - RightOpen | `(-∞, a+) = {x | x < a+}`
  - RightClosed | `(-∞, a+] = {x | x < a+}`
- Unbounded | `(-∞, +∞) = R`

## Relations

The library support the following relations:

- **Point-Point (PP)** relations between a pair of points.
- **Point-Interval (PI)** relations that between a point and an interval.
- **Interval-Interval (II)** relations that between a pair of intervals.

![relations.png](res/relations.png)

The 13 relations are:

- **Distinct** because no pair of definite intervals can be related by more than one of the relationships.
- **Exhaustive** because any pair of definite intervals are described by one of the relations.

## Interval Creation

### Make

`Interval.make` is a universal method that can be used to create an _empty_, _point_ or a _proper_ intervals.

```scala
// empty: ∅ = [5, 2)
val a1 = Interval.make(5, true, 2, false)
val a2 = Interval.make(Boundary.Left(5, true), Boundary.Right(2, false))

// point: {5} = [5, 5]
val b1 = Interval.make(5, true, 5, true)
val b2 = Interval.make(Boundary.Left(5, true), Boundary.Right(5, true))

// proper: [1, 5]
val c1 = Interval.make(1, true, 5, true)
val c2 = Interval.make(Boundary.Left(1, true), Boundary.Right(5, true))
```

### Special Factory Methods

In addition, there are a number of specialized methods for interval creation.

```scala
Interval.empty[Int]                 // ∅
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

## Basic Relations

A relation between a pair of intervals can be checked.

```text
  Relation                  AAAAA
  before(a,b)      b|B      :   : BBBBBBBBB  |  a+ < b-
  meets(a,b)       m|M      :   BBBBBBBBB    |  a+ = b-
  overlaps(a,b)    o|O      : BBBBBBBBB      |  a- < b- < a+ ; a+ < b+
  starts(a,b)      s|S      BBBBBBBBB        |  a- = b- ; a+ < b+
  during(a,b)      d|D    BBBBBBBBB          |  a- > b- ; a+ < b+
  finishes(a,b)    f|F  BBBBBBBBB            |  a+ = b+ ; a- > b-
  equals(a, b)     e        BBBBB            |  a- = b- ; a+ = b+
```

```scala
// before, after
Interval.open(1, 4).before(Interval.open(3, 6)) // true
Interval.open(3, 6).after(Interval.open(1, 4))  // true

// meets, isMetBy
Interval.closed(1, 5).meets(Interval.closed(5, 10))   // true
Interval.closed(5, 10).isMetBy(Interval.closed(1, 5)) // true

// overlaps, isOverlappedBy
Interval.open(1, 10).overlaps(Interval.open(5, 20))      // true
Interval.open(5, 30).isOverlapedBy(Interval.open(1, 10)) // true

// starts, isSatrtedBy
Interval.closed(1, 2).starts(Interval.closed(1, 10))      // true
Interval.closed(1, 10).isStartedBy(Interval.closed(1, 2)) // true

// finishes, isFinishedBy
Interval.leftClosedRightOpen(0, 5).finishes(Interval.leftClosedRightOpen(-1, 5))     // true
Interval.leftClosedRightOpen(-1, 5).isFinishedBy(Interval.leftClosedRightOpen(0, 5)) // true

// equalsTo
Interval.open(4, 7).equalsTo(Interval.open(4, 7)) // true
```

## Extended Relations

### IsSubset

Determines whether `A` is a subset of `B`.

```text
  isSubset                  AAAAA            |  a- >= b- AND a+ <= b+
                            :   :
  starts(a,b)      s        BBBBBBBBB
  during(a,b)      d      BBBBBBBBB
  finishes(a,b)    f    BBBBBBBBB
  equals(a, b)     e        BBBBB
```

```scala
Interval.open(4, 7).isSubset(Interval.open(4, 10)) // true
Interval.open(4, 7).isSubset(Interval.open(2, 10)) // true
Interval.open(4, 7).isSubset(Interval.open(2, 7))  // true
Interval.open(4, 7).isSubset(Interval.open(4, 7))  // true
```

### IsSuperset

Determines whether `A` is a superset of `B`.

```text
  isSuperset                    BBBBB         |  b- >= a- AND b+ <= a+
                                :   :
  is-started-by(a,b)   S        AAAAAAAAA
  contains(a,b)        D      AAAAAAAAA
  is-finished-by(a,b)  F    AAAAAAAAA
  equals(a, b)         e        AAAAA
```

```scala
Interval.open(4, 10).isSuperset(Interval.open(4, 7)) // true
Interval.open(2, 10).isSuperset(Interval.open(4, 7)) // true
Interval.open(2, 7).isSuperset(Interval.open(4, 7))  // true
Interval.open(4, 7).isSuperset(Interval.open(4, 7))  // true
```

### IsDisjoint

Determines if there `A` and `B` are disjoint. `A` and `B` are disjoint if `A` does not intersect `B`.

```text
  isDisjoint                AAAAA        |  a+ < b- OR a- > b+
                            :   :
  before(a,b)      b        :   : BBBBB
  after(a,b)       B  BBBBB :   :
```

```scala
Interval.open(1, 4).isDisjoint(Interval.open(3, 6)) // true
Interval.open(3, 6).isDisjoint(Interval.open(1, 4)) // true
```

### IsLess

Determines whether `A` less-than `B`.

```text
  isLess                    AAAAA            |  a- < b- AND a+ < b+
                            :   :
  before(a,b)      b        :   : BBBBBBBBB
  meets(a,b)       m        :   BBBBBBBBB
  overlaps(a,b)    o        : BBBBBBBBB
```

```scala
Interval.open(4, 7).isLess(Interval.open(10, 15)) // true
Interval.open(4, 7).isLess(Interval.open(6, 15))  // true
Interval.open(4, 7).isLess(Interval.open(5, 15))  // true
```

## Operations

Intervals support the following list of operations: `intersection`, `span`.

### Intersection

An intersection of two intervals `a` and `b`: `a ∩ b := | max(a-, b-), min(a+, b+) |`

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.intersection(b)      // [5, 7]

val canvas: Canvas = Canvas.make(40)
val diagram        = Diagram.make(List(a, b, c), canvas)

val data = Diagram.render(diagram, Theme.default.copy(legend = true))

data.foreach(println)
```

Will produce the following result:

```text
                  [******************]   | [5,10]
  [**********************]               | [1,7]
                  [******]               | [5,7]
--+---------------+------+-----------+-- |
  1               5      7          10   |
```

### Span

A span of two intervals `a` and `b`: `a # b := | min(a-, b-), max(a+, b+) |`

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.span(b)              // [5, 10]

val canvas: Canvas = Canvas.make(40)
val diagram        = Diagram.make(List(a, b, c), canvas)

val data = Diagram.render(diagram, Theme.default.copy(legend = true))

data.foreach(println)
```

```text
                  [******************]   | [5,10]
  [**********************]               | [1,7]
  [**********************************]   | [1,10]
--+---------------+------+-----------+-- |
  1               5      7          10   |
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

val data = Diagram.render(diagram, theme.copy(label = Theme.Label.NoOverlap)) // List[String]

data.foreach(println)
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
- `legend: Boolean` used to specify whether to display a legend or not (default: false)

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

val data = Diagram.render(diagram, theme.copy(legend = true)) // List[String]

data.foreach(println)
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

val data = Diagram.render(diagram, theme.copy(label = Theme.Label.Stacked)) // List[String]

data.foreach(println)
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

val data = Diagram.render(diagram, theme) // List[String]

data.foreach(println)
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

## Domain

The library can work and create intervals for a set of predefined types: ones that implement `Integral[T]` trait (e.g. `Int`, `Long`), `OffsetDateTime`, and `Instant`.

To support intervals with other types a `given` instance of `Domain[T]` is needed. `Domain[T]` is defined as:

```scala
trait Domain[T]:
  def succ(x: T): T
  def pred(x: T): T
```

where `succ` and `pred` are used to get the next and the previous value for `x`.

## Ordering

Intervals can be ordered.

```scala
val a = Interval.closed(0, 10)   // [0, 10]
val b = Interval.closed(20, 30)  // [20, 30]

List(b, a).sorted // List(a, b)  // [0, 10], [20, 30]
```

## Links

- [Allen's Interval Algebra](https://www.ics.uci.edu/~alspaugh/cls/shr/allen.html)

## Keywords

Allen’s Interval Algebra, Interval Arithmetic, Interval Relations, Infinite Temporal Intervals, Temporal Algorithms

## Contact

[Grigorii Chudnov](mailto:g.chudnov@gmail.com)

## License

Distributed under the [The MIT License (MIT)](LICENSE).
