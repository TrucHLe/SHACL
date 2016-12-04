package SHACL
package model

sealed trait ShNodeKind

object ShNodeKind {
  final case object ShBlankNode extends ShNodeKind
  final case object ShBlankNodeOrIRI extends ShNodeKind
  final case object ShBlankNodeOrLiteral extends ShNodeKind
  final case object ShIRI extends ShNodeKind
  final case object ShIRIOrLiteral extends ShNodeKind
  final case object ShLiteral extends ShNodeKind
}
