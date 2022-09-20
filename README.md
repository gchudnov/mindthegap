# mindthegap

Keywords: Allen’s Interval Algebra, Infinite Temporal Intervals, Temporal Knowledge Representation and Reasoning -- add them to git
Keywords: interval arithmetic, interval relations, hardware unit


> Express intervals and relations between them.

- **Point-Point (PP)** relations between a pair of points.
- **Point-Interval (PI)** relations that between a point and an interval.
- **Interval-Interval (II)** relations that between a pair of intervals.

```text
Relation                      Example        Endpoints

p before q            b        p               p < q
                                   q

p after q             a        q               q < p
                                   p

p equal q             e          p             p = q
                                 q

p before I            b        p               p < i-
                                 III

p after I             a            p           p > i+
                               III

p starts I            s        p               p = i-
                               III

                                p              i- < p < i+
p during I            d        III

p finishes I          f          p             p = i+
                               III


I before J            b        III             i+ < j-
J after I             a             JJJ

I meets J             m        IIII            i+ = j-
J met by I            M            JJJJ

I overlaps J          o        IIII            i- < j- < i+
J overlapped by I     O          JJJJ          i+ < j+

I during J            d          III           i- > j-
J includes I          D        JJJJJJJ         i+ < j+

I starts J            s        III             i- = j-
J started by I        S        JJJJJJJ         i+ < j+

I finishes J          f            III         i+ = j+
J finished by I       F        JJJJJJJ         i- > j-

I equals J            e         IIII           i- = j-
                                JJJJ           i+ = j+
```