# TODO

- rename same -> equalTo

- start arithmetic impl
- add separate edge tests:
        // TODO: impl it, see https://github.com/Breinify/brein-time-utilities/blob/master/test/com/brein/time/timeintervals/intervals/TestInterval.java

[info] Relation
[info]   when preceeds (before) & isPreceededBy (after)
[info]   - should check
[info]   - should check edge cases
[info]   when meets & isMetBy
[info]   - should check *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: true was not equal to false
[info]       Location: (RelationSpec.scala:340)
[info]       Occurred when passed generated values (
[info]         arg0 = ((None,Some(4)),true,true), // 1 shrink
[info]         arg1 = ((Some(4),Some(4)),true,true)
[info]       )
[info]     Init Seed: -5981356512257969015
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
[info]       Location: (RelationSpec.scala:339)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(4),Some(4)),true,true),
[info]         arg1 = ((Some(4),None),true,false)
[info]       )
[info]     Init Seed: -1888657284251818837
[info]   - should check edge cases
[info]   when finishes & isFinishedBy
[info]   - should check *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: true was not equal to false
[info]       Location: (RelationSpec.scala:340)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(1),Some(1)),true,true),
[info]         arg1 = ((None,Some(1)),true,true) // 1 shrink
[info]       )
[info]     Init Seed: -6201976837688870366
[info]   - should check edge cases
[info]   when equals
[info]   - should check *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: true was not equal to false
[info]       Location: (RelationSpec.scala:339)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(3),Some(3)),true,true),
[info]         arg1 = ((Some(3),Some(3)),true,true)
[info]       )
[info]     Init Seed: -8244932978580595827
[info]   - should check edge cases