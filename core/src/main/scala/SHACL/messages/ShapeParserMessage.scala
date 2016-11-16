package SHACL
package messages

import vocabulary.RDF


object ShapeParserMessages {
  val invalidShtargetNode: String =
    "Invalid `sh:targetNode`."

  val invalidShtargetClass: String =
    "Invalid `sh:targetClass`."

  val invalidShtargetSubjectsOf: String =
    "Invalid `sh:targetSubjectsOf`."

  val invalidShtargetObjectsOf: String =
    "Invalid `sh:targetObjectsOf`."

  def invalidShConstraint(constraint: String): String =
    s"Invalid `sh:Constraint $constraint`."

  val shpredicateMustBeIRI: String =
    s"`sh:predicate` must be an IRI."

  val shpathMustBeIRIOrBlankNode: String =
    s"`sh:path` must be an IRI or blank node."

  val shpredicateMustHaveValue: String =
    "`sh:predicate` must have a value."

  val shpathMustHaveValue: String =
    "`sh:path` must have a value."

  val moreThanOneShpredicate: String =
    s"Found more than one `sh:predicate`."

  val moreThanOneShpath: String =
    s"Found more than one `sh:path`."

  val moreThanOneShinversePath: String =
    s"Found more than one `sh:inversePath`."

  val emptyShproperty: String =
    s"Empty `sh:property`."

  val shpropertyMustHaveValue: String =
    "`sh:property` must have a value."

  val shpropertyMustBeIRIOrBlankNode: String =
    "`sh:property` must be an IRI or blank node."

  val oneOrMoreTriples: String =
    "One or more triples."

  val shnodeKindMustBeShBlankNodeOrShIRIOrShLiteral: String =
    "`sh:nodeKind` must be `sh:BlankNode`, `sh:IRI`, or `sh:Literal`."

  val shinMustHaveOneOrMoreValues: String =
    "`sh:in` must have one or more values."

  val shclassMustBeIRI: String =
    "`sh:class` must be an IRI."

  val shclassMustHaveValue: String =
    "`sh:class` must have a value."

  val shdatatypeMustBeIRI: String =
    "`sh:datatype` must be an IRI."

  val shdatatypeMustHaveValue: String =
    "`sh:datatype` must have a value."

  val shminLengthMustBeNumber: String =
    "`sh:minLength` must be a number."

  val shmaxLengthMustBeNumber: String =
    "`sh:maxLength` must be a number."

  val rdfFirstMustBeIRI: String =
    "`rdf:first` must be an IRI."

  val rdfLastMustBeIRI: String =
    "`rdf:last` must be an IRI."

  val expectingTwoIRIsFoundOne: String =
    "Expecting two IRIs but found only one IRI."

  val expectingTwoIRIsFoundMoreThanTwo: String =
    "Expecting only two IRIs but found more than two IRIs."

  val shpathMustNotBeRDFnil: String =
    s"`sh:path` must not be ${RDF.nil}."




  def emptyShPredicatePathConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def emptyShNodeConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def invalidShParameter(parm: String): String =
    s"Invalid `Sh:Parameter $parm`."
}
