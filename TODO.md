# TODO

TODO: FIX TESTS

[info]   - should one relation only *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: xx: Proper(Some(0),Some(5),true,false), yy: Proper(Some(-1),Some(5),true,false): |[0,5), [-1,5)| satisfies 0 relations: [], expected: 1
[info]       Location: (RelationSpec.scala:512)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(0),Some(5)),true,false), // 2 shrinks
[info]         arg1 = ((Some(-1),Some(5)),true,false) // 2 shrinks
[info]       )
[info]     Init Seed: 6626990138027787900



TODO: find relation between two intervals

TODO: reconsider points --- what relations do they have?

open, closed, degenerate, empty, unbounded

d|[-∞,4], [-∞,+∞)| == true; s|[-∞,4], [-∞,+∞)| mustBe false, got true
d|[-∞,2], [-∞,+∞)| == true; s|[-∞,2], [-∞,+∞)| mustBe false, got true
d|[-∞,1], [-∞,+∞)| == true; s|[-∞,1], [-∞,+∞)| mustBe false, got true
d|[-∞,0], [-∞,+∞)| == true; s|[-∞,0], [-∞,+∞)| mustBe false, got true


https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap

https://github.com/MenoData/Time4J

- starts, finishes and infinity, add tests
- add missing cases

- start arithmetic impl
- add tests for extended infinity
- add interval renderer
- check boundaries incl or not incl -- allen's algebra, open and closed intervals

https://davisvaughan.github.io/ivs/reference/allen-relation-locate.html

https://stewashton.wordpress.com/2015/06/08/merging-overlapping-date-ranges/
https://stewashton.wordpress.com/2015/06/10/merging-overlapping-date-ranges-with-match_recognize/

https://stackoverflow.com/questions/12069082/allens-interval-algebra-operations-in-sql

https://community.oracle.com/tech/developers/discussion/4177878/handling-nulls-in-interval-algebra


https://web.mit.edu/hyperbook/Patrikalakis-Maekawa-Cho/node45.html

https://github.com/gchudnov/bottleneck/blob/34ddfdbb8f0fead52a41cbe4d0c65972352768f2/bakka/src/main/scala/com/github/gchudnov/bottleneck/bakka/internal/intervals/IntervalSyntax.scala

https://github.com/Breinify/brein-time-utilities/blob/master/test/com/brein/time/timeintervals/intervals/TestInterval.java

https://github.com/philippus/between

https://github.com/novisci/interval-algebra


https://github.com/davedelong/time

/*
    func isBefore<OS>(_ other: Absolute<OS>) -> Bool {
        let r = relation(to: other)
        return r == .before || r == .meets
    }

    func isAfter<OS>(_ other: Absolute<OS>) -> Bool {
        return other.isBefore(self)
    }

    func contains<OS>(_ other: Absolute<OS>) -> Bool {
        let r = relation(to: other)
        return r == .contains || r == .isStartedBy || r == .isFinishedBy
    }

    func isDuring<OS>(_ other: Absolute<OS>) -> Bool {
        let r = relation(to: other)
        return r == .during || r == .starts || r == .finishes
    }

    func overlaps<OS>(_ other: Absolute<OS>) -> Bool {
        return relation(to: other).isOverlapping
    }
*/

FROM:
https://community.oracle.com/tech/developers/discussion/4177878/handling-nulls-in-interval-algebra

"Operations" include union, intersection, minus. "Constants" include the empty interval and the total set (from minus to plus infinity).

"No interval" - thinking about this further, I believe "interval" should be a composite data type - a tuple (an ordered pair). As such, the NULL tuple should represent "no" interval (undefined, unknown, whatever NULL means in other contexts), and NULL as the left or right end point can then represent infinity. From this viewpoint, if one endpoint of an interval is not known, then the whole interval should be viewed as NULL (the NULL tuple, not the tuple with one or both members NULL). And in set operations, if one operator is NULL then the result of the operation should be NULL (in most cases; NULL union the total set should still be the total set, but on the other hand, NULL * 0 is still NULL even though it should be 0......)

https://www.researchgate.net/publication/3336591_Fuzzifying_Allen's_Temporal_Interval_Relations

http://marcosh.github.io/post/2020/05/04/intervals-and-their-relations.html


http://time4j.net/javadoc-en/net/time4j/range/IsoInterval.html

https://package.elm-lang.org/packages/r-k-b/elm-interval/latest/Interval

https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap

https://github.com/MenoData/Time4J


Prior work: 

https://github.com/novisci/interval-algebra

- https://github.com/Breinify/brein-time-utilities
- https://github.com/philippus/between
- https://github.com/typelevel/spire/blob/main/core/src/main/scala/spire/math/Interval.scala

https://web.mit.edu/hyperbook/Patrikalakis-Maekawa-Cho/node45.html

https://www.scalatest.org/user_guide/generator_driven_property_checks
https://www.scalatest.org/user_guide/property_based_testing




## Links

- [Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))
- [Allen's Interval Algebra](https://en.wikipedia.org/wiki/Allen%27s_interval_algebra)
- [Allen's Interval Algebra](https://www.ics.uci.edu/~alspaugh/cls/shr/allen.html)
- [Complete Interval Arithmetic and its Implementation on the Computer](https://www.math.kit.edu/ianm2/~kulisch/media/arjpkx.pdf)
- [Interval Arithmetic](https://web.mit.edu/hyperbook/Patrikalakis-Maekawa-Cho/node45.html)
- [Finding a Consistent Scenario to an Interval Algebra Network Containing Possibly Infinite Intervals](https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.430.1918&rep=rep1&type=pdf)


https://hackage.haskell.org/package/rampart-2.0.0.3/docs/Rampart.html


https://abstractmath.org/MM/MMSetSpecific.htm

