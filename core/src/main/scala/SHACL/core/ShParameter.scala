package SHACL
package core

sealed trait ShParameter
sealed trait ShUnaryParameter extends ShParameter
sealed trait ShNaryParameter extends ShParameter

object ShUnaryParameter {

}

object NaryParameter {

}
