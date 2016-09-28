package SHACL
package core

import org.eclipse.rdf4j.model.Resource

final case class ShShape(
  label: Resource,
  targets: Set[ShTarget],
  filters: Set[ShShape],
  constraints: Set[ShTest]) {
  ???
}
