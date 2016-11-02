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

  def invalidShpredicate(pred: String): String =
    s"Invalid `sh:predicate $pred`."

  def invalidShpath(path: String): String =
    s"Invalid `sh:path $path`."

  def moreThanOneShpredicate(pred: String): String =
    s"Found more than one `sh:predicate $pred`."

  def moreThanOneShpath(path: String): String =
    s"Found more than one `sh:path $path`."

  def emptyShPathConstraint(sourceShape: String): String =
    s"Found an empty path constraint of `$sourceShape`."

  val invalidShproperty: String =
    "Invalid `sh:property`."

  val oneOrMoreTriples: String =
    "One or more triples."

  val nodeKindMustBeIRI: String =
    "`sh:nodeKind` must be an IRI."

  val classMustBeIRI: String =
    "`sh:class` must be an IRI."

  val datatypeMustBeIRI: String =
    "`sh:datatype` must be an IRI."

  val minLengthMustBeNumber: String =
    "`sh:minLength` must be a number."

  val maxLengthMustBeNumber: String =
    "`sh:maxLength` must be a number."







  def emptyShPredicatePathConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def emptyShNodeConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def invalidShParameter(parm: String): String =
    s"Invalid `Sh:Parameter $parm`."
}
