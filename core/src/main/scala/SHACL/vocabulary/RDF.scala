package SHACL
package vocabulary

object RDF {
  import org.eclipse.rdf4j.model.IRI
  import core.RdfSink.factory.createIRI

  val rdf: String = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  val rdfs: String = "http://www.w3.org/2000/01/rdf-schema#"
  val ty: IRI = createIRI(rdf, "type")
  val subClassOf: IRI = createIRI(rdf, "subClassOf")
  val subPropertyOf: IRI = createIRI(rdf, "subPropertyOf")
  val comment: IRI = createIRI(rdf, "comment")
  val label: IRI = createIRI(rdf, "label")
  val domain: IRI = createIRI(rdf, "domain")
  val range: IRI = createIRI(rdf, "range")
  val seeAlso: IRI = createIRI(rdf, "seeAlso")
  val isDefinedBy: IRI = createIRI(rdf, "isDefinedBy")
  val subject: IRI = createIRI(rdf, "subject")
  val predicate: IRI = createIRI(rdf, "predicate")
  val objct: IRI = createIRI(rdf, "object")
  val member: IRI = createIRI(rdf, "member")
  val value: IRI = createIRI(rdf, "value")
  val first: IRI = createIRI(rdf, "first")
  val rest: IRI = createIRI(rdf, "rest")
  val nil: IRI = createIRI(rdf, "nil")
}
