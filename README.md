# mindthegap

> Intervals, Relations and Algorithms

[![Build](https://github.com/gchudnov/mindthegap/actions/workflows/ci.yml/badge.svg)](https://github.com/gchudnov/mindthegap/actions/workflows/ci.yml)

<br clear="right" /><!-- Turn off the wrapping for the logo image. -->

## Usage

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.github.gchudnov" %% "mindthegap" % "x.y.z"
```

### Interval Creation

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

### Interval Operations

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

### Interval Relations

A relation between two of the interval can be verified.


## Theory

An **interval** is defined by a pair `{a-, a+}`, whose elements represent a starting `a-` and ending `a+` values on a discrete linear infinite domain `D`.
Interval is a convex set that contains all elements between `a-` and `a+`.

![domain.png](res/domain.png)

For each element of the domain, there is only one _successor_ and _predecessor_ and no other elements in-between.

A pair `{a-, a+} ∈ D × D` corresponds to a point on a two-dimensional plane.

Given _successor_, `succ()` and _predecessor_, `pred` concept, we can define intervals as:

- closed interval `[a-, a+]` is represented as-is `[a-, a+]`
- right-open interval`[a-, a+)` is represented as `[a-, pred(a+)]`
- left-open interval `(a-, a+]` is represented as `[succ(a-), a+]`
- open interval `(a-, a+)` is represented as `[succ(a-), pred(a+)]`

In this way we consider a closed interval as a _canonical_ form.

### Non-Empty and Empty Intervals

A pair `{a-, a+} ∈ D × D` represents a non-empty interval if `a- ≤ a+`; otherwise, the interval is empty.
If left boundary `a-` is equal to the right boundary, `a+`, we call it as a _degenerate_ interval or a _point_.

One can distinguish an _empty_, _point_ and _proper_ intervals:

- A _proper_ interval is an ordered pair of elements where the first element is less than the second: `a- < a+`.
- A _point_ is a degenerate interval where first element is equal to the second: `a- = a+`.
- An _empty_ interval is the one where first element is greater than the second: `a- > a+`.

Looking at the two-dimensional plane on the diagram above, _proper_ intervals correspond the are above the line `a+ = a-`, _point_ intervals
are located on the line `a+ = a-` and all empty intervals are below the line `a+ = a-`.

### Classification of Intervals

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

### Relations

TODO: clarify better

The library support the following relations:

- **Point-Point (PP)** relations between a pair of points.
- **Point-Interval (PI)** relations that between a point and an interval.
- **Interval-Interval (II)** relations that between a pair of intervals.

![relations.png](res/relations.png)

The 13 relations are:

- **Distinct** because no pair of definite intervals can be related by more than one of the relationships.
- **Exhaustive** because any pair of definite intervals are described by one of the relations.
- **Qualitative** because no specific time spans are considered.

```text
  Relation                         AAAAA
                                   :   :
  a.before(b)         b            :   : BBBBBBBBB  | a+ < b-
  a.meets(b)          m            :   BBBBBBBBB    | a+ = b-
  a.overlaps(b)       o            : BBBBBBBBB      | a- < b- < a+ < b+
  a.starts(b)         s            BBBBBBBBB        | a- = b- ; a+ < b+
  a.during(b)         d          BBBBBBBBB          | a- > b- ; a+ < b+
  a.finishes(b)       f        BBBBBBBBB            | a+ = b+ ; a- > b-
  a.after(b)          B  BBBBBBBBB :   :            | a- > b+
  a.isMetBy(b)        M    BBBBBBBBB   :            | a- = b+
  a.isOverlappedBy(b) O      BBBBBBBBB :            | b- < a- < b+ < a+
  a.isStartedBy(b)    S            BBB :            | a- = b- ; b+ < a+
  a.contains(b)       D            : B :            | a- < b- ; b+ < a+
  a.isFinishedBy(b)   F            : BBB            | a+ = b+ ; a- < b-
  a.equals(b)         e            BBBBB            | a- = b- ; a+ = b+
```

```scala
// before (b), after (B)
Interval.open(1, 4).before(Interval.open(3, 6)) // true
Interval.open(3, 6).after(Interval.open(1, 4))  // true

// meets (m), isMetBy (M)
Interval.closed(1, 5).meets(Interval.closed(5, 10))   // true
Interval.closed(5, 10).isMetBy(Interval.closed(1, 5)) // true

// overlaps (o), isOverlappedBy (O)
Interval.open(1, 10).overlaps(Interval.open(5, 20))      // true
Interval.open(5, 30).isOverlapedBy(Interval.open(1, 10)) // true

// starts (s), isSatrtedBy (S)
Interval.closed(1, 2).starts(Interval.closed(1, 10))      // true
Interval.closed(1, 10).isStartedBy(Interval.closed(1, 2)) // true

// finishes (f), isFinishedBy (F)
Interval.leftClosedRightOpen(0, 5).finishes(Interval.leftClosedRightOpen(-1, 5))     // true
Interval.leftClosedRightOpen(-1, 5).isFinishedBy(Interval.leftClosedRightOpen(0, 5)) // true

// equalsTo (e)
Interval.open(4, 7).equalsTo(Interval.open(4, 7)) // true
```

## Extended Relations

Extended relations are the ones that compose of several basic relations.

### IsSubset

Determines whether `a` is a subset of `b`.

```text
  a.isSubset(b)                    AAAAA            | a- >= b- AND a+ <= b+
                                   :   :
  a.starts(b)         s            BBBBBBBBB        | a- = b- ; a+ < b+
  a.during(b)         d          BBBBBBBBB          | a- > b- ; a+ < b+
  a.finishes(b)       f        BBBBBBBBB            | a+ = b+ ; a- > b-
  a.equals(b)         e            BBBBB            | a- = b- ; a+ = b+
```

```scala
Interval.open(4, 7).isSubset(Interval.open(4, 10)) // true
Interval.open(4, 7).isSubset(Interval.open(2, 10)) // true
Interval.open(4, 7).isSubset(Interval.open(2, 7))  // true
Interval.open(4, 7).isSubset(Interval.open(4, 7))  // true
```

### IsSuperset

Determines whether `a` is a superset of `b`.

```text
  a.isSuperset(b)                  AAAAA            | b- >= a- AND b+ <= a+
                                   :   :
  a.isStartedBy(b)    S            BBB :            | a- = b- ; b+ < a+
  a.contains(b)       D            : B :            | a- < b- ; b+ < a+
  a.isFinishedBy(b)   F            : BBB            | a+ = b+ ; a- < b-
  a.equals(b)         e            BBBBB            | a- = b- ; a+ = b+
```

```scala
Interval.open(4, 10).isSuperset(Interval.open(4, 7)) // true
Interval.open(2, 10).isSuperset(Interval.open(4, 7)) // true
Interval.open(2, 7).isSuperset(Interval.open(4, 7))  // true
Interval.open(4, 7).isSuperset(Interval.open(4, 7))  // true
```

### IsDisjoint

Determines if there `a` and `b` are disjoint. `a` and `b` are disjoint if `a` does not intersect `b`.

```text
  a.isDisjoint(b)                  AAAAA            | a+ < b- OR a- > b+
                                   :   :
  a.before(b)         b            :   : BBBBBBBBB  | a+ < b-
  a.after(b)          B  BBBBBBBBB :   :            | a- > b+
```

```scala
Interval.open(1, 4).isDisjoint(Interval.open(3, 6)) // true
Interval.open(3, 6).isDisjoint(Interval.open(1, 4)) // true
```

### IsAdjacent

Two intervals `a` and `b` are adjacent if they are disjoint and `succ(a+) = b- OR succ(b+) = a-`

```text
  a.isAdjacent(b)                  AAAAA            |  succ(a+) = b- OR succ(b+) = a-
                                   :   :
  a.before(b)         b            :   : BBBBBBBBB  | a+ < b- ; succ(a+) = b-
  a.after(b)          B  BBBBBBBBB :   :            | a- > b+ ; succ(b+) = a-
```

```scala
Interval.open(1, 4).isAdjacent(Interval.open(3, 6))     // true
Interval.open(3, 6).isAdjacent(Interval.open(1, 4))     // true
Interval.closed(1, 4).isAdjacent(Interval.closed(5, 6)) // true
Interval.closed(5, 6).isAdjacent(Interval.closed(1, 4)) // true
```

### Intersects

Two intervals `a` and `b` are intersecting if: `a- <= b+ AND b- <= a+`

```text
  a.intersects(b)                  AAAAA            | a- <= b+ AND b- <= a+
                                   :   :
  a.meets(b)          m            :   BBBBBBBBB    | a+ = b-
  a.overlaps(b)       o            : BBBBBBBBB      | a- < b- < a+ < b+
  a.starts(b)         s            BBBBBBBBB        | a- = b- ; a+ < b+
  a.during(b)         d          BBBBBBBBB          | a- > b- ; a+ < b+
  a.finishes(b)       f        BBBBBBBBB            | a+ = b+ ; a- > b-
  a.isMetBy(b)        M    BBBBBBBBB   :            | a- = b+
  a.isOverlappedBy(b) O      BBBBBBBBB :            | b- < a- < b+ < a+
  a.isStartedBy(b)    S            BBB :            | a- = b- ; b+ < a+
  a.contains(b)       D            : B :            | a- < b- ; b+ < a+
  a.isFinishedBy(b)   F            : BBB            | a+ = b+ ; a- < b-
  a.equals(b)         e            BBBBB            | a- = b- ; a+ = b+
```

```scala
Interval.empty[Int].intersects(Interval.empty[Int])     // false
Interval.point(5).intersects(Interval.point(5))         // true
Interval.closed(0, 5).intersects(Interval.closed(1, 6)) // true
```

### Merges

Two intervals `a` and `b` can be merged, if they are adjacent or intersect.

```text
  a.merges(b)                      AAAAA            | intersects(a,b) OR isAdjacent(a,b)
                                   :   :
  a.before(b)         b            :   : BBBBBBBBB  | a+ < b- ; succ(a+) = b-
  a.meets(b)          m            :   BBBBBBBBB    | a+ = b-
  a.overlaps(b)       o            : BBBBBBBBB      | a- < b- < a+ < b+
  a.starts(b)         s            BBBBBBBBB        | a- = b- ; a+ < b+
  a.during(b)         d          BBBBBBBBB          | a- > b- ; a+ < b+
  a.finishes(b)       f        BBBBBBBBB            | a+ = b+ ; a- > b-
  a.after(b)          B  BBBBBBBBB :   :            | a- > b+ ; succ(b+) = a-
  a.isMetBy(b)        M    BBBBBBBBB   :            | a- = b+
  a.isOverlappedBy(b) O      BBBBBBBBB :            | b- < a- < b+ < a+
  a.isStartedBy(b)    S            BBB :            | a- = b- ; b+ < a+
  a.contains(b)       D            : B :            | a- < b- ; b+ < a+
  a.isFinishedBy(b)   F            : BBB            | a+ = b+ ; a- < b-
  a.equals(b)         e            BBBBB            | a- = b- ; a+ = b+
```

```scala
Interval.point(5).merges(Interval.point(6))       // true
Interval.open(4, 10).merges(Interval.open(5, 12)) // true
```

### IsLess

Determines whether `a` is less-than `b`.

```text
  a.isLess(b)                      AAAAA            | a- < b- AND a+ < b+
                                   :   :
  a.before(b)         b            :   : BBBBBBBBB  | a+ < b-
  a.meets(b)          m            :   BBBBBBBBB    | a+ = b-
  a.overlaps(b)       o            : BBBBBBBBB      | a- < b- < a+ < b+
```

TODO: ADD CONTAINS, isFinishedBy

```scala
Interval.open(4, 7).isLess(Interval.open(10, 15)) // true
Interval.open(4, 7).isLess(Interval.open(6, 15))  // true
Interval.open(4, 7).isLess(Interval.open(5, 15))  // true
```

### IsGreater

Determines whether `a` is greater-than `b`.

```text
  a.isGreater                      AAAAA            | a- > b- AND a+ > b+
                                   :   :
  a.after(b)          B  BBBBBBBBB :   :            | a- > b+
  a.isMetBy(b)        M    BBBBBBBBB   :            | a- = b+
  a.isOverlappedBy(b) O      BBBBBBBBB :            | b- < a- < b+ < a+
```

TODO: ADD during, finithses

```scala
Interval.open(10, 15).isGreater(Interval.open(4, 7)) // true
Interval.open(6, 15).isGreater(Interval.open(4, 7))  // true
Interval.open(5, 15).isGreater(Interval.open(4, 7))  // true
```

NOTE: empty intervals cannot be compared -- operations `isLess`, `isGreater`, `equalsTo` return `false`.

## Operations

### Intersection

An intersection of two intervals `a` and `b`: `a ∩ b := [max(a-, b-), min(a+, b+)]`.

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.intersection(b)      // [5, 7]
```

```text
                  [******************]   | [5,10]
  [**********************]               | [1,7]
                  [******]               | [5,7]
--+---------------+------+-----------+-- |
  1               5      7          10   |
```

### Span

A span of two intervals `a` and `b`: `a # b := [min(a-, b-), max(a+, b+)]`.

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.span(b)              // [1, 10]
```

```text
                  [******************]   | [5,10]
  [**********************]               | [1,7]
  [**********************************]   | [1,10]
--+---------------+------+-----------+-- |
  1               5      7          10   |
```

If intervals are disjoint, the span is non-empty:

```scala
val a = Interval.closed(1, 5)   // [1. 5]
val b = Interval.closed(7, 10)  // [7, 10]

val c = a.span(b)               // [1, 10]
```

```text
  [***************]                      | [1,5]
                         [***********]   | [7,10]
  [**********************************]   | [1,10]
--+---------------+------+-----------+-- |
  1               5      7          10   |
```

### Union

A union of two intervals `a` and `b`: `[min(a-,b-), max(a+,b+)]` if `merges(a, b)` and `∅` otherwise.

```scala
val a = Interval.closed(1, 5)  // [1, 5]
val b = Interval.closed(6, 10) // [6, 10]

val c = a.union(b)             // [1, 10]
```

```text
  [***************]                      | [1,5]
                     [***************]   | [6,10]
  [**********************************]   | [1,10]
--+---------------+--+---------------+-- |
  1               5  6              10   |
```

If intervals are disjoint and not adjacent, the union is empty:

```scala
val a = Interval.closed(1, 4)  // [1, 4]
val b = Interval.closed(6, 10) // [6, 10]

val c = a.union(b)             // ∅
```

```text
  [***********]                          | [1,4]
                     [***************]   | [6,10]
                                         | ∅
--+-----------+------+---------------+-- |
  1           4      6              10   |
```

### Gap

A gap between two intervals `a` and `b`: `a ∥ b := [min(a-, b-), max(a+, b+)]`.

```scala
val a = Interval.closed(5, 10)   // [5, 10]
val b = Interval.closed(15, 20)  // [15, 20]

val c = a.gap(b)                 // [11, 14]
```

```text
  [***********]                          | [5,10]
                         [***********]   | [15,20]
                [******]                 | [11,14]
--+-----------+-+------+-+-----------+-- |
  5          10 11    14 15         20   |
```

If intervals are not disjoint, the gap is empty.

### Minus

Subtraction of two intervals, `a` minus `b` returns:

- `[a-, min(pred(b-), a+)]` if `(a- < b-)` and `(a+ <= b+)`.
- `[max(succ(b+), a-), a+]` if `(a- >= b-)` and `(a+ > b+)`.

```scala
val a = Interval.closed(1, 10)  // [1, 10]
val b = Interval.closed(5, 15)  // [5, 15]

val c = a.minus(b)              // [1, 4]
```

```text
  [**********************]               | [1,10]
            [************************]   | [5,15]
  [*******]                              | [1,4]
--+-------+-+------------+-----------+-- |
  1       4 5           10          15   |
```

NOTE: the operation `a.minus(b)` is not defined if `a.contains(b)` and throws a `UnsupportedOperationException`.
Use `Intervals.minus(a, b)` instead that returns a collection of intervals:

```scala
val a = Interval.closed(1, 15)  // [1, 15]
val b = Interval.closed(5, 10)  // [5, 10]

// val c = a.minus(b)           // throws UnsupportedOperationException
val cs = Intervals.minus(a, b)  // [[1, 4], [11, 15]]
```

```text
  [**********************************]   | [1,15]  : a
            [************]               | [5,10]  : b
  [*******]                              | [1,4]   : c1
                           [*********]   | [11,15] : c2
--+-------+-+------------+-+---------+-- |
  1       4 5           10          15   |
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

## Links

- [Allen's Interval Algebra](https://www.ics.uci.edu/~alspaugh/cls/shr/allen.html)

## Keywords

Allen's Interval Algebra, Interval Arithmetic, Interval Relations, Infinite Temporal Intervals, Temporal Algorithms

## Contact

[Grigorii Chudnov](mailto:g.chudnov@gmail.com)

## License

Distributed under the [The MIT License (MIT)](LICENSE).
