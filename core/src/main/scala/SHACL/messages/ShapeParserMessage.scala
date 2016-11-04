package SHACL
package messages

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

  val moreThanOneShpredicate: String =
    s"Found more than one `sh:predicate`."

  val moreThanOneShpath: String =
    s"Found more than one `sh:path`."

  val emptyShproperty: String =
    s"Empty sh:property."

  val shpropertyMustBeIRIOrBlankNode: String =
    "sh:property must be an IRI or blank node."

  val oneOrMoreTriples: String =
    "One or more triples."

  val shnodeKindMustBeShBlankNodeOrShIRIOrShLiteral: String =
    "`sh:nodeKind` must be sh:BlankNode, sh:IRI, or sh:Literal."

  val shclassMustBeIRI: String =
    "`sh:class` must be an IRI."

  val shdatatypeMustBeIRI: String =
    "`sh:datatype` must be an IRI."

  val shminLengthMustBeNumber: String =
    "`sh:minLength` must be a number."

  val shmaxLengthMustBeNumber: String =
    "`sh:maxLength` must be a number."







  def emptyShPredicatePathConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def emptyShNodeConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def invalidShParameter(parm: String): String =
    s"Invalid `Sh:Parameter $parm`."
}
