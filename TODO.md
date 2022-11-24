# TODO

## XDOC

- make svg adaptive

## PROJECT

FAILED TEST:

[info]   - should b.merges(a) if (a.isEmpty OR b.isEmpty) *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: false was not equal to true
[info]       Location: (MergesSpec.scala:63)
[info]       Occurred when passed generated values (
[info]         arg0 = IntArgs(Succ(At(Finite(-5))),Pred(At(Finite(-5)))),
[info]         arg1 = IntArgs(At(Finite(-4)),Pred(At(Finite(-5))))
[info]       )
[info]     Init Seed: 1494597923943396364


- how to make svg that adapts to light / dark theme?
  - use it for the domain
  - use it for the relations

- add inflate, deflate, swap documentation
- explain empty in the documentation
- add tests for negative intervals
- add laws tests
- add laws to the documentation??
- add COUNT -- or size to get the cardinality, add it to Domain (Ordinal)
- canonical - add a method to convert to [) interval
- add more operators and law tests
