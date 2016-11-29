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

  def extractValueList(bnode: BNode): Set[Value] = {
    shape.filter(bnode, RDF.first, null).asScala.toList match {
      case Rdf4j.Statement(_, _, iriOrLiteral) :: Nil =>
        shape.filter(bnode, RDF.rest, null).asScala.toList match {
          case Rdf4j.Statement(_, _, RDF.nil) :: Nil => Set(iriOrLiteral)
          case Rdf4j.Statement(_, _, Rdf4j.BNode(bn)) :: Nil => extractValueList(bn) + iriOrLiteral
        }
    }
  }

  def extractIRIPair(sourceShape: Resource, focusNode: Resource, bnode1: BNode): Check[(IRI, IRI)] = {
    def extractSecondIRI(bnode2: BNode, iri1: IRI): Check[(IRI, IRI)] =
      shape.filter(bnode2, RDF.first, null).asScala.toList match {
        case Rdf4j.Statement(_, _, RDF.nil) :: Nil =>
          violation(
            focusNode,
            Some(RDF.first),
            Some(RDF.nil),
            Some(sourceShape),
            new ShNodeKindConstraintComponent(ShIRI),
            None,
            Some(shPathMustNotBeRDFnil))
        case Rdf4j.Statement(_, _, Rdf4j.IRI(iri2)) :: Nil =>
          shape.filter(bnode2, RDF.rest, null).asScala.toList match {
            case Rdf4j.Statement(_, _, RDF.nil) :: Nil => checked((iri1, iri2))
            case Rdf4j.Statement(_, _, _) :: _ =>
              violation(
                focusNode,
                Some(RDF.rest),
                None,
                Some(sourceShape),
                new ShMaxCountConstraintComponent(2),
                None,
                Some(expectingTwoIRIsFoundMoreThanTwo))
          }
        case Rdf4j.Statement(_, _, v2) :: _ =>
          violation(
            focusNode,
            Some(RDF.first),
            Some(v2),
            Some(sourceShape),
            new ShNodeKindConstraintComponent(ShIRI),
            None,
            Some(rdfFirstMustBeIRI))
      }
    def extractFirstIRI: Check[(IRI, IRI)] =
      shape.filter(bnode1, RDF.first, null).asScala.toList match {
        case Rdf4j.Statement(_, _, RDF.nil) :: Nil =>
          violation(
            focusNode,
            Some(RDF.first),
            Some(RDF.nil),
            Some(sourceShape),
            new ShNodeKindConstraintComponent(ShIRI),
            None,
            Some(shPathMustNotBeRDFnil))
        case Rdf4j.Statement(_, _, Rdf4j.IRI(iri1)) :: Nil =>
          shape.filter(bnode1, RDF.rest, null).asScala.toList match {
            case Rdf4j.Statement(_, _, Rdf4j.BNode(bnode2)) :: Nil =>
              extractSecondIRI(bnode2, iri1)
            case Rdf4j.Statement(_, _, RDF.nil) :: Nil =>
              violation(
                focusNode,
                Some(RDF.rest),
                Some(RDF.nil),
                Some(sourceShape),
                new ShNodeKindConstraintComponent(ShIRI),
                None,
                Some(expectingTwoIRIsFoundOne))
          }
        case Rdf4j.Statement(_, _, v1) :: _ =>
          violation(
            focusNode,
            Some(RDF.first),
            Some(v1),
            Some(sourceShape),
            new ShNodeKindConstraintComponent(ShIRI),
            None,
            Some(rdfFirstMustBeIRI))
      }
    extractFirstIRI
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
          Some(shNodeKindMustBeShBlankNodeOrShIRIOrShLiteral))
    }

  def extractShPIn(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPIn] =
    value match {
      case RDF.nil =>
        violation(
          focusNode,
          Some(SH.in),
          Some(RDF.nil),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),          
          None,
          Some(shInMustHaveOneOrMoreValues))
      case iri: IRI => checked(new ShPIn(Set(iri)))
      case simpleLiteral: SimpleLiteral => checked(new ShPIn(Set(simpleLiteral)))
      case Rdf4j.BNode(bnode) => checked(new ShPIn(extractValueList(bnode)))
    }

  def extractShPClass(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPClass] =
    value match {
      case RDF.nil =>
        violation(
          focusNode,
          Some(SH.clss),
          Some(RDF.nil),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),          
          None,
          Some(shClassMustNotBeRDFnil))
      case iri: IRI => checked(new ShPClass(iri))
      case v =>
        violation(
          focusNode,
          Some(SH.clss),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shClassMustBeIRI)
        )
    }

  def extractShPDatatype(sourceShape: Resource, focusNode: Resource, value: Value): Check[ShPDatatype] =
    value match {
      case RDF.nil =>
        violation(
          focusNode,
          Some(SH.datatype),
          Some(RDF.nil),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),          
          None,
          Some(shDatatypeMustNotBeRDFnil))
      case iri: IRI => checked(new ShPDatatype(iri))
      case v =>
        violation(
          focusNode,
          Some(SH.datatype),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shDatatypeMustBeIRI)
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
          Some(shMinLengthMustBeNumber)
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
          Some(shMaxLengthMustBeNumber)
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
            extractShPIn(sourceShape, focusNode, value),
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

  def extractSequencePath(sourceShape: Resource, focusNode: Resource, headBNode: BNode): Check[ShPathConstraint] =
    Apply[Check].map2(
      extractIRIPair(sourceShape, focusNode, headBNode),
      extractSetShParameter(sourceShape, focusNode)
    )((t, s) => new ShPathConstraint(new ShSequencePath(t._1, t._2), s))

  def extractZeroOnePath(sourceShape: Resource, focusNode: Resource, headBNode: BNode): Check[ShPathConstraint] =
    shape.filter(headBNode, RDF.first, RDF.ty).asScala.toList match {
      case Rdf4j.Statement(_, _, _) :: Nil =>
        shape.filter(headBNode, RDF.rest, null).asScala.toList match {
          case Rdf4j.Statement(_, _, Rdf4j.BNode(bnode1)) :: Nil =>
            shape.filter(bnode1, RDF.first, null).asScala.toList match {
              // Only BNode is possible here because of how TurtleParser works
              case Rdf4j.Statement(_, _, Rdf4j.BNode(bnode2)) :: Nil =>
                shape.filter(bnode2, null, null).asScala.toList match {
                  case Rdf4j.Statement(_, SH.zeroOrMorePath, RDF.nil) :: Nil =>
                    violation(
                      focusNode,
                      Some(SH.zeroOrMorePath),
                      Some(RDF.nil),
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(shZeroOrMorePathMustNotBeRDFnil))
                  case Rdf4j.Statement(_, SH.zeroOrMorePath, Rdf4j.IRI(iri)) :: Nil =>
                    Apply[Check].map(
                      extractSetShParameter(sourceShape, focusNode)
                    )(s => new ShPathConstraint(new ShZeroOrMorePath(iri), s))
                  case Rdf4j.Statement(_, SH.zeroOrMorePath, v) :: Nil =>
                    violation(
                      focusNode,
                      Some(SH.zeroOrMorePath),
                      Some(v),
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(shZeroOrMorePathMustBeIRI))
                  case Rdf4j.Statement(_, SH.oneOrMorePath, RDF.nil) :: Nil =>
                    violation(
                      focusNode,
                      Some(SH.oneOrMorePath),
                      Some(RDF.nil),
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(shOneOrMorePathMustNotBeRDFnil))
                  case Rdf4j.Statement(_, SH.oneOrMorePath, Rdf4j.IRI(iri)) :: Nil =>
                    Apply[Check].map(
                      extractSetShParameter(sourceShape, focusNode)
                    )(s => new ShPathConstraint(new ShOneOrMorePath(iri), s))
                  case Rdf4j.Statement(_, SH.oneOrMorePath, v) :: Nil =>
                    violation(
                      focusNode,
                      Some(SH.oneOrMorePath),
                      Some(v),
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(shOneOrMorePathMustBeIRI))
                  case Rdf4j.Statement(_, SH.zeroOrOnePath, RDF.nil) :: Nil =>
                    violation(
                      focusNode,
                      Some(SH.zeroOrOnePath),
                      Some(RDF.nil),
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(shZeroOrOnePathMustNotBeRDFnil))
                  case Rdf4j.Statement(_, SH.zeroOrOnePath, Rdf4j.IRI(iri)) :: Nil =>
                    Apply[Check].map(
                      extractSetShParameter(sourceShape, focusNode)
                    )(s => new ShPathConstraint(new ShZeroOrOnePath(iri), s))
                  case Rdf4j.Statement(_, SH.zeroOrOnePath, v) :: Nil =>
                    violation(
                      focusNode,
                      Some(SH.zeroOrOnePath),
                      Some(v),
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(shZeroOrOnePathMustBeIRI))
                  case Rdf4j.Statement(_, Rdf4j.IRI(i), _) :: Nil =>
                    violation(
                      focusNode,
                      Some(RDF.ty),
                      Some(i),
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(invalidSPARQLpath))
                  case Nil => 
                    violation(
                      focusNode,
                      Some(RDF.ty),
                      None,
                      Some(sourceShape),
                      new ShNodeKindConstraintComponent(ShIRI),
                      None,
                      Some(expectingPathNameFoundNone))
                }
            }
        }
      case Nil => extractSequencePath(sourceShape, focusNode, headBNode)
    }

  def extractAlternativePath(sourceShape: Resource, focusNode: Resource, headBNode: BNode): Check[ShPathConstraint] =
    shape.filter(headBNode, SH.alternativePath, null).asScala.toList match {
      case Rdf4j.Statement(_, _, Rdf4j.BNode(bnode)) :: Nil =>
        Apply[Check].map2(
          extractIRIPair(sourceShape, focusNode, bnode),
          extractSetShParameter(sourceShape, focusNode)
        )((t, s) => new ShPathConstraint(new ShAlternativePath(t._1, t._2), s))
      case Rdf4j.Statement(_, _, v) :: _ =>
        violation(
          focusNode,
          Some(SH.alternativePath),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShBlankNode),
          None,
          Some(shAlternativePathMustBeBNode))
      case Nil => extractZeroOnePath(sourceShape, focusNode, headBNode)
    }

  def extractInversePath(sourceShape: Resource, focusNode: Resource, headBNode: BNode): Check[ShPathConstraint] =
    shape.filter(headBNode, SH.inversePath, null).asScala.toList match {
      case Rdf4j.Statement(_, _, RDF.nil) :: Nil =>
        violation(
          focusNode,
          Some(SH.inversePath),
          Some(RDF.nil),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shInversePathMustNotBeRDFnil))
      case Rdf4j.Statement(_, _, Rdf4j.IRI(inversePath)) :: Nil =>
        Apply[Check].map(
          extractSetShParameter(sourceShape, focusNode)
        )(s => new ShPathConstraint(new ShInversePath(inversePath), s))
      case Rdf4j.Statement(_, _, v) :: _ =>
        violation(
          focusNode,
          Some(SH.inversePath),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shInversePathMustBeIRI))
      case Nil => extractAlternativePath(sourceShape, focusNode, headBNode)
    }

  def extractShPath(sourceShape: Resource, focusNode: BNode): Check[ShPathConstraint] =
    shape.filter(focusNode, SH.path, null).asScala.toList match {
      case Rdf4j.Statement(_, _, RDF.nil) :: _ =>
        violation(
          focusNode,
          Some(SH.path),
          Some(RDF.nil),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shPathMustNotBeRDFnil))
      case Rdf4j.Statement(_, _, Rdf4j.IRI(path)) :: Nil =>
        Apply[Check].map(
          extractSetShParameter(sourceShape, focusNode)
        )(s => new ShPathConstraint(new ShPredicatePath(path), s))
      case Rdf4j.Statement(_, _, Rdf4j.IRI(_)) :: _ =>
        violation(
          focusNode,
          Some(SH.path),
          None,
          Some(sourceShape),
          new ShMaxCountConstraintComponent(1),
          None,
          Some(moreThanOneShPath))
      case Rdf4j.Statement(_, _, Rdf4j.BNode(bnode)) :: Nil =>
        extractInversePath(sourceShape, focusNode, bnode)
      case Rdf4j.Statement(_, _, Rdf4j.BNode(_)) :: _ =>
        violation(
          focusNode,
          Some(SH.path),
          None,
          Some(sourceShape),
          new ShMaxCountConstraintComponent(1),
          None,
          Some(moreThanOneShPath))
      case Rdf4j.Statement(_, _, v) :: _ =>
        violation(
          focusNode,
          Some(SH.path),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShBlankNodeOrIRI),
          None,
          Some(shPathMustBeIRIOrBlankNode))
      case Nil =>
        violation(
          focusNode,
          Some(SH.property),
          None,
          Some(sourceShape),
          new ShHasValueConstraintComponent(oneOrMoreTriples),
          None,
          Some(emptyShProperty))
    }

  def extractShPredicate(sourceShape: Resource, focusNode: BNode): Check[ShPathConstraint] = {
    shape.filter(focusNode, SH.predicate, null).asScala.toList match {
      case Rdf4j.Statement(_, _, RDF.nil) :: Nil =>
        violation(
          focusNode,
          Some(SH.predicate),
          Some(RDF.nil),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shPredicateMustNotBeRDFnil))
      case Rdf4j.Statement(_, _, Rdf4j.IRI(pred)) :: Nil =>
        Apply[Check].map(
          extractSetShParameter(sourceShape, focusNode)
        )(s => new ShPathConstraint(new ShPredicatePath(pred), s))
      case Rdf4j.Statement(_, _, Rdf4j.IRI(_)) :: _ =>
        violation(
          focusNode,
          Some(SH.predicate),
          None,
          Some(sourceShape),
          new ShMaxCountConstraintComponent(1),
          None,
          Some(moreThanOneShPredicate))
      case Nil =>
        extractShPath(sourceShape, focusNode)
      case Rdf4j.Statement(_, _, v) :: _ =>
        violation(
          focusNode,
          Some(SH.predicate),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShIRI),
          None,
          Some(shPredicateMustBeIRI))
    }
  }

  def extractShPathConstraint(sourceShape: Resource, statement: Statement): Check[ShPathConstraint] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.BNode(focusNode)) =>
        extractShPredicate(sourceShape, focusNode)
      case Rdf4j.Statement(_, _, v) =>
        violation(
          sourceShape,
          Some(SH.property),
          Some(v),
          Some(sourceShape),
          new ShNodeKindConstraintComponent(ShBlankNode),
          None,
          Some(shPropertyMustBeBlankNode))
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
      unchecked(new ShValidationResults(setFailure.toVector))
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
      // TurtleParser already catches label that isn't IRI or BNode
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
      unchecked(new ShValidationResults(setFailure.toVector))
  }

  val extractShSchema: Check[ShSchema] =
    Apply[Check].map(extractSetShShape)(setShShape => new ShSchema(setShShape))
}
