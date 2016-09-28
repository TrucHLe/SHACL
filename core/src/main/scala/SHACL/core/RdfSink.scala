package SHACL
package core

import com.github.jsonldjava.core.{ JsonLdTripleCallback, RDFDataset }
import org.eclipse.rdf4j.model.Model

// Only support default graph (no named graph)
class RdfSink(val model: Model) extends JsonLdTripleCallback {
  def call(dataset: RDFDataset): AnyRef = {
    import scala.collection.JavaConverters._
    import RdfSink._
    dataset.getQuads("@default").asScala.foreach { quad =>
      val s = quad.getSubject
      val p = quad.getPredicate
      val o = quad.getObject
      model.add(toResource(s), toIRI(p), toValue(o))
    }
    model
  }
}

object RdfSink {
  import com.github.jsonldjava.core.RDFDataset.Node
  import org.eclipse.rdf4j.model.{ Resource, IRI, Value }
  import org.eclipse.rdf4j.model.impl.{ SimpleValueFactory, LinkedHashModel }

  System.setProperty("com.github.jsonldjava.disallowRemoteContextLoading", "true")

  val factory = SimpleValueFactory.getInstance()

  def toResource(node: Node): Resource = {
    if (node.isIRI)
      factory.createIRI(node.getValue)
    else if (node.isBlankNode)
      factory.createBNode(node.getValue.substring(2))
    else
      throw new IllegalArgumentException(s"$node is not a valid Resource.")
  }

  def toIRI(node: Node): IRI = {
    if (node.isIRI)
      factory.createIRI(node.getValue)
    else
      throw new IllegalArgumentException(s"$node is not a valid IRI.")
  }

  def toValue(node: Node): Value = {
    if (node.isIRI)
      factory.createIRI(node.getValue)
    else if (node.isBlankNode)
      factory.createBNode(node.getValue.substring(2))
    else if (node.isLiteral) {
      val lexicalForm = node.getValue
      val lang = node.getLanguage
      val datatype = factory.createIRI(node.getDatatype)
      if (lang == null)
        factory.createLiteral(lexicalForm, datatype)
      else
        factory.createLiteral(lexicalForm, lang)
    }
    else
      throw new IllegalArgumentException(s"$node is not a valid Value.")
  }

  def apply(): RdfSink = new RdfSink(new LinkedHashModel)
}
