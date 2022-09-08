# TODO

- start arithmetic impl
- add separate edge tests:
        // TODO: impl it, see https://github.com/Breinify/brein-time-utilities/blob/master/test/com/brein/time/timeintervals/intervals/TestInterval.java


[info] RelationSpec:
[info] Relation
[info]   when preceeds (before) & isPreceededBy (after)
[info]   - should check
[info]   - should check edge cases
[info]   when meets & isMetBy
[info]   - should check *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: true was not equal to false
[info]       Location: (RelationSpec.scala:345)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(-4),Some(-4)),true,true),
[info]         arg1 = ((Some(-4),None),true,false) // 1 shrink
[info]       )
[info]     Init Seed: -7286489630672447182
[info]   - should check edge cases
[info]   when overlaps & isOverlapedBy
[info]   - should check
[info]   - should check edge cases
[info]   when during & contains (includes)
[info]   - should check
[info]   - should check edge cases
[info]   when starts & isStartedBy
[info]   - should check *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: true was not equal to false
[info]       Location: (RelationSpec.scala:345)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(1),Some(1)),true,true),
[info]         arg1 = ((Some(1),None),true,false) // 1 shrink
[info]       )
[info]     Init Seed: 3676354580559728458
[info]   - should check edge cases
[info]   when finishes & isFinishedBy
[info]   - should check *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: true was not equal to false
[info]       Location: (RelationSpec.scala:346)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(5),Some(5)),true,true),
[info]         arg1 = ((None,Some(5)),true,true) // 1 shrink
[info]       )
[info]     Init Seed: -5322753992139849303