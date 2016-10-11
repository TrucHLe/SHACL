package SHACL
package core

object CheckAbstraction {
  import cats.data.Validated
  import org.eclipse.rdf4j.model.{ Resource, IRI, Value }
  import ShAbstractResult._
  import ShSeverity._

  type Check[+A] = Validated[ShAbstractResult, A]

  def checked[A](a: A): Check[A] =
    Validated.valid[ShAbstractResult, A](a)
  def unchecked(a: ShAbstractResult): Check[Nothing] =
    Validated.invalid[ShAbstractResult, Nothing](a)

  def info(focusNode: IRI, path: IRI, value: Value, source: IRI, constraintComponent: ShConstraintComponent, detail: String, message: String): Check[ShValidationResult] =
    checked(ShValidationResult(focusNode, path, value, source, constraintComponent, detail, message, ShInfo))
  def warning(focusNode: IRI, path: IRI, value: Value, source: IRI, constraintComponent: ShConstraintComponent, detail: String, message: String): Check[ShValidationResult] =
    checked(ShValidationResult(focusNode, path, value, source, constraintComponent, detail, message, ShWarning))
  def violation(focusNode: IRI, path: IRI, value: Value, source: IRI, constraintComponent: ShConstraintComponent, detail: String, message: String): Check[Nothing] =
    unchecked(ShValidationResult(focusNode, path, value, source, constraintComponent, detail, message, ShViolation))
}
