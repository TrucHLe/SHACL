package SHACL
package model

sealed trait ShPropertyPath

object ShPropertyPath {
  final case object ShPredicatePath extends ShPropertyPath
  final case object ShInversePath extends ShPropertyPath
  final case object ShSequencePath extends ShPropertyPath
  final case object ShAlternativePath extends ShPropertyPath
  final case object ShZeroOrMorePath extends ShPropertyPath
  final case object ShOneOrMorePath extends ShPropertyPath
}
