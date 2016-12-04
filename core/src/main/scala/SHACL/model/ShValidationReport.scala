package SHACL
package model

import cats.Monoid
import org.eclipse.rdf4j.model.{ Resource, IRI, Value }

sealed trait ShValidationReport

object ShValidationReport {
  // TODO: check if `detail` should be `String`
  final case class ShValidationResult(focusNode: Resource, path: Option[IRI], value: Option[Value], source: Option[Resource], constraintComponent: ShConstraintComponent, detail: Option[String], message: Option[String], severity: ShSeverity) extends ShValidationReport
  final case class ShValidationResults(results: Vector[ShValidationReport]) extends ShValidationReport

  implicit val monoid: Monoid[ShValidationReport] = new Monoid[ShValidationReport] {
    def empty: ShValidationReport = ShValidationResults(Vector.empty)
    def combine(x: ShValidationReport, y: ShValidationReport): ShValidationReport = (x, y) match {
      case (ShValidationResults(xs), ShValidationResults(ys)) => ShValidationResults(xs ++ ys)
      case (ShValidationResults(xs), ys) => ShValidationResults(xs :+ ys)
      case (xs, ShValidationResults(ys)) => ShValidationResults(xs +: ys)
      case _ => ShValidationResults(Vector(x, y))
    }
  }
}
