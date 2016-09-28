package SHACL
package core

final case class ShSchemaExtractor() {
  import org.eclipse.rdf4j.rio._
  import org.eclipse.rdf4j.model.Model

  val shape: Model = {
    val shapeInput = classOf[ShSchemaExtractor].getResourceAsStream("/shapeInput.ttl")
    val shape = Rio.parse(shapeInput, "", RDFFormat.TURTLE)
    if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
    shape
  }

  val extractShSchema: ShSchema = ???

  val extractShShape: ShShape = ???

  val extractShTest: ShTest = ???
}
