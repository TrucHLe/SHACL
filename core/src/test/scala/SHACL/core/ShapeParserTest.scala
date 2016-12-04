package SHACL
package core

import java.io.StringReader
import org.eclipse.rdf4j.model.{ IRI, Literal, Model }
import org.eclipse.rdf4j.rio.{ RDFFormat, Rio }
import org.scalatest.WordSpec
import cats.data.Validated.{ Valid, Invalid }
import messages.ShapeParserMessages._
import core.RdfSink.factory.{ createIRI, createLiteral }
import vocabulary.{ SH, XSD, RDF }
import model.ShSchema
import model.ShConstraint._
import model.ShUnaryParameter._
import model.ShValidationReport._
import model.ShConstraintComponent._
import model.ShNodeKind._
import model.ShSeverity._
import model.ShPropertyPath._

class ShapeParserTest extends WordSpec {
  val ex: String = "http://www.example.org/ex#"
  val exRed: IRI = createIRI(ex, "Red")
  val exYellow: IRI = createIRI(ex, "Yellow")
  val exGreen: IRI = createIRI(ex, "Green")
  val exStat: IRI = createIRI(ex, "stat")
  val exState: IRI = createIRI(ex, "state")
  val exStatus: IRI = createIRI(ex, "status")
  val literal24: Literal = createLiteral("24", XSD.integer)
  val literal42: Literal = createLiteral("42", XSD.integer)

  "extractValueList" should {
    "return a value set of all elements" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:in (ex:Red ex:Yellow ex:Green) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPIn(in) = parms.head
      assert(Set(exRed, exYellow, exGreen) == in)
    }
  }

  "extractIRIPair" should {
    "return an IRI pair" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (ex:Red ex:Green) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(ShAlternativePath(first, last), _) = shapes.head.constraints.head
      assert(exRed == first)
      assert(exGreen == last)
    }
    "return violation if the first IRI is rdf:nil" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (rdf:nil ex:Green) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.first == path)
      assert(RDF.nil == value)
      assert(shPathMustNotBeRDFnil == msg)
    }
    "return violation if there is one IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (ex:Red) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.rest == path)
      assert(RDF.nil == value)
      assert(expectingTwoIRIsFoundOne == msg)
    }
    "return violation if the first is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (42) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.first == path)
      assert(literal42 == value)
      assert(rdfFirstMustBeIRI == msg)
    }
    "return violation if the second IRI is rdf:nil" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (ex:Red rdf:nil) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.first == path)
      assert(RDF.nil == value)
      assert(shPathMustNotBeRDFnil == msg)
    }
    "return violation if there is more than two IRIs" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (ex:Red ex:Green ex:Yellow) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.rest == path)
      assert(expectingTwoIRIsFoundMoreThanTwo == msg)
    }
    "return violation if the second is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (ex:Red 42) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.first == path)
      assert(literal42 == value)
      assert(rdfFirstMustBeIRI == msg)
    }
  }


  // TODO: fill in missing tests


  "extractShPNodeKind" should {
    "return an sh:nodeKind that is sh:BlankNode" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:nodeKind sh:BlankNode ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPNodeKind(kind) = parms.head
      assert(SH.BlankNode == kind)
    }
    "return an sh:nodeKind that is sh:IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:nodeKind sh:IRI ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPNodeKind(kind) = parms.head
      assert(SH.IRI == kind)
    }
    "return an sh:nodeKind that is sh:Literal" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:nodeKind sh:Literal ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPNodeKind(kind) = parms.head
      assert(SH.Literal == kind)
    }
    "return violation if sh:nodeKind is not sh:BlankNode, sh:IRI, or sh:Literal" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:nodeKind ex:Red ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.nodeKind == path)
      assert(exRed == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shNodeKindMustBeShBlankNodeOrShIRIOrShLiteral == msg)
    }
  }

  "extractShPIn" should {
    "return violation if sh:in is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:in () ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.in == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shInMustHaveOneOrMoreValues == msg)
    }
    "return an sh:in that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:in ex:Green ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPIn(in) = parms.head
      assert(Set(exGreen) == in)
    }
    "return an sh:in that is a literal" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:in 42 ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPIn(in) = parms.head
      assert(Set(literal42) == in)
    }
    "return an sh:in that is a set of IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:in (ex:Red ex:Green) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPIn(in) = parms.head
      assert(Set(exRed, exGreen) == in)
    }
    "return an sh:in that is a set of literal" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:in (24 42) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPIn(in) = parms.head
      assert(Set(literal24, literal42) == in)
    }
    "return an sh:in that is a set of IRI and literal" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:in (ex:Green 42 42) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPIn(in) = parms.head
      assert(Set(exGreen, literal42) == in)
    }
  }

  "extractShPClass" should {
    "return violation if sh:class is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:class () ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.clss == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shClassMustNotBeRDFnil == msg)
    }
    "return an sh:class that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:class ex:Green ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPClass(t) = parms.head
      assert(exGreen == t)
    }
    "return violation if sh:class is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:class 42 ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.clss == path)
      assert(literal42 == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shClassMustBeIRI == msg)
    }
  }

  "extractShPDatatype" should {
    "return violation if sh:datatype is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:datatype () ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.datatype == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shDatatypeMustNotBeRDFnil == msg)
    }
    "return an sh:datatype that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:datatype ex:Green ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPDatatype(dt) = parms.head
      assert(exGreen == dt)
    }
    "return violation if sh:datatype is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:datatype 42 ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.datatype == path)
      assert(literal42 == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shDatatypeMustBeIRI == msg)
    }
  }

  "extractShPMinLength" should {
    "return an sh:minLength that is a number" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:minLength 42 ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPMinLength(ref) = parms.head
      assert(42 == ref)
    }
    "return violation if sh:minLength is not a number" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:minLength ex:Green ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.minLength == path)
      assert(exGreen == value)
      assert(ShNodeKindConstraintComponent(ShLiteral) == conCom)
      assert(shMinLengthMustBeNumber == msg)
    }
  }

  "extractShPMaxLength" should {
    "return an sh:maxLength that is a number" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:maxLength 42 ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      val ShPMaxLength(ref) = parms.head
      assert(42 == ref)
    }
    "return violation if sh:maxLength is not a number" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:maxLength ex:Green ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.maxLength == path)
      assert(exGreen == value)
      assert(ShNodeKindConstraintComponent(ShLiteral) == conCom)
      assert(shMaxLengthMustBeNumber == msg)
    }
  }

  "extractSetShUnaryParameter" should {
    "return a set of unary parameters" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:minLength 24 ;
              sh:maxLength 42 ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(_, parms) = shapes.head.constraints.head
      assert(Set(ShPMinLength(24), ShPMaxLength(42)) == parms)
    }
    "return violation if there is 1 violation" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:minLength ex:Green ;
              sh:maxLength 42 ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, _, _, _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(shMinLengthMustBeNumber == msg)
    }
    "return accumulated violations if there are more than 1 violation" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:minLength ex:Green ;
              sh:maxLength ex:Red ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResults(Vector(
        ShValidationResult(_, _, _, _, _, _, Some(msg1), _),
        ShValidationResult(_, _, _, _, _, _, Some(msg2), _)
      ))) = Validator.getShSchema(shape)
      assert(shMinLengthMustBeNumber == msg1)
      assert(shMaxLengthMustBeNumber == msg2)
    }
  }

  // TODO
  "extractSetShNaryParameter" should {
    "return a set of n-ary parameters" in {

    }
    "return violation if there is 1 violation" in {

    }
    "return accumulated violations if there are more than 1 violation" in {

    }
  }

  // TODO after completing extractSetShNaryParameter
  "extractSetShParameter" should {
    "return a set of unary and n-ary parameters" in {

    }
    "return violation if there is 1 violation" in {

    }
    "return accumulated violations if there are more than 1 violation" in {

    }
  }

  "extractSequencePath" should {
    "return an sh:sequencePath" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (ex:state ex:status) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShSequencePath(exState, exStatus) == path)
    }
  }

  "extractZeroOneOrMorePath" should {
    "return violation if sh:zeroOrMorePath is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:zeroOrMorePath ()]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.zeroOrMorePath == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shZeroOrMorePathMustNotBeRDFnil == msg)
    }
    "return an sh:zeroOrMorePath that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:zeroOrMorePath ex:state]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShZeroOrMorePath(exState) == path)
    }
    "return violation if sh:zeroOrMorePath is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:zeroOrMorePath 42]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.zeroOrMorePath == path)
      assert(literal42 == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shZeroOrMorePathMustBeIRI == msg)
    }
    "return violation if sh:oneOrMorePath is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:oneOrMorePath ()]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.oneOrMorePath == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shOneOrMorePathMustNotBeRDFnil == msg)
    }
    "return an sh:oneOrMorePath that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:oneOrMorePath ex:state]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShOneOrMorePath(exState) == path)
    }
    "return violation if sh:oneOrMorePath is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:oneOrMorePath (ex:Red ex:Green)]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.oneOrMorePath == path)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shOneOrMorePathMustBeIRI == msg)
    }
    "return violation if sh:zeroOrOnePath is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:zeroOrOnePath ()]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.zeroOrOnePath == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shZeroOrOnePathMustNotBeRDFnil == msg)
    }
    "return an sh:zeroOrOnePath that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:zeroOrOnePath ex:state]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShZeroOrOnePath(exState) == path)
    }
    "return violation if sh:zeroOrOnePath is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [sh:zeroOrOnePath (ex:Red ex:Green)]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.zeroOrOnePath == path)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shZeroOrOnePathMustBeIRI == msg)
    }
    "return violation if path is not sh:zeroOrMorePath or sh:oneOrMorePath or sh:zeroOrOnePath" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type [ex:state ex:Red]) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.ty == path)
      assert(exState == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(invalidSPARQLpath == msg)
    }
    "return violation if node has no path name" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path (rdf:type []) ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(RDF.ty == path)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(expectingPathNameFoundNone == msg)
    }
  }

  "extractAlternativePath" should {
    "return an sh:alternativePath that is a blank node" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath (ex:Red ex:Green) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShAlternativePath(exRed, exGreen) == path)
    }
    "return violation if sh:alternativePath is not a blank node" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:alternativePath ex:Red ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.alternativePath == path)
      assert(exRed == value)
      assert(ShNodeKindConstraintComponent(ShBlankNode) == conCom)
      assert(shAlternativePathMustBeBNode == msg)
    }
  }

  "extractInversePath" should {
    "return violation if sh:inversePath is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:inversePath () ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.inversePath == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shInversePathMustNotBeRDFnil == msg)
    }
    "return an sh:inversePath that is a IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:inversePath ex:state ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShInversePath(exState) == path)
    }
    "return violation if sh:inversePath is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path [ sh:inversePath (ex:Red ex:Green) ] ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.inversePath == path)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shInversePathMustBeIRI == msg)
    }
  }

  "extractShPath" should {
    "return violation if sh:path is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path () ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.path == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shPathMustNotBeRDFnil == msg)
    }
    "return an sh:path constraint that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path ex:state ;
              sh:nodeKind sh:IRI ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShPredicatePath(exState) == path)
    }
    "return violation if node has more than 1 sh:path" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path ex:status ;
              sh:path (ex:state ex:stat);
              sh:nodeKind sh:IRI ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.path == path)
      assert(ShMaxCountConstraintComponent(1) == conCom)
      assert(moreThanOneShPath == msg)
    }
    "return violation if sh:path is not an IRI or blank node" in { // bbb
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path 42 ;
              sh:nodeKind sh:IRI ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.path == path)
      assert(literal42 == value)
      assert(ShNodeKindConstraintComponent(ShBlankNodeOrIRI) == conCom)
      assert(shPathMustBeIRIOrBlankNode == msg)
    }
    "return violation if sh:property has no component" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [] ;
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.property == path)
      assert(ShHasValueConstraintComponent(oneOrMoreTriples) == conCom)
      assert(emptyShProperty == msg)
    }
  }

  "extractShPredicate" should {
    "return violation if sh:predicate is rdf:nil or empty" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ()
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.predicate == path)
      assert(RDF.nil == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shPredicateMustNotBeRDFnil == msg)
    }
    "return an sh:predicate that is an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
              sh:nodeKind sh:IRI ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val ShPathConstraint(path, _) = shapes.head.constraints.head
      assert(ShPredicatePath(exState) == path)
    }
    "return violation if node has more than 1 sh:predicate" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:status ;
              sh:predicate ex:state ;
              sh:nodeKind sh:IRI ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.predicate == path)
      assert(ShMaxCountConstraintComponent(1) == conCom)
      assert(moreThanOneShPredicate == msg)
    }
    "return violation if sh:predicate is not an IRI" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate (ex:state ex:stat) ;
              sh:nodeKind sh:IRI ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), _, _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.predicate == path)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(shPredicateMustBeIRI == msg)
    }
  }

  "extractShPathConstraint" should {
    "return violation if sh:property is not a blank node" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property ex:Green ;
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, Some(path), Some(value), _, conCom, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(SH.property == path)
      assert(exGreen == value)
      assert(ShNodeKindConstraintComponent(ShBlankNode) == conCom)
      assert(shPropertyMustBeBlankNode == msg)
    }
  }

  // TODO when figure out which constraint is node constraint
  "extractSetShNodeConstraint" should {

  }

  "extractSetShPathConstraint" should {
    "return a set of path constraints" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:predicate ex:state ;
            ] ;
            sh:property [
              sh:path ex:status ;
            ] ;
            sh:property [
              sh:predicate ex:stat ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Valid(ShSchema(shapes)) = Validator.getShSchema(shape)
      val setShPathConstraints = shapes.head.constraints
      assert(
        Set(
          ShPathConstraint(ShPredicatePath(exState), Set.empty),
          ShPathConstraint(ShPredicatePath(exStatus), Set.empty),
          ShPathConstraint(ShPredicatePath(exStat), Set.empty)
        ) == setShPathConstraints)
    }
    "return violation if there is 1 violation" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path () ;
            ]
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, _, _, _, _, _, Some(msg), _)) = Validator.getShSchema(shape)
      assert(shPathMustNotBeRDFnil == msg)
    }
    "return accumulated violations if there are more than 1 violation" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path () ;
            ] ;
            sh:property [
              sh:predicate () ;
            ] ;
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResults(Vector(
        ShValidationResult(_, _, _, _, _, _, Some(msg1), _),
        ShValidationResult(_, _, _, _, _, _, Some(msg2), _)
      ))) = Validator.getShSchema(shape)
      assert(shPathMustNotBeRDFnil == msg1)
      assert(shPredicateMustNotBeRDFnil == msg2)
    }
  }

  // TODO after completing extractSetShNodeConstraint
  "extractSetShConstraint" should {

  }

  // TODO
  "extractSetShAlgebraic" should {

  }

  "extractShShapeLabel" should {

  }

  "extractSetShTarget" should {

  }

  "extractSetShFilterShape" should {

  }

  "extractSetShTest" should {

  }

  "extractShShape" should {

  }

  "extractSetShShape" should {

  }

  "extractShSchema" should {

  }

}
