# mindthegap

> Intervals, Relations and Algorithms

[![Build](https://github.com/gchudnov/mindthegap/actions/workflows/ci.yml/badge.svg)](https://github.com/gchudnov/mindthegap/actions/workflows/ci.yml)

<br clear="right" /><!-- Turn off the wrapping for the logo image. -->

## Intervals

- Empty | `[b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅`
- Degenerate | `[a, a] = {a}`
- Proper and Bounded
  - Open | `(a, b) = {x | a < x < b}`
  - Closed | `[a, b] = {x | a <= x <= b}`
  - LeftClosedRightOpen | `[a, b) = {x | a <= x < b}`
  - LeftOpenRightClosed | `(a, b] = {x | a < x <= b}`
- LeftBounded and RightUnbounded
  - LeftOpen | `(a, +∞) = {x | x > a}`
  - LeftClosed | `[a, +∞) = {x | x >= a}`
- LeftUnbounded and RightBounded
  - RightOpen | `(-∞, b) = {x | x < b}`
  - RightClosed | `(-∞, b] = {x | x < b}`
- Unbounded | `(-∞, +∞) = R`

## Relations

## Usage

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.github.gchudnov" %% "mindthegap" % "0.2.0"
```

## Interval Creation

### Factory Method

`Interval.make` is a universal method that can be used to create an _empty_, _degenerate_ or a _proper_ intervals.

```scala
// empty ∅
val a1 = Interval.make(5, true, 2, false)
val a2 = Interval.make(Boundary.Left(5, true), Boundary.Right(2, false))

// degenerate {5}
val b1 = Interval.make(5, true, 5, true)
val b2 = Interval.make(Boundary.Left(5, true), Boundary.Right(5, true))

// proper [1, 5]
val c1 = Interval.make(1, true, 5, true)
val c2 = Interval.make(Boundary.Left(1, true), Boundary.Right(5, true))
```

In addition, there are a number of specialized methods for interval creation.

### Empty

```scala
// [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
Interval.empty[Int]
```

### Degenerate

```scala
// [a, a] = {a}
Interval.degenerate(5)
```

### Proper and Bounded

#### Open

```scala
// (a, b) = {x | a < x < b}
Interval.open(1, 5)
```

#### Closed

```scala
// [a, b] = {x | a <= x <= b}
Interval.closed(1, 5)
```

#### LeftClosedRightOpen

```scala
// [a, b) = {x | a <= x < b}
Interval.leftClosedRightOpen(1, 10)
```

#### LeftOpenRightClosed

```scala
// (a, b] = {x | a < x <= b}
Interval.leftOpenRightClosed(1, 10)
```

### LeftBounded and RightUnbounded

#### LeftOpen

```scala
// (a, +∞) = {x | x > a}
Interval.leftOpen(1)
```

#### LeftClosed

```scala
// [a, +∞) = {x | x >= a}
Interval.leftClosed(5)
```

### LeftUnbounded and RightBounded

#### RightOpen

```scala
// (-∞, b) = {x | x < b}
Interval.rightOpen(1)
```

#### RightClosed

```scala
// (-∞, b] = {x | x < b}
Interval.rightClosed(5)
```

### Unbounded

```scala
// (-∞, +∞) = R
Interval.unbounded[Int]
```

## Show

To display an interval, `Show` can be used:

```scala
import com.github.gchudnov.mtg.Show.given

val a = Interval.empty[Int]
val b = Interval.degenerate(5)
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
val a = Interval.closed(0, 10)
val b = Interval.closed(20, 30)

List(b, a).sorted // List(a, b)
```

## Links

- [Allen's Interval Algebra](https://www.ics.uci.edu/~alspaugh/cls/shr/allen.html)

## Keywords

Allen’s Interval Algebra, Interval Arithmetic, Interval Relations, Infinite Temporal Intervals, Temporal Algorithms

## Contact

[Grigorii Chudnov](mailto:g.chudnov@gmail.com)

## License

Distributed under the [The MIT License (MIT)](LICENSE).
