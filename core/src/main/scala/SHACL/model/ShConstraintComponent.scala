package SHACL
package model

import java.util.Date
import org.eclipse.rdf4j.model.{ Resource, IRI }

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
  final case class ShMaxCountConstraintComponent(maxCount: Integer) extends ShConstraintComponent
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
  final case class ShShapeConstraintComponent(shape: ShShape) extends ShConstraintComponent
  final case class ShUniqueLangConstraintComponent(uniqueLang: Boolean) extends ShConstraintComponent
}
