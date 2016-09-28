package SHACL
package core

import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.rio._
import scala.collection.JavaConverters._

class ShProcessor(data: Model, shape: Model) {
}

object ShProcessor {
  import org.jsoup.Jsoup
  import org.jsoup.nodes.Document
  val shapeInput = classOf[ShProcessor].getResourceAsStream("/shapeInput.ttl")
  val shape: Model = Rio.parse(shapeInput, "http://www.example.org/", RDFFormat.TURTLE)

  def process(data: Model, shape: Model) = {
    val processor = new ShProcessor(data, shape)
  }

  def main(args: Array[String]): Unit = {
    import com.github.jsonldjava.core.{ DocumentLoader, JsonLdOptions, JsonLdProcessor, RemoteDocument }
    import com.github.jsonldjava.utils.JsonUtils
    import java.io.StringReader

    val html: String =
      """
      <html>
        <head></head>
        <body>
          <script type="application/ld+json">
           {
             "@context": "http://schema.org",
             "@id": "http://www.example.org/Organization",
             "@type": "Organization",
             "url": "http://www.example.org",
             "telephone": "+1-012-345-6789",
             "logo": {
               "@type": "ImageObject",
               "url": "http.//www.images.example.org/image.png"
             }
           }
          </script>
        </body>
      </html>
      """
    val doc: Document = Jsoup.parse(html, "UTF-8")

    val data: Model = {
      val options = {
        val loader = new DocumentLoader {
          override def loadDocument(url: String): RemoteDocument = url match {
            case "http://schema.org" =>
              val schemaOntology = classOf[ShProcessor].getResourceAsStream("/schema.jsonld")
              val schema = JsonUtils.fromInputStream(schemaOntology)
              new RemoteDocument("http://schema.org", schema)
          }
        }
        val options = new JsonLdOptions
        options.setDocumentLoader(loader)
        options
      }
      val rdfSink: RdfSink = RdfSink()
      doc.select("""script[type="application/ld+json"]""").asScala.foreach { scriptElt =>
        val snippet = scriptElt.html()
        val reader = new StringReader(snippet)
        val jsonld = JsonUtils.fromReader(reader)
        JsonLdProcessor.toRDF(jsonld, rdfSink, options)
      }
      rdfSink.model
    }

    process(data, shape)
  }
}

