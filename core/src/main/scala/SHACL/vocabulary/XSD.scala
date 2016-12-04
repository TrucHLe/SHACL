package SHACL
package vocabulary

object XSD {
  import org.eclipse.rdf4j.model.IRI
  import core.RdfSink.factory.createIRI

  val xsd: String = "http://www.w3.org/2001/XMLSchema#"

  val string: IRI = createIRI(xsd, "string")
  val boolean: IRI = createIRI(xsd, "boolean")
  val integer: IRI = createIRI(xsd, "integer")
  val decimal: IRI = createIRI(xsd, "decimal")
}
