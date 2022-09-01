# TODO

- add a test to make sure that a collection can hold different intervals
- start algebra impl


what if:

sealed trait Point[T: Ordering]

final case class Pin[T: Ordering](x: T) extends Point[T]
final case class Hole[T: Ordering](x: T) extends Point[T]