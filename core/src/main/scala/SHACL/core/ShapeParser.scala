package SHACL
package core

final case class ShapeParser() {
  import cats.Apply
  import cats.data.Validated._
  import scala.collection.mutable.HashSet
  import org.eclipse.rdf4j.model.{ Resource, IRI, Statement, BNode }
  import scala.collection.JavaConverters._
  import org.eclipse.rdf4j.rio._
  import org.eclipse.rdf4j.model.Model
  import vocabulary.{ RDF, SH, XSD }
  import messages.ShapeParserMessages._
  import model._
  import model.ShTarget._
  import model.ShConstraint._
  import model.ShConstraintComponent._
  import model.ShPropertyPath._
  import model.ShValidationReport._
  import model.ShNodeKind._

  import CheckAbstraction._

  val shape: Model = {
    val shapeInput = classOf[ShapeParser].getResourceAsStream("/shapeInput.ttl")
    val shape = Rio.parse(shapeInput, "", RDFFormat.TURTLE)
    if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
    shape
  }


/*
  def extractShTargetNode(statement: Statement): Check[ShTargetNode] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.Value(v)) => checked(new ShTargetNode(v))
      case Rdf4j.Statement(_, _, v) => violationF(invalidShtargetNode, v.toString)
    }

  // TODO: accumulate errors
  def extractSetShTargetNode(subject: Resource): Check[Set[ShTargetNode]] = {
    import scala.collection.mutable.HashSet
    val setShTargetNode: HashSet[ShTargetNode] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    shape.filter(subject, SH.targetNode, null).asScala.map { stm =>
      extractShTargetNode(stm) match { 
        case Valid(v) => setShTargetNode += v
        case Invalid(i) => setShSeverity += i
      }
    }
    if (setShSeverity.isEmpty)
      checked(setShTargetNode.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  def extractShTargetClass(statement: Statement): Check[ShTargetClass] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.IRI(iri)) => checked(new ShTargetClass(iri))
      case Rdf4j.Statement(_, _, v) => violationF(invalidShtargetClass, v.toString)
    }

  // TODO: accumulate errors
  def extractSetShTargetClass(subject: Resource): Check[Set[ShTargetClass]] = {
    import scala.collection.mutable.HashSet
    val setShTargetClass: HashSet[ShTargetClass] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    shape.filter(subject, SH.targetClass, null).asScala.map { stm =>
      extractShTargetClass(stm) match {
        case Valid(v) => setShTargetClass += v
        case Invalid(i) => setShSeverity += i
      }
    }
    if (setShSeverity.isEmpty)
      checked(setShTargetClass.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  def extractShTargetSubjectsOf(statement: Statement): Check[ShTargetSubjectsOf] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.IRI(iri)) => checked(new ShTargetSubjectsOf(iri))
      case Rdf4j.Statement(_, _, v) => violationF(invalidShtargetSubjectsOf, v.toString)
    }

  // TODO: accumulate errors
  def extractSetShTargetSubjectsOf(subject: Resource): Check[Set[ShTargetSubjectsOf]] = {
    import scala.collection.mutable.HashSet
    val setShTargetSubjectsOf: HashSet[ShTargetSubjectsOf] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    shape.filter(subject, SH.targetSubjectsOf, null).asScala.map { stm =>
      extractShTargetSubjectsOf(stm) match {
        case Valid(v) => setShTargetSubjectsOf += v
        case Invalid(i) => setShSeverity += i
      }
    }

    if (setShSeverity.isEmpty)
      checked(setShTargetSubjectsOf.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  def extractShTargetObjectsOf(statement: Statement): Check[ShTargetObjectsOf] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.IRI(iri)) => checked(new ShTargetObjectsOf(iri))
      case Rdf4j.Statement(_, _, v) => violationF(invalidShtargetObjectsOf, v.toString)
    }

  // TODO: accumulate errors
  def extractSetShTargetObjectsOf(subject: Resource): Check[Set[ShTargetObjectsOf]] = {
    import scala.collection.mutable.HashSet
    val setShTargetObjectsOf: HashSet[ShTargetObjectsOf] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    shape.filter(subject, SH.targetObjectsOf, null).asScala.map { stm =>
      extractShTargetObjectsOf(stm) match {
        case Valid(v) => setShTargetObjectsOf += v
        case Invalid(i) => setShSeverity += i
      }
    }
    if (setShSeverity.isEmpty)
      checked(setShTargetObjectsOf.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  // TODO: fill all cases
  def extractShUnaryParameter(statement: Statement): Option[ShUnaryParameter] =
    statement match {
      case Rdf4j.Statement(_, SH.nodeKind, Rdf4j.Value(v)) => Some(new ShNodeKind(v))
      //TODO: case Rdf4j.Statement(_, SH.in, ???))
      case Rdf4j.Statement(_, SH.clss, Rdf4j.IRI(iri)) => Some(new ShClass(iri))
      case Rdf4j.Statement(_, SH.datatype, Rdf4j.IRI(iri)) => Some(new ShDatatype(iri))
      case Rdf4j.Statement(_, SH.minLength, Rdf4j.Literal(n, XSD.integer)) => Some(new ShMinLength(n.toInt))
      case Rdf4j.Statement(_, SH.maxLength, Rdf4j.Literal(n, XSD.integer)) => Some(new ShMaxLength(n.toInt))
      case _ => None
    }

  // TODO: fill all cases
  def extractNaryParameter(statement: Statement): Option[ShNaryParameter] =
    statement match {
      case _ => None
    }

  def extractShParameter(statement: Statement): Check[ShParameter] = {
    extractShUnaryParameter(statement) match {
      case Some(unaryParameter) => checked(unaryParameter)
      case None =>
        extractNaryParameter(statement) match {
          case Some(naryParameter) => checked(naryParameter)
          case None =>
            statement match {
              case Rdf4j.Statement(_, parameter, _) => violation(invalidShParameter(parameter.toString))
            }
      }
    }
  }

  // TODO: accumulate errors
  def extractSetShParameter(resource: Resource): Check[Set[ShParameter]] = {
    import scala.collection.mutable.HashSet
    val setShParameter: HashSet[ShParameter] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    shape.filter(resource, null, null).asScala.map { stm =>
      stm match {
        case Rdf4j.Statement(_, SH.predicate, _) => None
        case Rdf4j.Statement(_, SH.path, _) => None
        case _ => extractShParameter(stm) match {
          case Valid(v) => setShParameter += v
          case Invalid(i) => setShSeverity += i
        }
      }
    }

    if (setShSeverity.isEmpty)
      checked(setShParameter.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }




*/


  def extractSetShTargetNode(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)
  def extractSetShTargetClass(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)
  def extractSetShTargetSubjectsOf(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)
  def extractSetShTargetObjectsOf(subject: Resource): Check[Set[ShTargetNode]] = checked(Set.empty)

  def extractSetShTarget(subject: Resource): Check[Set[ShTarget]] =
    Apply[Check].map4(
      extractSetShTargetNode(subject),
      extractSetShTargetClass(subject),
      extractSetShTargetSubjectsOf(subject),
      extractSetShTargetObjectsOf(subject)
    )((s1, s2, s3, s4) => s1 ++ s2 ++ s3 ++ s4)

  // TODO
  def extractSetShFilterShape(subject: Resource): Check[Set[ShShape]] = checked(Set.empty)




  // TODO
  def extractShNodeConstraint(sourceShape: Resource, resource: Resource): Check[ShNodeConstraint] = checked(new ShNodeConstraint(Set.empty))

  def extractShPathConstraint(sourceShape: Resource, resource: Resource, path: IRI): Check[ShPathConstraint] = {
    checked(new ShPathConstraint(ShPredicatePath, Set.empty))
    /*
    extractSetShParameter(resource) match {
      case Valid(vSet) =>
        if (vSet.size == 0) {
          if (isPredicatePath)
            violation(emptyShPredicatePathConstraint(path.toString))
          else
            violation(emptyShPathConstraint(path.toString))
        }
        else
          checked(new ShPathConstraint(ShPredicatePath, vSet))
      case Invalid(iSet) => Validated.invalid(iSet)
    }
    */
  }


  // TODO
  def extractShPathConstraint(sourceShape: Resource, resource: Resource, path: BNode): Check[ShPathConstraint] = ???

  def extractShConstraint(sourceShape: Resource, statement: Statement): Check[ShConstraint] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.Resource(r)) =>
        shape.filter(r, SH.predicate, null).asScala.toList match {
          case Rdf4j.Statement(_, _, Rdf4j.IRI(pred)) :: Nil =>
            extractShPathConstraint(sourceShape, r, pred)
          case Rdf4j.Statement(_, _, Rdf4j.IRI(pred)) :: _ =>
            violation(r, Some(SH.predicate), Some(pred), Some(sourceShape), new ShMaxCountConstraintComponent(1), None, Some(moreThanOneShpredicate(pred.toString)))
          case Nil =>
            shape.filter(r, SH.path, null).asScala.toList match {
              case Rdf4j.Statement(_, _, Rdf4j.IRI(path)) :: Nil =>
                extractShPathConstraint(sourceShape, r, path)
              case Rdf4j.Statement(_, _, Rdf4j.IRI(path)) :: _ =>
                violation(r, Some(SH.path), Some(path), Some(sourceShape), new ShMaxCountConstraintComponent(1), None, Some(moreThanOneShpath(path.toString)))
              case Rdf4j.Statement(_, _, Rdf4j.BNode(path)) :: Nil =>
                extractShPathConstraint(sourceShape, r, path)
              case Rdf4j.Statement(_, _, Rdf4j.BNode(path)) :: _ =>
                violation(r, Some(SH.path), Some(path), Some(sourceShape), new ShMaxCountConstraintComponent(1), None, Some(moreThanOneShpath(path.toString)))
              case Nil => extractShNodeConstraint(sourceShape, r)
              case Rdf4j.Statement(_, _, Rdf4j.Value(value)) =>
                violation(r, Some(SH.path), Some(value), Some(sourceShape), new ShNodeKindConstraintComponent(ShBlankNodeOrIRI), None, Some(invalidShpath(value.toString)))
            }
          case Rdf4j.Statement(_, _, Rdf4j.Value(value)) =>
            violation(r, Some(SH.predicate), Some(value), Some(sourceShape), new ShNodeKindConstraintComponent(ShBlankNodeOrIRI), None, Some(invalidShpredicate(value.toString)))
        }
      case _ => ??? // already caught by RDF4J turtle parser
    }

  def extractSetShConstraint(subject: Resource): Check[Set[ShConstraint]] = {
    val setSuccess: HashSet[ShConstraint] = HashSet.empty
    val setFailure: HashSet[ShValidationReport] = HashSet.empty
    shape.filter(subject, SH.property, null).asScala.map { stm =>
      extractShConstraint(subject, stm) match {
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

  // TODO
  def extractSetShAlgebraic(subject: Resource): Check[Set[ShAlgebraic]] = checked(Set.empty)

  def extractSetShTest(subject: Resource): Check[Set[ShTest]] =
    Apply[Check].map2(
      extractSetShConstraint(subject),
      extractSetShAlgebraic(subject)
    )((s1, s2) => s1 ++ s2)

  def extractShShapeLabel(statement: Statement): Check[Resource] =
    statement match {
      case Rdf4j.Statement(Rdf4j.Resource(r), _, _) => checked(r)
      case _ => ??? // already caught by RDF4J turtle parser
    }

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
