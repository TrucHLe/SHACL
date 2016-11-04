package SHACL
package core

import org.eclipse.rdf4j.model.Model

final case class ShapeParser(shape: Model) {
  import cats.Apply
  import cats.data.Validated._
  import scala.collection.mutable.HashSet
  import org.eclipse.rdf4j.model.{ Resource, IRI, Value, Statement, BNode }
  import org.eclipse.rdf4j.model.impl._
  import scala.collection.JavaConverters._
  import vocabulary.{ RDF, SH, XSD }
  import messages.ShapeParserMessages._
  import model._
  import model.ShTarget._
  import model.ShConstraint._
  import model.ShConstraintComponent._
  import model.ShPropertyPath._
  import model.ShValidationReport._
  import model.ShNodeKind._
  import model.ShUnaryParameter._
  import CheckAbstraction._

  def extractFirstRestList(bnode: BNode): Set[Value] = {
    shape.filter(bnode, RDF.first, null).asScala.toList match {
      case Rdf4j.Statement(_, _, iriOrLiteral) :: Nil =>
        shape.filter(bnode, RDF.rest, null).asScala.toList match {
          case Rdf4j.Statement(_, _, Rdf4j.IRI(iri)) :: Nil => Set(iriOrLiteral)
          case Rdf4j.Statement(_, _, Rdf4j.BNode(bn)) :: Nil => extractFirstRestList(bn) + iriOrLiteral
        }
    }
  }

  // TODO ---------------------------------------------------------
  def extractSetShTargetNode(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)
  def extractSetShTargetClass(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)
  def extractSetShTargetSubjectsOf(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)
  def extractSetShTargetObjectsOf(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)


  def extractShPNodeKind(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPNodeKind] =
    value match {
      case Rdf4j.IRI(SH.BlankNode) => checked(new ShPNodeKind(SH.BlankNode))
      case Rdf4j.IRI(SH.IRI) => checked(new ShPNodeKind(SH.IRI))
      case Rdf4j.IRI(SH.Literal) => checked(new ShPNodeKind(SH.Literal))
      case v =>
        violation(
          focusNode,
          Some(SH.nodeKind),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shnodeKindMustBeShBlankNodeOrShIRIOrShLiteral))
    }

  def extractShPIn(value: Value): Check[ShPIn] =
    value match {
      case iri: IRI => checked(new ShPIn(Set(iri)))
      case simpleLiteral: SimpleLiteral => checked(new ShPIn(Set(simpleLiteral)))
      case Rdf4j.BNode(bnode) => checked(new ShPIn(extractFirstRestList(bnode)))
    }

  def extractShPClass(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPClass] =
    value match {
      case iri: IRI => checked(new ShPClass(iri))
      case v =>
        violation(
          focusNode,
          Some(SH.clss),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shclassMustBeIRI)
        )
    }

  def extractShPDatatype(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPDatatype] =
    value match {
      case iri: IRI => checked(new ShPDatatype(iri))
      case v =>
        violation(
          focusNode,
          Some(SH.datatype),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shdatatypeMustBeIRI)
        )
    }

  def extractShPMinLength(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPMinLength] =
    value match {
      case Rdf4j.Literal(n, XSD.integer) => checked(new ShPMinLength(n.toInt))
      case v =>
        violation(
          focusNode,
          Some(SH.minLength),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShLiteral),
          None,
          Some(shminLengthMustBeNumber)
        )
    }

  def extractShPMaxLength(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPMaxLength] =
    value match {
      case Rdf4j.Literal(n, XSD.integer) => checked(new ShPMaxLength(n.toInt))
      case v =>
        violation(
          focusNode,
          Some(SH.maxLength),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShLiteral),
          None,
          Some(shmaxLengthMustBeNumber)
        )
    }

  def extractSetShUnaryParameter(sourceShape: Resource, focusNode: Resource): Check[Set[ShUnaryParameter]] = {
    def extractSet(list: List[Statement]): Check[Set[ShUnaryParameter]] =
      list match {
        case Nil => checked(Set.empty)
        case Rdf4j.Statement(_, SH.predicate, _) :: xs => extractSet(xs)
        case Rdf4j.Statement(_, SH.path, _) :: xs => extractSet(xs)
        case Rdf4j.Statement(r, SH.nodeKind, value) :: xs =>
          Apply[Check].map2(
            extractShPNodeKind(sourceShape, focusNode, value),
            extractSet(xs)
          )((p, s) => s + p)
        case Rdf4j.Statement(_, SH.in, value) :: xs =>
          Apply[Check].map2(
            extractShPIn(value),
            extractSet(xs)
          )((p, s) => s + p)
        case Rdf4j.Statement(_, SH.clss, value) :: xs =>
          Apply[Check].map2(
            extractShPClass(sourceShape, focusNode, value),
            extractSet(xs)
          )((p, s) => s + p)
        case Rdf4j.Statement(_, SH.datatype, value) :: xs =>
          Apply[Check].map2(
            extractShPDatatype(sourceShape, focusNode, value),
            extractSet(xs)
          )((p, s) => s + p)
        case Rdf4j.Statement(_, SH.minLength, value) :: xs =>
          Apply[Check].map2(
            extractShPMinLength(sourceShape, focusNode, value),
            extractSet(xs)
          )((p, s) => s + p)
        case Rdf4j.Statement(_, SH.maxLength, value) :: xs =>
          Apply[Check].map2(
            extractShPMaxLength(sourceShape, focusNode, value),
            extractSet(xs)
          )((p, s) => s + p)

        //TODO: implement the rest of ShUnaryparameter
        case _ => checked(Set.empty)
      }

    extractSet(shape.filter(focusNode, null, null).asScala.toList)
  }

  // TODO
  def extractSetShNaryParameter(sourceShape: Resource, resource: Resource): Check[Set[ShNaryParameter]] = checked(Set.empty)

  def extractSetShParameter(sourceShape: Resource, focusNode: Resource): Check[Set[ShParameter]] =
    Apply[Check].map2(
      extractSetShUnaryParameter(sourceShape, focusNode),
      extractSetShNaryParameter(sourceShape, focusNode)
    )((s1, s2) => s1 ++ s2)

  def extractShPathConstraint(sourceShape: Resource, statement: Statement): Check[ShPathConstraint] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.Resource(focusNode)) =>
        shape.filter(focusNode, SH.predicate, null).asScala.toList match {
          case Rdf4j.Statement(_, _, Rdf4j.IRI(pred)) :: Nil =>
            Apply[Check].map(
              extractSetShParameter(sourceShape, focusNode)
            )(s => new ShPathConstraint(ShPredicatePath, s))
          case Rdf4j.Statement(_, _, Rdf4j.IRI(_)) :: _ =>
            violation(
              focusNode,
              Some(SH.predicate),
              None,
              Some(sourceShape),
              new ShMaxCountConstraintComponent(1),
              None,
              Some(moreThanOneShpredicate))
          case Nil =>
            shape.filter(focusNode, SH.path, null).asScala.toList match {
              case Rdf4j.Statement(_, _, Rdf4j.IRI(path)) :: Nil =>
                Apply[Check].map(
                  extractSetShParameter(sourceShape, focusNode)
                )(s => new ShPathConstraint(ShPredicatePath, s))
              case Rdf4j.Statement(_, _, Rdf4j.IRI(_)) :: _ =>
                violation(
                  focusNode,
                  Some(SH.path),
                  None,
                  Some(sourceShape),
                  new ShMaxCountConstraintComponent(1),
                  None,
                  Some(moreThanOneShpath))
              // NOTE: does not support SPARQL path constraints
              case Rdf4j.Statement(_, _, Rdf4j.BNode(path)) :: Nil =>
                checked(new ShPathConstraint(ShPredicatePath, Set.empty))
              case Rdf4j.Statement(_, _, Rdf4j.BNode(_)) :: _ =>
                violation(
                  focusNode,
                  Some(SH.path),
                  None,
                  Some(sourceShape),
                  new ShMaxCountConstraintComponent(1),
                  None,
                  Some(moreThanOneShpath))
              case Rdf4j.Statement(_, _, Rdf4j.Value(value)) :: _ =>
                violation(
                  focusNode,
                  Some(SH.path),
                  Some(value),
                  Some(sourceShape),
                  new ShNodeKindConstraintComponent(ShBlankNodeOrIRI),
                  None,
                  Some(shpathMustBeIRIOrBlankNode))
              case _ =>
                violation(
                  focusNode,
                  Some(SH.property),
                  None,
                  Some(sourceShape),
                  new ShHasValueConstraintComponent(oneOrMoreTriples),
                  None,
                  Some(emptyShproperty))
            }
          case Rdf4j.Statement(_, _, Rdf4j.Value(value)) :: _ =>
            violation(
              focusNode,
              Some(SH.predicate),
              Some(value),
              Some(sourceShape),
              new ShNodeKindConstraintComponent(ShIRI),
              None,
              Some(shpredicateMustBeIRI))
        }
      case _ =>
        violation(
          sourceShape,
          Some(SH.property),
          None,
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShBlankNodeOrIRI),
          None,
          Some(shpropertyMustBeIRIOrBlankNode))
    }


  // TODO: SHACL documentation doesn't specify which constraint
  // can be NodeConstraint. Check again in the future.
  def extractSetShNodeConstraint(subject: Resource): Check[Set[ShNodeConstraint]] = checked(Set.empty)

  def extractSetShPathConstraint(subject: Resource): Check[Set[ShPathConstraint]] = {
    val setSuccess: HashSet[ShPathConstraint] = HashSet.empty
    val setFailure: HashSet[ShValidationReport] = HashSet.empty
    shape.filter(subject, SH.property, null).asScala.map { stm =>
      extractShPathConstraint(subject, stm) match {
        case Valid(v) => setSuccess += v
        case Invalid(i) => setFailure += i
      }
    }
    if (setFailure.isEmpty)
      checked(setSuccess.toSet)
    else if (setFailure.size == 1)
      unchecked(setFailure.head)
    else
      unchecked(ShValidationResults(setFailure.toVector))
  }

  def extractSetShConstraint(subject: Resource): Check[Set[ShConstraint]] =
    Apply[Check].map2(
      extractSetShNodeConstraint(subject),
      extractSetShPathConstraint(subject)
    )((s1, s2) => s1 ++ s2)

  // TODO
  def extractSetShAlgebraic(subject: Resource): Check[Set[ShAlgebraic]] = checked(Set.empty)

  def extractShShapeLabel(statement: Statement): Check[Resource] =
    statement match {
      case Rdf4j.Statement(Rdf4j.Resource(r), _, _) => checked(r)
      case _ => ??? // RDF4J turtle parser already catches shape's label that isn't IRI or BNode
    }

  def extractSetShTarget(subject: Resource): Check[Set[ShTarget]] =
    Apply[Check].map4(
      extractSetShTargetNode(subject),
      extractSetShTargetClass(subject),
      extractSetShTargetSubjectsOf(subject),
      extractSetShTargetObjectsOf(subject)
    )((s1, s2, s3, s4) => s1 ++ s2 ++ s3 ++ s4)

  // TODO
  def extractSetShFilterShape(subject: Resource): Check[Set[ShShape]] = checked(Set.empty)

  def extractSetShTest(subject: Resource): Check[Set[ShTest]] =
    Apply[Check].map2(
      extractSetShConstraint(subject),
      extractSetShAlgebraic(subject)
    )((s1, s2) => s1 ++ s2)

  def extractShShape(statement: Statement): Check[ShShape] =
    extractShShapeLabel(statement).andThen { label =>
      Apply[Check].map3(
        extractSetShTarget(label),
        extractSetShFilterShape(label),
        extractSetShTest(label)
      )((setShTarget, setShFilter, setShTest) => new ShShape(label, setShTarget, setShFilter, setShTest))
  }

  val extractSetShShape: Check[Set[ShShape]] = {
    val setSuccess: HashSet[ShShape] = HashSet.empty
    val setFailure: HashSet[ShValidationReport] = HashSet.empty
    shape.filter(null, RDF.ty, SH.Shape).asScala.map { stm =>
      extractShShape(stm) match {
        case Valid(v) => setSuccess += v
        case Invalid(i) => setFailure += i
      }
    }
    if (setFailure.isEmpty)
      checked(setSuccess.toSet)
    else if (setFailure.size == 1)
      unchecked(setFailure.head)
    else
      unchecked(ShValidationResults(setFailure.toVector))
  }

  val extractShSchema: Check[ShSchema] =
    Apply[Check].map(extractSetShShape)(setShShape => new ShSchema(setShShape))
}
