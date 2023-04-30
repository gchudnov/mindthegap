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
val a = Interval.closed(1, 5)   // [1, 5]
val b = Interval.closed(7, 10)  // [7, 10]

val c = a.span(b)               // [1, 10]
```

![span-disjoint.svg](./span-disjoint.svg)

- **Commutative property**: `a # b = b # a` holds for _any_ intervals `a` and `b`. It means that changing the _order of the operands_ does not change the result.
- **Associative property**: `(a # b) # c = a # (b # c)` holds _for_ any intervals `a`, `b` and `c`. It means that _order of operations_ does not affect the result.

## Union

A _union_ `∪` of two intervals `a` and `b` is the interval `c`, such that: `c = a ∪ b = [min(a-, b-), max(a+, b+)]` if `merges(a, b)` and `∅` otherwise.

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

A _gap_ `∥` between two intervals `a` and `b` is the interval `c`, such that: `c = a ∥ b := [succ(min(a+, b+)), pred(max(a-, b-))]`.

```scala
val a = Interval.closed(1, 4)   // [1, 4]
val b = Interval.closed(7, 10)  // [7, 10]

val c = a.gap(b)                // [5, 6]
```

![gap.svg](./gap.svg)

If the intervals are not disjoint, the gap is empty.

```scala
val a = Interval.closed(5, 10) // [5, 10]
val b = Interval.closed(1, 7)  // [1, 7]

val c = a.gap(b)               // ∅
```

![gap-if-intersect.svg](./gap-if-intersect.svg)

- **Commutative property**: `a ∥ b = b ∥ a` holds for _any_ intervals `a` and `b`. It means that changing the _order of the operands_ does not change the result.

**NOTE:** _intersection_ and _gap_ operations are related: for _any_ intervals `a` and `b`, `(a & b).swap == (a ∥ b).inflate`.

## Minus

Subtraction `-` of two intervals, `a` _minus_ `b` is the interval `c`, such that:

- `c := a - b = [a-, min(pred(b-), a+)]` if `a- < b-` and `a+ <= b+`;
- `c := a - b = [max(succ(b+), a-), a+]` if `a- >= b-` and `a+ > b+`.

`a.minus(b)` is defined if and only if:

- `a` and `b` are _disjoint_;
- `a` _contains_ either `b-` or `b+` but not both;
- either `b.starts(a)` or `b.finishes(a)` is true.

`a.minus(b)` is undefined if:

- either `a.starts(b)` or `a.finishes(b)`;
- either `a` or `b` is _properly included_ in the other.
  - NOTE: when `a.contains(b)`, use `Intervals.minus(a, b)` instead, it returns a collection of intervals.

```scala
val a = Interval.closed(1, 10)  // [1, 10]
val b = Interval.closed(5, 15)  // [5, 15]

val c = a.minus(b)              // [1, 4]
```

![minus-left.svg](./minus-left.svg)

```scala
val a = Interval.closed(5, 15) // [5, 15]
val b = Interval.closed(1, 10) // [1, 10]

val c = a.minus(b)             // [11, 15]
```

![minus-right.svg](./minus-right.svg)

**NOTE:** the operation `a.minus(b)` is not defined if `a.contains(b)` and throws `UnsupportedOperationException`.
Use `Intervals.minus(a, b)` instead that returns a collection of intervals: `c1, c2 := (a - b) = list([a-, pred(b-)], [succ(b+), a+])`

```scala
val a = Interval.closed(1, 15)  // [1, 15]
val b = Interval.closed(5, 10)  // [5, 10]

// val c = a.minus(b)           // throws UnsupportedOperationException
val cs = Interval.minus(a, b)   // [[1, 4], [11, 15]]
```

![minus-contains.svg](./minus-contains.svg)

## Group

Group operation takes a collection of intervals `[a1, a2, ... an]` and merges all of the _adjacent_ or _intersecting_ ones (`ak = [min(ai-, aj-), max(ai+, aj+)]` if `merges(ai, aj)`), creating a new collection of intervals -- interval groups.

Methods:

- `Interval.group([a1, a2, ... an])` returns a collection of grouped intervals: `[g1, g2, ... gn]`.
- `Interval.groupFind([a1, a2, ... an])` returns a collection of tuples `[(g1, {i, ... }), (g2, {j, ... }), ... (gn, {k, ... })]` where `gk` is the grouped interval and `{i, ... }` is a set of indices of intervals that were grouped.

The produced collection of intervals has the following properties:

- *disjoint* and contains no overlapping intervals;
- contain no *adjacent* intervals;
- sorted

```scala
val a = Interval.closed(0, 10)  // [0, 10]
val b = Interval.closed(3, 50)  // [3, 50]
val c = Interval.closed(20, 30) // [20, 30]

val d = Interval.closed(60, 70) // [60, 70]
val e = Interval.closed(71, 80) // [71, 80]

val input = List(a, b, c, d, e)
//               0  1  2  3  4

val gs = Interval.group(input)
// [ [0, 50], [60, 80] ]

val ts = Interval.groupFind(input)
// [ ([0, 50], {0, 1, 2}), ([60, 80], {3, 4}) ]
```

![group.svg](./group.svg)

## Complement

Complement is a collection of gaps between grouped intervals `[a1, a2, ... an]`.

```scala
val a = Interval.closed(0, 10)  // [0, 10]
val b = Interval.closed(5, 20)  // [5, 20]
val c = Interval.closed(25, 30) // [25, 30]
val d = Interval.closed(35, 40) // [35, 40]

val input = List(a, b, c, d)

val is = Interval.complement(input)
// [ (-∞, -1], [21, 24], [31, 34], [41, +∞) ]
```

![complement.svg](./complement.svg)

## Split

Split intervals into a collection of intervals (splits), where every pair of splits is _adjacent_. The splits are sorted.

Methods:

- `Interval.split([a1, a2, ... an])` takes a collection of intervals and returns a collection of splits: `[s1, s2, ... sn]`.
- `Interval.splitFind([a1, a2, ... an])` takes a collection of intervals and returns a collection of tuples `[(s1, {i, ... }), (s2, {j, ... }), ... (sn, {k, ... })]` where `sk` is the split-interval and `{i, ... }` is a set of indices of input-intervals that belong to `sk`-split.


```scala
val a = Interval.closed(0, 20)  // [0, 20]
val b = Interval.closed(10, 30) // [10, 30]
val c = Interval.closed(40, 50) // [40, 50]

val input = List(a, b, c)

val ss = Interval.split(input)
// [ [0, 9], [10, 20], [21, 30], [31, 39], [40, 50]

val gs = Interval.splitFind(input)
// [ ([0, 9], {0}), ([10, 20], {0, 1}), ([21, 30], {1}), ([31, 39], {}), ([40, 50], {2}) ]
```

![split.svg](./split.svg)
