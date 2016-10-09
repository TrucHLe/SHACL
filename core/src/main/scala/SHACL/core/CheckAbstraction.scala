package SHACL
package core

object CheckAbstraction {
  import cats.data.Validated
  import ShAbstractResult._

  type Check[+A] = Validated[ShSeverity, A]

  def checked[A](a: A): Check[A] =
    Validated.valid[ShSeverity, A](a)
  def unchecked(a: ShViolation): Check[Nothing] =
    Validated.invalid[ShViolation, Nothing](a)
  def info(message: String): Check[ShInfo] =
    checked(ShInfo(message, None))
  def info(message: String, found: String): Check[ShInfo] =
    checked(ShInfo(message, Some(found)))
  def warning(message: String): Check[ShWarning] =
    checked(ShWarning(message, None, None))
  def warningF(message: String, found: String): Check[ShWarning] =
    checked(ShWarning(message, Some(found), None))
  def warningE(message: String, expected: String): Check[ShWarning] =
    checked(ShWarning(message, None, Some(expected)))
  def warning(message: String, found: String, expected: String): Check[ShWarning] =
    checked(ShWarning(message, Some(found), Some(expected)))
  def violation(message: String): Check[Nothing] =
    unchecked(ShViolation(message, None, None))
  def violationF(message: String, found: String): Check[Nothing] =
    unchecked(ShViolation(message, Some(found), None))
  def violationE(message: String, expected: String): Check[Nothing] =
    unchecked(ShViolation(message, None, Some(expected)))
  def violation(message: String, found: String, expected: String): Check[Nothing] =
    unchecked(ShViolation(message, Some(found), Some(expected)))
}
