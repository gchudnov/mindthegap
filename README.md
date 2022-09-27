# mindthegap

Keywords: Allen’s Interval Algebra, Infinite Temporal Intervals, Temporal Knowledge Representation and Reasoning -- add them to git
Keywords: interval arithmetic, interval relations, hardware unit


> Express intervals and relations between them.

- **Point-Point (PP)** relations between a pair of points.
- **Point-Interval (PI)** relations that between a point and an interval.
- **Interval-Interval (II)** relations that between a pair of intervals.

## Intervals

## Relations

![relations.png](res/relations.png)

- Before (b) / After (B)
- Meets / IsMetBy
- Overlaps / IsOverlappedBy
- During / Contains
- Starts / IsStartedBy
- Finishes / IsFinishedBy
- Equals


```text
  Relation                  AAAAA
  before(a,b)      b|B      :   : BBBBBBBBB
  meets(a,b)       m|M      :   BBBBBBBBB
  overlaps(a,b)    o|O      : BBBBBBBBB
  starts(a,b)      s|S      BBBBBBBBB
  during(a,b)      d|D    BBBBBBBBB
  finishes(a,b)    f|F  BBBBBBBBB
  equals(a, b)     e        BBBBB
```

- IsSubset

```text
                            AAAAA
  starts(a,b)      s        BBBBBBBBB
  during(a,b)      d      BBBBBBBBB
  finishes(a,b)    f    BBBBBBBBB
  equals(a, b)     e        BBBBB
```




| Relation   |   | Boundaries |
|------------|---|------------|
| p before q |   | p < q      |
| q after p  |   |            |
| p before A |   | p < a-     |
| p after A  |   | p > a+     |
| A before B |   | a+ < b-    |
| B after A  |   |            |




```
Relation                                 Example        Boundaries

p before q                      b        p               p < q
q after p                       B           q
        
p before A                      b        p               p < a-
                                           AAA
        
p after A                       B            p           p > a+
                                         AAA

A before B                      b        AAA             a+ < b-
B after A                       B             BBB
```



TODO: update the readme

```text
Relation                                 Example        Boundaries

p before q                      b        p               p < q
q after p                       B           q
        
p before A                      b        p               p < a-
                                           AAA
        
p after A                       B            p           p > a+
                                         AAA
        
                                          p              a- < p < a+
p during A                      d        AAA
        
p starts A                      s        p               p = a-
                                         AAA
        
p finishes A                    f          p             p = a+
                                         AAA
        
p equal q                       e          p             p = q
                                           q
        
        
        
       
A meets B                       m        AAAA            a+ = b-
B is-met-by A                   M            BBBB
        
A overlaps B                    o        AAAA            a- < b- < a+
B is-overlapped-by A            O          BBBB          a+ < b+
        
A during B                      d          AAA           a- > b-
B contains A                    D        BBBBBBB         a+ < b+
        
A starts B                      s        AAA             a- = b-
B is-started-by A               S        BBBBBBB         a+ < b+
        
A finishes B                    f            AAA         a+ = b+
B is-finished-by A              F        BBBBBBB         a- > b-
        
A equals B                      e         AAAA           a- = b-
                                          BBBB           a+ = b+
        
A is-subset B         (A ⊆ B)                            a- >= b-
                                        s, d, f, e       a+ <= b+


```