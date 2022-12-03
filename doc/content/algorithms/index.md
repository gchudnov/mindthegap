+++
title = "Algorithms"
description = "Algorithms"
date = 2022-11-19
draft = false

[extra]
+++

## Intersection

An _intersection_ `&` of two intervals `a` and `b` is the interval `c`, such that `c = a & b := [max(a-, b-), min(a+, b+)]`.
When two intervals are not intersecting, the _intersection_ produces an empty-interval.

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.intersection(b)      // [5, 7]
```

![intersection.svg](./intersection.svg)

- **Commutative property**: `a & b = b & a` holds for _any_ intervals `a` and `b`. It means that changing the _order of the operands_ does not change the result.
- **Associative property**: `(a & b) & c = a & (b & c)` holds _for_ any intervals `a`, `b` and `c`. It means that _order of operations_ does not affect the result.

## Span

A _span_ `#` of two intervals `a` and `b` is the interval `c`, such shat `c = a # b := [min(a-, b-), max(a+, b+)]`.

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.span(b)              // [1, 10]
```

![span.svg](./span.svg)

The resulting interval `c` covers the duration of both intervals `a` and `b` even if they are disjoint:

```scala
val a = Interval.closed(1, 5)   // [1. 5]
val b = Interval.closed(7, 10)  // [7, 10]

val c = a.span(b)               // [1, 10]
```

![span-disjoint.svg](./span-disjoint.svg)

- **Commutative property**: `a # b = b # a` holds for _any_ intervals `a` and `b`. It means that changing the _order of the operands_ does not change the result.
- **Associative property**: `(a # b) # c = a # (b # c)` holds _for_ any intervals `a`, `b` and `c`. It means that _order of operations_ does not affect the result.

## Union

A _union_ `∪` of two intervals `a` and `b` is the interval `c`, such that: `c = a ∪ b = [min(a-,b-), max(a+,b+)] if merges(a, b)` and `∅` otherwise.

The operation is similar to _span_, but in order to be merged, the intervals should be _adjacent_ or _intersecting_.

```scala
val a = Interval.closed(1, 5)  // [1, 5]
val b = Interval.closed(6, 10) // [6, 10]

val c = a.union(b)             // [1, 10]
```

![union-adjacent.svg](./union-adjacent.svg)

If the intervals are _disjoint_ but _not adjacent_, the union is empty:

```scala
val a = Interval.closed(1, 4)  // [1, 4]
val b = Interval.closed(6, 10) // [6, 10]

val c = a.union(b)             // ∅
```

![union-adjacent.svg](./union-disjoint.svg)

- **Commutative property**: `a ∪ b = b ∪ a` holds for _any_ intervals `a` and `b`. It means that changing the _order of the operands_ does not change the result.

## Gap

A gap `∥` between two intervals `a` and `b`: `a ∥ b := [min(a-, b-), max(a+, b+)]`.

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

## Minus

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
