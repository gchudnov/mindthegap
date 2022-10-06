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

### Empty

```text
[b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
```

```scala
Interval.empty[Int]
```

### Degenerate

```text
[a, a] = {a}
```

```scala
Interval.degenerate(5)
```

### Proper and Bounded

#### Open

```text
(a, b) = {x | a < x < b}
```

```scala
Interval.open(1, 5)
```

#### Closed

```text
[a, b] = {x | a <= x <= b}
```

```scala
Interval.closed(1, 5)
```

#### LeftClosedRightOpen

```text
[a, b) = {x | a <= x < b}
```

```scala
Interval.leftClosedRightOpen(1, 10)
```

#### LeftOpenRightClosed

```text
(a, b] = {x | a < x <= b}
```

```scala
Interval.leftOpenRightClosed(1, 10)
```

### LeftBounded and RightUnbounded

#### LeftOpen

```text
(a, +∞) = {x | x > a}
```

```scala
Interval.leftOpen(1)
```

#### LeftClosed

```text
[a, +∞) = {x | x >= a}
```

```scala
Interval.leftClosed(5)
```

### LeftUnbounded and RightBounded

#### RightOpen

```text
(-∞, b) = {x | x < b}
```

```scala
Interval.rightOpen(1)
```

#### RightClosed

```text
(-∞, b] = {x | x < b}
```

```scala
Interval.rightClosed(5)
```

### Unbounded

```text
(-∞, +∞) = R
```

```scala
Interval.unbounded[Int]
```

## Ordering

Intervals can be ordered.

```scala
given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]
given iOrd: Ordering[Interval[Int]] = IntervalOrdering.intervalOrdering[Int]

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
