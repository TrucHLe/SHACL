package SHACL
package model

sealed trait ShAlgebraic extends ShTest

object ShAlgebraic {
  final case class ShAnd(shapes: Set[ShShape]) extends ShAlgebraic
  final case class ShOr(shapes: Set[ShShape]) extends ShAlgebraic
  final case class ShNot(shape: ShShape) extends ShAlgebraic
}
