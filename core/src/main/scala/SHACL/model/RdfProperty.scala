package SHACL
package model

sealed trait RdfProperty

object RdfProperty {
  final case object RdfType extends RdfProperty
  final case object RdfSubClassOf extends RdfProperty
  final case object RdfSubPropertyOf extends RdfProperty
  final case object RdfComment extends RdfProperty
  final case object RdfLabel extends RdfProperty
  final case object RdfDomain extends RdfProperty
  final case object RdfRange extends RdfProperty
  final case object RdfSeeAlso extends RdfProperty
  final case object RdfIsDefinedBy extends RdfProperty
  final case object RdfSubject extends RdfProperty
  final case object RdfPredicate extends RdfProperty
  final case object RdfObject extends RdfProperty
  final case object RdfMemeber extends RdfProperty
  final case object RdfValue extends RdfProperty
  final case object RdfFirst extends RdfProperty
  final case object RdfRest extends RdfProperty
}
