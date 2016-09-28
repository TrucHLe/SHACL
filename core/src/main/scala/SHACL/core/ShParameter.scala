package SHACL
package core

sealed trait ShParameter

object ShParameter {
  final case object ShUnaryParameter extends ShParameter
  final case object ShNaryParameter extends ShParameter
}
