package SHACL
package core

import CheckAbstraction.Check
import model.ShSchema
import org.eclipse.rdf4j.model.Model
import scala.collection.JavaConverters._

class Validator(data: Model, shape: Model) {

}

object Validator {

  // TOOD: implement the complete `def process`
  def process(data: Model, shape: Model) = {
    val processor = new Validator(data, shape)
  }
  

  def getShSchema(shape: Model): Check[ShSchema] = {
    val shapeParser = new ShapeParser(shape)
    val shSchema = shapeParser.extractShSchema

    // shapeParser.shape.filter(null, null, null).asScala.toList.foreach(println)  
    println(shSchema)

    shSchema
  }

  def main(args: Array[String]): Unit = {

    // Test shape graph, will be removed once done testing
    import org.eclipse.rdf4j.rio._
    val shape: Model = {
      val shapeInput = classOf[ShapeParser].getResourceAsStream("/shapeInput.ttl")
      val shape = Rio.parse(shapeInput, "", RDFFormat.TURTLE)
      if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
      shape
    }
    // !Test shape graph

    // Test data graph, will be removed once done testing
    import org.jsoup.Jsoup
    import org.jsoup.nodes.Document
    import com.github.jsonldjava.core.{ DocumentLoader, JsonLdOptions, JsonLdProcessor, RemoteDocument }
    import com.github.jsonldjava.utils.JsonUtils
    import java.io.StringReader
    val doc: Document = {
      val html: String =
        """
      <html>
        <head></head>
        <body>
          <script type="application/ld+json">
           {
             "@context": "http://schema.org",
             "@type": "Organization",
             "@id": "http://www.example.org/Organization",
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
      Jsoup.parse(html, "UTF-8")
    }

    val data: Model = {
      val options = {
        val loader = new DocumentLoader {
          override def loadDocument(url: String): RemoteDocument = url match {
            case "http://schema.org" =>
              val resource = classOf[Validator].getResourceAsStream("/sdo.jsonld")
              val sdo = JsonUtils.fromInputStream(resource)
              new RemoteDocument("http://schema.org", sdo)
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
    // !Test data graph //

    getShSchema(shape)

  }
}



