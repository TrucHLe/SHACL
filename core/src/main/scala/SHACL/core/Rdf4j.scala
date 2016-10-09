package SHACL
package core

package object Rdf4j {
  import org.eclipse.rdf4j.model.{ BNode, IRI, Literal, Resource, Statement, Value }
  import org.eclipse.rdf4j.model.impl._

  object Resource {
    def unapply(value: Value): Option[Resource] =
      value match {
        case iri: IRI => Some(iri)
        case bnode: BNode => Some(bnode)
        case _ => None
      }
  }

  object IRI {
    def unapply(value: Value): Option[IRI] =
      value match {
        case iri: IRI => Some(iri)
        case _ => None
      }
  }

  object BNode {
    def unapply(value: Value): Option[BNode] =
      value match {
        case bnode: BNode => Some(bnode)
        case _ => None
      }
  }

  object Value {
    def unapply(value: Value): Option[Value] =
      value match {
        case iri: IRI => Some(iri)
        case bnode: BNode => Some(bnode)
        case literal: Literal => Some(literal)
        case _ => None
      }
  }

  object Literal {
    def unapply(value: Value): Option[(String, IRI)] =
      value match {
        case simpleLiteral: SimpleLiteral => Some((simpleLiteral.stringValue, simpleLiteral.getDatatype))
        case _ => None
      }
  }

  object Statement {
    def unapply(statement: Statement): Option[(Resource, IRI, Value)] =
      Some((statement.getSubject, statement.getPredicate, statement.getObject))
  }
}
