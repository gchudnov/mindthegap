+++
title = "Relations"
description = "Relations"
date = 2022-11-19
draft = false

[extra]
+++

## Basic Relations

The library support the following relations:

- **Point-Point (PP)** relations between a pair of points.
- **Point-Interval (PI)** relations that between a point and an interval.
- **Interval-Interval (II)** relations that between a pair of intervals.

![relations.svg](./relations.svg)

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

### Before

TBD

### Meets

TBD

### Overlaps

TBD

### Starts

TBD

### During

TBD

### After

TBD

### IsMetBy

TBD

### IsOverlappedBy

TBD

### IsStartedBy

TBD

### Contains

TBD

### IsFinishedBy

TBD

### Equals

TBD


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
