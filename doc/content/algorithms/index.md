+++
title = "Algorithms"
description = "Algorithms"
date = 2022-11-19
draft = false

[extra]
+++

## Intersection

An _intersection_ `&` of two intervals `a` and `b` is defined as the interval `c`, such that `c = a & b := [max(a-, b-), min(a+, b+)]`.
When two intervals are not intersecting, the _intersection_ produces an empty-interval.

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.intersection(b)      // [5, 7]
```

![intersection.svg](./intersection.svg)

The intersection is _commutative_ as changing the _order of the operands_ does not change the result. It means that for any intervals `a` and `b`, `a & b = b & a`.

The intersection is _associative_ as the _order of operations_ does not affect the final result. It means that for any intervals `a`, `b` and `c`, `(a & b) & c = a & (b & c)`.

## Span

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

## Union

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

## Gap

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
