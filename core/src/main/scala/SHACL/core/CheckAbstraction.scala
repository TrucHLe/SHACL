package SHACL
package core

object CheckAbstraction {
  import cats.data.Validated
  import org.eclipse.rdf4j.model.{ Resource, IRI, Value }
  import model.{ ShValidationReport, ShConstraintComponent }
  import model.ShValidationReport.ShValidationResult
  import model.ShSeverity._

  type Check[+A] = Validated[ShValidationReport, A]

  def checked[A](a: A): Check[A] =
    Validated.valid[ShValidationReport, A](a)
  def unchecked(a: ShValidationReport): Check[Nothing] =
    Validated.invalid[ShValidationReport, Nothing](a)

  def info(focusNode: Resource, path: Option[IRI], value: Option[Value], source: Option[Resource], constraintComponent: ShConstraintComponent, detail: Option[String], message: Option[String]): Check[ShValidationResult] =
    checked(ShValidationResult(focusNode, path, value, source, constraintComponent, detail, message, ShInfo))
  def warning(focusNode: Resource, path: Option[IRI], value: Option[Value], source: Option[Resource], constraintComponent: ShConstraintComponent, detail: Option[String], message: Option[String]): Check[ShValidationResult] =
    checked(ShValidationResult(focusNode, path, value, source, constraintComponent, detail, message, ShWarning))
  def violation(focusNode: Resource, path: Option[IRI], value: Option[Value], source: Option[Resource], constraintComponent: ShConstraintComponent, detail: Option[String], message: Option[String]): Check[Nothing] =
    unchecked(ShValidationResult(focusNode, path, value, source, constraintComponent, detail, message, ShViolation))
}
