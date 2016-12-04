package SHACL
package model

sealed trait ShSeverity

object ShSeverity {
  final case object ShInfo extends ShSeverity
  final case object ShViolation extends ShSeverity
  final case object ShWarning extends ShSeverity
}
