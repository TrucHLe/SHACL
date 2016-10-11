package SHACL
package core

import org.eclipse.rdf4j.model.{ Resource, IRI, Value }

final case class ShSchema(shapes: Set[ShShape])

final case class ShShape(label: Resource, targets: Set[ShTarget], filters: Set[ShShape], constraints: Set[ShTest])

sealed trait ShTarget

object ShTarget {
  final case class ShTargetNode(node: Value) extends ShTarget
  final case class ShTargetClass(classType: IRI) extends ShTarget
  final case class ShTargetSubjectsOf(predicate: IRI) extends ShTarget
  final case class ShTargetObjectsOf(predicate: IRI) extends ShTarget
}

sealed trait ShTest
sealed trait ShConstraint extends ShTest
sealed trait ShAlgebraic extends ShTest

object ShConstraint {
  final case class ShNodeConstraint(parms: Set[ShParameter]) extends ShConstraint
  final case class ShPathConstraint(path: ShPropertyPath, parms: Set[ShParameter]) extends ShConstraint
}

object ShAlgebraic {
  final case class ShAnd(shapes: Set[ShShape]) extends ShAlgebraic
  final case class ShOr(shapes: Set[ShShape]) extends ShAlgebraic
  final case class ShNot(shape: ShShape) extends ShAlgebraic
}

sealed trait ShParameter
sealed trait ShUnaryParameter extends ShParameter
sealed trait ShNaryParameter extends ShParameter

object ShUnaryParameter {
  final case class ShPredicate(pred: IRI) extends ShUnaryParameter
  final case class ShNodeKind(kind: Value) extends ShUnaryParameter
  final case class ShIn(in: Set[Value]) extends ShUnaryParameter
  final case class ShClass(t: IRI) extends ShUnaryParameter
  final case class ShDatatype(dt: IRI) extends ShUnaryParameter
  final case class ShMinLength(ref: Int) extends ShUnaryParameter
  final case class ShMaxLength(ref: Int) extends ShUnaryParameter

  // TODO: implement the rest of ShUnaryparameter
}

// TODO: implement ShNaryparameter
object ShNaryParameter {

}

sealed trait ShPropertyPath

object ShPropertyPath {
  final case object ShPredicatePath extends ShPropertyPath
  final case object ShInversePath extends ShPropertyPath
  final case object ShSequencePath extends ShPropertyPath
  final case object ShAlternativePath extends ShPropertyPath
  final case object ShZeroOrMorePath extends ShPropertyPath
  final case object ShOneOrMorePath extends ShPropertyPath
}
