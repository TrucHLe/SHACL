/*
 * W3C Shapes Constraint Language (SHACL) Vocabulary
 * Draft last edited 2016-09-22
 */

package SHACL
package vocabulary

object SH {
  import org.eclipse.rdf4j.model.IRI
  import core.RdfSink.factory.createIRI

  val sh: String = "http://www.w3.org/ns/shacl#"

  /*** Shapes vocabulary ***/

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


  /*** SPARQL execution support ***/

  val SPARQLExecutable: IRI = createIRI(sh, "SPARQLExecutable")
  val SPARQLAskExecutable: IRI = createIRI(sh, "SPARQLAskExecutable")
  val ask: IRI = createIRI(sh, "ask")
  val SPARQLConstructExecutable: IRI = createIRI(sh, "SPARQLConstructExecutable")
  val construct: IRI = createIRI(sh, "construct")
  val SPARQLSelectExecutable: IRI = createIRI(sh, "SPARQLSelectExecutable")
  val select: IRI = createIRI(sh, "select")
  val SPARQLUpdateExecutable: IRI = createIRI(sh, "SPARQLUpdateExecutable")
  val update: IRI = createIRI(sh, "update")
  val prefixes: IRI = createIRI(sh, "prefixes")
  val PrefixDeclaration: IRI = createIRI(sh, "PrefixDeclaration")
  val declare: IRI = createIRI(sh, "declare")
  val prefix: IRI = createIRI(sh, "prefix")
  val namespace: IRI = createIRI(sh, "namespace")


  /*** Target vocabulary ***/

  val Target: IRI = createIRI(sh, "Target")
  val SPARQLTarget: IRI = createIRI(sh, "SPARQLTarget")
  val TargetType: IRI = createIRI(sh, "TargetType")
  val SPARQLTargetType: IRI = createIRI(sh, "SPARQLTargetType")


  /*** Path vocabulary ***/

  val path: IRI = createIRI(sh, "path")
  val inversePath: IRI = createIRI(sh, "inversePath")
  val alternativePath: IRI = createIRI(sh, "alternativePath")
  val zeroOrMorePath: IRI = createIRI(sh, "zeroOrMorePath")
  val oneOrMorePath: IRI = createIRI(sh, "oneOrMorePath")
  val zeroOrOnePath: IRI = createIRI(sh, "zeroOrOnePath")


  /*** Parameters metamodel ***/

  val Parameterizable: IRI = createIRI(sh, "Parameterizable")
  val parameter: IRI = createIRI(sh, "parameter")
  val labelTemplate: IRI = createIRI(sh, "labelTemplate")
  val Parameter: IRI = createIRI(sh, "Parameter")
  val optional: IRI = createIRI(sh, "optional")


  /*** Constraint components metamodel ***/

  val ConstraintComponent: IRI = createIRI(sh, "ConstraintComponent")
  val validator: IRI = createIRI(sh, "validator")
  val shapeValidator: IRI = createIRI(sh, "shapeValidator")
  val propertyValidator: IRI = createIRI(sh, "propertyValidator")
  val Validator: IRI = createIRI(sh, "Validator")
  val SPARQLAskValidator: IRI = createIRI(sh, "SPARQLAskValidator")
  val SPARQLSelectValidator: IRI = createIRI(sh, "SPARQLSelectValidator")
  val SPARQLConstraint: IRI = createIRI(sh, "SPARQLConstraint")
  val sparql: IRI = createIRI(sh, "sparql")

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


  /*** Derived values support ***/

  val DerivedValuesConstraintComponent: IRI = createIRI(sh, "DerivedValuesConstraintComponent")
  val derivedValues: IRI = createIRI(sh, "derivedValues")
  val ValuesDeriver: IRI = createIRI(sh, "ValuesDeriver")
  val SPARQLValuesDeriver: IRI = createIRI(sh, "SPARQLValuesDeriver")


  /*** Non-validating constraint properties ***/

  val defaultValue: IRI = createIRI(sh, "defaultValue")
  val description: IRI = createIRI(sh, "description")
  val group: IRI = createIRI(sh, "group")
  val name: IRI = createIRI(sh, "name")
  val order: IRI = createIRI(sh, "order")
  val PropertyGroup: IRI = createIRI(sh, "PropertyGroup")


  /*** Functions vocabulary ***/

  val Function: IRI = createIRI(sh, "Function")
  val returnType: IRI = createIRI(sh, "returnType")
  val SPARQLFunction: IRI = createIRI(sh, "SPARQLFunction")
  val hasShape: IRI = createIRI(sh, "hasShape")


  /*** Result annotations ***/

  val resultAnnotation: IRI = createIRI(sh, "resultAnnotation")
  val ResultAnnotation: IRI = createIRI(sh, "ResultAnnotation")
  val annotationProperty: IRI = createIRI(sh, "annotationProperty")
  val annotationValue: IRI = createIRI(sh, "annotationValue")
  val annotationVarName: IRI = createIRI(sh, "annotationVarName")
}
