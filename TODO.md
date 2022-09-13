# TODO

- starts, finishes and infinity, add tests
- add missing cases

- start arithmetic impl
- add tests for extended infinity
- add interval renderer
- check boundaries incl or not incl -- allen's algebra, open and closed intervals

TODO: FIX

[info]   when satisfy
[info]   - should only one relation *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: false was not equal to true
[info]       Location: (RelationSpec.scala:319)
[info]       Occurred when passed generated values (
[info]         arg0 = ((None,None),true,true), // 2 shrinks
[info]         arg1 = ((None,None),true,false) // 2 shrinks
[info]       )
[info]     Init Seed: 7688653401697036339

      ^^^ TODO: STARTS ^^^^

https://davisvaughan.github.io/ivs/reference/allen-relation-locate.html

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