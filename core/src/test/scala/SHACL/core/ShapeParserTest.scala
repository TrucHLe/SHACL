package SHACL
package core

import java.io.StringReader
import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.rio.{ RDFFormat, Rio }
import org.scalatest.WordSpec
import cats.data.Validated.{ Valid, Invalid }
import messages.ShapeParserMessages._
import core.RdfSink.factory.{ createIRI, createLiteral }
import vocabulary.SH
import vocabulary.XSD
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

  /*** extractFirstRestList  ***/

  "extractFirstRestList" should {
    "return a set of all listed elements" in {
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
      assert(Set(createIRI(ex, "Red"), createIRI(ex, "Yellow"), createIRI(ex, "Green")) == in)
    }
  }



  // TODO: fill in missing tests


  /*** extractShPNodeKind ***/

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
      val Invalid(ShValidationResult(_, path, value, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.nodeKind) == path)
      assert(Some(createIRI(ex, "Red")) == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(Some(shnodeKindMustBeShBlankNodeOrShIRIOrShLiteral) == msg)
      assert(ShViolation == severity)
    }
  }


  /*** extractShPIn ***/

  "extractShPIn" should {
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
      assert(Set(createIRI(ex, "Green")) == in)
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
      assert(Set(createLiteral("42", XSD.integer)) == in)
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
      assert(Set(createIRI(ex, "Red"), createIRI(ex, "Green")) == in)
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
      assert(Set(createLiteral("24", XSD.integer), createLiteral("42", XSD.integer)) == in)
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
      assert(Set(createIRI(ex, "Green"), createLiteral("42", XSD.integer)) == in)
    }
  }


  /*** extractShPClass ***/

  "extractShPClass" should {
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
      assert(createIRI(ex, "Green") == t)
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
      val Invalid(ShValidationResult(_, path, value, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.clss) == path)
      assert(Some(createLiteral("42", XSD.integer)) == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(Some(shclassMustBeIRI) == msg)
      assert(ShViolation == severity)
    }
  }


  /*** extractShPDatatype ***/

  "extractShPDatatype" should {
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
      assert(createIRI(ex, "Green") == dt)
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
      val Invalid(ShValidationResult(_, path, value, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.datatype) == path)
      assert(Some(createLiteral("42", XSD.integer)) == value)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(Some(shdatatypeMustBeIRI) == msg)
      assert(ShViolation == severity)
    }
  }


  /*** extractShPMinLength ***/

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
      val Invalid(ShValidationResult(_, path, value, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.minLength) == path)
      assert(Some(createIRI(ex, "Green")) == value)
      assert(ShNodeKindConstraintComponent(ShLiteral) == conCom)
      assert(Some(shminLengthMustBeNumber) == msg)
      assert(ShViolation == severity)
    }
  }


  /*** extractShPMaxLength ***/

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
      val Invalid(ShValidationResult(_, path, value, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.maxLength) == path)
      assert(Some(createIRI(ex, "Green")) == value)
      assert(ShNodeKindConstraintComponent(ShLiteral) == conCom)
      assert(Some(shmaxLengthMustBeNumber) == msg)
      assert(ShViolation == severity)
    }
  }


  /*** extractSetShUnaryParameter ***/

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
    "return violation if there is one violation" in {
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
      val Invalid(ShValidationResult(_, _, _, _, _, _, msg, _)) = Validator.getShSchema(shape)
      assert(Some(shminLengthMustBeNumber) == msg)
    }
    "return accumulated violations if there are more than one violation" in {
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
        ShValidationResult(_, _, _, _, _, _, msg1, _),
        ShValidationResult(_, _, _, _, _, _, msg2, _)
      ))) = Validator.getShSchema(shape)
      assert(Some(shminLengthMustBeNumber) == msg1)
      assert(Some(shmaxLengthMustBeNumber) == msg2)
    }
  }


  /*** extractSetShNaryParameter ***/
  // TODO
  "extractSetShNaryParameter" should {
    "return a set of n-ary parameters" in {

    }
    "return violation if there is one violation" in {

    }
    "return accumulated violations if there are more than one violation" in {

    }
  }


  /*** extractSetShParameter ***/
  // TODO after completing extractSetShNaryParameter
  "extractSetShParameter" should {
    "return a set of unary and n-ary parameters" in {

    }
    "return violation if there is one violation" in {

    }
    "return accumulated violations if there are more than one violation" in {

    }
  }


  /*** extractShPathConstraint ***/

  "extractShPathConstraint" should {
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
      assert(ShPredicatePath == path)
    }
    "prioritize returning an sh:predicate over an sh:path" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property [
              sh:path ex:status ;
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
      assert(ShPredicatePath == path)
    }
    "return violation if a node has more than 1 sh:predicate" in {
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
      val Invalid(ShValidationResult(_, path, _, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.predicate) == path)
      assert(ShMaxCountConstraintComponent(1) == conCom)
      assert(Some(moreThanOneShpredicate) == msg)
      assert(ShViolation == severity)
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
      assert(ShPredicatePath == path)
    }
    "return violation if a node has more than 1 sh:path" in {
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
      val Invalid(ShValidationResult(_, path, _, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.path) == path)
      assert(ShMaxCountConstraintComponent(1) == conCom)
      assert(Some(moreThanOneShpath) == msg)
      assert(ShViolation == severity)
    }
    "return violation if sh:path is a Literal" in {
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
      val Invalid(ShValidationResult(_, path, value, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.path) == path)
      assert(Some(createLiteral("42", XSD.integer)) == value)
      assert(ShNodeKindConstraintComponent(ShBlankNodeOrIRI) == conCom)
      assert(Some(shpathMustBeIRIOrBlankNode) == msg)
      assert(ShViolation == severity)
    }
    "return violation if sh:property has zero component" in {
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
      val Invalid(ShValidationResult(_, path, _, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.property) == path)
      assert(ShHasValueConstraintComponent(oneOrMoreTriples) == conCom)
      assert(Some(emptyShproperty) == msg)
      assert(ShViolation == severity)
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
      val Invalid(ShValidationResult(_, path, _, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.predicate) == path)
      assert(ShNodeKindConstraintComponent(ShIRI) == conCom)
      assert(Some(shpredicateMustBeIRI) == msg)
      assert(ShViolation == severity)
    }
    "return violation if sh:property is a Literal" in {
      val shape: Model = {
        val snippet: String =
          """
          @prefix sh: <http://www.w3.org/ns/shacl#> .
          @prefix ex: <http://www.example.org/ex#> .
          ex:Shape
            a sh:Shape ;
            sh:property 42 ;
          .
          """
        val reader = new StringReader(snippet)
        val shape = Rio.parse(reader, "", RDFFormat.TURTLE)
        if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
        shape
      }
      val Invalid(ShValidationResult(_, path, _, _, conCom, _, msg, severity)) = Validator.getShSchema(shape)
      assert(Some(SH.property) == path)
      assert(ShNodeKindConstraintComponent(ShBlankNodeOrIRI) == conCom)
      assert(Some(shpropertyMustBeIRIOrBlankNode) == msg)
      assert(ShViolation == severity)
    }
  }


  /*** extractSetShNodeConstraint ***/
  // TODO when figure out which constraint is a node constraint
  "extractSetShNodeConstraint" should {

  }


  /*** extractSetShPathConstraint ***/

  "extractSetShPathConstraint" should {

  }


  /*** extractSetShConstraint ***/
  // TODO after completing extractSetShNodeConstraint
  "extractSetShConstraint" should {

  }


  /*** extractSetShAlgebraic ***/
  // TODO
  "extractSetShAlgebraic" should {

  }


  /***  ***/

  "extractShShapeLabel" should {

  }


  /***  ***/

  "extractSetShTarget" should {

  }


  /***  ***/

  "extractSetShFilterShape" should {

  }


  /***  ***/

  "extractSetShTest" should {

  }


  /***  ***/

  "extractShShape" should {

  }


  /***  ***/

  "extractSetShShape" should {

  }


  /***  ***/

  "extractShSchema" should {

  }

}
