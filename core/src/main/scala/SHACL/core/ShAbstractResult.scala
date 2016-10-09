package SHACL
package core

import java.util.Date
import cats.Monoid
import org.eclipse.rdf4j.model.{ Resource, IRI, Value }

sealed trait ShConstraintComponent

object ShConstraintComponent {
  final case class ShAndConstraintComponent(and: Set[ShShape]) extends ShConstraintComponent
  final case class ShClassConstraintComponent(clss: Resource) extends ShConstraintComponent
  final case class ShClosedConstraintComponent(closed: Boolean, ignoredProperties: Set[RdfProperty]) extends ShConstraintComponent
  final case class ShDatatypeConstraintComponent(datatype: Resource) extends ShConstraintComponent
  final case class ShDisjointConstraintComponent(disjoint: Resource) extends ShConstraintComponent
  final case class ShEqualsConstraintComponent(equals: Resource) extends ShConstraintComponent
  final case class ShHasValueConstraintComponent(hasValue: Any) extends ShConstraintComponent
  final case class ShInConstraintComponent(in: Set[IRI]) extends ShConstraintComponent
  final case class ShLanguageInConstraintComponent(languageIn: Set[String]) extends ShConstraintComponent
  final case class ShLessThanConstraintComponent(lessThan: Resource) extends ShConstraintComponent
  final case class ShLessThanOrEqualsConstraintComponent(lessThanOrEquals: Resource) extends ShConstraintComponent
  final case class MaxCountConstraintComponent(maxCount: Integer) extends ShConstraintComponent
  final case class ShMaxExclusiveConstraintComponentS(maxExclusive: String) extends ShConstraintComponent
  final case class ShMaxExclusiveConstraintComponentB(maxExclusive: Boolean) extends ShConstraintComponent
  final case class ShMaxExclusiveConstraintComponentI(maxExclusive: Integer) extends ShConstraintComponent
  final case class ShMaxExclusiveConstraintComponentD(maxExclusive: Date) extends ShConstraintComponent
  final case class ShMaxInclusiveConstraintComponentS(maxInclusive: String) extends ShConstraintComponent
  final case class ShMaxInclusiveConstraintComponentB(maxInclusive: Boolean) extends ShConstraintComponent
  final case class ShMaxInclusiveConstraintComponentI(maxInclusive: Integer) extends ShConstraintComponent
  final case class ShMaxInclusiveConstraintComponentD(maxInclusive: Date) extends ShConstraintComponent
  final case class ShMaxLengthConstraintComponent(maxLength: Integer) extends ShConstraintComponent
  final case class ShMinCountConstraintComponent(minCount: Integer) extends ShConstraintComponent
  final case class ShMinExclusiveConstraintComponentS(minExclusive: String) extends ShConstraintComponent
  final case class ShMinExclusiveConstraintComponentB(minExclusive: Boolean) extends ShConstraintComponent
  final case class ShMinExclusiveConstraintComponentI(minExclusive: Integer) extends ShConstraintComponent
  final case class ShMinExclusiveConstraintComponentD(minExclusive: Date) extends ShConstraintComponent
  final case class ShMinInclusiveConstraintComponentS(minInclusive: String) extends ShConstraintComponent
  final case class ShMinInclusiveConstraintComponentB(minInclusive: Boolean) extends ShConstraintComponent
  final case class ShMinInclusiveConstraintComponentI(minInclusive: Integer) extends ShConstraintComponent
  final case class ShMinInclusiveConstraintComponentD(minInclusive: Date) extends ShConstraintComponent
  final case class ShMinLengthConstraintComponent(minLength: Integer) extends ShConstraintComponent
  final case class ShNodeKindConstraintComponent(nodeKind: ShNodeKind) extends ShConstraintComponent
  final case class ShNotConstraintComponent(not: ShShape) extends ShConstraintComponent
  final case class ShOrConstraintComponent(or: Set[ShShape]) extends ShConstraintComponent
  final case class ShPatternConstraintComponent(pattern: String, flags: Option[String]) extends ShConstraintComponent
  final case class ShStemConstraintComponent(stem: String) extends ShConstraintComponent
  final case class ShQualifiedMaxCountConstraintComponent(qualifiedMaxCount: Integer, qualifiedValueShape: ShShape) extends ShConstraintComponent
  final case class ShQualifiedMinCountConstraintComponent(qualifiedMinCount: Integer, qualifiedValueShape: ShShape) extends ShConstraintComponent
  final case class ShShapeConstraintComponetn(shape: ShShape) extends ShConstraintComponent
  final case class ShUniqueLangConstraintComponent(uniqueLang: Boolean) extends ShConstraintComponent
}

sealed trait ShSeverity

object ShSeverity {
  final case object ShInfo extends ShSeverity
  final case object ShViolation extends ShSeverity
  final case object ShWarning extends ShSeverity
}

sealed trait ShAbstractResult

object ShAbstractResult {
  // TODO: check if `detail` should be `String`
  final case class ShValidationResult(focusNode: IRI, path: IRI, value: Value, source: IRI, constraintComponent: ShConstraintComponent, detail: String, message: String, severity: ShSeverity) extends ShAbstractResult
  final case class ShValidationReport(results: Vector[ShAbstractResult]) extends ShAbstractResult

  implicit val monoid: Monoid[ShAbstractResult] = new Monoid[ShAbstractResult] {
    def empty: ShAbstractResult = ShValidationReport(Vector.empty)
    def combine(x: ShAbstractResult, y: ShAbstractResult): ShAbstractResult = (x, y) match {
      case (ShValidationReport(xs), ShValidationReport(ys)) => ShValidationReport(xs ++ ys)
      case (ShValidationReport(xs), ys) => ShValidationReport(xs :+ ys)
      case (xs, ShValidationReport(ys)) => ShValidationReport(xs +: ys)
      case _ => ShValidationReport(Vector(x, y))
    }
  }
}
