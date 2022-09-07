# TODO

- start arithmetic impl
- add separate edge tests


 Relation when during & contains (includes) should check - org.scalatest.exceptions.GeneratorDrivenPropertyCheckFailedException: TestFailedException was thrown during property evaluation.
  Message: false was not equal to true
  Location: (RelationSpec.scala:135)
  Occurred when passed generated values (
    arg0 = ((Some(0),Some(0)),true,true), // 4 shrinks
    arg1 = ((Some(-1),None),true,true) // 2 shrinks
  )
Init Seed: -3125474044064801446
===
[info]   - should check *** FAILED ***
[info]     TestFailedException was thrown during property evaluation.
[info]       Message: false was not equal to true
[info]       Location: (RelationSpec.scala:195)
[info]       Occurred when passed generated values (
[info]         arg0 = ((Some(0),Some(4)),false,true), // 2 shrinks
[info]         arg1 = ((Some(1),Some(4)),false,true) // 1 shrink
[info]       )
[info]     Init Seed: -4499602663247088130