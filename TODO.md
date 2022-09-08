# TODO

- start arithmetic impl
- add interval renderer

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

/**
 * Relations
 *
 * {{{
 *   AAA              | A preceeds B            | (p)
 *        BBB         | B is-predeeded-by A     | (P)
 *
 *   AAA              | A meets B               | (m)
 *      BBB           | B is-met-by A           | (M)
 *
 *   AAA              | A overlaps B            | (o)
 *     BBB            | B is-overlapped-by A    | (O)
 *
 *     AA             | A during B              | (d)
 *   BBBBBB           | B contains A            | (D)
 *
 *   AAA              | A starts B              | (s)
 *   BBBBBB           | B is-started-by A       | (S)
 *
 *      AAA           | A finishes B            | (f)
 *   BBBBBB           | B is-finished-by A      | (F)
 *
 *   AAA              | A equals B              | (e)
 *   BBB              |                         |
 * }}}
 */


https://web.mit.edu/hyperbook/Patrikalakis-Maekawa-Cho/node45.html

https://github.com/gchudnov/bottleneck/blob/34ddfdbb8f0fead52a41cbe4d0c65972352768f2/bakka/src/main/scala/com/github/gchudnov/bottleneck/bakka/internal/intervals/IntervalSyntax.scala

https://github.com/Breinify/brein-time-utilities/blob/master/test/com/brein/time/timeintervals/intervals/TestInterval.java

https://github.com/philippus/between

https://github.com/novisci/interval-algebra


