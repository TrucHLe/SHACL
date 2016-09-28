package SHACL
package core

sealed trait ShTest
sealed trait ShConstraint extends ShTest
sealed trait ShAlgebraic extends ShTest

object ShConstraint {
  final case class ShNodeConstraint(parms: Set[ShParameter]) extends ShConstraint
  final case class ShPathConstraint(path: Set[ShPropertyPath], parms: Set[ShParameter]) extends ShConstraint
}

object ShAlgebraic {
  final case class ShAnd(shapes: Set[ShShape]) extends ShAlgebraic
  final case class ShOr(shapes: Set[ShShape]) extends ShAlgebraic
  final case class ShNot(shape: ShShape) extends ShAlgebraic
}
