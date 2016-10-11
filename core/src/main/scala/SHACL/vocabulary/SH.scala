package SHACL
package vocabulary

object SH {
  import org.eclipse.rdf4j.model.IRI
  import core.RdfSink.factory.createIRI

  val sh: String = "http://www.w3.org/ns/shacl#"

  /* Shapes vocabulary */

  val Constraint: IRI = createIRI(sh, "Constraint")
  val Shape: IRI = createIRI(sh, "Shape")
  val filterShape: IRI = createIRI(sh, "filterShape")
  val target: IRI = createIRI(sh, "target")
  val targetClass: IRI = createIRI(sh, "targetClass")
  val targetNode: IRI = createIRI(sh, "targetNode")
  val targetObjectsOf: IRI = createIRI(sh, "targetObjectsOf")
  val targetSubjectsOf: IRI = createIRI(sh, "targetSubjectsOf")
  val property: IRI = createIRI(sh, "property")
  val PropertyConstraint: IRI = createIRI(sh, "PropertyConstraint")
  val predicate: IRI = createIRI(sh, "predicate")


  /*** Node kind vocabulary ***/

  val NodeKind: IRI = createIRI(sh, "NodeKind")
  val BlankNode: IRI = createIRI(sh, "BlankNode")
  val BlankNodeOrIRI: IRI = createIRI(sh, "BlankNodeOrIRI")
  val BlankNodeOrLiteral: IRI = createIRI(sh, "BlankNodeOrLiteral")
  val IRI: IRI = createIRI(sh, "IRI")
  val IRIOrLiteral: IRI = createIRI(sh, "IRIOrLiteral")
  val Literal: IRI = createIRI(sh, "Literal")


  /*** Results vocabulary ***/

  val AbstractResult: IRI = createIRI(sh, "AbstractResult")
  val ValidationResult: IRI = createIRI(sh, "ValidationResult")
  val Severity: IRI = createIRI(sh, "Severity")
  val Info: IRI = createIRI(sh, "Info")
  val Violation: IRI = createIRI(sh, "Violation")
  val Warning: IRI = createIRI(sh, "Warning")
  val detail: IRI = createIRI(sh, "detail")
  val focusNode: IRI = createIRI(sh, "focusNode")
  val message: IRI = createIRI(sh, "message")
  val severity: IRI = createIRI(sh, "severity")
  val sourceConstraint: IRI = createIRI(sh, "sourceConstraint")
  val sourceShape: IRI = createIRI(sh, "sourceShape")
  val sourceConstraintComponent: IRI = createIRI(sh, "sourceConstraintComponent")
  val value: IRI = createIRI(sh, "value")


  /* Target vocabulary */


  /* Path vocabulary */

  val path: IRI = createIRI(sh, "path")


  /* Parameters metamodel */


  /* Constraint components metamodel */

  val ConstraintComponent: IRI = createIRI(sh, "ConstraintComponent")


  /*** Library of core constraint components and their properties ***/

  val AndConstraintComponent: IRI = createIRI(sh, "AndConstraintComponent")
  val and: IRI = createIRI(sh, "and")
  val ClassConstraintComponent: IRI = createIRI(sh, "ClassConstraintComponent")
  val clss: IRI = createIRI(sh, "class")
  val ClosedConstraintComponent: IRI = createIRI(sh, "ClosedConstraintComponent")
  val closed: IRI = createIRI(sh, "closed")
  val ignoredProperties: IRI = createIRI(sh, "ignoredProperties")
  val DatatypeConstraintComponent: IRI = createIRI(sh, "DatatypeConstraintComponent")
  val datatype: IRI = createIRI(sh, "datatype")
  val DisjointConstraintComponent: IRI = createIRI(sh, "DisjointConstraintComponent")
  val disjoint: IRI = createIRI(sh, "disjoint")
  val EqualsConstraintComponent: IRI = createIRI(sh, "EqualsConstraintComponent")
  val equals: IRI = createIRI(sh, "equals")
  val HasValueConstraintComponent: IRI = createIRI(sh, "HasValueConstraintComponent")
  val hasValue: IRI = createIRI(sh, "hasValue")
  val InConstraintComponent: IRI = createIRI(sh, "InConstraintComponent")
  val in: IRI = createIRI(sh, "in")
  val LanguageInConstraintComponent: IRI = createIRI(sh, "LanguageInConstraintComponent")
  val languageIn: IRI = createIRI(sh, "languageIn")
  val LessThanConstraintComponent: IRI = createIRI(sh, "LessThanConstraintComponent")
  val lessThan: IRI = createIRI(sh, "lessThan")
  val LessThanOrEqualsConstraintComponent: IRI = createIRI(sh, "LessThanOrEqualsConstraintComponent")
  val lessThanOrEquals: IRI = createIRI(sh, "lessThanOrEquals")
  val MaxCountConstraintComponent: IRI = createIRI(sh, "MaxCountConstraintComponent")
  val maxCount: IRI = createIRI(sh, "maxCount")
  val MaxExclusiveConstraintComponent: IRI = createIRI(sh, "MaxExclusiveConstraintComponent")
  val maxExclusive: IRI = createIRI(sh, "maxExclusive")
  val MaxInclusiveConstraintComponent: IRI = createIRI(sh, "MaxInclusiveConstraintComponent")
  val maxInclusive: IRI = createIRI(sh, "maxInclusive")
  val MaxLengthConstraintComponent: IRI = createIRI(sh, "MaxLengthConstraintComponent")
  val maxLength: IRI = createIRI(sh, "maxLength")
  val MinCountConstraintComponent: IRI = createIRI(sh, "MinCountConstraintComponent")
  val minCount: IRI = createIRI(sh, "minCount")
  val MinExclusiveConstraintComponent: IRI = createIRI(sh, "MinExclusiveConstraintComponent")
  val minExclusive: IRI = createIRI(sh, "minExclusive")
  val MinInclusiveConstraintComponent: IRI = createIRI(sh, "MinInclusiveConstraintComponent")
  val minInclusive: IRI = createIRI(sh, "minInclusive")
  val MinLengthConstraintComponent: IRI = createIRI(sh, "MinLengthConstraintComponent")
  val minLength: IRI = createIRI(sh, "minLength")
  val NodeKindConstraintComponent: IRI = createIRI(sh, "NodeKindConstraintComponent")
  val nodeKind: IRI = createIRI(sh, "nodeKind")
  val NotConstraintComponent: IRI = createIRI(sh, "NotConstraintComponent")
  val not: IRI = createIRI(sh, "not")
  val OrConstraintComponent: IRI = createIRI(sh, "OrConstraintComponent")
  val or: IRI = createIRI(sh, "or")
  val PatternConstraintComponent: IRI = createIRI(sh, "PatternConstraintComponent")
  val pattern: IRI = createIRI(sh, "pattern")
  val flags: IRI = createIRI(sh, "flags")
  val StemConstraintComponent: IRI = createIRI(sh, "StemConstraintComponent")
  val stem: IRI = createIRI(sh, "stem")
  val QualifiedMaxCountConstraintComponent: IRI = createIRI(sh, "QualifiedMaxCountConstraintComponent")
  val qualifiedMaxCount: IRI = createIRI(sh, "qualifiedMaxCount")
  val qualifiedValueShape: IRI = createIRI(sh, "qualifiedValueShape")
  val QualifiedMinCountConstraintComponent: IRI = createIRI(sh, "QualifiedMinCountConstraintComponent")
  val qualifiedMinCount: IRI = createIRI(sh, "qualifiedMinCount")
  val ShapeConstraintComponent: IRI = createIRI(sh, "ShapeConstraintComponent")
  val shape: IRI = createIRI(sh, "shape")
  val UniqueLangConstraintComponent: IRI = createIRI(sh, "UniqueLangConstraintComponent")
  val uniqueLang: IRI = createIRI(sh, "uniqueLang")


  /*** Derived values support: Not Implemented ***/


  /* Non-validating constraint properties */


  /* Functions vocabulary */


  /* Result annotations */
}
