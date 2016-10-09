package SHACL
package core

final case class ShapeParser() {
  import cats.Apply
  import cats.data.Validated
  import cats.data.Validated._
  import org.eclipse.rdf4j.model.{ Resource, IRI, Statement, BNode }
  import scala.collection.JavaConverters._
  import org.eclipse.rdf4j.rio._
  import org.eclipse.rdf4j.model.Model
  import vocabulary.{ RDF, SH, XSD }
  import messages.ShapeParserMessages._
  import ShTarget._
  import ShConstraint._
  import ShAlgebraic._
  import ShUnaryParameter._
  import ShNaryParameter._
  import ShPropertyPath._
  import CheckAbstraction._
  import ShSeverity._

  val shape: Model = {
    val shapeInput = classOf[ShapeParser].getResourceAsStream("/shapeInput.ttl")
    val shape = Rio.parse(shapeInput, "", RDFFormat.TURTLE)
    if (shape.size == 0) throw new IllegalArgumentException("Empty shape graph.")
    shape
  }

  // TODO: accumulate errors
  val extractShSchema: Check[ShSchema] = {
    import scala.collection.mutable.HashSet
    val setShShape: HashSet[ShShape] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    shape.filter(null, RDF.ty, SH.Shape).asScala.map { stm =>
      extractShShape(stm) match {
        case Valid(v) => setShShape += v
        case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
        case _ => None
      }
    }
    if (setShSeverity.isEmpty)
      checked(new ShSchema(setShShape.toSet))
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  def extractShShapeLabel(statement: Statement): Check[Resource] =
    statement match {
      case Rdf4j.Statement(Rdf4j.Resource(r), _, _) => checked(r)
      case Rdf4j.Statement(r, _, _) => violationF(invalidResource, r.toString)
    }

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

  def extractSetShTarget(subject: Resource): Check[Set[ShTarget]] = {
    import scala.collection.mutable.HashSet
    val setShTarget: HashSet[ShTarget] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    extractSetShTargetNode(subject) match {
      case Valid(vSet) => setShTarget ++= vSet
      case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
      case _ => None
    }
    extractSetShTargetClass(subject) match {
      case Valid(vSet) => setShTarget ++= vSet
      case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
      case _ => None
    }
    extractSetShTargetSubjectsOf(subject) match {
      case Valid(vSet) => setShTarget ++= vSet
      case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
      case _ => None
    }
    extractSetShTargetObjectsOf(subject) match {
      case Valid(vSet) => setShTarget ++= vSet
      case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
      case _ => None
    }

    if (setShSeverity.isEmpty)
      checked(setShTarget.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  // TODO
  def extractSetShFilterShape(subject: Resource): Check[Set[ShShape]] = checked(Set.empty)

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

  // TODO
  def extractShNodeConstraint(resource: Resource): Check[ShNodeConstraint] = ???

  def extractShPathConstraint(resource: Resource, path: IRI, isPredicatePath: Boolean): Check[ShPathConstraint] = {
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
  }

  // TODO
  def extractShPathConstraint(resource: Resource, path: BNode): Check[ShPathConstraint] = ???

  def extractShConstraint(statement: Statement): Check[ShConstraint] =
    statement match {
      case Rdf4j.Statement(_, _, Rdf4j.Resource(r)) =>
        shape.filter(r, SH.predicate, null).asScala.toList match {
          case Rdf4j.Statement(_, _, Rdf4j.IRI(predPath)) :: Nil => extractShPathConstraint(r, predPath, true)
          case Rdf4j.Statement(_, _, Rdf4j.IRI(predPath)) :: _ => violation(moreThanOneShpredicate(predPath.toString))
          case Nil =>
            shape.filter(r, SH.path, null).asScala.toList match {
              case Rdf4j.Statement(_, _, Rdf4j.IRI(path)) :: Nil => extractShPathConstraint(r, path, false)
              case Rdf4j.Statement(_, _, Rdf4j.IRI(path)) :: _ => violation(moreThanOneShpath(path.toString))
              case Rdf4j.Statement(_, _, Rdf4j.BNode(path)) :: Nil => extractShPathConstraint(r, path)
              case Rdf4j.Statement(_, _, Rdf4j.BNode(path)) :: _ => violation(moreThanOneShpath(path.toString))
              case Nil => extractShNodeConstraint(r)
              case Rdf4j.Statement(_, _, vpath) => violation(invalidShpath(vpath.toString))
            }
          case Rdf4j.Statement(_, _, vpred) => violation(invalidShpredicate(vpred.toString))
        }
      case Rdf4j.Statement(_, _, v) => violation(invalidShConstraint(v.toString))
    }

  // TODO: accumulate errors
  def extractSetShConstraint(subject: Resource): Check[Set[ShConstraint]] = {
    import scala.collection.mutable.HashSet
    val setShConstraint: HashSet[ShConstraint] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    shape.filter(subject, SH.property, null).asScala.map { stm =>
      extractShConstraint(stm) match {
        case Valid(v) => setShConstraint += v
        case Invalid(i) => setShSeverity += i
      }
    }

    if (setShSeverity.isEmpty)
      checked(setShConstraint.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  // TODO
  def extractSetShAlgebraic(subject: Resource): Check[Set[ShAlgebraic]] = checked(Set.empty)

  def extractSetShTest(subject: Resource): Check[Set[ShTest]] = {
    import scala.collection.mutable.HashSet
    val setShTest: HashSet[ShTest] = HashSet.empty
    val setShSeverity: HashSet[ShSeverity] = HashSet.empty

    extractSetShConstraint(subject) match {
      case Valid(vSet) => setShTest ++= vSet
      case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
      case _ => None
    }
    extractSetShAlgebraic(subject) match {
      case Valid(vSet) => setShTest ++= vSet
      case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
      case _ => None
    }

    if (setShSeverity.isEmpty)
      checked(setShTest.toSet)
    else
      Validated.invalid(ShSeverities(setShSeverity.toVector))
  }

  def extractShShape(statement: Statement): Check[ShShape] =
    extractShShapeLabel(statement).andThen { label =>
      import scala.collection.mutable.HashSet
      val setShTarget: HashSet[ShTarget] = HashSet.empty
      val setShShape: HashSet[ShShape] = HashSet.empty
      val setShTest: HashSet[ShTest] = HashSet.empty
      val setShSeverity: HashSet[ShSeverity] = HashSet.empty

      extractSetShTarget(label) match {
        case Valid(vSet) => setShTarget ++= vSet
        case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
        case _ => None
      }
      extractSetShFilterShape(label) match {
        case Valid(vSet) => setShShape ++= vSet
        case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
        case _ => None
      }
      extractSetShTest(label) match {
        case Valid(vSet) => setShTest ++= vSet
        case Invalid(ShSeverities(iVector)) => setShSeverity ++= iVector.toSet
        case _ => None
      }

      if (setShSeverity.isEmpty)
        checked(new ShShape(label, setShTarget.toSet, setShShape.toSet, setShTest.toSet))
      else
        Validated.invalid(ShSeverities(setShSeverity.toVector))
  }
}
