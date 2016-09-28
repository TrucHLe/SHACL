package SHACL
package core

package object Rdf4j {
  import org.eclipse.rdf4j.model.{ Resource, IRI, Value, BNode, Statement }
  import org.eclipse.rdf4j.model.impl._

  object IRI {
    def unapply(value: Value): Option[IRI] = {
      value match {
        case iri: IRI => Some(iri)
        case _ => None
      }
    }
  }

  object Literal {
    def unapply(value: Value): Option[(String, IRI)] = {
      ???
    }
  }

  object Resource {
    def unapply(value: Value): Option[Resource] = {
      value match {
        case iri: IRI => Some(iri)
        case bnode: BNode => Some(bnode)
        case _ => None
      }
    }
  }

  object Statement {
    def unapply(statement: Statement): Option[(Resource, IRI, Value)] =
      Some((statement.getSubject, statement.getPredicate, statement.getObject))
  }
}
