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

  def emptyShPredicatePathConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def emptyShPathConstraint(path: String): String =
    s"Found no constraint of `sh:path $path`."

  def emptyShNodeConstraint(pred: String): String =
    s"Found no constraint of `sh:predicate $pred`."

  def invalidShParameter(parm: String): String =
    s"Invalid `Sh:Parameter $parm`."
}
