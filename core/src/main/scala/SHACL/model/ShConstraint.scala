package SHACL
package model

sealed trait ShConstraint extends ShTest

object ShConstraint {
  final case class ShNodeConstraint(parms: Set[ShParameter]) extends ShConstraint
  final case class ShPathConstraint(path: ShPropertyPath, parms: Set[ShParameter]) extends ShConstraint
}
