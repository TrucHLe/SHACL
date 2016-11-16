package SHACL
package model

import org.eclipse.rdf4j.model.IRI

sealed trait ShPropertyPath

object ShPropertyPath {
  final case class ShPredicatePath(path: IRI) extends ShPropertyPath
  final case class ShInversePath(path: IRI) extends ShPropertyPath
  final case class ShSequencePath(first: IRI, last: IRI) extends ShPropertyPath
  final case class ShAlternativePath(first: IRI, last: IRI) extends ShPropertyPath
  final case class ShZeroOrMorePath(path: IRI) extends ShPropertyPath
  final case class ShOneOrMorePath(path: IRI) extends ShPropertyPath
}
