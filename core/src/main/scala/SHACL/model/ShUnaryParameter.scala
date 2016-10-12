package SHACL
package model

import org.eclipse.rdf4j.model.{ IRI, Value, Literal }

sealed trait ShUnaryParameter extends ShParameter

object ShUnaryParameter {
  final case class ShPredicate(pred: IRI) extends ShUnaryParameter
  final case class ShNodeKind(kind: Value) extends ShUnaryParameter
  final case class ShIn(in: Set[Value]) extends ShUnaryParameter
  final case class ShClass(t: IRI) extends ShUnaryParameter
  final case class ShDatatype(dt: IRI) extends ShUnaryParameter
  final case class ShMinLength(ref: Int) extends ShUnaryParameter
  final case class ShMaxLength(ref: Int) extends ShUnaryParameter

  // TODO: implement the rest of ShUnaryparameter
}
