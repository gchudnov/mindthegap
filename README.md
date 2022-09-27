# mindthegap

Keywords: Allen’s Interval Algebra, Infinite Temporal Intervals, Temporal Knowledge Representation and Reasoning -- add them to git
Keywords: interval arithmetic, interval relations, hardware unit


> Express intervals and relations between them.

- **Point-Point (PP)** relations between a pair of points.
- **Point-Interval (PI)** relations that between a point and an interval.
- **Interval-Interval (II)** relations that between a pair of intervals.

## Relations

```text
Relation                        Example        Boundaries

p before q              b        p               p < q
                                     q

p after q               B        q               q < p
                                     p

p equal q               e          p             p = q
                                   q

p before A              b        p               p < a-
                                   AAA

p after A               B            p           p > a+
                                 AAA

p starts A              s        p               p = a-
                                 AAA

                                  p              a- < p < a+
p during A              d        AAA

p finishes A            f          p             p = a+
                                 AAA


A before B              b        AAA             a+ < b-
B after A               B             BBB

A meets B               m        AAAA            a+ = b-
B met-by A              M            BBBB

A overlaps B            o        AAAA            a- < b- < a+
B overlapped-by A       O          BBBB          a+ < b+

A during B              d          AAA           a- > b-
B contains A            D        BBBBBBB         a+ < b+

A starts B              s        AAA             a- = b-
B started-by A          S        BBBBBBB         a+ < b+

A finishes B            f            AAA         a+ = b+
B finished-by A         F        BBBBBBB         a- > b-

A equals B              e         AAAA           a- = b-
                                  BBBB           a+ = b+

A is-subset B (A ⊆ B)                            a- >= b-
                                s, d, f, e       a+ <= b+


```