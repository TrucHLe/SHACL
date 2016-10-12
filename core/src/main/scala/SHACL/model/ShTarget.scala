package SHACL
package model

import org.eclipse.rdf4j.model.{ IRI, Value }

sealed trait ShTarget

object ShTarget {
  final case class ShTargetNode(node: Value) extends ShTarget
  final case class ShTargetClass(classType: IRI) extends ShTarget
  final case class ShTargetSubjectsOf(predicate: IRI) extends ShTarget
  final case class ShTargetObjectsOf(predicate: IRI) extends ShTarget
}
