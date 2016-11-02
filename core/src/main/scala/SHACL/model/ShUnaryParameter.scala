package SHACL
package model

import org.eclipse.rdf4j.model.{ IRI, Value, Literal }

sealed trait ShUnaryParameter extends ShParameter

object ShUnaryParameter {
  final case class ShPNodeKind(kind: IRI) extends ShUnaryParameter
  final case class ShPIn(in: Set[Value]) extends ShUnaryParameter
  final case class ShPClass(t: IRI) extends ShUnaryParameter
  final case class ShPDatatype(dt: IRI) extends ShUnaryParameter
  final case class ShPMinLength(ref: Int) extends ShUnaryParameter
  final case class ShPMaxLength(ref: Int) extends ShUnaryParameter

  // TODO: implement the rest of ShUnaryparameter
}
